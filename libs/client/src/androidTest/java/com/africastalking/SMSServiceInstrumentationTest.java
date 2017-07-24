package com.africastalking;

import android.provider.Telephony;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.text.TextUtils;

import com.africastalking.models.Recipient;
import com.africastalking.models.SendMessageResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.mock.MockRetrofit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

/**
 * Created by jay on 7/13/17.
 */
@RunWith(AndroidJUnit4.class)
//@PrepareForTest(TextUtils.class)
public class SMSServiceInstrumentationTest extends InstrumentationTestCase {
    @Test
    public void send1() throws Exception {
        sms = new SMSService("testuser", Format.JSON, Currency.KES);
        SendMessageResponse actual = sms.send("message","from",new String[]{"to"});

        String recipientOneNumber = "+254711XXXYYY";
        String recipientOneCost = "KES YY";
        String recipientOneStatus = "Success";
        String recipientOneMessageId = "ATSid_1";

        assertNotNull("Response is null", actual);
        assertEquals("Number of recipients not correct", 2, actual.getSMSMessageData().getRecipients().size());
        assertEquals("First recipient number incorrect", recipientOneNumber, actual.getSMSMessageData().getRecipients().get(0).getNumber());
        assertEquals("First recipient cost incorrect", recipientOneCost, actual.getSMSMessageData().getRecipients().get(0).getCost());
        assertEquals("First recipient number incorrect", recipientOneStatus, actual.getSMSMessageData().getRecipients().get(0).getStatus());
        assertEquals("First recipient number incorrect", recipientOneMessageId, actual.getSMSMessageData().getRecipients().get(0).getMessageId());
    }

    @Test
    public void testSendBulk() throws Exception {
        sms = new SMSService("testuser", Format.JSON, Currency.KES);
        SendMessageResponse actual = sms.sendBulk("message", "from", new String[]{"to","recipients"});

        String recipientOneNumber = "+254711XXXYYY";
        String recipientOneCost = "KES YY";
        String recipientOneStatus = "Success";
        String recipientOneMessageId = "ATSid_1";

        assertNotNull("Response is null", actual);
        assertEquals("Number of recipients not correct", 2, actual.getSMSMessageData().getRecipients().size());
        assertEquals("First recipient number incorrect", recipientOneNumber, actual.getSMSMessageData().getRecipients().get(0).getNumber());
        assertEquals("First recipient cost incorrect", recipientOneCost, actual.getSMSMessageData().getRecipients().get(0).getCost());
        assertEquals("First recipient number incorrect", recipientOneStatus, actual.getSMSMessageData().getRecipients().get(0).getStatus());
        assertEquals("First recipient number incorrect", recipientOneMessageId, actual.getSMSMessageData().getRecipients().get(0).getMessageId());
    }

    @Test
    public void testSendPremium() throws Exception {
        sms = new SMSService("testuser", Format.JSON, Currency.KES);
        SendMessageResponse actual = sms.sendPremium("message", "from", "keyword", new String[]{"to","recipient"});

        String recipientOneNumber = "+254711XXXYYY";
        String recipientOneCost = "KES YY";
        String recipientOneStatus = "Success";
        String recipientOneMessageId = "ATSid_1";

        assertNotNull("Response is null", actual);
        assertEquals("Number of recipients not correct", 2, actual.getSMSMessageData().getRecipients().size());
        assertEquals("First recipient number incorrect", recipientOneNumber, actual.getSMSMessageData().getRecipients().get(0).getNumber());
        assertEquals("First recipient cost incorrect", recipientOneCost, actual.getSMSMessageData().getRecipients().get(0).getCost());
        assertEquals("First recipient number incorrect", recipientOneStatus, actual.getSMSMessageData().getRecipients().get(0).getStatus());
        assertEquals("First recipient number incorrect", recipientOneMessageId, actual.getSMSMessageData().getRecipients().get(0).getMessageId());
    }



    SMSService sms;
    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @org.junit.Test
    public void getInstance() throws Exception {

    }

    @Before
    public void setup() throws Exception {
        super.setUp();
        sms = new SMSService("testuser", Format.JSON, Currency.KES);

    }

    @Test
    public void formatRecipients() throws Exception {
        String actual = sms.formatRecipients(new String[]{"john","jean","juan"});
        String expected = "john,jean,juan";
        assertEquals("Failed formatRecipients",expected,actual);
    }

    @Test
    public void send() throws Exception {
        SendMessageResponse actual = sms.send("message","from",new String[]{"to"});
        assertNull("Response is null", actual);
    }

}