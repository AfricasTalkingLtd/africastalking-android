package com.africastalking.interceptors;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jay on 7/31/17.
 */

public class AirtimeMockInterceptor implements Interceptor {
    private static final MediaType MEDIA_JSON = MediaType.parse("application/json");
    private Context context;
    @Override
    public Response intercept(Chain chain) throws IOException {
        String json = "{\n" +
                "\"numSent\": " + 792424735 + ",\n" +
                "\"totalAmount\": \"KES XXX\",\n" +
                "\"totalDiscount\" : \"KES YYY\",\n" +
                "\"responses\" : [\n" +
                "{\n" +
                "\"errorMessage\":\"None\",\n" +
                "\"phoneNumber\":\"+254711XXXYYY\",\n" +
                "\"amount\":\"KES 1000\",\n" +
                "\"discount\":\"40.0000\",\n" +
                "\"status\":\"sent\",\n" +
                "\"requestId\":\"ATQid_4fbec14b3c6b976d398957f9f8a65b3d\"\n" +
                "},\n" +
                "{\n" +
                "\"errorMessage\":\"None\",\n" +
                "\"phoneNumber\":\"+254733YYYZZZ\",\n" +
                "\"amount\":\"KES 2000\",\n" +
                "\"discount\":\"80.0000\",\n" +
                "\"status\":\"sent\",\n" +
                "\"requestId\":\"ATQid_7gbec14b5c7b876h375848fy8a56c2b\"\n" +
                "}\n" +
                "],\n" +
                "\"errorMessage\":\"None\"\n" +
                "}";

        okhttp3.Response response = new okhttp3.Response.Builder()
                .body(ResponseBody.create(MEDIA_JSON, json))
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .build();
        return response;
    }
}
