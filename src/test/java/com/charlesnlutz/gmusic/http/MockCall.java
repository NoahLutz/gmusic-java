package com.charlesnlutz.gmusic.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nlutz on 7/25/17.
 */
public class MockCall implements Call {

    private HttpResponse getResponse;
    private HttpResponse postResponse;
    private HttpResponse executeResponse;

    public void setGetResponse(HttpResponse response) {
        this.getResponse = response;
    }

    public void setPostResponse(HttpResponse response) {
        this.postResponse = response;
    }

    public void setExecuteResponse(HttpResponse response) {
        this.executeResponse = response;
    }


    @Override
    public HttpResponse get(String url) throws IOException {
        return getResponse;
    }

    @Override
    public HttpResponse post(String url, Map<String, String> formParams) throws IOException {
        return postResponse;
    }

    @Override
    public HttpResponse execute(HttpRequestBase request) throws IOException {
        return executeResponse;
    }
}
