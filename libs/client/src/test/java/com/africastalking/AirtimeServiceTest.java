package com.africastalking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by jay on 7/26/17.
 */
public class AirtimeServiceTest {

    AirtimeService airtime;

    @Before
    public void setUp() throws Exception {
        AfricasTalking.CALLSERVICE = CallService.AIRTIME;
        airtime = new AirtimeService("testuser", Format.JSON, Currency.KES);

    }

    @After
    public void tearDown() throws Exception {
        airtime = null;
    }

    @Test
    public void send() throws Exception {
        assertNotNull("AirtimeService: Response null", airtime.send("+254792424735", 0.00f));
        assertEquals("AirtimeService: No responses", false, airtime.send("+254792424735", 0.00f).getResponses().isEmpty());
        assertEquals("AirtimeService: Response is not successful" + airtime.send("+254792424735", 0.00f).getErrorMessage(), "sent", airtime.send("+254792424735", 0.00f).getResponses().get(0).getStatus());
    }

    @Test
    public void _makeRecipientsJSON() throws Exception {
        HashMap recipients1 = new HashMap<String, Float>();
        recipients1.put("+254792424735", 0.00f);
        assertNotNull("makeRecipientsJson response not null", airtime._makeRecipientsJSON(recipients1));
        assertEquals("Json response not correct", "[{\"phoneNumber\":\"+254792424735\", \"amount\": \"KES 0.0\"}]", airtime._makeRecipientsJSON(recipients1));
    }

}