package com.example.triptrooperapp;



import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackendServiceClass {

    private String url;
    private JSONObject json;
    private final String ipAddress = "https://44.225.75.82:8081/"; // this if for ubc secure.
    private OkHttpClient client;
    private String headerKey;
    private String headerValue;
    BackendServiceClass(String apiEndpoint, JSONObject json){
        this.url = ipAddress+ apiEndpoint;
        this.json = json;
        this.client = getOkHttpClient();
    }

    BackendServiceClass(String apiEndpoint){
        this.url = ipAddress+ apiEndpoint;
        this.client = getOkHttpClient();
    }

    BackendServiceClass(String apiEndpoint, JSONObject json, String headerKey, String headerValue){
        this.url = ipAddress + apiEndpoint;
        this.json = json;
        this.headerKey = headerKey;
        this.headerValue = headerValue;
        this.client = getOkHttpClient();
    }

    BackendServiceClass(String apiEndpoint, String headerKey, String headerValue){
        this.url = ipAddress + apiEndpoint;
        this.headerValue = headerValue;
        this.headerKey = headerKey;
        this.client = getOkHttpClient();
    }


    public Request getPostRequestWithHeaderAndJsonParameter(){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(headerKey, headerValue)
                .post(body)
                .build();
        return request;
    }

    public Request doDeleteRequestWithHeaderOnly(){
        Request request = new Request.Builder()
                .url(url)
                .header(headerKey, headerValue)
                .delete()
                .build();
        return request;

    }

    public Request doPutRequestWithJsonAndHeader(){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(headerKey, headerValue)
                .put(body)
                .build();
        return  request;
    }

    public Request doPutRequestWithHeaderOnly(){
        Request request = new Request.Builder()
                .url(url)
                .header(headerKey, headerValue)
                .put(RequestBody.create(new byte[0]))
                .build();
        return  request;
    }

    public  Request getGetRequestWithHeaderOnly(){
        Request request = new Request.Builder()
                .url(url)
                .header(headerKey, headerValue)
                .get()
                .build();
        return request;
    }

    /**
     * POST REQUEST
     * Makes a post request object with the json object.
     * @return Request
     */
    public Request getPostRequestWithJsonParameter(){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return request;
    }

    /**
     * GET REQUEST without JSON parameter.
     * @return request object
     */
    public Request getGetRequestWithoutJsonParameter(){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return request;
    }

    /**
     * Gets the response body.
     * @param response
     * @return
     */
    public String getResponseBody(Response response){
        try {
            if (response.body() != null){
                return response.body().string();
            }
            else {
                return "";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets response object for any request.
     * @param request
     * @return
     */
    public Response getResponseFromRequest(Request request){
        try {
            return this.client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private OkHttpClient getOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
