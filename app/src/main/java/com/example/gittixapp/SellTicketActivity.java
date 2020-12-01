package com.example.gittixapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;

public class SellTicketActivity extends AppCompatActivity implements OnMapReadyCallback {
    MapView mMapView;
    GoogleMap gMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    EditText editTextSellTitle;
    EditText editTextSellPrice;
    EditText editTextSellLocation;
    Double ticketSellLat;
    Double ticketSellLong;
    Button btnSellTicketNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_ticket);

        editTextSellTitle = findViewById(R.id.editTextSellTitle);
        editTextSellPrice = findViewById(R.id.editTextSellPrice);
        editTextSellLocation = findViewById(R.id.editTextSellLocation);
        btnSellTicketNow = findViewById(R.id.btnSellTicketNow);

        initGoogleMap(savedInstanceState);


        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        PlacesClient placesClient = Places.createClient(this);


        editTextSellLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    startAutocompleteActivity(getCurrentFocus());
                    editTextSellLocation.clearFocus();
                }
            }
        });


        btnSellTicketNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject ticketInfo = new JSONObject();
                try {
                    ticketInfo.put("title", editTextSellTitle.getText());
                    ticketInfo.put("price", editTextSellPrice.getText());
                    ticketInfo.put("latitude", ticketSellLat);
                    ticketInfo.put("longitude", ticketSellLong);
                    RequestController.postTicket(ticketInfo, getApplicationContext(), SellTicketActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.sellTicketMapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(SellTicketActivity.this, MainActivity.class);
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
        gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(15,45)));
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

    // deals with google places address autocomplete
    Integer AUTOCOMPLETE_REQUEST_CODE = 1;
    public void startAutocompleteActivity(View view){
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                editTextSellLocation.setText(place.getAddress());
                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14.0f));
                ticketSellLat = place.getLatLng().latitude;
                ticketSellLong = place.getLatLng().longitude;

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED){

            }
        }
    }

}