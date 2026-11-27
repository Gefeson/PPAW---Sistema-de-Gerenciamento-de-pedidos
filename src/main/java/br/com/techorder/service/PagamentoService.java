package br.com.techorder.service;

import java.time.LocalDateTime;

import br.com.techorder.dao.PagamentoDAO;
import br.com.techorder.dao.PedidoDAO;
import br.com.techorder.database.Transacao;
import br.com.techorder.dto.PagamentoRequest;
import br.com.techorder.dto.PagamentoResponse;
import br.com.techorder.enums.StatusPagamento;
import br.com.techorder.enums.StatusPedido;
import br.com.techorder.exception.ApiException;
import br.com.techorder.exception.RegraNegocioException;
import br.com.techorder.model.Pagamento;
import br.com.techorder.model.Pedido;

public class PagamentoService {

    private final PagamentoDAO pagamentoDAO;
    private final PedidoDAO pedidoDAO;

    public PagamentoService(PagamentoDAO pagamentoDAO, PedidoDAO pedidoDAO) {
        this.pagamentoDAO = pagamentoDAO;
        this.pedidoDAO = pedidoDAO;
    }

    public PagamentoResponse pagar(Sessao sessao, Long idPedido, PagamentoRequest req) {

        if (!sessao.ehCliente()) {
            throw ApiException.proibido("Somente clientes podem realizar pagamentos");
        }

        if (req == null || req.formaPagamento() == null) {
            throw ApiException.requisicaoInvalida("Informe a forma de pagamento");
        }

        return Transacao.executar(conn -> {

            Pedido pedido = pedidoDAO.buscarPorId(conn, idPedido, true);

            if (pedido == null) {
                throw ApiException.naoEncontrado("Pedido não encontrado");
            }

            if (!pedido.getCliente().getIdCliente().equals(sessao.id())) {
                throw ApiException.proibido("Você só pode pagar seus próprios pedidos");
            }

            if (pedido.getStatus() != StatusPedido.CONFIRMADO) {
                throw new RegraNegocioException(
                        "Somente pedidos CONFIRMADOS podem ser pagos");
            }

            if (pagamentoDAO.buscarPorPedido(conn, idPedido) != null) {
                throw ApiException.conflito("Este pedido já possui um pagamento");
            }

            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setFormaPagamento(req.formaPagamento());
            pagamento.setValor(pedido.getValorTotal());
            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setDataPagamento(LocalDateTime.now());

            pagamentoDAO.salvar(conn, pagamento);

            return PagamentoResponse.de(pagamento);
        });
    }

    public PagamentoResponse buscar(Sessao sessao, Long idPedido) {

        return Transacao.executar(conn -> {

            Pedido pedido = pedidoDAO.buscarPorId(conn, idPedido, false);

            if (pedido == null) {
                throw ApiException.naoEncontrado("Pedido não encontrado");
            }

            if (sessao.ehCliente()
                    && !pedido.getCliente().getIdCliente().equals(sessao.id())) {
                throw ApiException.proibido("Você só pode consultar seus próprios pagamentos");
            }

            Pagamento pagamento = pagamentoDAO.buscarPorPedido(conn, idPedido);

            if (pagamento == null) {
                throw ApiException.naoEncontrado("Pagamento não encontrado");
            }

            pagamento.setPedido(pedido);

            return PagamentoResponse.de(pagamento);
        });
    }
}