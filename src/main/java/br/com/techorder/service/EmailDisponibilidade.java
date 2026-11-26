package br.com.techorder.service;

import br.com.techorder.dao.ClienteDAO;
import br.com.techorder.dao.FuncionarioDAO;
import br.com.techorder.exception.ApiException;
import br.com.techorder.model.Cliente;
import br.com.techorder.model.Funcionario;

import java.sql.Connection;

/** O e-mail é o login: não pode repetir entre clientes e funcionários. */
public class EmailDisponibilidade {

  private final ClienteDAO clienteDAO;
  private final FuncionarioDAO funcionarioDAO;

  public EmailDisponibilidade(ClienteDAO clienteDAO, FuncionarioDAO funcionarioDAO) {
    this.clienteDAO = clienteDAO;
    this.funcionarioDAO = funcionarioDAO;
  }

  public void garantirLivre(Connection conn, String email, Long ignorarCliente, Long ignorarFuncionario) {
    Cliente c = clienteDAO.buscarPorEmail(conn, email);
    if (c != null && !c.getIdCliente().equals(ignorarCliente)) {
      throw ApiException.conflito("E-mail já cadastrado");
    }
    Funcionario f = funcionarioDAO.buscarPorEmail(conn, email);
    if (f != null && !f.getIdFuncionario().equals(ignorarFuncionario)) {
      throw ApiException.conflito("E-mail já cadastrado");
    }
  }
}
