package com.example.triptrooperapp;

import android.util.Log;

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

/**
 * Help from chat gpt.
 */
public class BackendServiceClass {
    private static final String ipAddress = "https://44.225.75.82:8081/";
    private static final String authHeader = "authorization";

    private static final MediaType JSON = MediaType.parse("application/json; " +
            "charset=utf-8");
    private static final OkHttpClient client = getOkHttpClient();

    /**
     * Gets the response body.
     *
     * @param response
     * @return
     */
    public static String getResponseBody(Response response) {
        try {
            if (response.body() != null) {
                return response.body().string();
            } else {
                return "";
            }
        } catch (IOException e) {
            throw new CustomException("Error occurred when parsing " +
                    "response", e);
        }
    }

    /**
     * Gets response object for any request.
     *
     * @param request
     * @return
     */
    public static Response getResponseFromRequest(Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new CustomException("error", e);
        }
    }


    /**
     * Method: Post
     * Functionality: creates group.
     */
    public static Request createGroupPostRequest(JSONObject json,
                                                 String authToken) {
        RequestBody body = RequestBody.create(json.toString(), JSON);
        String url = ipAddress + "groups/create";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .post(body)
                .build();
        return request;
    }

    /**
     * Method: Post
     * Endpoint: groups/join
     * Functionality: join a group
     */
    public static Request joinGroupPutRequest(JSONObject json,
                                              String authToken) {
        RequestBody body = RequestBody.create(json.toString(), JSON);
        String url = ipAddress + "groups/join";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;
    }

    /**
     * Method: PUT
     * Endpoint: groups/:groupId/leave
     * Functionality: user to leave group.
     */
    public static Request leaveGroupPutRequest(String authToken,
                                               String groupId) {
        String url = ipAddress + "groups/" + groupId + "/leave";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(RequestBody.create(new byte[0]))
                .build();
        return request;
    }

    /**
     * Method: GET
     * Endpoint: groups/:groupId
     * Functionality: get all groups for a user.
     */
    public static Request getGroupsForUserGetRequest(String authToken,
                                                     String groupId) {
        String url = ipAddress + "groups/" + groupId;
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .get()
                .build();
        return request;
    }

    /**
     * Method: DELETE
     * Endpoint: groups/:groupId/delete
     * Functionality: Delete group
     */
    public static Request deleteGroupDeleteRequest(String authToken,
                                                   String groupId) {
        String url = ipAddress + "groups/" + groupId + "/delete";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .delete()
                .build();
        return request;
    }

    /**
     * Method: GET
     * Endpoint: groups/:groupId/lists
     * Functionality: get all lists for a group.
     */
    public static Request getGroupListsGetRequest(String authToken,
                                                  String groupId) {
        String url = ipAddress + "groups/" + groupId + "/lists";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .get()
                .build();
        return request;
    }

    /**
     * Method: PUT
     * Endpoint: groups/:groupId/add/list
     * Functionality: create group list
     */
    public static Request createGroupListPutRequest(JSONObject json,
                                                    String authToken,
                                                    String groupId) {
        String url = ipAddress + "groups/" + groupId + "/add/list";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;
    }

    /**
     * Method: GET
     * Endpoint: groups/all
     * Functionality: get all groups of user
     */
    public static Request getGroupsOfUserGetRequest(String authToken) {
        String url = ipAddress + "groups/all";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .get()
                .build();
        return request;
    }

    /**
     * Method: GET
     * Endpoint: users/lists
     * Functionality: get all list of user
     */
    public static Request getListsOfUserGetRequest(String authToken) {
        String url = ipAddress + "users/lists";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .get()
                .build();
        return request;
    }

    /**
     * Method: PUT
     * Endpoint: users/add/list
     * Functionality: create list for user.
     */
    public static Request createListForUser(JSONObject json,
                                            String authToken) {
        String url = ipAddress + "users/add/list";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;
    }

    /**
     * Method: POST
     * Endpoint: users/login
     * Functionality: Logins in user.
     */
    public static Request loginUserPostRequest(JSONObject json) {
        String url = ipAddress + "users/login";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return request;
    }

    /**
     * Method: PUT
     * Endpoint: lists/:listId/add/schedule
     * Functionality: Creates an optimized schedule
     * (complex algorithm)
     */
    public static Request getOptimizedSchedulePutRequest(
            JSONObject json,
            String authToken,
            String listId
    ) {
        String url = ipAddress + "lists/" + listId + "/add/schedule";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;
    }

    /**
     * Method: GET
     * Endpoint: lists/:listId/places
     */
    public static Request getPlacesForListGetRequest(String authToken,
                                                     String listId) {
        String url = ipAddress + "lists/" + listId + "/places";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .get()
                .build();
        return request;
    }

    /**
     * Method: DELETE
     * Endpoint: users/:listId/remove/list
     * Functionality: delete list for user
     */
    public static Request deleteUserListDeleteRequest(
            String authToken,
            String listId
    ) {
        String url = ipAddress + "users/" + listId + "/remove/list";
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(RequestBody.create(new byte[0]))
                .build();
        return request;
    }

    /**
     * Method: DELETE
     * Endpoint: groups/:groupId/remove/list
     * Functionality: Deletes a list in the group.
     */
    public static Request deleteGroupListDeleteRequest(
            String authToken,
            JSONObject json,
            String groupId
    ) {
        String url = ipAddress + "groups/" + groupId + "/remove/list";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;
    }

    /**
     * Method: GET
     * Endpoint: "places/currLocation?latitude=" + latitude +
     * "&longitude" +
     * "=" + longitude
     * Functionality: Get places nearby
     */
    public static Request getPlacesNearbyGetRequest(
            String authToken,
            String longitude,
            String latitude
    ) {
        String url = ipAddress + "places/currLocation?latitude=" + latitude +
                "&longitude" +
                "=" + longitude;
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .get()
                .build();
        return request;
    }

    /**
     * Method: PUT
     * Endpoint: lists/:listId/add/place
     * Functionality: Add place to list.
     */
    public static Request addNearbyPlaceToListPutRequest(
            String authToken,
            JSONObject json,
            String listId
    ) {
        String url = ipAddress + "lists/" + listId + "/add/place";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;
    }

    /**
     * Method: GET
     * Endpoint: "places/destination?textQuery=" + destination +
     * "&category="+category
     * Functionality: retrieve places by destination
     */
    public static Request getPlacesByDestination(
            String authToken,
            String destination,
            String category /*To be added*/
    ) {
        String url = ipAddress + "places/destination?textQuery=" + destination +
                "&category=" + category;
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .get()
                .build();
        return request;
    }

    /**
     * Method: PUT
     * Endpoint: lists/:listId/add/place
     * Functionality: add destination place to list
     */
    public static Request addDestinationPlacesToList(
            String authToken,
            JSONObject json,
            String listId
    ) {
        String url = ipAddress + "lists/" + listId + "/add/place";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;
    }

    public static Request removePlaceFromList(
            String authToken,
            JSONObject json,
            String listId
    ) {
        String url = ipAddress + "lists/" + listId + "/remove/place";
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header(authHeader, authToken)
                .put(body)
                .build();
        return request;

    }

    /**
     * Taken from CHAT GPT.
     *
     * @return
     */

    private static OkHttpClient getOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain,
                                                       String authType)
                                throws CertificateException {
                            Log.d("TAG", "Checking client trusted..");
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            Log.d("TAG", "Checking server trusted..");
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory =
                    sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory,
                    (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new CustomException("error", e);
        }
    }
}
