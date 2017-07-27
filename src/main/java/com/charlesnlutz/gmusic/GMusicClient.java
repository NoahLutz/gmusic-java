package com.charlesnlutz.gmusic;

import com.charlesnlutz.gmusic.http.Call;
import com.charlesnlutz.gmusic.http.GMusicCall;
import com.charlesnlutz.gmusic.oauth2.Credentials;
import com.charlesnlutz.gmusic.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nlutz on 6/26/17.
 */
public class GMusicClient {

    private static Logger log = Utils.getLogger();

    private String clientID;
    private String clientSecret;

    private Call callProvider;

    public final static String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    public final static String GOOGLE_TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
    public final static String GOOGLE_REDIRECT_URL = "http://localhost:4567/callback";
    public final static String GMUSIC_SCOPE = "https://www.googleapis.com/auth/musicmanager";

    private Credentials credentials;

    public GMusicClient(String clientID, String clientSecret, Call callProvider) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.callProvider = callProvider;
    }

    public GMusicClient(String clientID, String clientSecret) {
        this(clientID, clientSecret, new GMusicCall());
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
    public URI oauthStepOneRedirectURL(String redirectURI) {
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


    /**
     * Performs the second step in oauth process.
     * @param code code received from redirect URL in {@link #oauthStepOneRedirectURL(String)}
     * @param redirectURI one of the redirect URI's registered in google console
     * @return the credentials returned from the authorization process
     * @throws IOException thrown because of issue with sending request or parsing response
     */
    public Credentials oauthStepTwo(String code, String redirectURI) throws IOException{
        //assemble parameters
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientID);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectURI);
        params.put("grant_type", "authorization_code");

        //Send the request to google
        HttpResponse response = callProvider.post(GOOGLE_TOKEN_URL, params);

        //Get the response
        String body = EntityUtils.toString(response.getEntity());

        //Check the status
        if(response.getStatusLine().getStatusCode() != 200) {
            log.error("Failed to authenticate. Received status code " + response.getStatusLine().getStatusCode());
            log.debug("Body: " + body);
            return null;
        }

        //Return the credentials
        return new Credentials(body);
    }

    /**
     * Does a check to see if the credentials are valid/can be refreshed
     * @param credentials credentials to check
     * @return true if there is a valid access token or a refresh has been successful
     */
    protected boolean verifyCredentials(Credentials credentials) {
        //Check if credentials are expired
        if(credentials.isExpired()) {
            //If so try to refresh
            return credentials.refreshToken(clientID, clientSecret, callProvider);
        }
        return true;
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


}
