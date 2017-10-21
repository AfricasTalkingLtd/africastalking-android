package com.africastalking;

import com.africastalking.services.AirtimeService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jay on 7/26/17.
 */
public class AirtimeServiceTest {

    AirtimeService airtime;

    @Before
    public void setUp() throws Exception {
        AfricasTalking.initialize("localhost");
        airtime = AfricasTalking.getAirtimeService();

    }

    @After
    public void tearDown() throws Exception {
        airtime = null;
    }

    @Test
    public void send() throws Exception {
        assertNotNull("AirtimeService: Response null", airtime.send("+254792424735", "KES", 0));
        assertEquals("AirtimeService: No responses", false, airtime.send("+254792424735", "KES", 0).getResponses().isEmpty());
        assertEquals("AirtimeService: Response is not successful" + airtime.send("+254792424735", "KES", 0).getErrorMessage(), "sent", airtime.send("+254792424735", "KES", 0).getResponses().get(0).getStatus());
    }
}