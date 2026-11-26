package br.com.techorder.service;

import br.com.techorder.dao.ClienteDAO;
import br.com.techorder.database.Transacao;
import br.com.techorder.dto.ClienteRequest;
import br.com.techorder.dto.ClienteResponse;
import br.com.techorder.exception.ApiException;
import br.com.techorder.model.Cliente;
import br.com.techorder.util.SenhaUtil;
import br.com.techorder.util.Validacao;

import java.util.List;

public class ClienteService {

  private final ClienteDAO clienteDAO;
  private final EmailDisponibilidade emails;

  public ClienteService(ClienteDAO clienteDAO, EmailDisponibilidade emails) {
    this.clienteDAO = clienteDAO;
    this.emails = emails;
  }

  public ClienteResponse criar(ClienteRequest req) {
    String nome = Validacao.texto(req.nome(), "nome", 100);
    String email = Validacao.email(req.email());
    Validacao.senha(req.senha());

    Cliente cliente = new Cliente(null, nome, email, SenhaUtil.gerarHash(req.senha()), req.telefone());

    return Transacao.executar(conn -> {
      emails.garantirLivre(conn, email, null, null);
      return ClienteResponse.de(clienteDAO.salvar(conn, cliente));
    });
  }

  public List<ClienteResponse> listar() {
    return Transacao.executar(conn -> clienteDAO.listarTodos(conn).stream().map(ClienteResponse::de).toList());
  }

  public ClienteResponse buscar(Long id) {
    return Transacao.executar(conn -> ClienteResponse.de(obter(conn, id)));
  }

  /** Atualização parcial: só os campos enviados são alterados. */
  public ClienteResponse atualizar(Long id, ClienteRequest req) {
    return Transacao.executar(conn -> {
      Cliente cliente = obter(conn, id);

      if (req.nome() != null) {
        cliente.setNome(Validacao.texto(req.nome(), "nome", 100));
      }
      if (req.email() != null) {
        String email = Validacao.email(req.email());
        emails.garantirLivre(conn, email, id, null);
        cliente.setEmail(email);
      }
      if (req.senha() != null) {
        Validacao.senha(req.senha());
        cliente.setSenhaHash(SenhaUtil.gerarHash(req.senha()));
      }
      if (req.telefone() != null) {
        cliente.setTelefone(req.telefone());
      }

      clienteDAO.atualizar(conn, cliente);
      return ClienteResponse.de(cliente);
    });
  }

  public void excluir(Long id) {
    Transacao.executar(conn -> {
      obter(conn, id);
      clienteDAO.excluir(conn, id);
      return null;
    });
  }

  private Cliente obter(java.sql.Connection conn, Long id) {
    Cliente cliente = clienteDAO.buscarPorId(conn, id);
    if (cliente == null) {
      throw ApiException.naoEncontrado("Cliente não encontrado: " + id);
    }
    return cliente;
  }
}
