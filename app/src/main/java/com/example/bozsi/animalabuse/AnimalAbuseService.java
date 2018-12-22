package com.example.bozsi.animalabuse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.w3c.dom.Document;

public class AnimalAbuseService extends AppCompatActivity implements LocationListener {

    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String USERNAME = "Username";
    String longitude,latitude,username;
    LocationManager locationManager;
    TextView textView;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_abuse_service);
        textView = findViewById(R.id.textview);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_animal_abuse, menu);
        return true;
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
    longitude = String.valueOf(location.getLongitude());
    latitude = String.valueOf(location.getLatitude());
    textView.setText("Your current location:\nLongitude: "+longitude+"\n Latitude: "+latitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        fab.setEnabled(true);
        Toast.makeText(this,"Sikeres engedélyezés!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        fab.setEnabled(false);
        Toast.makeText(this,"Kérlek kapcsold be a GPS-t",Toast.LENGTH_SHORT).show();
    }
}
