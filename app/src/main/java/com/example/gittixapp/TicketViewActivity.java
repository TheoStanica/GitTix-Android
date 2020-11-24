package com.example.gittixapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TicketViewActivity extends AppCompatActivity {
    String ticketId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ticketId = getIntent().getStringExtra("ticket_id");

        updateTicketViewPage(ticketId);
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(TicketViewActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }


    private void updateTicketViewPage(String ticketId) {
        String url = "http://10.0.2.2/api/tickets/" + ticketId;

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

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200){
                    String usernameValue = response.body().string();

                }

            }
        });
    }


}