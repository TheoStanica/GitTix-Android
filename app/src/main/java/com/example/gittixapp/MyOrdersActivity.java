package com.example.gittixapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONObject;

import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity {

    ArrayList<JSONObject> ordersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        //loads orders into OrdersRecycleView
        RequestController.handleOrdersRecyclerView(getApplicationContext(), MyOrdersActivity.this, ordersList);
    }
}