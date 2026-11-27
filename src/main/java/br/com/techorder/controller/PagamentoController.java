package br.com.techorder.controller;

import br.com.techorder.dto.PagamentoRequest;
import br.com.techorder.http.Resposta;
import br.com.techorder.http.Router;
import br.com.techorder.service.AutenticacaoService;
import br.com.techorder.service.PagamentoService;

public class PagamentoController {

    private final PagamentoService service;
    private final AutenticacaoService auth;

    public PagamentoController(PagamentoService service, AutenticacaoService auth) {
        this.service = service;
        this.auth = auth;
    }

    public void registrar(Router router) {

        router.post("/pedidos/{id}/pagamento", r ->
                Resposta.criado(service.pagar(
                        auth.exigirLogin(r),
                        r.paramId("id"),
                        r.corpoComo(PagamentoRequest.class)
                ))
        );

        router.get("/pedidos/{id}/pagamento", r ->
                Resposta.ok(service.buscar(
                        auth.exigirLogin(r),
                        r.paramId("id")
                ))
        );
    }
}