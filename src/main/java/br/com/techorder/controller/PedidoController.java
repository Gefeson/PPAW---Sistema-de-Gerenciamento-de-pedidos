package br.com.techorder.controller;

import br.com.techorder.dto.PedidoRequest;
import br.com.techorder.http.Resposta;
import br.com.techorder.http.Router;
import br.com.techorder.service.AutenticacaoService;
import br.com.techorder.service.PedidoService;

public class PedidoController {

  private final PedidoService service;
  private final AutenticacaoService auth;

  public PedidoController(PedidoService service, AutenticacaoService auth) {
    this.service = service;
    this.auth = auth;
  }

  public void registrar(Router router) {
    router.post("/pedidos", r ->
        Resposta.criado(service.criar(auth.exigirLogin(r), r.corpoComo(PedidoRequest.class))));

    router.get("/pedidos", r -> Resposta.ok(service.listar(auth.exigirLogin(r))));

    router.get("/pedidos/{id}", r -> Resposta.ok(service.buscar(auth.exigirLogin(r), r.paramId("id"))));

    router.post("/pedidos/{id}/confirmar", r ->
        Resposta.ok(service.confirmar(auth.exigirFuncionario(r), r.paramId("id"))));

    router.post("/pedidos/{id}/cancelar", r ->
        Resposta.ok(service.cancelar(auth.exigirFuncionario(r), r.paramId("id"))));
  }
}
