package br.com.techorder.service;

import br.com.techorder.dao.FuncionarioDAO;
import br.com.techorder.database.Transacao;
import br.com.techorder.dto.FuncionarioRequest;
import br.com.techorder.dto.FuncionarioResponse;
import br.com.techorder.enums.PerfilFuncionario;
import br.com.techorder.exception.ApiException;
import br.com.techorder.model.Funcionario;
import br.com.techorder.util.SenhaUtil;
import br.com.techorder.util.Validacao;

import java.util.List;

public class FuncionarioService {

  private final FuncionarioDAO funcionarioDAO;
  private final EmailDisponibilidade emails;

  public FuncionarioService(FuncionarioDAO funcionarioDAO, EmailDisponibilidade emails) {
    this.funcionarioDAO = funcionarioDAO;
    this.emails = emails;
  }

  /** Sem nenhum funcionário não haveria quem cadastrar o primeiro administrador (RF13). */
  public void garantirAdministradorInicial() {
    String email = envOu("TECHORDER_ADMIN_EMAIL", "admin@techorder.com");
    String senha = envOu("TECHORDER_ADMIN_SENHA", "admin123");

    boolean criado = Transacao.executar(conn -> {
      if (funcionarioDAO.contar(conn) > 0) {
        return false;
      }
      funcionarioDAO.salvar(conn, new Funcionario(null, "Administrador", email.toLowerCase(),
          SenhaUtil.gerarHash(senha), PerfilFuncionario.ADMINISTRADOR));
      return true;
    });

    if (criado) {
      System.out.println("Administrador inicial criado: " + email + " (troque a senha após o primeiro acesso)");
    }
  }

  public FuncionarioResponse criar(FuncionarioRequest req) {
    String nome = Validacao.texto(req.nome(), "nome", 100);
    String email = Validacao.email(req.email());
    Validacao.senha(req.senha());
    PerfilFuncionario perfil = exigirPerfil(req.perfil());

    Funcionario funcionario = new Funcionario(null, nome, email, SenhaUtil.gerarHash(req.senha()), perfil);

    return Transacao.executar(conn -> {
      emails.garantirLivre(conn, email, null, null);
      return FuncionarioResponse.de(funcionarioDAO.salvar(conn, funcionario));
    });
  }

  public List<FuncionarioResponse> listar() {
    return Transacao.executar(conn -> funcionarioDAO.listarTodos(conn).stream().map(FuncionarioResponse::de).toList());
  }

  public FuncionarioResponse buscar(Long id) {
    return Transacao.executar(conn -> FuncionarioResponse.de(obter(conn, id)));
  }

  public FuncionarioResponse atualizar(Long id, FuncionarioRequest req) {
    return Transacao.executar(conn -> {
      Funcionario f = obter(conn, id);

      if (req.nome() != null) {
        f.setNome(Validacao.texto(req.nome(), "nome", 100));
      }
      if (req.email() != null) {
        String email = Validacao.email(req.email());
        emails.garantirLivre(conn, email, null, id);
        f.setEmail(email);
      }
      if (req.senha() != null) {
        Validacao.senha(req.senha());
        f.setSenhaHash(SenhaUtil.gerarHash(req.senha()));
      }
      if (req.perfil() != null) {
        f.setPerfil(req.perfil());
      }

      funcionarioDAO.atualizar(conn, f);
      return FuncionarioResponse.de(f);
    });
  }

  public void excluir(Long id) {
    Transacao.executar(conn -> {
      obter(conn, id);
      funcionarioDAO.excluir(conn, id);
      return null;
    });
  }

  private PerfilFuncionario exigirPerfil(PerfilFuncionario perfil) {
    if (perfil == null) {
      throw ApiException.requisicaoInvalida("O campo 'perfil' é obrigatório (ADMINISTRADOR ou ATENDENTE)");
    }
    return perfil;
  }

  private Funcionario obter(java.sql.Connection conn, Long id) {
    Funcionario f = funcionarioDAO.buscarPorId(conn, id);
    if (f == null) {
      throw ApiException.naoEncontrado("Funcionário não encontrado: " + id);
    }
    return f;
  }

  private static String envOu(String chave, String padrao) {
    String valor = System.getenv(chave);
    return (valor == null || valor.isBlank()) ? padrao : valor;
  }
}
