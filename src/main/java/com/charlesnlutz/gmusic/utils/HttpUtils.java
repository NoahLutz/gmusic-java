package com.charlesnlutz.gmusic.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nlutz on 6/27/17.
 */
public class HttpUtils {

    private static Logger log = Utils.getLogger();

    /**
     * Executes a GET with the  given URL
     * @param url URL to execute GET with
     * @return the response from server
     * @throws IOException if the GET fails
     */
    public static HttpResponse get(String url) throws IOException {
        return execute(new HttpGet(url));
    }

    /**
     * Executes a POST on the given URL with the given parameters
     * @param url
     * @param formParameters
     * @return
     * @throws IOException
     */
    public static HttpResponse post(String url, Map<String,String> formParameters) throws IOException {
        HttpPost post = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<>();

        for(String key : formParameters.keySet()) {
            nvps.add(new BasicNameValuePair(key, formParameters.get(key)));
        }
        post.setEntity(new UrlEncodedFormEntity(nvps));

        return execute(post);
    }


    private static HttpResponse execute(HttpRequestBase request) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        return httpClient.execute(request);
    }
}
