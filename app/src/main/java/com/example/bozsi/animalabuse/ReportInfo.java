package com.example.bozsi.animalabuse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class ReportInfo extends AppCompatActivity {
    String longitude,latitude,username,imagecode,messagetext;
    Bitmap decodedImage;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportview);
        TextView userid = findViewById(R.id.userid);
        TextView location = findViewById(R.id.location);
        TextView message = findViewById(R.id.message);
        final ImageView fullscreen = findViewById(R.id.imageView3);
        final ConstraintLayout constraintLayout = findViewById(R.id.constraint2);
        image = findViewById(R.id.imageView2);
        final Intent reportdetails = getIntent();
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
        fullscreen.setImageBitmap(decodedImage);
         messagetext = reportdetails.getStringExtra(AdminService.MESSAGE);
         message.setText(messagetext);

        Button mark = findViewById(R.id.mark);
        mark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deletefromDb(username,imagecode,messagetext);
                Intent intent = new Intent(getApplicationContext(),AdminService.class);
                startActivity(intent);
            }
        });

        Button open = findViewById(R.id.openmap);
        open.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
                startActivity(intent);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullscreen.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
                fullScreen();
            }
        });
        fullscreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                fullscreen.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
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
        } else {
            Log.i("TAG", "Turning immersive mode mode on.");
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

    private void deletefromDb(String username,String image,String message){
        MongoClientURI connectionstring = new MongoClientURI(BuildConfig.Db_ip);
        com.mongodb.MongoClient mongoClient = new com.mongodb.MongoClient(connectionstring);
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("reports");
        BasicDBObject query = new BasicDBObject();
        query.put("username",username);
        query.put("image",image);
        query.put("message",message);
        collection.deleteOne(query);
        Toast.makeText(getApplicationContext(),"Report has been successfully removed from the database!",Toast.LENGTH_LONG).show();
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
