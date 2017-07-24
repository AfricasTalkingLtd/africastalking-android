package com.africastalking;

public abstract class AfricasTalking {

  public static ATServer initialize(String username, String apiKey, String environment) {
    ATServer server = new ATServer(username, apiKey, environment);
    return server;
  }

  public static ATServer initialize(String username, String apiKey) {
    return initialize(username, apiKey, "production");
  }

}