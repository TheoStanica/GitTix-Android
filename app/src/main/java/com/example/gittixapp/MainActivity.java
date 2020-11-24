package com.example.gittixapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView username;
    ArrayList<JSONObject> TicketsList = new ArrayList<>();
    String responseUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.txt_username);

        RecyclerView recyclerView = findViewById(R.id.rvTickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        TicketsRecyclerViewAdapter adapter = new TicketsRecyclerViewAdapter(getApplicationContext(), TicketsList);
//                adapter.setClickListener();
        recyclerView.setAdapter(adapter);

        RequestController.updateUsernameValue(getApplicationContext(), MainActivity.this, username);

        RequestController.handleTicketsRecyclerView(getApplicationContext(), MainActivity.this,  TicketsList);

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

//    public void moveToTicketView(){
//        Intent intent = new Intent(MainActivity.this, TicketViewActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
}