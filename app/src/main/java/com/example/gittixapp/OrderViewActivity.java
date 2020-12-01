package com.example.gittixapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderViewActivity extends AppCompatActivity {
    private String orderId;
    private String orderDetails;
    private JSONObject orderDetailsJSON;
    private JSONObject orderTicketDetailsJSON;
    private TextView txtOrderViewTitle, txtOrderViewPrice, txtOrderViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        txtOrderViewTitle = findViewById(R.id.txtOrderViewTitle);
        txtOrderViewPrice = findViewById(R.id.txtOrderViewPrice);
        txtOrderViewStatus = findViewById(R.id.txtOrderViewStatus);

        orderId = getIntent().getStringExtra("order_id");

        try {
            getOrderDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getOrderDetails() throws JSONException {
        String orderDetails = RequestController.getOrderDetails(orderId, getApplicationContext(), OrderViewActivity.this);

        orderDetailsJSON = new JSONObject(orderDetails);
        orderTicketDetailsJSON = new JSONObject(orderDetailsJSON.getString("ticket"));
        txtOrderViewTitle.setText(orderTicketDetailsJSON.getString("title"));
        txtOrderViewPrice.setText(orderTicketDetailsJSON.getString("price"));
        txtOrderViewStatus.setText(orderDetailsJSON.getString("status"));
    }
}