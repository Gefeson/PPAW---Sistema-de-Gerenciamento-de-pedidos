package br.com.techorder.service;

import br.com.techorder.dao.ClienteDAO;
import br.com.techorder.dao.FuncionarioDAO;
import br.com.techorder.database.Transacao;
import br.com.techorder.exception.ApiException;
import br.com.techorder.http.Requisicao;
import br.com.techorder.model.Cliente;
import br.com.techorder.model.Funcionario;
import br.com.techorder.util.SenhaUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Autenticação por HTTP Basic (e-mail:senha) e verificação de perfis.
 * No Postman: aba Authorization > Basic Auth.
 */
public class AutenticacaoService {

  private final ClienteDAO clienteDAO;
  private final FuncionarioDAO funcionarioDAO;

  public AutenticacaoService(ClienteDAO clienteDAO, FuncionarioDAO funcionarioDAO) {
    this.clienteDAO = clienteDAO;
    this.funcionarioDAO = funcionarioDAO;
  }

  /** Devolve null se a requisição não trouxe credenciais; 401 se trouxe e estão erradas. */
  public Sessao autenticarOpcional(Requisicao requisicao) {
    String cabecalho = requisicao.header("Authorization");
    if (cabecalho == null || !cabecalho.regionMatches(true, 0, "Basic ", 0, 6)) {
      return null;
    }

    String credenciais;
    try {
      credenciais = new String(Base64.getDecoder().decode(cabecalho.substring(6).trim()), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      throw ApiException.naoAutenticado("Cabeçalho Authorization inválido");
    }

    int separador = credenciais.indexOf(':');
    if (separador < 0) {
      throw ApiException.naoAutenticado("Cabeçalho Authorization inválido");
    }
    String email = credenciais.substring(0, separador).trim().toLowerCase();
    String senha = credenciais.substring(separador + 1);

    Sessao sessao = Transacao.executar(conn -> {
      Funcionario f = funcionarioDAO.buscarPorEmail(conn, email);
      if (f != null && SenhaUtil.confere(senha, f.getSenhaHash())) {
        return Sessao.funcionario(f.getIdFuncionario(), f.getPerfil());
      }
      Cliente c = clienteDAO.buscarPorEmail(conn, email);
      if (c != null && SenhaUtil.confere(senha, c.getSenhaHash())) {
        return Sessao.cliente(c.getIdCliente());
      }
      return null;
    });

    if (sessao == null) {
      throw ApiException.naoAutenticado("E-mail ou senha inválidos");
    }
    return sessao;
  }

  public Sessao exigirLogin(Requisicao requisicao) {
    Sessao sessao = autenticarOpcional(requisicao);
    if (sessao == null) {
      throw ApiException.naoAutenticado("Autenticação necessária (Basic Auth com e-mail e senha)");
    }
    return sessao;
  }

  public Sessao exigirFuncionario(Requisicao requisicao) {
    Sessao sessao = exigirLogin(requisicao);
    if (!sessao.ehFuncionario()) {
      throw ApiException.proibido("Apenas funcionários podem realizar esta operação");
    }
    return sessao;
  }

  public Sessao exigirAdministrador(Requisicao requisicao) {
    Sessao sessao = exigirLogin(requisicao);
    if (!sessao.ehAdministrador()) {
      throw ApiException.proibido("Apenas funcionários com perfil ADMINISTRADOR podem realizar esta operação");
    }
    return sessao;
  }

  /** O próprio cliente ou qualquer funcionário. */
  public Sessao exigirDonoOuFuncionario(Requisicao requisicao, Long idCliente) {
    Sessao sessao = exigirLogin(requisicao);
    if (sessao.ehCliente() && !sessao.id().equals(idCliente)) {
      throw ApiException.proibido("Você só pode acessar os seus próprios dados");
    }
    return sessao;
  }
}
