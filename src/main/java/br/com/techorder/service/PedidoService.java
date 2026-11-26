package br.com.techorder.service;

import br.com.techorder.dao.ClienteDAO;
import br.com.techorder.dao.EnderecoDAO;
import br.com.techorder.dao.PedidoDAO;
import br.com.techorder.dao.ProdutoDAO;
import br.com.techorder.database.Transacao;
import br.com.techorder.dto.ItemPedidoRequest;
import br.com.techorder.dto.PedidoRequest;
import br.com.techorder.dto.PedidoResponse;
import br.com.techorder.enums.StatusPedido;
import br.com.techorder.exception.ApiException;
import br.com.techorder.exception.RegraNegocioException;
import br.com.techorder.model.Cliente;
import br.com.techorder.model.Endereco;
import br.com.techorder.model.Funcionario;
import br.com.techorder.model.ItemPedido;
import br.com.techorder.model.Pedido;
import br.com.techorder.model.Produto;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Regras RF05–RF12 / RN04–RN10: registrar, confirmar (baixa de estoque), cancelar (devolve estoque) e consultar. */
public class PedidoService {

  private final PedidoDAO pedidoDAO;
  private final ProdutoDAO produtoDAO;
  private final ClienteDAO clienteDAO;
  private final EnderecoDAO enderecoDAO;

  public PedidoService(PedidoDAO pedidoDAO, ProdutoDAO produtoDAO, ClienteDAO clienteDAO, EnderecoDAO enderecoDAO) {
    this.pedidoDAO = pedidoDAO;
    this.produtoDAO = produtoDAO;
    this.clienteDAO = clienteDAO;
    this.enderecoDAO = enderecoDAO;
  }

  /** RF05, RF06, RF07: registra o pedido (PENDENTE) já com o total calculado e o estoque verificado. */
  public PedidoResponse criar(Sessao sessao, PedidoRequest req) {
    Long idCliente = sessao.ehCliente() ? sessao.id() : req.clienteId();
    if (idCliente == null) {
      throw ApiException.requisicaoInvalida("Informe 'clienteId' ao registrar um pedido como funcionário");
    }
    if (req.enderecoId() == null) {
      throw ApiException.requisicaoInvalida("O campo 'enderecoId' é obrigatório");
    }
    if (req.itens() == null || req.itens().isEmpty()) {
      throw ApiException.requisicaoInvalida("O pedido precisa ter ao menos um item");
    }

    // junta itens repetidos do mesmo produto e valida as quantidades (TreeMap = ordem fixa por id)
    Map<Long, Integer> quantidades = new TreeMap<>();
    for (ItemPedidoRequest item : req.itens()) {
      if (item == null || item.produtoId() == null || item.quantidade() == null || item.quantidade() <= 0) {
        throw ApiException.requisicaoInvalida("Cada item precisa de 'produtoId' e 'quantidade' maior que zero");
      }
      quantidades.merge(item.produtoId(), item.quantidade(), Integer::sum);
    }

    return Transacao.executar(conn -> {
      Cliente cliente = clienteDAO.buscarPorId(conn, idCliente);
      if (cliente == null) {
        throw ApiException.naoEncontrado("Cliente não encontrado: " + idCliente);
      }

      Endereco endereco = enderecoDAO.buscarPorId(conn, req.enderecoId());
      if (endereco == null) {
        throw ApiException.naoEncontrado("Endereço não encontrado: " + req.enderecoId());
      }
      if (!endereco.getCliente().getIdCliente().equals(idCliente)) {
        throw new RegraNegocioException("O endereço informado não pertence ao cliente do pedido");
      }

      Pedido pedido = new Pedido();
      pedido.setCliente(cliente);
      pedido.setEndereco(endereco);
      if (sessao.ehFuncionario()) {
        pedido.setFuncionario(referencia(sessao));
      }

      for (Map.Entry<Long, Integer> entrada : quantidades.entrySet()) {
        Produto produto = produtoDAO.buscarPorId(conn, entrada.getKey());
        if (produto == null) {
          throw ApiException.naoEncontrado("Produto não encontrado: " + entrada.getKey());
        }
        if (!Boolean.TRUE.equals(produto.getAtivo())) {
          throw new RegraNegocioException("O produto '" + produto.getNome() + "' está inativo");
        }
        if (!produto.possuiEstoque(entrada.getValue())) {
          throw new RegraNegocioException("Estoque insuficiente para o produto '" + produto.getNome() + "'");
        }
        pedido.adicionarItem(new ItemPedido(null, pedido, produto, entrada.getValue(), produto.getPreco()));
      }

      return PedidoResponse.de(pedidoDAO.salvar(conn, pedido));
    });
  }

  /** RF11 / RF12 (RN09 / RN10): cliente vê só os seus; funcionário vê todos. */
  public List<PedidoResponse> listar(Sessao sessao) {
    return Transacao.executar(conn -> {
      List<Pedido> pedidos = sessao.ehFuncionario()
          ? pedidoDAO.listarTodos(conn)
          : pedidoDAO.listarPorCliente(conn, sessao.id());
      return pedidos.stream().map(PedidoResponse::de).toList();
    });
  }

  public PedidoResponse buscar(Sessao sessao, Long id) {
    return Transacao.executar(conn -> {
      Pedido pedido = obter(conn, id, false);
      if (sessao.ehCliente() && !pedido.getCliente().getIdCliente().equals(sessao.id())) {
        throw ApiException.proibido("Você só pode consultar os seus próprios pedidos");
      }
      return PedidoResponse.de(pedido);
    });
  }

  /** RF08 / RN06 / RN08: confirma e baixa o estoque; se faltar estoque, nada é alterado (rollback). */
  public PedidoResponse confirmar(Sessao sessao, Long id) {
    return Transacao.executar(conn -> {
      Pedido pedido = obter(conn, id, true);
      pedido.confirmar(referencia(sessao));

      for (ItemPedido item : pedido.getItens()) {
        Produto produto = produtoDAO.buscarPorIdParaAtualizar(conn, item.getProduto().getIdProduto());
        produto.baixarEstoque(item.getQuantidade());
        produtoDAO.atualizarEstoque(conn, produto.getIdProduto(), produto.getQuantidadeEstoque());
      }

      pedidoDAO.atualizarStatus(conn, pedido);
      return PedidoResponse.de(pedido);
    });
  }

  /** RF09 / RN07: cancela; se o pedido estava CONFIRMADO, devolve os produtos ao estoque. */
  public PedidoResponse cancelar(Sessao sessao, Long id) {
    return Transacao.executar(conn -> {
      Pedido pedido = obter(conn, id, true);
      boolean estavaConfirmado = pedido.getStatus() == StatusPedido.CONFIRMADO;
      pedido.cancelar(referencia(sessao));

      if (estavaConfirmado) {
        for (ItemPedido item : pedido.getItens()) {
          Produto produto = produtoDAO.buscarPorIdParaAtualizar(conn, item.getProduto().getIdProduto());
          produto.adicionarEstoque(item.getQuantidade());
          produtoDAO.atualizarEstoque(conn, produto.getIdProduto(), produto.getQuantidadeEstoque());
        }
      }

      pedidoDAO.atualizarStatus(conn, pedido);
      return PedidoResponse.de(pedido);
    });
  }

  private Pedido obter(Connection conn, Long id, boolean bloquear) {
    Pedido pedido = pedidoDAO.buscarPorId(conn, id, bloquear);
    if (pedido == null) {
      throw ApiException.naoEncontrado("Pedido não encontrado: " + id);
    }
    return pedido;
  }

  private Funcionario referencia(Sessao sessao) {
    Funcionario funcionario = new Funcionario();
    funcionario.setIdFuncionario(sessao.id());
    return funcionario;
  }
}
