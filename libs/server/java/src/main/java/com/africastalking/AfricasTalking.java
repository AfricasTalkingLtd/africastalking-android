package com.africastalking;

public abstract class AfricasTalking {

  public static ATServer initialize(String username, String apiKey, String environment, Authenticator authenticator) {
    return new ATServer(username, apiKey, environment, authenticator);
  }

  public static ATServer initialize(String username, String apiKey, String environment) {
    return initialize(username, apiKey, environment, null);
  }


  public static ATServer initialize(String username, String apiKey, Authenticator authenticator) {
    return initialize(username, apiKey, "production", authenticator);
  }

  public static ATServer initialize(String username, String apiKey) {
    return initialize(username, apiKey, "production", null);
  }

}