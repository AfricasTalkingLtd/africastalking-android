package com.africastalking;

import java.io.IOException;

import io.grpc.ManagedChannel;

public final class AfricasTalking{

  private static String HOST;
  private static int PORT = 35897;

  private static String tokenString;

  private static AccountService account;
  private static AirtimeService airtime;
  private static PaymentsService payments;
  private static SMSService sms;
  private static Voice voice;
  private static Token token;

  private static ManagedChannel CHANNEL;
  static Environment ENV = Environment.SANDBOX;
  static Boolean LOGGING = false;
  static Logger LOGGER = new BaseLogger();


  public static void initialize(String host){
    HOST = host;
    CHANNEL = null;
    tokenString = getToken();
  }
  public static void initialize(String host, int port){
    HOST = host;
    PORT = port;
    CHANNEL = null;
    tokenString = getToken();
  }

  protected static ManagedChannel getChannel(){
    if(HOST == null || PORT == -1) throw  new RuntimeException("call AfricasTalking.initialize(host, port, token) first");
    if(CHANNEL == null){

    }
    return CHANNEL;
  }

  public static SMSService getSmsService(){
    if(sms == null){
      sms = new SMSService();
      return sms;
    }
    return sms;
  }
  public static AirtimeService getAirtimeService(){
    if(airtime == null){
      airtime = new AirtimeService();
    }
    return airtime;
  }
  public static PaymentsService getPaymentsService(){
    if(payments == null){
      payments = new PaymentsService();
    }
    return payments;
  }
  public static AccountService getAccount(){
    if(account == null){
      account = new AccountService();
    }
    return account;
  }

  protected static String getToken() {
    if(token == null) {
      token = new Token();
      try {
        tokenString = token.getToken();
      } catch (IOException e) {
        LOGGER.log(e.getMessage());
      }
    }
    else{
      //TODO check if token not expired
    }
    return tokenString;
  }



  public static void setEnvironment(Environment env) {
    ENV = env;
  }

  public static void enableLogging(boolean enable) {
    LOGGING = enable;
  }

  public static void setLogger(Logger logger) {
    if (logger != null) {
      enableLogging(true);
    }
    LOGGER = logger;
  }
}
