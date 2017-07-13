package com.africastalking;

import android.text.TextUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

/**
 * Created by jay on 7/13/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class SMSServiceTest {

    SMSService sms;

    @Before
    public void setup(){
        sms = new SMSService();
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

}