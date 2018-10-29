# Africa's Talking

[ ![Download](https://api.bintray.com/packages/africastalking/android/com.africastalking/images/download.svg) ](https://bintray.com/africastalking/android/com.africastalking/_latestVersion)

This SDK simplifies the integration of Africa's Talking APIs into your Android apps. For better security,
the SDK is split into two components: A **server** module that stores API keys, SIP credentials and other secrets.
And a **client** module that runs in your app. This client module gets secrets from the server component (via RPC), and uses them to interact with the various APIs.

For instance, to send an SMS, the client will request a token from the server; The server will use it's API key to request a token from Africa's Talking on behalf of the client. It will then forward the token to the client which will use it to request, say, the SMS API to send a text. All in a split second!


## Usage

**NOTE** The code samples seen here are for running a simple server and doing simple API requests. See the advanced section for the list of all methods you can use within the SDK to access the various services.

### 1. Server
You can code your server using android studio, eclipse or any other IDE you may deem fit.

**NOTE** You are not restricted to use Java to code your server. You can instead use Node.js, PHP, C/C++, C#, and all languages supported by gRPC. Visit these links to get the Africa's Talking Node.js SDK to see a template of how your server should look like in Node.js.

https://github.com/AfricasTalkingLtd/africastalking-node.js


**If you are using Java for your server...**

Whichever IDE you choose to work with, the following need to be done.

#### a. Download dependencies

If you are using android studio (with gradle dependencies), add the following code snippet in your build.gradle file (Module: your_server's_module_name)

```groovy
repositories {
  maven {
    url  "http://dl.bintray.com/africastalking/java"
  }
}
dependencies{
  implementation 'com.africastalking:server:VERSION'
}
```
If you are using Eclipse IDE, or you use Maven dependencies instead, add this code snipppet in your Maven dependencies.

Maven (from `http://dl.bintray.com/africastalking/java`)

```xml
<dependency>
  <groupId>com.africastalking</groupId>
  <artifactId>server</artifactId>
  <version>VERSION</version>
</dependency>
```

#### b. Add server side code

First, import the following library classes in the class you've created to run the server.

```Java

    import com.africastalking.AfricasTalking;
    import com.africastalking.Server;
    
    import java.io.IOException;
    
```

Now put this Java code in the class you've created to run your server.

Your server application could be something like this:

```java
/* On The Server (Java, Node.js, PHP, C/C++, C# and all languages supported by gRPC.) */

import com.africastalking.*;

public class SomeJavaApplication {

    //Save your credentials in static final variables
    private static final String USERNAME = "your_username"; //Use "sandbox" if you are testing
    
    //You can generate this for testing in your Africa's Talking account.
    //go to https://account.africastalking.com/apps/sandbox/settings/key
    private static final String API_KEY = "your_API_KEY"; 
                                                  
    //Optional
    private static final int port = "your_server's_port_number";
                                                  
    public static void main(String[] args) {
    
        // Initialize the SDK
        AfricasTalking.initialize(USERNAME, API_KEY);
        
        // Initialize the server
        Server server = new Server();
        
        // Add SIP credentials (Voice Only)
        server.addSipCredentials(SIP_USERNAME, SIP_PASSWORD, SIP_HOST);
        
        // Start the server
        try{
                server.startInsecure();
                
                //Use this if you have provided a port
                //server.startInsecure(port);
                
                //Please see the Advanced section for a list of other functions you can use to start your server
        } catch (IOException e){
        
                //Do something if server doesn't start
        }
    }
}
```

You may need to add the following code snippet just after **server.startInsecure()**, to ensure your server stays alive until you terminate it (Only add this if you run the above code and the server stops prematurely). For a server set to serve a production application, this may not be necessary.

```
//A loop to help us keep our server online until we terminate it
            System.out.println("Press ENTER to exit");
            System.in.read();
```

This is enough to run your simple server. Now to the android app.

### 2. Android App

In your android app, you need to add the following dependencies, and structure your code as shown below.

#### a. Add dependencies

Go to the build.gradle file (Module: app [or the name of the module your app's code is in]) and add the following code snippet.

```groovy

android {

    // ...
    
    defaultConfig {
        
        // ...
        
        //ADD THIS
        ndk {
            abiFilters "armeabi", "x86"
        }
    }
}

//ADD THIS
repositories {
  maven {
    url  "http://dl.bintray.com/africastalking/android"
  }
}
dependencies{

   //ADD THIS
  implementation 'com.africastalking:client:VERSION'
  // or
  implementation 'com.africastalking:client-ui:VERSION' // with checkout UI for payment
}
```

#### b. Add Africa's Talking SDK Code

First, import the following library classes.

```Java 

    import com.africastalking.AfricasTalking;
    
    import java.io.IOException;
```

Adding the following code snippet in, preferrably, MainActivity (or whatever activity your app launches to).

```java
/* On The Client (Android) */
public class SomeActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle savedInstanceState);
        setContentView(R.layout.some_activity);
        
        // Initialize SDK
        //Use this method below if you had not specified a port for your server above
        try{
        
            AfricasTalking.initialize(SERVER_HOSTNAME);

            //Use this if you specified a port for your server
            //AfricasTalking.initialize(SERVER_HOSTNAME, port, true);

            //The SERVER_HOSTNAME can be the ip address of your server on the network or a domain name that can be resolved
          
        }catch (IOException e){
        
            //Do something
            
        }
        
        //
    }
}
```

The code snippet above initializes the SDK. Now, you can use the various Africa's Talking API services as follows:

#### b.1 Airtime Service

Add this code snippet in whatever method or activity you would want to invoke before using the Airtime service.

Import the following library classes

```Java

       import com.africastalking.services.AirtimeService;
       import com.africastalking.AfricasTalking;
       import com.africastalking.models.airtime.AirtimeResponse;
       
       import java.io.IOException;
       
```

Write the java code. It may look like this.

```Java
// Get Service

        try{
        
            AirtimeService airtime = AfricasTalking.getAirtimeService();

            // Use Service
            //Will send +254... 100KES airtime
            airtime.send("+254...", "KES", 100, new Callback<AirtimeResponses>() {
              @Override
              void onSuccess(AirtimeResponses responses) {
                //...
              }

              @Override
              void onError(Throwable throwable) {
                //...
              }
            });
        
        } catch (IOException e){
        
            //Do something
        
        }
```

#### b.2 SMS Service

Add this code snippet in whatever method or activity you would want to invoke before using the SMS service.

Import the following library classes

```Java

       import com.africastalking.services.SmsService;
       import com.africastalking.AfricasTalking;
       import com.africastalking.models.sms.Recipient
       
       import java.io.IOException;
       
```

Write the Java code. It may look like this.

```java

//Get service

        try{
        
            SmsService sms = AfricasTalking.getSmsService();
            
            //Will send "Hello" to number "+254...". Note that the String is an array, so you can add more numbers to do a bulk
            //sms
            sms.send("Hello", new String[]{"+254...."}, new Callback<List<Recipient>>() {
                        @Override
                        public void onSuccess(List<Recipient> data) {
                            
                            //Do something
                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                            //Do something
                        }
                    });
                    
        } catch (IOException e) {
        
            //Do something
            
        }
        
```

#### b.3 Payment Service

Add this code snippet in whatever method or activity you would want to invoke before using the Payment service.

Import the following library classes

```Java

       import com.africastalking.services.PaymentService;
       import com.africastalking.AfricasTalking;
       import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
       import com.africastalking.models.payment.checkout.CheckoutResponse;
       
       import java.io.IOException;
       
```

Write the Java code. It may look like this.

```Java

//Get service

        try{
        
            //get the service
            PaymentService payment = AfricasTalking.getPaymentService();
            
            //Create the checkout request. You can create a product for testing by visiting
            // https://account.africastalking.com/apps/sandbox/payments/products
            MobileCheckoutRequest request = new MobileCheckoutRequest("your_product_name", "KES amount",phoneNumber);
            
            //Checkout
            request.checkout(checkoutRequest, new Callback<CheckoutResponse>() {
                        @Override
                        public void onSuccess(CheckoutResponse data) {
                            
                            //Do something
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            
                            //Do something
                        }
                    });
        
        } catch (IOException e) {
        
            //Do something
            
        }
            
```

#### b.4 Voice Service

Please find this code provided in the Advanced section

**NOTE**
See the [example](./example) for complete sample apps (Android, Web Java+Node)

The code snippets above will allow you to write 


## Advanced



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
