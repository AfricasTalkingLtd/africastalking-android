package com.africastalking;

import com.africastalking.models.Business;
import com.africastalking.models.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jay on 7/26/17.
 */
public class PaymentsServiceTest {

    private PaymentsService payment;

    @Before
    public void setUp() throws Exception {
        AfricasTalking.CALLSERVICE = CallService.PAYMENT;
        payment = new PaymentsService("testuser", Format.JSON, Currency.KES);
    }

    @After
    public void tearDown() throws Exception {
        payment = null;
    }

    @Test
    public void checkout() throws Exception {
        assertNotNull("Response response is null", payment.checkout("","", 0.00f, payment.currency));
        assertEquals("Status not correct", "PendingConfirmation", payment.checkout("","", 0.00f, payment.currency).getStatus());
    }

    @Test
    public void payConsumer() throws Exception {
        assertNotNull("Pay Consummer response is null", payment.payConsumer("", new Consumer("+254792424735", payment.currency, 0.00f)));
        assertEquals("Total value is not accurate", "KES 100", payment.payConsumer("", new Consumer("+254792424735", payment.currency, 0.00f)).getTotalValue());
    }

    @Test
    public void payBusiness() throws Exception {
        assertNotNull("Pay Business response is null", payment.payBusiness("", new Business("","", Business.TransferType.TRANSFER, payment.currency, 0.00f)));
        assertEquals("Status not correct", "Queued", payment.payBusiness("", new Business("","", Business.TransferType.TRANSFER, payment.currency, 0.00f)).getStatus());
    }

}