package com.africastalking.interceptors;

import android.content.Context;

import com.africastalking.AfricasTalking;
import com.africastalking.CallService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jay on 7/31/17.
 */

public class PaymentMockInterceptor implements Interceptor {
    private static final MediaType MEDIA_JSON = MediaType.parse("application/json");
    private Context context;
    @Override
    public Response intercept(Chain chain) throws IOException {
        String checkoutjson = "{\n" +
                "\"status\": \"PendingConfirmation\",\n" +
                "\"description\": \"Waiting for user input\",\n" +
                "\"transactionId\": \"ATPid_SampleTxnId123\"\n" +
                "}";

        String b2cjson = "{\n" +
                "\"numQueued\": 1,\n" +
                "\"totalValue\": \"KES 100\",\n" +
                "\"totalTransactionFee\" : \"KES 1.50\",\n" +
                "\"entries\": [\n" +
                "{\n" +
                "\"phoneNumber\": \"+254711XXXYYY\",\n" +
                "\"status\": \"Queued\",\n" +
                "\"provider\": \"Mpesa\",\n" +
                "\"providerChannel\": \"525900\",\n" +
                "\"value\": \"KES 100\",\n" +
                "\"transactionId\": \"ATPid_SampleTxnId123\",\n" +
                "\"transactionFee\": \"KES 1.50\"\n" +
                "},\n" +
                "{\n" +
                "\"phoneNumber\": \"+254733YYYZZZ\",\n" +
                "\"status\": \"Failed\",\n" +
                "\"errorMessage\": \"Insufficient Credit\"\n" +
                "}\n" +
                "]\n" +
                "} ";

        String b2bjson = "{\n" +
                "\"status\": \"Queued\",\n" +
                "\"transactionId\": \"ATPid_SampleTxnId123\",\n" +
                "\"transactionFee\": \"KES XXX\",\n" +
                "\"providerChannel\": \"myPaymentProviderChannel\"\n" +
                "}";

        String json = "";

        if(AfricasTalking.CALLSERVICE == CallService.CHECKOUT)
            json = checkoutjson;
        if(AfricasTalking.CALLSERVICE == CallService.B2C)
            json = b2cjson;
        if(AfricasTalking.CALLSERVICE == CallService.B2B)
            json = b2bjson;

        okhttp3.Response response = new okhttp3.Response.Builder()
                .body(ResponseBody.create(MEDIA_JSON, json))
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .build();
        return response;
    }
}
