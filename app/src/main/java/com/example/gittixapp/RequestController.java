package com.example.gittixapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;

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

public abstract class RequestController {

    public static void Register(JSONObject registerCredentials, Context context, Activity activity){
        String url = "http://10.0.2.2/api/users/signup";

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, registerCredentials.toString());
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

                if(response.code() == 201){
                    // Credentials are ok,get cookie in response, save it as  session, move to mainActivity
                    String sessionCookie = response.headers("Set-Cookie").get(0);
                    SessionManagement sessionManagement = new SessionManagement(context);
                    sessionManagement.saveSession(sessionCookie);

                    //move to MainActivity
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    // Incorrect credentials, show Toast with error
                    String responseString = response.body().string();

                    ErrorParser errorParser = new ErrorParser(activity, context, responseString);
                    errorParser.displayErrors();
                }
            }
        });
    }

    public static void Login(JSONObject loginCredentials, Context context, Activity activity){
        String url = "http://10.0.2.2/api/users/signin";

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

                    SessionManagement sessionManagement = new SessionManagement(context);
                    sessionManagement.saveSession(sessionCookie);

                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } else {
                    // Incorrect credentials, show Toast with error

                    String responseString = response.body().string();

                    ErrorParser errorParser = new ErrorParser(activity, context, responseString);
                    errorParser.displayErrors();
                }
            }
        });
    }

    public static void handleTicketsRecyclerView(Context context, Activity activity, ArrayList<JSONObject> TicketsList){
        String url = "http://10.0.2.2/api/tickets";

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // get the list of tickets
                // add them to the Ticket RecyclerView
                String result =  response.body().string();

                if(response.code() == 200){
                    ArrayList<JSONObject> TicketsList = new ArrayList<>();
                    try {
                        JSONArray resultJSON = new JSONArray(result);
                        for(int i =0; i < resultJSON.length(); i++){
                            TicketsList.add(resultJSON.getJSONObject(i));
                        }
                        Thread thread =new Thread(){
                            public void run() {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupRecyClerView(activity,context, TicketsList);;
                                    }
                                });
                            }
                        };
                        thread.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ErrorParser errorParser = new ErrorParser(activity, context, result);
                    errorParser.displayErrors();
                }
            }
        });
    }
    public static void handleOrdersRecyclerView(Context context, Activity activity, ArrayList<JSONObject> ordersList){
        String url = "http://10.0.2.2/api/orders/";

        SessionManagement sessionManagement = new SessionManagement(context);
        String cookie = sessionManagement.getSession();

        OkHttpClient client = new OkHttpClient();
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

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // get the list of orders
                // add them to the Orders RecyclerView
                String result =  response.body().string();
                if(response.code() == 200){
                    ArrayList<JSONObject> ordersList = new ArrayList<>();
                    try {
                        JSONArray resultJSON = new JSONArray(result);
                        for(int i =0; i < resultJSON.length(); i++){
                            ordersList.add(resultJSON.getJSONObject(i));
                        }
                        Thread thread =new Thread(){
                            public void run() {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RecyclerView recyclerView = activity.findViewById(R.id.rvOrders);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                        OrdersRecycleViewAdapter adapter = new OrdersRecycleViewAdapter(context, ordersList);
                                        recyclerView.setAdapter(adapter);
                                    }
                                });
                            }
                        };
                        thread.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ErrorParser errorParser = new ErrorParser(activity, context, result);
                    errorParser.displayErrors();
                }
            }
        });
    }

    public static void updateTicketsRecyclerView(Context context, Activity activity, ArrayList<JSONObject> TicketsList, String searchText){
        String url = "http://10.0.2.2/api/tickets/search/" + searchText;
        if (searchText.equals("")){
            url = "http://10.0.2.2/api/tickets/";
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // get the list of tickets
                // add them to the Ticket RecyclerView
                String result =  response.body().string();

                if(response.code() == 200){
                    ArrayList<JSONObject> TicketsList = new ArrayList<>();
                    try {
                        JSONArray resultJSON = new JSONArray(result);
                        for(int i =0; i < resultJSON.length(); i++){

                            TicketsList.add(resultJSON.getJSONObject(i));
                        }
                        Thread thread =new Thread(){
                            public void run() {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupRecyClerView(activity,context, TicketsList);;
                                    }
                                });
                            }
                        };
                        thread.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ErrorParser errorParser = new ErrorParser(activity, context, result);
                    errorParser.displayErrors();
                }
            }
        });
    }

    private static void setupRecyClerView(Activity activity, Context context, ArrayList<JSONObject> TicketsList) {
        RecyclerView recyclerView = activity.findViewById(R.id.rvTickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        TicketsRecyclerViewAdapter adapter = new TicketsRecyclerViewAdapter(context, TicketsList);
        recyclerView.setAdapter(adapter);
    }

    public  static void updateUsernameValue(Context context, Activity activity, TextView username){
        String url = "http://10.0.2.2/api/users/currentuser";
        SessionManagement sessionManagement = new SessionManagement(context);
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
                    try {
                        JSONObject usernameJSON = new JSONObject(usernameValue);
                        JSONObject userDetailsJson = new JSONObject(usernameJSON.get("currentUser").toString());
                        String responseUsername = userDetailsJson.get("username").toString();
                        new Thread(){
                            public void run() {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        username.setText(responseUsername);
                                    }
                                });
                            }
                        }.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }



    //Loads TicketViewActivity data
    public static String loadTicketViewPage(Activity activity, Context context, String ticketId,
                                            TextView ticketTitle, TextView ticketPrice, GoogleMap gMap) throws IOException {

        String url = "http://10.0.2.2/api/tickets/" + ticketId;

        SessionManagement sessionManagement = new SessionManagement(context);
        String cookie = sessionManagement.getSession();
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .addHeader("Cookie", cookie)
                .url(url)
                .get()
                .build();
        final String[] result = new String[1];

        Thread thread =new Thread() {
            public void run() {
                try (Response response = client.newCall(request).execute()) {
                    result[0] = response.body().string();
                    if(response.code() != 200){
                        //if wrong ticketID/ something goes bad
                        // show errors in Toast message
                        ErrorParser errorParser = new ErrorParser(activity, context, result[0]);
                        errorParser.displayErrors();

                        // move back to mainActivity
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return result[0];

    }


    public static void postTicket(JSONObject ticketInfo, Context context, Activity activity){
        String url = "http://10.0.2.2/api/tickets";

        SessionManagement sessionManagement = new SessionManagement(context);
        String cookie = sessionManagement.getSession();

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, ticketInfo.toString());
        Request request = new Request.Builder()
                .addHeader("Cookie", cookie)
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

                if(response.code() == 201){
                    // Credentials are ok,get cookie in response, save it as  session, move to mainActivity
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } else {
                    String responseString = response.body().string();
                    // Incorrect credentials, show Toast with error
                    ErrorParser errorParser = new ErrorParser(activity, context, responseString);
                    errorParser.displayErrors();
                }
            }
        });
    }

    public static String postOrder(String bodytoSend, Context context, Activity activity){
        String url = "http://10.0.2.2/api/orders";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody data = RequestBody.create(JSON, bodytoSend.toString());
        SessionManagement sessionManagement = new SessionManagement(context);
        String cookie = sessionManagement.getSession();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Cookie", cookie)
                .url(url)
                .post(data)
                .build();

        // run the request on new thread and wait for result
        final String[] postOrderresult = new String[1];
        Thread thread = new Thread(){
            public void run(){
                try (Response response = client.newCall(request).execute()){
                    postOrderresult[0] =response.body().string();
                    if(response.code() != 201){
                        ErrorParser errorParser = new ErrorParser(activity, context, postOrderresult[0]);
                        errorParser.displayErrors();
                        //TODO other errors. An Error activity that will redirect user to home page
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return postOrderresult[0];
    }


    public static String registerPaymentDetails(String bodytoSend, Context context, Activity activity){

        String url = "http://10.0.2.2/api/payments/registerpayment";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody data = RequestBody.create(JSON, bodytoSend);
        SessionManagement sessionManagement = new SessionManagement(context);
        String cookie = sessionManagement.getSession();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Cookie", cookie)
                .url(url)
                .post(data)
                .build();


        // run the request on new thread and wait for result

        final String[] token = new String[1];
        Thread thread = new Thread(){
            public void run(){
                try (Response response = client.newCall(request).execute()){
                    token[0] =response.body().string();

                    if(response.code() != 201){
                        ErrorParser errorParser = new ErrorParser(activity, context, token[0]);
                        errorParser.displayErrors();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return token[0];
    }

    public static String getOrderDetails(String orderId, Context context, Activity activity){
        String url = "http://10.0.2.2/api/orders/"+ orderId;

        SessionManagement sessionManagement = new SessionManagement(context);
        String cookie = sessionManagement.getSession();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Cookie", cookie)
                .url(url)
                .get()
                .build();

        final String[] orderDetails = new String[1];
        Thread thread = new Thread(){
            public void run(){
                try (Response response = client.newCall(request).execute()){
                    orderDetails[0] =response.body().string();
                    if(response.code() != 200){
                        ErrorParser errorParser = new ErrorParser(activity, context, orderDetails[0]);
                        errorParser.displayErrors();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return orderDetails[0];
    }

}
