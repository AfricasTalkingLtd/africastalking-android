# Africa's Talking

[ ![Download](https://api.bintray.com/packages/africastalking/android/com.africastalking/images/download.svg) ](https://bintray.com/africastalking/android/com.africastalking/_latestVersion)

This SDK simplifies the integration of Africa's Talking APIs into your Android apps. For better security,
the SDK is split into two components: A **server** module that stores API keys, SIP credentials and other secrets.
And a **client** module that runs in your app. This client module gets secrets from the server component (via RPC), and uses them to interact with the various APIs.

For instance, to send an SMS, the client will request a token from the server; The server will use it's API key to request a token from Africa's Talking on behalf of the client. It will then forward the token to the client which will use it to request, say, the SMS API to send a text. All in a split second!

If you plan on integrating the SDK to your android app, please avoid cloning this whole repo. Instead, just follow as instructed in this ReadMe.



## Install 

### 1. Server

**Download dependencies**

If you are using android studio (or gradle dependencies), add the following code snippet in your build.gradle file

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

### 2. Android app

**download dependencies**

Go to the build.gradle file for your app module and add the following code snippet.

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

## Initialization

### 1. Server

Import the following library classes.

```Java

    import com.africastalking.AfricasTalking;
    import com.africastalking.Server;
    
    import java.io.IOException;
    
```

Now put this Java code.

Your server application could be something like this:

```java
/* On The Server (Java) */

import com.africastalking.*;

public class SomeJavaApplication {

    //Save your credentials. Use "sandbox" if you are testing
    private static final String USERNAME = "your_username";
    
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
                
        } catch (IOException e){
        
                //Do something if server doesn't start
        }
    }
}
```

You can code your server using android studio, eclipse or any other IDE you may deem fit.

**NOTE** You are not restricted to use Java to code your server. You can instead use Node.js, PHP, C/C++, C#, and all languages supported by gRPC. Visit these links to get the Africa's Talking Node.js SDK to see a template of how your server should look like in Node.js.

https://github.com/AfricasTalkingLtd/africastalking-node.js

### 2. Android App

Import the following library classes.

```Java 

    import com.africastalking.AfricasTalking;
    
    import java.io.IOException;
```

Add the following code snippet in, preferrably, MainActivity (or whatever activity your app launches to).

```java
/* On The Client (Android) */
public class SomeActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
         
         //....
        
        // Initialize SDK
        try{
        
            AfricasTalking.initialize(SERVER_HOSTNAME);

            //Use this instead if you specified a port for your server
            //AfricasTalking.initialize(SERVER_HOSTNAME, port, true);

            //The SERVER_HOSTNAME can be the ip address of your server on the network or a domain name that can be resolved
          
        }catch (IOException e){
        
            //Do something
            
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


## Usage

### Android App

#### 1. Airtime Service

Add this code snippet in whatever method or activity you would want to invoke for using the Airtime service.

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
            //Will send ksh 100 airtime to +254...
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

#### 2. SMS Service

Add this code snippet in whatever method or activity you would want to invoke for using the SMS service.

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
            
            //Will send "Hello" to "+254...". Note that the String is an array, so you can add more numbers to do a bulk
            //sms
            sms.send("Hello", new String[]{"+254...."}, false, new Callback<List<Recipient>>() {
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

#### 3. Payment Service

Add this code snippet in whatever method or activity you would want to invoke for using the Payment service.

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
            MobileCheckoutRequest checkoutRequest = new MobileCheckoutRequest("your_product_name", "KES 500",phoneNumber);
            
            //Checkout
            payment.checkout(checkoutRequest, new Callback<CheckoutResponse>() {
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

#### 4. Voice Service

Please find this code provided in the Advanced section

**NOTE**
See the [example](./example) for complete sample apps (Android, Web Java+Node)

The code snippets above will allow for simple use of the Android SDK to use Africa's Talking API services. For a full list and description of the methods, classes and functionalities of the android SDK, see the Advanced section below.


## Advanced

This section holds the set of all functions that can be used to perform various tasks in the Android SDK.

### 1. Server

#### a. Initialization
The following static method is available on the `AfricasTalking` class to initialize the library, server side.

- `initialize(String username, String apiKey)`: Initialize the library, passing in the username and apiKey.

#### b. Start Server
The following methods are available to start your server.

- `startInsecure()`: Start the server insecurely.

- `startInsecure(int port)`: Start the server insecurely, providing a port for the server.

- `start(File certChainFile, File privateKeyFile)`: Start the server securely, providing the certificate and private key.

- `start(File certChainFile, File privateKeyFile, int port)`: Same as method shown before, but pass in the port to start the server.

#### c. Add Sip Credentials (Voice Only)

- `addSipCredentials(String username, String password, String host)`: Add sip credentials. The actual values for the arguments will be provided by Africa's Talking on request of a SIP phone.

- `addSipCredentials(String username, String password, String host, int port, String transport)`: Add sip credentials, this
time passing in a port, and transport value. The value for transport will be provided by Africa's Talking on request of a SIP phone.

#### d. Stop server

- `stop()`: Stop the server.

### 2. Android

#### a. Initialization
The following static methods are available on the `AfricasTalking` class to initialize the library:

- `initialize(String host)`: Initialize the library, only passing in the server hostname. 

- `initialize(String host, int port)`: Initialize the library, passing in the server hostname and port only.

- `initialize(String host, int port, bool disableTLS)`: Initialize the library, passing the server hostname, port, and disableTLS boolean option.

#### b. Services

##### Get a service

- `getXXXService()`: Get an instance to a given `XXX` service. e.g. `AfricasTalking.getSmsService()`, `AfricasTalking.getPaymentService()`, etc.

**NOTE**
All methods for all services are synchronous (i.e. will block current thread) but provide asynchronous variants that take a `Callback<T>` as the last argument.

Synchronous variants return a service reponse, asynchronous variants return void.

If you use the synchronous variants, then make sure you run them in a separate thread from the main UI thread to prevent your app from crashing, and ensuring that you conform to Android's programming standards (since we are making network calls).

##### b.1 Application Service (For getting user account information
- `getUser()`: Get user information.

- `getUser(Callback <AccountResponse> callback)`: asynchronous variant of getUser(). 

##### b.2 Airtime Service

- `send(String phone, String currency, float amount)`: Send airtime to a single phone number.

- `send(HashMap<String, String> recipients)`: Send airtime to a bunch of phone numbers. The keys in the `recipients` map are phone numbers while the values are airtime amounts ( e.g. `KES 678`).

- `send(String phone, String currency, float amount, Callback<AirtimeResponse> callback)`: Send airtime to a single phone number, with a callback.

- `send(HashMap<String, String> recipients, Callback<AirtimeResponse> callback)`: Send airtime to a bunch of phone numbers, with a callback.

For more information about status notification, please read [http://docs.africastalking.com/airtime/callback](http://docs.africastalking.com/airtime/callback) 

###### b.3 Token Service

- `createCheckoutToken(String phoneNumber)`: Create a checkout token.

- `createCheckoutToken(String phoneNumber, Callback<CheckoutTokenResponse> callback)`: Create a checkout token with a callback response.

###### b.4 SMS Service

###### Send an sms to one or more numbers

**NOTE:** Setting enqueue to false means that the message will be sent and you will receive the response immediately. Setting it to true means that the response will be received later after the message was sent. 

- `send(String message, String[] recipients, boolean enqueue)`: Send a message

- `send(String message, String[] recipients, boolean enqueue, Callback<List<Recipient>> callback)`: Send a message, with a callback

- `send(String message, String from, String[] recipients, boolean enqueue)`: Send a message, passing in the number the message is from.

- `send(String message, String from, String[] recipients, boolean enqueue, Callback<List<Recipient>> callback)`: Send a message, passing in the number the message is from, with a callback.

**Note** "from" is your sms shortcode or alphanumeric name created from your Africa's Talking account. You can create one for testing from: https://account.africastalking.com/apps/sandbox/sms/shortcodes/create

###### Send premium sms

- `sendPremium(String message, String keyword, String linkId, String[] recipients)`: Send a premium SMS

- `sendPremium(String message, String from, String keyword, String linkId, String[] recipients)`: Send a premium SMS, passing in the number the message is from

- `sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients)`: Send a premium SMS, passing in the retry duration

- `sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients)`: Send a premium SMS, passing in the retry duration and the number the message is from

- `sendPremium(String message, String keyword, String linkId, String[] recipients, Callback<List<Recipient>> callback)`: Send a premium SMS, with a callback

- `sendPremium(String message, String from, String keyword, String linkId, String[] recipients, Callback<List<Recipient>> callback)`: Send a premium SMS, passing in the number the message is from and a callback

- `sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients, Callback<List<Recipient>> callback)`: Send a premium SMS, passing in the retry duration and callback.

- `sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients, Callback<List<Recipient>> callback)`: Send a premium SMS,passing in the number the message is from, retry duration, and a callback

###### Create premium sms subscription

- `createSubscription(String shortCode, String keyword, String phoneNumber, String checkoutToken)`: Create a premium subscription

- `createSubscription(String shortCode, String keyword, String phoneNumber, String checkoutToken, Callback<SubscriptionResponse> callback)`: create a premium sms subscription, with a callback.

For more information on: 

- How to receive SMS: [http://docs.africastalking.com/sms/callback](http://docs.africastalking.com/sms/callback)

- How to get notified of delivery reports: [http://docs.africastalking.com/sms/deliveryreports](http://docs.africastalking.com/sms/deliveryreports)

- How to listen for subscription notifications: [http://docs.africastalking.com/subscriptions/callback](http://docs.africastalking.com/subscriptions/callback)

###### Fetch messages

- `fetchMessage()`: Fetch your messages

- `fetchMessage(Callback<List<Message>> callback)`: Fetch your messages, with a callback.

- `fetchMessage(String lastReceivedId)`: Fetch the last received message

- `fetchMessage(String lastReceivedId, Callback<List<Message>> callback)`: Fetch the last received message with a callback

- `fetchSubscription(String shortCode, String keyword)`: Fetch your premium subscription data

- `fetchSubscription(String shortCode, String keyword, String lastReceivedId)`: Fetch your premium subscription data, for last received message

- `fetchSubscription(String shortCode, String keyword, Callback<List<Message>> callback)`: Fetch your premium subscription data, with a callback

- `fetchSubscription(String shortCode, String keyword, String lastReceivedId)`: Fetch your premium subscription data, for last received message, with a callback

###### b.5 Payment

- `checkout(CheckoutRequest request)`: Initiate checkout(mobile, card or bank).

- `checkout(CheckoutRequest request, Callback<CheckoutResponse> callback)`: Initiate checkout(mobile, card or bank), with a callback.

- `validateCheckout(CheckoutValidateRequest request)`: Validate checkout (card or bank).

- `validateCheckout(CheckoutValidateRequest request, Callback<CheckoutValidationResponse> callback)`: Validate checkout (card or bank), with a callback.

- `mobileB2C(String productName, List<Consumer> recipients)`: Send money to consumer. 

- `mobileB2C(String productName, List<Consumer> recipients, Callback<B2CResponse> callback)`: Send money to consumer, with a callback. 

- `mobileB2B(String productName, Business recipient)`: Send money to business.

- `mobileB2B(String productName, Business recipient, Callback<B2BResponse> callback)`: Send money to business, with a callback.

- `bankTransfer(String productName, List<Bank> recipients)`: Move money from payment wallet to bank account.

- `bankTransfer(String productName, List<Bank> recipients, Callback<BankTransferReponse> callback)`: Move money from payment wallet to bank account, with a callback.

###### b.6 Voice

To use the voice service, you'll need to import the following libraries.

```java
  
  import com.africastalking.AfricasTalking;
  import com.africastalking.AfricasTalkingException; //handling exceptions
  import com.africastalking.utils.Callback;
  import com.africastalking.utils.Logger; //Used for logging

  import com.africastalking.services.VoiceService;
  import com.africastalking.utils.voice.CallInfo;
  import com.africastalking.utils.voice.CallListener;
  import com.africastalking.utils.voice.RegistrationListener;
  
```

Unlike other services, voice is initialized as follows:

```java
try{

  AfricasTalking.initializeVoiceService(Context cxt, RegistrationListener listener, new Callback<VoiceService>() {
      @Override
      public void onSuccess(VoiceService service) {
        // keep a reference to the 'service'
        mService = service;
      }

      @Override
      public void onFailure(Throwable throwable) {
        // something blew up
      }
  });
  
  } catch (Exception ex){
    //failed to initialize
  }
```

The following listeners are needed:

**i. Registration listener**

```java

    RegistrationListener listener = new RegistrationListener() {
        @Override
        public void onError(Throwable error) {
            
            //handle error in registering to the SIP server
            }

        @Override
        public void onStarting() {

            //registration starting
        }

        @Override
        public void onComplete() {

            //register call listener
            mService.registerCallListener(mCallListener);
            
            //register logger
            mService.registerLogger(mLogger);
            
            //mCallListener is an instance of CallListener. It's instantiated below
        }
    };

```

**ii. Call Listener**

```java

    private CallListener mCallListener = new CallListener() {
        @Override
        public void onCallBusy(CallInfo call) {
            
            //Do something
        }

        @Override
        public void onError(CallInfo call, final int errorCode, final String errorMessage) {

            //Do something based on error
        }

        @Override
        public void onRinging(final CallInfo callInfo) {
            
            //Do something
        }

        @Override
        public void onRingingBack(final CallInfo call) {
            
            //Do something
        }

        @Override
        public void onCallEstablished(CallInfo call) {
            
            //The call has been established. So, start audio
            mService.startAudio();
            mService.setSpeakerMode(VoiceActivity.this, false);

            // show in-call ui
            //Probably set up an activity or fragment to do this
        }

        @Override
        public void onCallEnded(CallInfo call) {
            
            //Handle the end of the call
        }

        @Override
        public void onIncomingCall(CallInfo callInfo) {
            
            //Do something. 
            //You can show in-call UI here,
        }
    };

  
```

You can optionally set up a logger as follows:

```Java

    private Logger logger = new Logger() {
        @Override
        public void log(String message, Object... args) {
            //Log the messages
            Timber.d(message, args);
        }
    };

```

The following methods may be of use in using the Voice Service in the SDK. They belong to the VoiceService class.

- `registerCallListener(CallListener listener)`: register the call listener

- `unregisterCallListener(CallListener listener)`: unregister the call listener

- `registerLogger(Logger logger)`: register the logger

- `unregisterLogger(Logger logger)`: unregister the logger

- `makeCall(String phoneNumber)`: make a phone call

- `pickCall()`: pick a call

- `holdCall()`: hold a call

- `resumeCall()`: resume a held call

- `endCall()`: end a call

- `sendDtmf(char character)`: send the dialpad character the user has pressed

- `startAudio()`: initiate the phone speaker to start audio

- `toggleMute()`: mute or unmute a call

- `setSpeakerMode(Context context, boolean loudSpeaker)`: toggle between using the loudspeaker and not

- `isCallInProgress()`: returns a boolean indicating whether a call is in progress or not

- `getCallInfo()`: get an instance of CallInfo

- `destroyService()`: destroy the voice service

You can get an instance of the voice service using the following static method of the Africa's Talking class

- `getVoiceService()`: get an instance of the voice service



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
