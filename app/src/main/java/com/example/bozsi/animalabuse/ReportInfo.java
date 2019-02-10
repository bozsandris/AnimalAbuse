package com.example.bozsi.animalabuse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class ReportInfo extends AppCompatActivity {

    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String USERNAME = "Username";
    String longitude,latitude,username,imagecode,messagetext;

    FloatingActionButton fab;
    TextView distance;
    Bitmap decodedImage;
    ImageView image;
    ViewGroup.LayoutParams params=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportview);
        TextView userid = findViewById(R.id.userid);
        TextView location = findViewById(R.id.location);
        TextView message = findViewById(R.id.message);
        image = findViewById(R.id.imageView2);
        final Intent reportdetails = getIntent();
        //TODO imaget is megcsinálni
        username = reportdetails.getStringExtra(AdminService.USERNAME);
        userid.setText(username);
        longitude = reportdetails.getStringExtra(AdminService.LONGITUDE);
        latitude = reportdetails.getStringExtra(AdminService.LATITUDE);
        String locationtext = longitude+" "+latitude;
        location.setText(locationtext);
        imagecode=reportdetails.getStringExtra(AdminService.IMAGECODE);
        byte[] imageBytes = Base64.decode(imagecode, Base64.DEFAULT);;
        decodedImage = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        image.setImageBitmap(decodedImage);
         messagetext = reportdetails.getStringExtra(AdminService.MESSAGE);
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
                String uri = String.format(Locale.ENGLISH, "geo:"+Double.valueOf(latitude)+","+Double.valueOf(longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
                startActivity(intent);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreen();
            }
        });
    }

    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)

        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("TAG", "Turning immersive mode mode off. ");
            image.setLayoutParams(params);
        } else {
            Log.i("TAG", "Turning immersive mode mode on.");
            params = image.getLayoutParams();
            image.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
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
