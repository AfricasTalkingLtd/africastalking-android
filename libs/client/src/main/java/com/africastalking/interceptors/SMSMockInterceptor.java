package com.africastalking.interceptors;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jay on 7/17/17.
 */

public class SMSMockInterceptor implements Interceptor {
    private static final MediaType MEDIA_JSON = MediaType.parse("application/json");
    private Context context;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

//        String path = request.url().getPath();

//        InputStream stream = Resources.getSystem().getAssets().open("sendResponse.json");
//        InputStream stream = getClass().getResourceAsStream("assets/sendResponse");

        String json = "{\"SMSMessageData\":\n" +
                "{\"Recipients\":\n" +
                "[\n" +
                "  {\n" +
                "    \"number\"    : \"+254711XXXYYY\",\n" +
                "    \"cost\"      : \"KES YY\",\n" +
                "    \"status\"    : \"Success\",\n" +
                "    \"messageId\" : \"ATSid_1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"number\"    : \"+254733YYYZZZ\",\n" +
                "    \"cost\"      : \"KES XX\",\n" +
                "    \"status\"    : \"Success\",\n" +
                "    \"messageId\" : \"ATSid_n\"\n" +
                "  }\n" +
                "]\n" +
                "}\n" +
                "}";

        Response response = new Response.Builder()
                .body(ResponseBody.create(MEDIA_JSON, json))
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .build();
        return response;
    }

    private String parseStream(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            builder.append(line);
        }
        in.close();
        return builder.toString();
    }
}
