package com.example.digitalplatformclient.api;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class AJAX {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    public static String URL = "http://192.168.0.100:3000/";
    public static String URL_API = URL + "api/";

    public static Call get(String endpoint, String json, Callback callback) {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URL_API + endpoint)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
