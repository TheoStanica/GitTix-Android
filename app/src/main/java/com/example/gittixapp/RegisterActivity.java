package com.example.gittixapp;

import android.app.Activity;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // navbar back button set to send user back to login screen
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }

    public void register(View view) {
        // Create JSON object to send
        JSONObject loginCredentials = new JSONObject();
        try {
            loginCredentials.put("email", email.getText());
            loginCredentials.put("password", password.getText());
            loginCredentials.put("username", username.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make the request
        RequestController.Register(loginCredentials,getApplicationContext(),RegisterActivity.this);
    }

}
