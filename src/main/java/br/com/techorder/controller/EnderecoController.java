package br.com.techorder.controller;

import br.com.techorder.dto.EnderecoRequest;
import br.com.techorder.http.Resposta;
import br.com.techorder.http.Router;
import br.com.techorder.service.AutenticacaoService;
import br.com.techorder.service.EnderecoService;

public class EnderecoController {

  private final EnderecoService service;
  private final AutenticacaoService auth;

  public EnderecoController(EnderecoService service, AutenticacaoService auth) {
    this.service = service;
    this.auth = auth;
  }

  public void registrar(Router router) {
    // RF04: consulta de CEP (API externa ViaCEP)
    router.get("/cep/{cep}", r -> Resposta.ok(service.consultarCep(r.param("cep"))));

    router.post("/clientes/{id}/enderecos", r -> {
      Long id = r.paramId("id");
      auth.exigirDonoOuFuncionario(r, id);
      return Resposta.criado(service.cadastrar(id, r.corpoComo(EnderecoRequest.class)));
    });

    router.get("/clientes/{id}/enderecos", r -> {
      Long id = r.paramId("id");
      auth.exigirDonoOuFuncionario(r, id);
      return Resposta.ok(service.listarPorCliente(id));
    });
  }
}
