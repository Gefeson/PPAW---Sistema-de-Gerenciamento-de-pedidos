package br.com.techorder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.model.Pagamento;

public class PagamentoDAO {

    public Pagamento salvar(Connection conn, Pagamento pagamento) {
        String sql = """
            INSERT INTO pagamento
            (id_pedido, forma_pagamento, valor, status, data_pagamento)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id_pagamento
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, pagamento.getPedido().getIdPedido());
            stmt.setString(2, pagamento.getFormaPagamento().name());
            stmt.setBigDecimal(3, pagamento.getValor());
            stmt.setString(4, pagamento.getStatus().name());

            if (pagamento.getDataPagamento() != null) {
                stmt.setTimestamp(5,
                        java.sql.Timestamp.valueOf(pagamento.getDataPagamento()));
            } else {
                stmt.setNull(5, java.sql.Types.TIMESTAMP);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                pagamento.setIdPagamento(rs.getLong("id_pagamento"));
            }

            return pagamento;

        } catch (SQLException e) {
            throw new ErroBancoException("Erro ao salvar pagamento", e);
        }
    }

    public Pagamento buscarPorPedido(Connection conn, Long idPedido) {
        String sql = "SELECT * FROM pagamento WHERE id_pedido = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Pagamento pagamento = new Pagamento();
                pagamento.setIdPagamento(rs.getLong("id_pagamento"));
                pagamento.setFormaPagamento(
                        br.com.techorder.enums.FormaPagamento.valueOf(
                                rs.getString("forma_pagamento")));
                pagamento.setValor(rs.getBigDecimal("valor"));
                pagamento.setStatus(
                        br.com.techorder.enums.StatusPagamento.valueOf(
                                rs.getString("status")));

                if (rs.getTimestamp("data_pagamento") != null) {
                    pagamento.setDataPagamento(
                            rs.getTimestamp("data_pagamento").toLocalDateTime());
                }

                return pagamento;
            }

        } catch (SQLException e) {
            throw new ErroBancoException("Erro ao buscar pagamento", e);
        }
    }
}