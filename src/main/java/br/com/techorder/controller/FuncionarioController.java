package br.com.techorder.controller;

import br.com.techorder.dto.FuncionarioRequest;
import br.com.techorder.exception.RegraNegocioException;
import br.com.techorder.http.Resposta;
import br.com.techorder.http.Router;
import br.com.techorder.service.AutenticacaoService;
import br.com.techorder.service.FuncionarioService;
import br.com.techorder.service.Sessao;

/** RF13: somente administradores gerenciam funcionários. */
public class FuncionarioController {

  private final FuncionarioService service;
  private final AutenticacaoService auth;

  public FuncionarioController(FuncionarioService service, AutenticacaoService auth) {
    this.service = service;
    this.auth = auth;
  }

  public void registrar(Router router) {
    router.post("/funcionarios", r -> {
      auth.exigirAdministrador(r);
      return Resposta.criado(service.criar(r.corpoComo(FuncionarioRequest.class)));
    });

    router.get("/funcionarios", r -> {
      auth.exigirAdministrador(r);
      return Resposta.ok(service.listar());
    });

    router.get("/funcionarios/{id}", r -> {
      auth.exigirAdministrador(r);
      return Resposta.ok(service.buscar(r.paramId("id")));
    });

    router.put("/funcionarios/{id}", r -> {
      auth.exigirAdministrador(r);
      return Resposta.ok(service.atualizar(r.paramId("id"), r.corpoComo(FuncionarioRequest.class)));
    });

    router.delete("/funcionarios/{id}", r -> {
      Sessao sessao = auth.exigirAdministrador(r);
      Long id = r.paramId("id");
      if (sessao.id().equals(id)) {
        throw new RegraNegocioException("Você não pode excluir o seu próprio usuário");
      }
      service.excluir(id);
      return Resposta.semConteudo();
    });
  }
}
