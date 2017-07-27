package com.charlesnlutz.gmusic.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nlutz on 7/25/17.
 */
public interface Call {

    public HttpResponse get(String url) throws IOException;
    public HttpResponse post(String url, Map<String,String> formParams) throws IOException;
    public HttpResponse execute(HttpRequestBase request) throws IOException;

}
