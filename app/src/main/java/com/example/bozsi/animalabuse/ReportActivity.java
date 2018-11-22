package com.example.bozsi.animalabuse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView mImageView;
    String username;
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
            Report report = new Report();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[]b = baos.toByteArray();
                String image = Base64.encodeToString(b , Base64.DEFAULT);
            report.setUsername("Guest");
            report.setImage(image);
            report.setLongitude(intent.getStringExtra(AnimalAbuseService.LONGITUDE));
            report.setLatitude(intent.getStringExtra(AnimalAbuseService.LATITUDE));
            report.setMessage(message.getText().toString());

            MongoLabSaveReport tsk = new MongoLabSaveReport();
            tsk.execute(report);

            Toast.makeText(getApplicationContext(),"Report sent to the organization!",Toast.LENGTH_SHORT).show();
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

    final class MongoLabSaveReport extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            Report report = (Report) params[0];
            Log.d("Report", ""+report.getLatitude());

            try {
                ReportController rc = new ReportController();
                URL url = new URL(rc.buildReportsSaveURL());

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("POST"); //put to update post do insert new
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type",
                        "application/json");
                connection.setRequestProperty("Accept", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(
                        connection.getOutputStream());

                osw.write(rc.createReport(report));
                Log.d("Debug",osw.toString());
                osw.flush();
                osw.close();

                if(connection.getResponseCode() <205)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            } catch (Exception e) {
                e.getMessage();
                Log.d("Got error", e.getMessage());
                return false;
            }
        }
    }
}
