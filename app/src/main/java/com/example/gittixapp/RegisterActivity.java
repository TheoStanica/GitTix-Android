package com.example.gittixapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity  extends AppCompatActivity {
    EditText email, password, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.txt_email);
        password = findViewById(R.id.txt_password);
        username = findViewById(R.id.txt_username);
    }

    public void register(View view) {
        String url = "http://10.0.2.2/api/users/signup";
        JSONObject loginCredentials = new JSONObject();
        try {
            loginCredentials.put("email", email.getText());
            loginCredentials.put("password", password.getText());
            loginCredentials.put("username", username.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, loginCredentials.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
                if(response.code() == 201){
                    // Credentials are ok,get cookie in response, save it as  session, move to mainActivity

                    String sessionCookie = response.headers("Set-Cookie").get(0);
                    SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
                    sessionManagement.saveSession(sessionCookie);
                    moveToMainActivity();

                } else {
                    // Incorrect credentials, show Toast with error
                    String responseString = response.body().string();

                    ErrorParser errorParser = new ErrorParser(RegisterActivity.this, getApplicationContext(), responseString);
                    errorParser.displayErrors();
                }
            }
        });
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
