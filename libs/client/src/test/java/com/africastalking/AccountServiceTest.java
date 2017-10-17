package com.africastalking;

import com.africastalking.services.AccountService;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.*;

/**
 * Created by jay on 7/26/17.
 */
public class AccountServiceTest {
    private AccountService account;

    @BeforeClass
    public static void init() {
        // TODO: Start mock grpc server
    }

    @Before
    public void setUp() throws IOException {
        AfricasTalking.initialize("localhost");
        account = AfricasTalking.getAccountService();
    }

    @After
    public void tearDown() throws Exception {
        if (account != null)
            account = null;
    }

    @Test
    public void getUser() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{\"UserData\": {\"balance\": \"2000\" } }"));
        server.start();
        assertNotNull("GetUser: Response is null", account.getUser());
        assertEquals("GetUser: Balance is not 2000", "2000", account.getUser().getUserData().getBalance().trim());
    }

}