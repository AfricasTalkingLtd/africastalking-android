package com.africastalking.example;

import com.africastalking.Server;
import com.africastalking.AfricasTalking;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;


public class App {

    private static final int HTTP_PORT = 3001;
    private static final int RPC_PORT = 35897;

    private static final String USERNAME = "sandbox";
    private static final String API_KEY = "fake";

    private static final String SIP_USERNAME = "fake";
    private static final String SIP_PASSWORD = "key";
    private static final String SIP_HOST = "ke.sip.africastalking.com";
    private static final String SIP_TRANSPORT = "udp";
    private static final int SIP_PORT = 5060;


    private final static List<String> ALLOWED_CLIENTS = Arrays.asList(
            "zFTF4GTJS6n3bryppQRXP7zg", // e.g. generated when a user logs into my application
            "2k3KuYSdhsjSAJeQPMCz5kcG",
            "mvyXTQ8sVChSryf3nRdn4EWh",
            "fZgVEQNXB55Mt4VAAe3ExKRk",
            "rsZBpDQSWfAHJHwkABbgH8cx"
    );


    // private static Server sdkServer;
    private static HandlebarsTemplateEngine hbs = new HandlebarsTemplateEngine("/views");

    public static void main(String[] args) {

        // SDK Server
        try {
            AfricasTalking.initialize(USERNAME, API_KEY);
            AfricasTalking.setLogger((s, objects) -> System.out.println(String.format(s, objects)));
            Server sdkServer = new Server(ALLOWED_CLIENTS::contains);
            sdkServer.addSipCredentials(SIP_USERNAME, SIP_PASSWORD, SIP_HOST, SIP_PORT, SIP_TRANSPORT);
            sdkServer.startInsecure(RPC_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Web sdkServer
        port(HTTP_PORT);
        staticFiles.location("/public");
        staticFiles.expireTime(300L);


        // set up routes
        get("/", (req, res) -> {
            Map<String, Object> data = new HashMap<>();
            return hbs.render(new ModelAndView(data, "index.hbs"));
        });
    }

}
