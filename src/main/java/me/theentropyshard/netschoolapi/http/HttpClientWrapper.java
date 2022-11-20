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

package me.theentropyshard.netschoolapi.http;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientWrapper {
    private final OkHttpClient client;
    private final CookieJar cookieJar;
    private final List<Headers> globalHeaders;
    private final String baseUrl;

    public HttpClientWrapper(String baseUrl) {
        this.client = new OkHttpClient.Builder()
                .cookieJar(this.cookieJar = new SimpleCookieJar())
                .build();
        this.globalHeaders = new ArrayList<>();
        this.baseUrl = baseUrl;

        this.addHeader(Headers.of("Accept", "application/json, text/javascript, */*; q=0.01"));
        this.addHeader(Headers.of("Accept-Encoding", "gzip, deflate, br"));
        this.addHeader(Headers.of("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7"));
        this.addHeader(Headers.of("X-Requested-With", "XMLHttpRequest"));
        this.addHeader(Headers.of("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
        this.addHeader(Headers.of("Referer", this.baseUrl));
    }

    public Response get(String url) throws IOException {
        return this.get(url, Headers.of());
    }

    public Response get(String url, Headers headers) throws IOException {
        url = url.startsWith("http") ? url : this.baseUrl + url;

        Request.Builder builder = new Request.Builder().url(url).headers(headers);
        this.globalHeaders.forEach(builder::headers);
        Request request = builder.build();
        this.cookieJar.loadForRequest(request.url());
        Response response = this.client.newCall(request).execute();
        HttpUrl httpUrl = response.request().url();
        this.cookieJar.saveFromResponse(httpUrl, Cookie.parseAll(httpUrl, response.headers()));

        return response;
    }

    public Response post(String url, String data) throws IOException {
        return this.post(url, data, Headers.of(), ContentType.FORM_URLENCODED);
    }

    public Response post(String url, String data, ContentType contentType) throws IOException {
        return this.post(url, data, Headers.of(), contentType);
    }

    public Response post(String url, String data, Headers headers, ContentType contentType) throws IOException {
        url = url.startsWith("http") ? url : this.baseUrl + url;

        Request.Builder builder =  new Request.Builder().url(url).headers(headers)
                .post(RequestBody.create(data, contentType.getMediaType()));
        this.globalHeaders.forEach(builder::headers);
        Request request = builder.build();
        this.cookieJar.loadForRequest(request.url());
        Response response = this.client.newCall(request).execute();
        HttpUrl httpUrl = response.request().url();
        this.cookieJar.saveFromResponse(httpUrl, Cookie.parseAll(httpUrl, response.headers()));

        return response;
    }

    public void addHeader(Headers headers) {
        this.globalHeaders.add(headers);
    }

    public OkHttpClient getClient() {
        return this.client;
    }
}
