package br.com.techorder.controller;

import br.com.techorder.dto.ClienteRequest;
import br.com.techorder.http.Resposta;
import br.com.techorder.http.Router;
import br.com.techorder.service.AutenticacaoService;
import br.com.techorder.service.ClienteService;

public class ClienteController {

  private final ClienteService service;
  private final AutenticacaoService auth;

  public ClienteController(ClienteService service, AutenticacaoService auth) {
    this.service = service;
    this.auth = auth;
  }

  public void registrar(Router router) {
    // RF01: cadastro aberto (o cliente ainda não tem login)
    router.post("/clientes", r -> Resposta.criado(service.criar(r.corpoComo(ClienteRequest.class))));

    router.get("/clientes", r -> {
      auth.exigirFuncionario(r);
      return Resposta.ok(service.listar());
    });

    router.get("/clientes/{id}", r -> {
      Long id = r.paramId("id");
      auth.exigirDonoOuFuncionario(r, id);
      return Resposta.ok(service.buscar(id));
    });

    router.put("/clientes/{id}", r -> {
      Long id = r.paramId("id");
      auth.exigirDonoOuFuncionario(r, id);
      return Resposta.ok(service.atualizar(id, r.corpoComo(ClienteRequest.class)));
    });

    router.delete("/clientes/{id}", r -> {
      auth.exigirFuncionario(r);
      service.excluir(r.paramId("id"));
      return Resposta.semConteudo();
    });
  }
}
