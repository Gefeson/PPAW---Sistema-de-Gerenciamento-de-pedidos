package br.com.techorder.http;

@FunctionalInterface
public interface Handler {
  Resposta tratar(Requisicao requisicao) throws Exception;
}
