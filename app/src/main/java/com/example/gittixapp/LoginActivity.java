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
        // make http request to login using credentials
        // if ok response, move to mainActivity
        // else Toast incorrect credentials
        String url = "http://10.0.2.2/api/users/signin";
        JSONObject loginCredentials = new JSONObject();
        try {
            loginCredentials.put("email", email.getText());
            loginCredentials.put("password", password.getText());
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
                if(response.code() == 200){
                    // Credentials are ok,get cookie in response, save it as  session, move to mainActivity

                    String sessionCookie = response.headers("Set-Cookie").get(0).toString();

                    SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
                    sessionManagement.saveSession(sessionCookie);
                    moveToMainActivity();

                } else {
                    // Incorrect credentials, show Toast with error
                    String responseString = response.body().string();

                    ErrorParser errorParser = new ErrorParser(LoginActivity.this, getApplicationContext(), responseString);
                    errorParser.displayErrors();
                }
            }
        });

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