package com.anzo.igniteacademy;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.Helper.Helper;
import com.anzo.igniteacademy.Helper.UserPreferences;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    public static int NETWORK_CODE_NO_INTERNET = 101;
    public static int NETWORK_CODE_DEFAULT = 100;

    public UserPreferences userPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreference = new UserPreferences(this);
    }

    public void requestApi(Map<String, String> requestParams, int method, String url, final RequestApiInterface apiInterface, @Nullable String tag) {
        if (!Helper.isInternetAvailable(this)) {
            new AlertDialog.Builder(BaseActivity.this)
                    .setTitle(getString(R.string.network_error))
                    .setMessage(getString(R.string.no_internet_msg))
                    .setCancelable(false)
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestApi(requestParams, method, url, apiInterface, tag);
                        }
                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    apiInterface.onError(null, NETWORK_CODE_NO_INTERNET);
                }
            }).show();
            return;
        }
        Helper.LOG("API-Url", url);
        Helper.LOG("API-Request", requestParams == null ? "{black}" : requestParams.toString());
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(BaseActivity.this);

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Helper.LOG("API-Response", response);
                if (apiInterface != null)
                    apiInterface.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Helper.LOG("API-Error", error.toString());

                String errorRes = "";
                try {
                    errorRes = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Helper.LOG("API-Error-Response", errorRes);
                } catch (Exception e) {
                    errorRes = "";
                }

                /*int statusCode = 0;
                try {
                    statusCode = error.networkResponse.statusCode;
                } catch (Exception e) {
                    statusCode = 0;
                }*/
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestParams == null ? super.getParams() : requestParams;
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //if (token.isEmpty() || url.contains("login") || url.contains("signup")) {
                if (Helper.isNullOrEmpty(token)) {
                    return super.getHeaders();
                } else {
                    Map<String, String> headerKey = new HashMap<>();
                    headerKey.put("Authorization", "Bearer " + token);
                    return headerKey;
                }
            }*/
        };

        if (!Helper.isNullOrEmpty(tag))
            stringRequest.setTag(tag);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                WebService.TIMEOUT_LIMIT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    public interface RequestApiInterface {
        void onResponse(String response);

        void onError(VolleyError error, int errorCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
