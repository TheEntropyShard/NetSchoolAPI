package me.theentropyshard.netschoolapi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientWrapper {
    private final HttpClientContext clientContext;
    private final CookieStore cookieStore;
    private final CloseableHttpClient httpClient;
    private final List<Header> globalHeaders;

    private final String baseUrl;

    public HttpClientWrapper(String baseUrl) {
        this.clientContext = HttpClientContext.create();
        this.cookieStore = new BasicCookieStore();
        this.clientContext.setAttribute(HttpClientContext.COOKIE_STORE, this.cookieStore);
        this.httpClient = HttpClients.custom()
                .setDefaultCookieStore(this.cookieStore)
                .setDefaultHeaders(this.globalHeaders = new ArrayList<>())
                .build();

        this.baseUrl = baseUrl;

        this.addHeader(new BasicHeader("Accept", "application/json, text/javascript, */*; q=0.01"));
        this.addHeader(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
        this.addHeader(new BasicHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7"));
        this.addHeader(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        this.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
        this.addHeader(new BasicHeader("Referer", this.baseUrl));
    }

    public CloseableHttpResponse get(String relativeUrl) throws IOException {
        String resultUrl = this.baseUrl + relativeUrl;
        HttpGet httpGet = new HttpGet(resultUrl);
        return this.httpClient.execute(httpGet, this.clientContext);
    }

    public CloseableHttpResponse post(String relativeUrl, HttpEntity content) throws IOException {
        String resultUrl = this.baseUrl + relativeUrl;
        HttpPost httpPost = new HttpPost(resultUrl);
        httpPost.setEntity(content);
        return this.httpClient.execute(httpPost, this.clientContext);
    }

    public void addHeader(Header header) {
        this.globalHeaders.add(header);
    }
}
