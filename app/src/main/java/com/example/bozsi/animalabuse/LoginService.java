package com.example.bozsi.animalabuse;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class LoginService extends AppCompatActivity {
    public static final String EMAIL = "Email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final EditText email = findViewById(R.id.emailtext);
        final EditText password = findViewById(R.id.passwordtext);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().contains("@") || !email.getText().toString().contains("."))
                    Toast.makeText(LoginService.this, "Error! Probably you mistyped your email address.", Toast.LENGTH_SHORT).show();
                else{

                checkCredentials(email.getText().toString(),password.getText().toString());
                }
            }
        });
    }

    private void checkCredentials(String email, String password){
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("users");
        Document myDoc = collection.find(eq("username",email)).first();
        if(password.equals(myDoc.getString("password")))
            Toast.makeText(getApplicationContext(), "Error! Wrong password.", Toast.LENGTH_SHORT).show();
        else{
            Intent intent = new Intent(getApplicationContext(),AnimalAbuseService.class);
            intent.putExtra(EMAIL,email);
            startActivity(intent);}
    }
}
