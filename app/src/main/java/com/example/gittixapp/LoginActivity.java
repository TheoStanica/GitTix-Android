package com.example.gittixapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.txt_email);
        password = findViewById(R.id.txt_password);

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkSession();
    }

    private void checkSession() {
        SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
        String session = sessionManagement.getSession();

        if(!session.equals("none")){
            //user is logged in > move to mainActivity
            moveToMainActivity();
        }
    }



    public void login(View view) {

        // create JSON object to send
        JSONObject loginCredentials = new JSONObject();
        try {
            loginCredentials.put("email", email.getText());
            loginCredentials.put("password", password.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // make http request to login using credentials
        RequestController.Login(loginCredentials, getApplicationContext(), LoginActivity.this);

    }

    private void moveToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    public void register(View view) {
        moveToRegisterActivity();
    }

    private void moveToRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}