package com.africastalking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jay on 7/26/17.
 */
public class AccountServiceTest {

    AccountService account;

    @Before
    public void setUp() throws Exception {
        account = new AccountService();
    }

    @After
    public void tearDown() throws Exception {
        if (account != null)
            account = null;
    }

    @Test
    public void getUser() throws Exception {
        assertNotNull("GetUser: Response is null", account.getUser());
        assertNotEquals("GetUser: Balance is an empty string", "", account.getUser().getBalance());
    }

}