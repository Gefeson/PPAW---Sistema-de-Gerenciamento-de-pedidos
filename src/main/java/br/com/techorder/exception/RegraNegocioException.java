package br.com.techorder.exception;

/** Violação de regra de negócio (ex.: estoque insuficiente). Vira HTTP 422. */
public class RegraNegocioException extends RuntimeException {

  public RegraNegocioException(String mensagem) {
    super(mensagem);
  }
}
