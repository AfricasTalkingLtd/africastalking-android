package com.africastalking;

import com.africastalking.models.payment.Bank;
import com.africastalking.models.payment.BankTransferResponse;
import com.africastalking.models.payment.Business;
import com.africastalking.models.payment.Consumer;
import com.africastalking.models.payment.checkout.BankCheckoutRequest.BankAccount;
import com.africastalking.models.payment.checkout.BankCode;
import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
import com.africastalking.services.PaymentService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jay on 7/26/17.
 */
public class PaymentServiceTest {

    private PaymentService payment;

    @Before
    public void setUp() throws Exception {
        AfricasTalking.initialize( "localhost");
        payment = AfricasTalking.getPaymentService();
    }

    @After
    public void tearDown() throws Exception {
        payment = null;
    }

    @Test
    public void checkout() throws Exception {
        assertNotNull("Response response is null", payment.checkout(new MobileCheckoutRequest("", "KES 234", "043343", "")));
        assertEquals("Status not correct", "PendingConfirmation", payment.checkout(new MobileCheckoutRequest("", "KES 234", "043343", "")).status);
    }

    @Test
    public void mobileB2C() throws Exception {
        assertNotNull("Pay Consumer response is null", payment.mobileB2C("TestProduct", Arrays.asList(new Consumer("Salama","+254792424735", "KES 0.00", ""))));
        assertEquals("Total value is not accurate", "KES 100.000", payment.mobileB2C("TestProduct", Arrays.asList(new Consumer("Salama","+254792424735", "KES 100.00", ""))).totalValue);
    }

    @Test
    public void mobileB2B() throws Exception {
        assertNotNull("Pay Business response is null", payment.mobileB2B("TestProduct", new Business("","", Business.TRANSFER_TYPE_PAYBILL, Business.PROVIDER_ATHENA,"KES 110.00")));
        assertEquals("Status not correct", "Queued", payment.mobileB2B("TestProduct", new Business("","", Business.TRANSFER_TYPE_PAYBILL, Business.PROVIDER_ATHENA,"KES 110.00")).status);
    }

    @Test
    public void bankTransfer() throws Exception {
        List<Bank> recipients = Arrays.asList(new Bank(new BankAccount("Bob", "2323", BankCode.Zenith_NG), "NGN 5673", "Some narration", new HashMap<String, String>()));
        BankTransferResponse resp = payment.bankTransfer("Ikoyi Store", recipients);
        assertEquals("InvalidRequest", resp.entries.get(0).status);
    }
}