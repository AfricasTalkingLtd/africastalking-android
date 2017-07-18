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
public class SMSServiceTest  {

    SMSService sms;


    @Before
    public void setup() throws IOException {
        sms = new SMSService("testuser", Format.JSON, Currency.KES);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.join(any(CharSequence.class),any(List.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                CharSequence delimiter = (CharSequence) invocation.getArguments()[0];
                List<String> inputList = (List<String>) invocation.getArguments()[1];

                String output = "";
                for (CharSequence cs: inputList) {
                    output += "," + cs;
                }
                return output.substring(1, output.length());
            }
        });
        PowerMockito.mockStatic(Resources.class);
        PowerMockito.when(Resources.getSystem().getAssets().open("sendResponse.json")).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return this.getClass().getClassLoader().getResourceAsStream("rs/sendResponse.json");
            }
        });
    }

    @Test
    public void testFormatRecipients() throws Exception {
        String actual = new SMSService().formatRecipients(new String[]{"john","jean","juan"});
        String expected = "john,jean,juan";
        assertEquals("Failed formatRecipients",expected,actual);
    }

    @Test
    public void testFormatEmptyRecipients() throws Exception {
        String actual = new SMSService().formatRecipients(null);
        String expected = null;
        assertEquals("Failed formatRecipients",expected,actual);
    }

    @Test
    public void testFormatOneRecipients() throws Exception {
        String actual = new SMSService().formatRecipients(new String[]{"john"});
        String expected = "john";
        assertEquals("Failed formatRecipients",expected,actual);
    }

    @Test
    public void testSyncTest() throws Exception {
//        assertTrue("sms.send returns type Response<List<Recipient>>", sms.send("jay", "john,jean", new String[]{"Jay","joseph"}, "Hey there").execute());
    }

    @Test
    public void testSend() throws Exception {
        SendMessageResponse actual = sms.send ("message","from",new String[]{"to"});
        assertNull("Response is null", actual);
    }

}