package br.com.techorder.util;

import br.com.techorder.exception.ApiException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Validações de entrada reutilizadas pelos Services (lançam HTTP 400). */
public final class Validacao {

  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private Validacao() {
  }

  public static String texto(String valor, String campo, int max) {
    if (valor == null || valor.isBlank()) {
      throw ApiException.requisicaoInvalida("O campo '" + campo + "' é obrigatório");
    }
    String limpo = valor.trim();
    if (limpo.length() > max) {
      throw ApiException.requisicaoInvalida("O campo '" + campo + "' aceita no máximo " + max + " caracteres");
    }
    return limpo;
  }

  public static String email(String valor) {
    String limpo = texto(valor, "email", 150).toLowerCase();
    if (!EMAIL.matcher(limpo).matches()) {
      throw ApiException.requisicaoInvalida("E-mail inválido");
    }
    return limpo;
  }

  public static void senha(String valor) {
    if (valor == null || valor.length() < 6) {
      throw ApiException.requisicaoInvalida("A senha é obrigatória e deve ter ao menos 6 caracteres");
    }
  }

  public static BigDecimal preco(BigDecimal valor) {
    if (valor == null || valor.signum() < 0) {
      throw ApiException.requisicaoInvalida("O campo 'preco' é obrigatório e não pode ser negativo");
    }
    return valor;
  }

  public static int estoque(Integer valor) {
    if (valor == null || valor < 0) {
      throw ApiException.requisicaoInvalida("O campo 'quantidadeEstoque' é obrigatório e não pode ser negativo");
    }
    return valor;
  }
}
