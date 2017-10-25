package com.africastalking;

import com.africastalking.ui.SmsService;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jay on 7/13/17.
 */
public class SmsServiceTest {

    private SmsService sms;

    @Before
    public void setup() throws IOException {
        AfricasTalking.initialize("localhost");
        sms = AfricasTalking.getSmsService();
    }

    @Test
    public void send() throws Exception {
        assertNotNull("SMS not sent", sms.send("Test sms", "", new String[]{"+250784476268"}));
        assertEquals("SMS not sent", "Success", sms.send("Test sms", "", new String[]{"+250784476268"}).getSMSMessageData().getRecipients().get(0).getStatus());
    }

    @Test
    public void sendBulk() throws Exception {
        assertNotNull("Bulk SMS not sent", sms.sendBulk("Test sms", "", new String[]{"+250784476268"}));
    }

    @Test
    public void sendPremium() throws Exception {
        assertNotNull("Premium SMS not sent", sms.sendPremium("Test", "", "", new String[]{""}));
    }

    @Test
    public void fetchMessage() throws Exception {
        assertNotNull("Fetch message failed", sms.fetchMessage());
    }

    @Test
    public void fetchSubscription() throws Exception {
        assertNotNull("Fetch Subscription failed", sms.fetchSubscription("","",""));
    }

    @Test
    public void createSubscription() throws Exception {
        assertNotNull("Create subscription", sms.createSubscription("","",""));
    }

}