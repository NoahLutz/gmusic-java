package com.charlesnlutz.gmusic.oauth2;

import com.charlesnlutz.gmusic.http.MockCall;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by nlutz on 7/20/17.
 */
public class CredentialsTest {

    private Credentials credentials;
    private final String jsonWithDate = "{\"access_token\":\"access_token\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"refresh_token\":\"refresh_token\",\"date_generated\":\"date\"}";
    private final String jsonWithoutDate = "{\"access_token\":\"access_token\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"refresh_token\":\"refresh_token\"}";

    @After
    public void after() {
        credentials = null;
    }

    /**
     * Checks to make sure credentials actually get parsed correctly
     * @throws IOException if there are problems parsing credentials
     */
    @Test
    public void checkCredentialsAreParsed() throws IOException {
        credentials = new Credentials(jsonWithDate);

        //Check to make sure that fields get parsed properly
        assertEquals("access_token", credentials.getAccessToken());
        assertEquals("refresh_token", credentials.getRefreshToken());
        assertEquals("date", credentials.getDateGenerated());
    }

    /**
     * Checks to make sure the date is inserted into the JSON if there is non supplied
     * This simulates what happens when tokens are exchanged for the first time with google
     * @throws IOException if there are problems parsing credentials
     */
    @Test
    public void checkDateInserted() throws IOException {
        //Generate time
        long dateGenerated = Instant.now().getEpochSecond();

        //Pass in the manually generated time
        credentials = new Credentials(jsonWithoutDate, dateGenerated);

        //check that date was inserted correctly
        assertEquals(String.valueOf(dateGenerated), credentials.getDateGenerated());
    }

    /**
     * Checks to make sure that tokens and dates are refreshed given a valid response from google
     * Gives a mock response from google with "new" access code
     * @throws IOException
     */
    @Test
    public void checkRefreshToken() throws IOException {
        long dateGenerated = Instant.now().getEpochSecond() - (3600*2);
        credentials = new Credentials(jsonWithoutDate, dateGenerated);

        //Create mock response
        MockCall callProvider = new MockCall();
        HttpResponse postResponse =  new BasicHttpResponse(new ProtocolVersion("TEST", 0, 0), 200, "TEST");
        postResponse.setEntity(new StringEntity("{ \"access_token\":\"new_access_token\", \"expires_in\":3920, \"token_type\":\"Bearer\" }"));
        callProvider.setPostResponse(postResponse);

        //Make assertions
        assertTrue(credentials.refreshToken(null, null, callProvider));
        assertEquals("new_access_token", credentials.getAccessToken());
        assertNotEquals(String.valueOf(dateGenerated), credentials.getDateGenerated());
    }
}
