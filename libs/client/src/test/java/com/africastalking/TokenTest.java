package com.africastalking;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jay on 7/26/17.
 */
public class TokenTest {

    private Token token;

    @Before
    public void setUp() throws Exception {
        token = new Token();

    }

    @Test
    public void getTokenString() throws Exception {
        assertNotNull("Token response null", token.getTokenString());
        assertNotEquals("Token response is empty string", "", token.getTokenString());
    }

    @Test
    public void getExpiration() throws Exception {
        assertNotNull("Expiration response null", token.getExpiration());
        assertNotEquals("Expiration response equals to 0", 0L, token.getExpiration());
    }

}