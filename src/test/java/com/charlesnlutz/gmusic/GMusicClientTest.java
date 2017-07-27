package com.charlesnlutz.gmusic;

import com.charlesnlutz.gmusic.http.MockCall;
import com.charlesnlutz.gmusic.oauth2.Credentials;
import com.charlesnlutz.gmusic.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import static org.junit.Assert.*;

/**
 * Created by nlutz on 7/24/17.
 */
public class GMusicClientTest {

    private GMusicClient client;
    private final String clientID = "clientID";
    private final String clientSecret = "clientSecret";
    private final String redirectURL = "redirect";
    private final String oauthStepTwoTestJSON = "{\"access_token\":\"access_token\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"refresh_token\":\"refresh_token\",\"date_generated\":1234567890}";

    private final String verifyCredsTestDefaultJSON = "{\"access_token\":\"access_token\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"refresh_token\":\"refresh_token\"}";
    private final String verifyCredsTestResponseJSON = "{ \"access_token\":\"new_access_token\", \"expires_in\":3920, \"token_type\":\"Bearer\" }";

    private MockCall callProvider;

    @Before
    public void setUp() throws IOException{
        callProvider = new MockCall();
        client = new GMusicClient(clientID, clientSecret, callProvider);
    }

    @After
    public void destroy() {
        client = null;
        callProvider = null;
    }

    @Test
    public void testOAuthStepOneRedirectURL() {
        String expected = "https://accounts.google.com/o/oauth2/v2/auth?client_id=clientID&response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fmusicmanager&access_type=offline&redirect_uri=redirect&state=google_login&prompt=consent";

        //Compare the two URI's
        assertTrue(Utils.compareURI(URI.create(expected), client.oauthStepOneRedirectURL(redirectURL)));
    }

    @Test
    public void testOAuthStepTwo() throws IOException {
        //Create the mock response
        HttpResponse postResponse = new BasicHttpResponse(new ProtocolVersion("TEST", 0, 0), 200, "TEST");
        postResponse.setEntity(new StringEntity(oauthStepTwoTestJSON));

        //Set the call providers mock response
        callProvider.setPostResponse(postResponse);


        //Perform mock oauth step 2
        Credentials credentials = client.oauthStepTwo(null, null);

        //Check null is not returned
        assertNotNull(credentials);

        //Check the info is what it should be
        assertEquals("access_token", credentials.getAccessToken());
        assertEquals("refresh_token", credentials.getRefreshToken());
        assertEquals("1234567890", credentials.getDateGenerated());
    }

    @Test
    public void testVerifyCredentialsExpired() throws IOException {
        //Create the mock response
        HttpResponse postResponse = new BasicHttpResponse(new ProtocolVersion("TEST", 0, 0), 200,"TEST");
        postResponse.setEntity(new StringEntity(verifyCredsTestResponseJSON));
        callProvider.setPostResponse(postResponse);

        //Create credentials with date generated that will cause it to try to refresh the token
        Credentials credentials = new Credentials(verifyCredsTestDefaultJSON, (Instant.now().getEpochSecond() - 3600*2));

        //Verify that credentials should be valid
        assertTrue(client.verifyCredentials(credentials));
    }

    @Test
    public void testVerifyCredentialsNotExpired() throws IOException{
        //Initialize credentials and have it insert current time for date_generated
        Credentials credentials = new Credentials(verifyCredsTestDefaultJSON);

        //Verify that credentials should be valid
        assertTrue(client.verifyCredentials(credentials));
    }


}
