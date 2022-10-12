package com.example.gardenmobileapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtils {

    public static final String stdURL = "http://192.168.178.109:8000";

    public static boolean checkInternetConnection(Context context){
        ConnectivityManager cn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        return nf != null && nf.isConnected();
    }

    public static void doGet(String url, Context context, VolleyCallback callback) {
        //RequestQueue initialized
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        callback.onSuccess(response.get("status"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> callback.onError(error)
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void doPost(String url, boolean status, Context context) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject().put("status", status),
                response -> Log.d("POST", response.toString()),
                error -> Log.d("POST", error.toString()));
        requestQueue.add(jsonObjectRequest);
    }
}
