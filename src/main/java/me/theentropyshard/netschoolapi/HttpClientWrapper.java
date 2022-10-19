/*      NetSchoolAPI. A simple API client for NetSchool by irTech
 *      Copyright (C) 2022 TheEntropyShard
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientWrapper implements Closeable {
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
        //this.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
        this.addHeader(new BasicHeader("Referer", this.baseUrl));
    }

    public CloseableHttpResponse get(String relativeUrl) throws IOException {
        return this.get(relativeUrl, "application/x-www-form-urlencoded; charset=UTF-8");
    }

    public CloseableHttpResponse get(String relativeUrl, String contentType) throws IOException {
        HttpGet httpGet = new HttpGet(this.baseUrl + relativeUrl);
        httpGet.setHeader("Content-Type", contentType);
        return this.httpClient.execute(httpGet, this.clientContext);
    }

    public CloseableHttpResponse post(String relativeUrl, HttpEntity content) throws IOException {
        return this.post(relativeUrl, "application/x-www-form-urlencoded; charset=UTF-8", content);
    }

    public CloseableHttpResponse post(String relativeUrl, String contentType, HttpEntity content) throws IOException {
        String resultUrl = this.baseUrl + relativeUrl;
        if(relativeUrl.startsWith("http")) resultUrl = relativeUrl;
        HttpPost httpPost = new HttpPost(resultUrl);
        httpPost.setHeader("Content-Type", contentType);
        httpPost.setEntity(content);
        return this.httpClient.execute(httpPost, this.clientContext);
    }

    public void addHeader(Header header) {
        this.globalHeaders.add(header);
    }

    @Override
    public void close() throws IOException {
        this.httpClient.close();
        this.cookieStore.clear();
    }
}
