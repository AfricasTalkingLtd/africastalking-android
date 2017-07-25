package com.africastalking;

import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by jay on 7/13/17.
 */
@RunWith(PowerMockRunner.class)
public class AfricasTalkingTest {

    AfricasTalking africasTalking;
    MockContext context;

    @Before
    public void setUp() throws Exception {
        africasTalking = new AfricasTalking();
        AfricasTalking.initialize("sandbox.africastalking.com");
        context = new MockContext();
    }

    @Test
    public void initialize() throws Exception {
        assertNotNull("ManagedChannel is null", AfricasTalking.getChannel());
        assertNotNull("Token is null", AfricasTalking.getToken());
    }

    @Test
    public void getChannel() throws Exception {
        assertNotNull("ManagedChannel is null", AfricasTalking.getChannel());
    }

    @Test
    public void getSmsService() throws Exception {
        assertNotNull("SMSService is null", AfricasTalking.getSmsService());
    }

    @Test
    public void getAirtimeService() throws Exception {
        assertNotNull("AirtimeService is null", AfricasTalking.getAirtimeService());
    }

    @Test
    public void getPaymentsService() throws Exception {
        assertNotNull("PaymentService", AfricasTalking.getPaymentsService());
    }

    @Test
    public void getAccount() throws Exception {
        assertNotNull("Account is null", AfricasTalking.getAccount());
    }

    @Test
    public void getToken() throws Exception {
        assertNotNull("Token is null", AfricasTalking.getToken());
    }

}