package com.example.gittixapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TicketViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    String ticketId;
    TextView ticketTitle;
    TextView ticketPrice;
    Button buyNowButton;

    Double ticketLat;
    Double ticketLong;

    MapView mMapView;
    GoogleMap gMap;

    String response;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view);

        ticketId = getIntent().getStringExtra("ticket_id");

        ticketTitle = findViewById(R.id.txtTicketTitle);
        ticketPrice = findViewById(R.id.txtTicketPrice);
        buyNowButton = findViewById(R.id.btnBuyTicket);

        initGoogleMap(savedInstanceState);


        try {
            response = RequestController.loadTicketViewPage(TicketViewActivity.this, getApplicationContext(), ticketId, ticketTitle, ticketPrice, gMap);
            updateTicketDetails(response);
            setupBuyNowButton();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void setupBuyNowButton() {
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to OrderActivity
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ticket_id", ticketId);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    private void updateTicketDetails(String response) throws IOException {
        if (response != null) {
            try {
                JSONObject TicketDetails = new JSONObject(response);
                String TicketTitle = TicketDetails.get("title").toString();
                String TicketPrice = TicketDetails.get("price").toString();
                String TicketLongitude = TicketDetails.get("longitude").toString();
                String TicketLatitude = TicketDetails.get("latitude").toString();


                Thread thread = new Thread() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ticketTitle.setText(TicketTitle);
                                ticketPrice.setText(TicketPrice);
                                ticketLat = Double.parseDouble(TicketLatitude);
                                ticketLong = Double.parseDouble(TicketLongitude);

                                //add marker on map
                                gMap.addMarker(new MarkerOptions().position(new LatLng(ticketLat, ticketLong)).title("Marker"));
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ticketLat, ticketLong),15.0f));

                            }
                        });
                    }
                };
                thread.start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // called in activity onCreate method
    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.ticketMapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(TicketViewActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}