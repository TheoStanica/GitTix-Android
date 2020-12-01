package com.example.gittixapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.CardParams;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {
    String ticketId;
    Button payButton;
    private Stripe stripe;
    String orderId;
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    LoadingDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        payButton = findViewById(R.id.payButton);
        ticketId = getIntent().getStringExtra("ticket_id");

        dialog = new LoadingDialog(OrderActivity.this);

        try {
            startCheckout();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void startCheckout() throws JSONException, InterruptedException {
        //initialize stripe
        stripe = new Stripe(
                getApplicationContext(),
                "pk_test_51HTnsJDGCw3oLuCfy1UM4QWSmo0hR7HH2XXoq3b8R0jr2BYISYXmvqihkuYVyRWeXSo43QvS3CpN5soLVr1zPNY600zM87Fydd"
        );



        JSONObject data = new JSONObject();
        data.put("ticketId", ticketId);

        // create a new order
        String order = RequestController.postOrder(data.toString(), getApplicationContext(), OrderActivity.this);


        JSONObject orderJSON = new JSONObject(order);
        orderId = orderJSON.get("id").toString();

        // prepare and send data to get a stripe clientSecret at page load
        String json = "{"
                + "\"orderId\": " + "\""+ orderId + "\""
                + "}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody bodyToSedn = RequestBody.create(JSON, json);
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        String cookie = sessionManagement.getSession();

        long time = 1 ;
        TimeUnit.SECONDS.sleep(time);

        Request request = new Request.Builder()
                .addHeader("Cookie", cookie)
                .url("http://10.0.2.2/api/payments/connectiontoken")
                .post(bodyToSedn)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));



        //setup listener for pay button
        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                //API request to check if order is expired or not
                //if expired(status: expired) , don't proceed to make payment, redirect to main activity
                //if not(status: created), proceed to make payment
                String orderStatus = RequestController.getOrderDetails(orderId, getApplicationContext(), OrderActivity.this);
                try {
                    JSONObject orderStatusJSON = new JSONObject(orderStatus);
                    String status = orderStatusJSON.getString("status");
                    if(status.equals("created")){
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                        stripe.confirmPayment(this, confirmParams);
                        dialog.startLoadingDialog();
                    } else if(status.equals("cancelled")){
                        dialog.startLoadingOrderExpiredDialog();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                dialog.dismissDialog();
                            }
                        },2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }
    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
    }


    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<OrderActivity> activityRef;
        PayCallback(@NonNull OrderActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }
        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }
    private  final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult>  {
        @NonNull private final WeakReference<OrderActivity> activityRef;

        PaymentResultCallback(@NonNull OrderActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                dialog.dismissDialog();
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                // send request to API to register the payment
                // show success messages and change activities
                JSONObject paymentDetails = new JSONObject();

                try {
                    paymentDetails.put("stripeId", paymentIntent.getId());
                    paymentDetails.put("orderId", orderId);

                    RequestController.registerPaymentDetails(paymentDetails.toString(), getApplicationContext(), OrderActivity.this);

                    dialog = new LoadingDialog(OrderActivity.this);
                    dialog.startLoadingCompletedDialog();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(OrderActivity.this, MyOrdersActivity.class);
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dialog.dismissDialog();
                        }
                    },2000);

                } catch (JSONException e ) {
                    e.printStackTrace();
                }


            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                dialog.startLoadingRejectedDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismissDialog();
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                },2000);
                dialog.dismissDialog();
            }
        }
        @Override
        public void onError(@NonNull Exception e) {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            dialog.dismissDialog();
            dialog.startLoadingRejectedDialog();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    dialog.dismissDialog();
                }
            },2000);
        }
    }
}