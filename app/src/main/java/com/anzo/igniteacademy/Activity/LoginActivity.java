package com.anzo.igniteacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.Helper.UserPreferences;
import com.anzo.igniteacademy.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        binding.cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtUsername.getText().toString().isEmpty()) {
                    binding.edtUsername.setError("Enter UserName");
                    return;
                }
                if (binding.edtPassword.getText().toString().isEmpty()) {
                    binding.edtPassword.setError("Enter Password");
                    return;
                }
                requestLogin();
            }
        });
    }

    private void requestLogin() {

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("username", binding.edtUsername.getText().toString());
        reqParams.put("password", binding.edtPassword.getText().toString());

        requestApi(reqParams, Request.Method.POST, WebService.LOGIN, new RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {

                        JSONObject dataJsonObject = jsonObject.getJSONObject(WebService.Params.DATA);
                        userPreference.setData(UserPreferences.USER_ID, dataJsonObject.getString("id"));
                        userPreference.setData(UserPreferences.USER_NAME, dataJsonObject.getString("name"));

                        userPreference.setLogin(true);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError error, int errorCode) {
                progressDialog.cancel();
            }
        }, null);
    }
}