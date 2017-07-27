package com.charlesnlutz.gmusic.http;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nlutz on 7/25/17.
 */
public class GMusicCall implements Call{


    @Override
    public HttpResponse get(String url) throws IOException {
        return execute(new HttpGet(url));
    }

    @Override
    public HttpResponse post(String url, Map<String, String> formParams) throws IOException {
        HttpPost post  = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<>();

        for(String key: formParams.keySet()) {
            params.add(new BasicNameValuePair(key, formParams.get(key)));
        }
        post.setEntity(new UrlEncodedFormEntity(params));
        return execute(post);
    }

    @Override
    public HttpResponse execute(HttpRequestBase request) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        return httpClient.execute(request);
    }
}
