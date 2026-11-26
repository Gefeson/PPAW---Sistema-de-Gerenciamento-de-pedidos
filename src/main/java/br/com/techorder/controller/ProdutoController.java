package br.com.techorder.controller;

import br.com.techorder.dto.ProdutoRequest;
import br.com.techorder.http.Requisicao;
import br.com.techorder.http.Resposta;
import br.com.techorder.http.Router;
import br.com.techorder.service.AutenticacaoService;
import br.com.techorder.service.ProdutoService;
import br.com.techorder.service.Sessao;

public class ProdutoController {

  private final ProdutoService service;
  private final AutenticacaoService auth;

  public ProdutoController(ProdutoService service, AutenticacaoService auth) {
    this.service = service;
    this.auth = auth;
  }

  public void registrar(Router router) {
    // catálogo aberto: visitantes e clientes veem só produtos ativos
    router.get("/produtos", r -> Resposta.ok(service.listar(ehFuncionario(r))));

    router.get("/produtos/{id}", r -> Resposta.ok(service.buscar(r.paramId("id"), ehFuncionario(r))));

    router.post("/produtos", r -> {
      auth.exigirFuncionario(r);
      return Resposta.criado(service.criar(r.corpoComo(ProdutoRequest.class)));
    });

    router.put("/produtos/{id}", r -> {
      auth.exigirFuncionario(r);
      return Resposta.ok(service.atualizar(r.paramId("id"), r.corpoComo(ProdutoRequest.class)));
    });

    router.delete("/produtos/{id}", r -> {
      auth.exigirFuncionario(r);
      service.excluir(r.paramId("id"));
      return Resposta.semConteudo();
    });
  }

  private boolean ehFuncionario(Requisicao r) {
    Sessao sessao = auth.autenticarOpcional(r);
    return sessao != null && sessao.ehFuncionario();
  }
}
