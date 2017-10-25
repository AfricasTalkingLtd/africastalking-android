package com.africastalking.ui;

import com.africastalking.AfricasTalking;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceBlockingStub;
import com.africastalking.proto.SdkServerServiceOuterClass.ClientTokenRequest;
import com.africastalking.proto.SdkServerServiceOuterClass.ClientTokenResponse;
import com.africastalking.utils.Callback;
import com.africastalking.utils.Logger;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.stub.MetadataUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A given service offered by AT API
 */
public abstract class Service {

    static final String PRODUCTION_DOMAIN = "africastalking.com";
    static final String SANDBOX_DOMAIN = "sandbox.africastalking.com";


    private static final Metadata.Key<String> CLIENT_ID_HEADER_KEY =
            Metadata.Key.of("X-Client-Id", Metadata.ASCII_STRING_MARSHALLER);

    public static String HOST;
    public static int PORT = 35897;
    public static boolean DISABLE_TLS = false;

    public static Boolean LOGGING = false;
    public static Logger LOGGER = new Logger() {
        @Override
        public void log(String message, Object... args) {
            System.out.println(String.format(message, args));
        }
    };


    Retrofit.Builder retrofitBuilder;
    static boolean isSandbox = false;
    static String username = null;
    static private ClientTokenResponse token;

    Service() throws IOException {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                if (token == null || token.getExpiration() < System.currentTimeMillis()) {
                    token = fetchToken(HOST, PORT);
                    if (token == null) {
                        throw new IOException("Failed to fetch token");
                    }
                }

                Request original = chain.request();
                HttpUrl url = original.url();
                if (AfricasTalking.hostOverride != null) {
                    url = url.newBuilder()
                        .host(AfricasTalking.hostOverride)
                        .build();
                }
                Request request = original.newBuilder()
                        .url(url)
                        .addHeader("authToken", token.getToken())
                        .addHeader("Accept", "application/json")
                        .build();

                return chain.proceed(request);
            }
        });

        if (LOGGING) {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LOGGER.log(message);
                }
            });
            logger.setLevel(HttpLoggingInterceptor.Level.BASIC);
            httpClient.addInterceptor(logger);
        }

        retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())) // switched from ScalarsConverterFactory
                .client(httpClient.build());

        token = fetchToken(HOST, PORT);
        isSandbox = token.getEnvironment().toLowerCase().contentEquals("sandbox");
        username = token.getUsername();
        
        initService();
    }

    SdkServerServiceBlockingStub addClientIdentification(SdkServerServiceBlockingStub stub) {
        // Optional client id header
        String clientId = AfricasTalking.getClientId();
        if (clientId != null) {
            Metadata headers = new Metadata();
            headers.put(CLIENT_ID_HEADER_KEY, clientId);
            stub = MetadataUtils.attachHeaders(stub, headers);
        }
        return stub;
    }

    static ManagedChannel getChannel(String host, int port) {
        OkHttpChannelBuilder channelBuilder;

        if (DISABLE_TLS) {
            channelBuilder = OkHttpChannelBuilder
                    .forAddress(host, port)
                    .usePlaintext(true);
        } else {
            channelBuilder = OkHttpChannelBuilder
                    .forAddress(host, port);
        }

        return channelBuilder.build();
    }

    protected ClientTokenResponse fetchToken(String host, int port) throws IOException {

        if (LOGGING) { LOGGER.log("Fetching token..."); }

        ManagedChannel channel = getChannel(host, port);
        SdkServerServiceBlockingStub stub = addClientIdentification(SdkServerServiceGrpc.newBlockingStub(channel));
        ClientTokenRequest req = ClientTokenRequest.newBuilder().build();
        ClientTokenResponse token = stub.getToken(req);

        if (LOGGING) {
            LOGGER.log(
                "\n\nToken: %s\nUsername: %s\nEnvironment: %s\nExpires: %s\n\n",
                token.getToken(),
                token.getUsername(),
                token.getEnvironment(),
                String.valueOf(token.getExpiration()));
        }
        return token;
    }


    public static <T extends Service> T newInstance(String service) throws IOException {

        if (service.contentEquals("account")) {
            if (AccountService.sInstance == null) {
                AccountService.sInstance = new AccountService();
            }
            return (T) AccountService.sInstance;
        }


        if (service.contentEquals("airtime")) {
            if (AirtimeService.sInstance == null) {
                AirtimeService.sInstance = new AirtimeService();
            }
            return (T) AirtimeService.sInstance;
        }


        if (service.contentEquals("payment")) {
            if (PaymentService.sInstance == null) {
                PaymentService.sInstance = new PaymentService();
            }
            return (T) PaymentService.sInstance;
        }


        if (service.contentEquals("sms")) {
            if (SmsService.sInstance == null) {
                SmsService.sInstance = new SmsService();
            }
            return (T) SmsService.sInstance;
        }

        if (service.contentEquals("voice")) {
            if (VoiceService.sInstance == null) {
                VoiceService.sInstance = new VoiceService();
            }
            return (T) VoiceService.sInstance;
        }

        if (service.contentEquals("token")) {
            if (TokenService.sInstance == null) {
                TokenService.sInstance = new TokenService();
            }
            return (T) TokenService.sInstance;
        }

        throw new IOException("Invalid service");
    }

    /**
     *
     * @param cb
     * @param <T>
     * @return
     */
    protected <T> retrofit2.Callback<T> makeCallback(final Callback<T> cb) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.isSuccessful()) {
                    cb.onSuccess(response.body());
                } else {
                    cb.onFailure(new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                cb.onFailure(t);
            }
        };
    }

    /**
     * Get an instance of a service.
     * @param <T>
     * @return
     */
    protected abstract <T extends Service> T getInstance() throws IOException;

    /**
     * Check if a service is initialized
     * @return boolean true if yes, false otherwise
     */
    protected abstract boolean isInitialized();

    /**
     * Initializes a service
     */
    protected abstract void initService();

    /**
     * Destroys a service
     */
    protected abstract void destroyService();
}
