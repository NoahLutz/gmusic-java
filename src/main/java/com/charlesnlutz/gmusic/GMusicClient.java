package com.charlesnlutz.gmusic;

import com.charlesnlutz.gmusic.oauth2.Credentials;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by nlutz on 6/26/17.
 */
public class GMusicClient {

    private static Logger log = Logger.getRootLogger();

    private String clientID;
    private String clientSecret;

    public final static String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    public final static String GOOGLE_TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
    public final static String GOOGLE_REDIRECT_URL = "http://localhost:4567/callback";
    public final static String GMUSIC_SCOPE = "https://www.googleapis.com/auth/musicmanager";

    private Credentials credentials;

    public GMusicClient(String clientID, String clientSecret) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    /**
     * Actually just verifies that the access code is still valid and if not, then it attempts a refresh
     * @param credentials credentials from file
     * @return true if <code>credentials</code> contains a valid access token or if it refreshes successfully
     */
    public boolean login(Credentials credentials) {
        if(!verifyCredentials(credentials)) {
            log.error("Failed to get a valid access token");
            return false;
        }

        //TODO: perform some sort of test to verify the access token is valid
        this.credentials = credentials;

        return true;
    }

    /**
     * Builds a redirect URL for a webserver authentication flow
     * TODO: make this more versatile (not just webserver flow)
     * @param redirectURI redirect URI for local application
     * @return constructed URL
     */
    public URI buildRedirectURL(String redirectURI) {
        URI redirectURL = null;
        try {
            redirectURL = new URIBuilder(GOOGLE_AUTH_URL)
                    .addParameter("client_id", clientID)
                    .addParameter("response_type", "code")
                    .addParameter("scope", GMUSIC_SCOPE)
                    .addParameter("redirect_uri", redirectURI)
                    //TODO: make this some sort of hash?
                    .addParameter("state", "google_login")
                    .addParameter("access_type", "offline")
                    //for debugging purposes
                    .addParameter("prompt", "consent")
                    .build();
        } catch (URISyntaxException e) {
            log.error("Failed to build URI", e);
            return null;
        }
        return redirectURL;
    }

    public List<String> getAllSongs() {
        return null;
    }

    public List<String> getAllPlaylists() {
        return null;
    }

    public List<String> search(String keyword) {
        return null;
    }

    /**
     * Does a check to see if the credentials are valid/can be refreshed
     * @param credentials credentials to check
     * @return true if there is a valid access token or a refresh has been successful
     */
    private boolean verifyCredentials(Credentials credentials) {
        //Check if credentials are expired
        if(credentials.isExpired()) {
            //If so try to refresh
            return credentials.refreshToken(clientID, clientSecret);
        }
        return true;
    }
}
