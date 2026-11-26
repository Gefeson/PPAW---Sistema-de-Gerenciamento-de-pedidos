package br.com.techorder.http;

/** Status HTTP + objeto que será serializado como JSON (corpo pode ser null). */
public record Resposta(int status, Object corpo) {

  public static Resposta ok(Object corpo)      { return new Resposta(200, corpo); }
  public static Resposta criado(Object corpo)  { return new Resposta(201, corpo); }
  public static Resposta semConteudo()         { return new Resposta(204, null); }
}
