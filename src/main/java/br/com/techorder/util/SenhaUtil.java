package br.com.techorder.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Hash de senha com PBKDF2 (somente JDK). Formato salvo: iteracoes:salt:hash (Base64). */
public final class SenhaUtil {

  private static final int ITERACOES = 65536;
  private static final int TAMANHO_CHAVE = 256;

  private SenhaUtil() {
  }

  public static String gerarHash(String senha) {
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    byte[] hash = pbkdf2(senha.toCharArray(), salt, ITERACOES);
    return ITERACOES + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
  }

  public static boolean confere(String senha, String armazenado) {
    try {
      String[] partes = armazenado.split(":");
      int iteracoes = Integer.parseInt(partes[0]);
      byte[] salt = Base64.getDecoder().decode(partes[1]);
      byte[] esperado = Base64.getDecoder().decode(partes[2]);
      return MessageDigest.isEqual(esperado, pbkdf2(senha.toCharArray(), salt, iteracoes));
    } catch (RuntimeException e) {
      return false;
    }
  }

  private static byte[] pbkdf2(char[] senha, byte[] salt, int iteracoes) {
    try {
      return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
          .generateSecret(new PBEKeySpec(senha, salt, iteracoes, TAMANHO_CHAVE))
          .getEncoded();
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("PBKDF2 indisponível", e);
    }
  }
}
