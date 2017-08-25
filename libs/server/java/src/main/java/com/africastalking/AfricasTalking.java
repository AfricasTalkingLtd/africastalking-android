package com.africastalking;

public abstract class AfricasTalking {

  public static Server initialize(String username, String apiKey, String environment, Authenticator authenticator) {
    return new Server(username, apiKey, environment, authenticator);
  }

  public static Server initialize(String username, String apiKey, String environment) {
    return initialize(username, apiKey, environment, null);
  }


  public static Server initialize(String username, String apiKey, Authenticator authenticator) {
    return initialize(username, apiKey, "production", authenticator);
  }

  public static Server initialize(String username, String apiKey) {
    return initialize(username, apiKey, "production", null);
  }

}