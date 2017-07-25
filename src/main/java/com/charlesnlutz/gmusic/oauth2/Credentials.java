package com.charlesnlutz.gmusic.oauth2;

import com.charlesnlutz.gmusic.GMusicClient;
import com.charlesnlutz.gmusic.utils.HttpUtils;
import com.charlesnlutz.gmusic.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nlutz on 7/9/17.
 */
public class Credentials {

    private ObjectNode content;
    private static Logger log = Utils.getLogger();

    /**
     * Constructor for Credentials
     * Contains information like auth_token, refresh_token and expiration info
     * @param json initial JSON from google authentication/JSON from file
     * @param dateGenerated date (in epoch) to insert into the provided json
     * @throws IOException malformed JSON
     */
    public Credentials(String json, long dateGenerated) throws IOException{
        //Parse JSON
        content = ((ObjectNode) Utils.mapper.readTree(json));

        //Check to see if the date_generated field is included
        if(!content.has("date_generated")) {
            //If not,  then add it with the current epoch time
            content.put("date_generated", dateGenerated);
        }
    }

    /**
     * Constructor for Credentials
     * Contains information like auth_token, refresh_token and expiration info
     * This constructor calls {@link #Credentials(String, long)} and passes in the current time in epoch
     * using <code>Instant.now().getEpochSecond()</code>
     * @param json initial JSON from google authentication/JSON from file
     * @throws IOException malformed JSON
     */
    public Credentials(String json) throws IOException {
        this(json, Instant.now().getEpochSecond());
    }

    /**
     * Loads credentials from file from <code>creds_location</code> specified in the config file
     * @return Credentials object or null if file is not found
     * @throws IOException is not readable or contains malformed JSON
     */
    public static Credentials fromFile(String file) throws IOException {
        File tokenFile = new File(file);
        return new Credentials(new String(Files.readAllBytes(tokenFile.toPath())));
    }

    /**
     * Checks access_token to see if it is expired
     * @return true if expired, false if not
     */
    public boolean isExpired() {
        return Instant.now().getEpochSecond() - content.get("date_generated").asLong() >= content.get("expires_in").asLong();
    }

    /**
     * Returns the current access token
     * @return current access token
     */
    public String getAccessToken() {
        return content.get("access_token").asText();
    }

    /**
     * Returns the long-lived refresh token
     * @return refresh token
     */
    public String getRefreshToken() {
        return content.get("refresh_token").asText();
    }

    /**
     * Gets the date the tokens were generated
     * @return the date in epoch
     */
    public String getDateGenerated() {
        return content.get("date_generated").asText();
    }

    /**
     * Writes the Credential object to file in JSON format
     *
     * @param file file to save credentials to
     * @throws IOException if the write fails
     */
    public void save(String file) throws IOException {

        File credFile = new File(file);
        //check to see if file exists
        if(!credFile.exists()) {
            //If not, check if parent dir exists
            if(!credFile.getParentFile().exists()) {
                //If not, make them
                if(!credFile.getParentFile().mkdirs()) {
                    //If it fails, throw IOException
                    throw new IOException("Failed to make directory " + credFile.getParentFile().getAbsolutePath());
                }
            }
            //Try to create the file
            if(!credFile.createNewFile()){
                //If it fails, throw IOException
                throw new IOException("Failed to create file " + credFile.getAbsolutePath());
            }
        }
        //Write out the JSON
        Utils.mapper.writeValue(credFile, content);
    }

    /**
     * Updates the access token of this object
     * Note: this only updates the current Credentials object. To save to file, call {@link #save(String)}
    */
    public boolean refreshToken(String clientID, String clientSecret){
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", clientID);
        params.put("client_secret", clientSecret);
        params.put("refresh_token", getRefreshToken());

        //Send refresh request to google
        HttpResponse response = null;
        try {
            response = HttpUtils.post(GMusicClient.GOOGLE_TOKEN_URL, params);
        } catch (IOException e) {
            log.error("Failed to send request for refresh token", e);
            return false;
        }

        String body = null;
        try {
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            log.error("Failed to parse HTTP response");
            return false;
        }

        //Check status code
        if(response.getStatusLine().getStatusCode() != 200) {
            //if its anything other than 200 it probably failed, so log it
            log.warn("Failed to refresh token. Received status code " + response.getStatusLine().getStatusCode());
            log.warn("Body: " + body);
            return false;
        }

        //Parse the json
        JsonNode newCreds = null;
        try {
            newCreds = Utils.mapper.readTree(body);
        } catch (IOException e) {
            log.error("Failed to parse JSON", e);
            return false;
        }

        //Update this object content
        content.put("access_token", newCreds.get("access_token").asText());
        content.put("expires_in", newCreds.get("expires_in").asText());

        return true;
    }
}
