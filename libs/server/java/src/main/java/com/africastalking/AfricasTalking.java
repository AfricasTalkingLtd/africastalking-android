package com.africastalking;

public abstract class AfricasTalking {

  public static Server initialize(String username, String apiKey, Authenticator authenticator) {
    return new Server(username, apiKey, authenticator);
  }

  public static Server initialize(String username, String apiKey) {
    return initialize(username, apiKey, null);
  }

}