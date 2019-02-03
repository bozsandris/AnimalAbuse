package com.example.bozsi.animalabuse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class ReportInfo extends AppCompatActivity {

    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String USERNAME = "Username";
    String longitude,latitude,username,imagecode,messagetext;

    FloatingActionButton fab;
    TextView distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportview);
        TextView userid = findViewById(R.id.userid);
        TextView location = findViewById(R.id.location);
        TextView message = findViewById(R.id.message);
        ImageView image = findViewById(R.id.imageView2);
        final Intent reportdetails = getIntent();
        //TODO imaget is megcsinálni
        username = reportdetails.getStringExtra(LoginService.EMAIL);
        userid.setText(username);
        longitude = reportdetails.getStringExtra(LoginService.EMAIL);
        latitude = reportdetails.getStringExtra(LoginService.EMAIL);
        String locationtext = longitude+" "+latitude;
        location.setText(locationtext);
        //imagecode=;
        //image=;
         messagetext = reportdetails.getStringExtra(LoginService.EMAIL);
         message.setText(messagetext);

        Button mark = findViewById(R.id.mark);
        mark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO Admin can mark it as dealt with,the report gets deleted from this collection
                //and is being transferred @ the same time to another collection (for later tracing purposes)
            }
        });

        Button open = findViewById(R.id.openmap);
        open.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO test if works
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.parseFloat(latitude), Float.parseFloat(longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
