package br.com.techorder.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDateTime;

/** Ponto único de conversão objeto <-> JSON (Gson). */
public final class Json {

  private static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .registerTypeAdapter(LocalDateTime.class,
          (JsonSerializer<LocalDateTime>) (valor, tipo, ctx) -> new JsonPrimitive(valor.toString()))
      .create();

  private Json() {
  }

  public static String paraJson(Object objeto) {
    return GSON.toJson(objeto);
  }

  public static <T> T deJson(String json, Class<T> tipo) {
    return GSON.fromJson(json, tipo);
  }
}
