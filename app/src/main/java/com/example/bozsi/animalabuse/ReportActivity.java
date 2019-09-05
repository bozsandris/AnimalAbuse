package com.example.bozsi.animalabuse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;

import java.io.ByteArrayOutputStream;

public class ReportActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView mImageView;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_service);
        Button picture = findViewById(R.id.picture);
        Button report = findViewById(R.id.report);
        final EditText message = findViewById(R.id.editText);
        mImageView = findViewById(R.id.imageView);
        final Intent intent = getIntent();
        picture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dispatchTakePictureIntent();
            }
        });
        report.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            if(message.getText().length()==0) {message.setError("You must type at least a little message.");return;}
            if(imageBitmap==null) {Toast.makeText(getApplicationContext(),"Please take a picture!",Toast.LENGTH_LONG).show();return;}
            if(message.length()>50) {message.setError("Try to make your message shorter.50 characters should be enough to express yourself.");return;}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[]b = baos.toByteArray();
                String image = Base64.encodeToString(b , Base64.DEFAULT);
            saveToDb(intent.getStringExtra(AnimalAbuseService.USERNAME),image,intent.getStringExtra(AnimalAbuseService.LONGITUDE),
                        intent.getStringExtra(AnimalAbuseService.LATITUDE),message.getText().toString());
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private void saveToDb(String username,String image,String longitude,String latitude,String message){
        MongoClientURI connectionstring = new MongoClientURI(BuildConfig.Db_ip);
        com.mongodb.MongoClient mongoClient = new com.mongodb.MongoClient(connectionstring);
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<org.bson.Document> collection = database.getCollection("reports");
        Document myDoc = new Document();
        Position coordinates = new Position(Double.valueOf(longitude),Double.valueOf(latitude));
        Point location = new Point(coordinates);
        database.getCollection("reports").createIndex(new BasicDBObject("loc","2dsphere"));
        myDoc.append("username",username)
         .append("image",image)
         .append("loc", location)
         .append("message",message);
        collection.insertOne(myDoc);
        Toast.makeText(getApplicationContext(),"Report sent to the organization! Thanks for your contribution!",Toast.LENGTH_LONG).show();
        Intent animalabuse = new Intent(getApplicationContext(),AnimalAbuseService.class);
        startActivity(animalabuse);
    }
}
