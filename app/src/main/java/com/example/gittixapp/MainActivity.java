package com.example.gittixapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    EditText editTextSearchTicket;
    Button btnSearchTicket;
    FloatingActionButton sellTicketActionButton;

    boolean doubleBackToExitPressedOnce = false;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.txt_username);
        editTextSearchTicket = findViewById(R.id.editTextSearchTicket);
        btnSearchTicket = findViewById(R.id.btnSearchTickets);
        sellTicketActionButton = findViewById(R.id.floatingActionButton);

        requestLocationPermission();

        // this will deal with loading tickets into the RecyclerView
        RequestController.handleTicketsRecyclerView(getApplicationContext(), MainActivity.this,  TicketsList);

        //updates the Welcome bac, ___ text
        RequestController.updateUsernameValue(getApplicationContext(), MainActivity.this, username);

        setupSearchButton();
        setupAddTicketActionButton();
    }

    private void setupAddTicketActionButton() {
        sellTicketActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SellTicketActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    // Adds custom navbar into Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //  Adds listeners for each link item in the navbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemMyOrders:
                moveToMyOrders();
                return true;
            case R.id.itemLogout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setupSearchButton() {
       btnSearchTicket.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String query = editTextSearchTicket.getText().toString();
               RequestController.updateTicketsRecyclerView(getApplicationContext(), MainActivity.this, TicketsList, query);
           }
       });
   }

   private void moveToMyOrders(){
       Intent intent = new Intent(MainActivity.this, MyOrdersActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       startActivity(intent);
   }

    private void logout(){
        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        sessionManagement.removeSession();
        moveToLogin();
    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // will ask for location permission
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    // Double press back button to exit app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 1000);
    }

}