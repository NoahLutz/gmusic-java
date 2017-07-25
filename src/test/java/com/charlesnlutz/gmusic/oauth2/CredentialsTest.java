package com.charlesnlutz.gmusic.oauth2;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void checkCredentialsAreParsed() throws IOException {
        credentials = new Credentials(jsonWithDate);

        //Check to make sure that fields get parsed properly
        assertEquals("access_token", credentials.getAccessToken());
        assertEquals("refresh_token", credentials.getRefreshToken());
        assertEquals("date", credentials.getDateGenerated());
    }

    @Test
    public void checkDateInserted() throws IOException {
        //Generate time
        long dateGenerated = Instant.now().getEpochSecond();

        //Pass in the manually generated time
        credentials = new Credentials(jsonWithoutDate, dateGenerated);

        //check that date was inserted correctly
        assertEquals(String.valueOf(dateGenerated), credentials.getDateGenerated());
    }
}
