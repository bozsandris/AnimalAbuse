package com.example.bozsi.animalabuse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AnimalAbuseService extends AppCompatActivity implements LocationListener {

    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String USERNAME = "Username";
    String longitude,latitude,username;
    LocationManager locationManager;
    TextView textView;
    TextView warning;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_abuse_service);
        textView = findViewById(R.id.textview);
        warning = findViewById(R.id.warning);
        CheckPermission();
        final Intent loginintent = getIntent();
        username = loginintent.getStringExtra(LoginService.EMAIL);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);
                intent.putExtra(USERNAME,username);
                intent.putExtra(LONGITUDE,longitude);
                intent.putExtra(LATITUDE,latitude);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void getLocation(){
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);
        }
        catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    public void CheckPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        fab.setEnabled(true);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008577")));
        warning.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
        textView.setText("Your current location:\nLongitude: "+longitude+"\n Latitude: "+latitude+"\nNow you can fill out your report by clicking the envelope button!");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this,"GPS enabled!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String s) {
        fab.setEnabled(false);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        Toast.makeText(this,"Please turn on GPS!",Toast.LENGTH_SHORT).show();
        textView.setVisibility(View.INVISIBLE);
        warning.setVisibility(View.VISIBLE);
    }
}
