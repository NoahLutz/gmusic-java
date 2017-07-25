package com.charlesnlutz.gmusic;

import com.charlesnlutz.gmusic.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertTrue;

/**
 * Created by nlutz on 7/24/17.
 */
public class GMusicClientTest {

    private GMusicClient client;
    private final String clientID = "clientID";
    private final String clientSecret = "clientSecret";
    private final String redirectURL = "redirect";

    @Before
    public void setUp() {
        client = new GMusicClient(clientID, clientSecret);
    }

    @After
    public void destroy() {
        client = null;
    }

    @Test
    public void testRedirectURLGeneration() {
        String expected = "https://accounts.google.com/o/oauth2/v2/auth?client_id=clientID&response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fmusicmanager&access_type=offline&redirect_uri=redirect&state=google_login&prompt=consent";

        //Compare the two URI's
        assertTrue(Utils.compareURI(URI.create(expected), client.buildRedirectURL(redirectURL)));
    }
}
