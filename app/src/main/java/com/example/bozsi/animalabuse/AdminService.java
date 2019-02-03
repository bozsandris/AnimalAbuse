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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;

public class AdminService extends AppCompatActivity implements LocationListener,View.OnClickListener{
    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String USERNAME = "Username";
    public static final String IMAGECODE = "Imagecode";
    public static final String MESSAGE = "Message";

    String longitude,latitude;
    String reports[][]= new String[5][5];
    LocationManager locationManager;
    TextView textView;
    TextView warning;
    FloatingActionButton fab;
    Button list;
    TextView distance;
    Button report1,report2,report3,report4,report5;

    private void findNear(CharSequence maxdistance) {
        MongoClientURI connectionstring = new MongoClientURI("mongodb://192.168.2.156:27017");
        MongoClient mongoClient = new MongoClient(connectionstring);
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("users");
        Point refPoint = new Point(new Position( Float.parseFloat(longitude), Float.parseFloat(latitude)));
        MongoCursor<Document> cursor = collection.find(Filters.near("contact.location", refPoint, Double.valueOf(maxdistance.toString()), 0.0)).iterator();
        int i=0;
        try {
            while (cursor.hasNext() && i<5) {
                Document doc = cursor.next();
                String coordinates = doc.getString("coordinates");
                coordinates = coordinates.substring(coordinates.indexOf("["),coordinates.indexOf("]"));
                String [] coords = coordinates.split(",");
                reports[i][0]=doc.getString("username");
                reports[i][1]=coords[0];
                reports[i][2]=coords[1];
                reports[i][3]=doc.getString("image");
                reports[i][4]=doc.getString("message");
                switch(i){
                 case 0: report1.setVisibility(View.VISIBLE);
                 case 1: report2.setVisibility(View.VISIBLE);
                 case 2: report3.setVisibility(View.VISIBLE);
                 case 3: report4.setVisibility(View.VISIBLE);
                 case 4: report5.setVisibility(View.VISIBLE);
                 }
                i++;
            }
        } finally {
            cursor.close();
            distance.setText("Nearby reports:");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminservicelayout);
        textView = findViewById(R.id.textview);
        warning = findViewById(R.id.warning);
        CheckPermission();
        distance = findViewById(R.id.maxdistance);
        Button report1 = findViewById(R.id.report1);
        Button report2 = findViewById(R.id.report2);
        Button report3 = findViewById(R.id.report3);
        Button report4 = findViewById(R.id.report4);
        Button report5 = findViewById(R.id.report5);
        list = findViewById(R.id.button);
        list.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                findNear(distance.getText());
                list.setVisibility(View.INVISIBLE);
            }
        });
        report1.setOnClickListener(this);
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
        list.setEnabled(true);
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
        list.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        Intent report = new Intent(getApplicationContext(),ReportInfo.class);
        switch(view.getId()){
            case R.id.report1:
                report.putExtra(USERNAME,reports[0][0]);
                report.putExtra(LONGITUDE,reports[0][1]);
                report.putExtra(LATITUDE,reports[0][2]);
                report.putExtra(IMAGECODE,reports[0][3]);
                report.putExtra(MESSAGE,reports[0][4]);
                break;
            case R.id.report2:
                report.putExtra(USERNAME,reports[1][0]);
                report.putExtra(LONGITUDE,reports[1][1]);
                report.putExtra(LATITUDE,reports[1][2]);
                report.putExtra(IMAGECODE,reports[1][3]);
                report.putExtra(MESSAGE,reports[1][4]);
                break;
            case R.id.report3:
                report.putExtra(USERNAME,reports[2][0]);
                report.putExtra(LONGITUDE,reports[2][1]);
                report.putExtra(LATITUDE,reports[2][2]);
                report.putExtra(IMAGECODE,reports[2][3]);
                report.putExtra(MESSAGE,reports[2][4]);
                break;
            case R.id.report4:
                report.putExtra(USERNAME,reports[3][0]);
                report.putExtra(LONGITUDE,reports[3][1]);
                report.putExtra(LATITUDE,reports[3][2]);
                report.putExtra(IMAGECODE,reports[3][3]);
                report.putExtra(MESSAGE,reports[3][4]);
                break;
            case R.id.report5:
                report.putExtra(USERNAME,reports[4][0]);
                report.putExtra(LONGITUDE,reports[4][1]);
                report.putExtra(LATITUDE,reports[4][2]);
                report.putExtra(IMAGECODE,reports[4][3]);
                report.putExtra(MESSAGE,reports[4][4]);
                break;
            default:
                break;
        }
        startActivity(report);
    }
}
