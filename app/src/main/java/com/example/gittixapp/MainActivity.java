package com.example.gittixapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.txt_username);

        updateUsernameValue();
    }

    private void updateUsernameValue() {
        String url = "http://10.0.2.2/api/users/currentuser";

        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        String cookie = sessionManagement.getSession();

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .addHeader("Cookie", cookie)
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                if(response.code() == 200){
                    // Credentials are ok,get cookie in response, save it as  session, move to mainActivity
                    String usernameValue = response.body().string();
                    try {
                        JSONObject usernameJSON = new JSONObject(usernameValue);
                        JSONObject userDetailsJson = new JSONObject(usernameJSON.get("currentUser").toString());

                        username.setText(userDetailsJson.get("username").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void logout(View view) {
        // this method will remove the session and open the login screen
        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        sessionManagement.removeSession();
        moveToLogin();
    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}