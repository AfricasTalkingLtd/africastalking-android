# Africa's Talking

[ ![Download](https://api.bintray.com/packages/africastalking/android/com.africastalking/images/download.svg) ](https://bintray.com/africastalking/android/com.africastalking/_latestVersion)

This SDK simplifies the integration of Africa's Talking APIs into your Android apps. For better security,
the SDK is split into two components: A **server** module that stores API keys, SIP credentials and other secrets.
And a **client** module that runs in your app. This client module gets secrets from the server component (via RPC), and uses them to interact with the various APIs.

For instance, to send an SMS, the client with (**will**) request a token from the server; The server will use it's API key to request a token from Africa's Talking on behalf of the client. It will then forward the token to the client which will use it to request the SMS API to send a text. All in a split second!


### Usage

Your server application could be something like this:

```java
/* On The Server (Java, Node.js, PHP, C/C++, C# and all languages supported by gRPC.) */

import com.africastalking.*;

public class SomeJavaApplication {

    public static void main(String[] args) {
    
        // Initialize the SDK
        AfricasTalking.initialize(USERNAME, API_KEY);
        
        // Initialize the server
        Server server = new Server();
        
        // Add SIP credentials (Voice Only)
        server.addSipCredentials(SIP_USERNAME, SIP_PASSWORD, SIP_HOST);
        
        // Start the server
        server.start();
    }
}
```

And your Android app:

```java
/* On The Client (Android) */
public class SomeActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(args);
        setContentView(R.layout.some_activity);
        
        // Init SDK
        AfricasTalking.initialize(SERVER_HOSTNAME);
       
        // Get Service
        AirtimeService airtime = AfricasTalking.getAirtimeService();

        // Use Service
        airtime.send("+25467675655", "KES", 100, new Callback<AirtimeResponses>() {
          @Override
          void onSuccess(AirtimeResponses responses) {
            //...
          }

          @Override
          void onError(Throwable throwable) {
            //...
          }
        });
    }
}
```

See the [example](./example) for complete sample apps (Android, Web Java+Node)

### Download

#### Server

**Node**

```shell
npm install --save africastalking
```

**Java**

```groovy
repositories {
  maven {
    url  "http://dl.bintray.com/africastalking/java"
  }
}
dependencies{
  compile 'com.africastalking:server:VERSION'
}
```

Or Maven (from `http://dl.bintray.com/africastalking/java`)

```xml
<dependency>
  <groupId>com.africastalking</groupId>
  <artifactId>server</artifactId>
  <version>VERSION</version>
</dependency>
```


#### Client (Android)
```groovy

android {

    // ...
    
    defaultConfig {
        
        // ...
        
        ndk {
            abiFilters "armeabi", "x86"
        }
    }
}

repositories {
  maven {
    url  "http://dl.bintray.com/africastalking/android"
  }
}
dependencies{
  compile 'com.africastalking:client:VERSION'
  // or
  compile 'com.africastalking:client-ui:VERSION' // with checkout UI for payment
}
```



## Initialization
The following static methods are available on the `AfricasTalking` class to initialize the library:

- `initialize(String host, int port, bool disableTLS)`: Initialize the library.
- `getXXXService()`: Get an instance to a given `XXX` service. e.g. `AfricasTalking.getSmsService()`, `AfricasTalking.getPaymentService()`, etc.


## Services

All methods are synchronous (i.e. will block current thread) but provide asynchronous variants that take a `Callback<T>` as the last argument.

### `Account`
- `getUser()`: Get user information.

### `Airtime`

- `send(String phone, String currencyCode, float amount)`: Send airtime to a phone number.

- `send(HashMap<String, String> recipients)`: Send airtime to a bunch of phone numbers. The keys in the `recipients` map are phone numbers while the values are airtime amounts ( e.g. `KES 678`).

For more information about status notification, please read [http://docs.africastalking.com/airtime/callback](http://docs.africastalking.com/airtime/callback)

### `Token`

- `createCheckoutToken(String phoneNumber)`: Create a checkout token.

### `SMS`

- `send(String message, String[] recipients)`: Send a message

- `sendBulk(String message, String[] recipients)`: Send a message in bulk

- `sendPremium(String message, String keyword, String linkId, String[] recipients)`: Send a premium SMS

- `fetchMessage()`: Fetch your messages

- `fetchSubscription(String shortCode, String keyword)`: Fetch your premium subscription data

- `createSubscription(String shortCode, String keyword, String phoneNumber)`: Create a premium subscription

For more information on: 

- How to receive SMS: [http://docs.africastalking.com/sms/callback](http://docs.africastalking.com/sms/callback)

- How to get notified of delivery reports: [http://docs.africastalking.com/sms/deliveryreports](http://docs.africastalking.com/sms/deliveryreports)

- How to listen for subscription notifications: [http://docs.africastalking.com/subscriptions/callback](http://docs.africastalking.com/subscriptions/callback)

### `Payment`

- `checkout(CheckoutRequest request)`: Initiate checkout(mobile, card or bank).

- `validateCheckout(CheckoutValidateRequest request)`: Validate checkout (card or bank).

- `mobileB2C(String productName, List<Consumer> recipients)`: Send money to consumer. 

- `mobileB2B(String productName, Business recipient)`: Send money to business.

- `bankTransfer(String productName, List<Bank> recipients)`: Move money form payment wallet to bank account.

### Voice

Unlike other services, voice is initialized as follows:

```java
AfricasTalking.initializeVoiceService(Context cxt, RegistrationListener listener, new Callback<VoiceService>() {
    @Override
    public void onSuccess(VoiceService service) {
      // keep a reference to the 'service'
    }

    @Override
    public void onFailure(Throwable throwable) {
      // something blew up
    }
});
```


- `registerCallListener(CallListener listener)`:

- `makeCall(String phoneNumber)`:

- `picCall()`:

- `holdCall()`:

- `resumeCall()`:

- `endCall()`:

- `sendDtmf(char character)`:

- `startAudio()`:

- `toggleMute()`:

- `setSpeakerMode(Context context, boolean loudSpeaker)`:

- `isCallInProgress()`:

- `getCallInfo()`

- `queueStatus(String phoneNumbers)`:

- `mediaUpload(String url)`:



## Requirements

On Android, This SDK requires **API 16+**. Your app will also need the following permissions:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    
    <!-- The following are required if you want use the voice service -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    
    <!-- ... -->
    
</manifest>
```

For more info, please visit [https://www.africastalking.com](https://www.africastalking.com)
