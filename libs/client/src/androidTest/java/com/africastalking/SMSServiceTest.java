package com.africastalking;

import android.provider.Telephony;
import android.text.TextUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by jay on 7/13/17.
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(TextUtils.class)
public class SMSServiceTest {

    SMSService sms;

    @org.junit.Test
    public void getInstance() throws Exception {

    }

    @Before
    public void setup(){
        sms = new SMSService();
    }

    @Test
    public void formatRecipients() throws Exception {
        String actual = sms.formatRecipients(new String[]{"john","jean","juan"});
        String expected = "john,jean,juan";
        assertEquals("Failed formatRecipients",expected,actual);
    }

}