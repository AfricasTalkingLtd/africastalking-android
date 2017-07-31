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

public class AccountMockInterceptor implements Interceptor {
    private static final MediaType MEDIA_JSON = MediaType.parse("application/json");
    private Context context;
    @Override
    public Response intercept(Chain chain) throws IOException {
        String json = "{\"UserData\" :\n" +
                "{\n" +
                "\"balance\" : \" 2000\"\n" +
                "}\n" +
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
