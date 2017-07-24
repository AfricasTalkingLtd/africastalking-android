package com.africastalking.example;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);

    setContentView(R.layout.activity_main);



//    AfricasTalking.initialize(); //TODO define parameters
//    SmsService sms = AfricasTalking.getSmsService();
//    sms.send("","",""); //TODO define parameters
  }
}
