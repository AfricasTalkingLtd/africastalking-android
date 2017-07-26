package com.africastalking;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.text.TextUtils;

import com.africastalking.models.Recipient;
import com.africastalking.models.SendMessageResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import retrofit2.Response;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

/**
 * Created by jay on 7/13/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class, Resources.class})
@PowerMockIgnore("javax.net.ssl.*")
public class SMSServiceTest {

    private SMSService sms;

    @Before
    public void setup() throws IOException {
        sms = new SMSService("testuser", Format.JSON, Currency.KES);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.join(any(CharSequence.class), any(List.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                CharSequence delimiter = (CharSequence) invocation.getArguments()[0];
                List<String> inputList = (List<String>) invocation.getArguments()[1];

                String output = "";
                for (CharSequence cs : inputList) {
                    output += "," + cs;
                }
                return output.substring(1, output.length());
            }
        });
    }

    @Test
    public void testFormatRecipients() throws Exception {
        String actual = new SMSService().formatRecipients(new String[]{"john", "jean", "juan"});
        String expected = "john,jean,juan";
        assertEquals("Failed formatRecipients", expected, actual);
        assertEquals("Failed formatRecipients", null, new SMSService().formatRecipients(null));
        assertEquals("Failed formatRecipients", "john", new SMSService().formatRecipients(new String[]{"john"}));
    }

    @Test
    public void testFormatOneRecipients() throws Exception {
        String actual = new SMSService().formatRecipients(new String[]{"john"});
        String expected = "john";
        assertEquals("Failed formatRecipients", expected, actual);
    }

    @Test
    public void destroyService() throws Exception {
        sms.destroyService();
        assertNull("SMSService not destroyed", sms);
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
        assertNotNull("Fetch message failed", "");
    }

    @Test
    public void fetchSubscription() throws Exception {
        assertNotNull("Fetch Subscription failed", "");
    }

    @Test
    public void createSubscription() throws Exception {
        assertNotNull("Create subscription", "");
    }

}