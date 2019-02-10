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
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class RegisterService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrationlayout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final EditText name = findViewById(R.id.nametext);
        final EditText email = findViewById(R.id.emailtext);
        final EditText password = findViewById(R.id.passwordtext);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText()==null) {name.setError("You can't leave this field empty!"); return;}
                if(!name.getText().toString().contains(" ")) {name.setError("You should write your full name!"); return;}
                if(email.getText()==null) {email.setError("You can't leave this field empty!"); return;}
                if(!email.getText().toString().contains("@")){email.setError("You mistyped your email address!"); return;}
                if(password.getText().length()<5) {password.setError("Your password must contain at least 5 characters"); return;}
                try {
                    register(name.getText().toString(),email.getText().toString(),password.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getApplicationContext(), LoginService.class);
                startActivity(login);
            }
        });
    }

    private void register(String name,String email,String password) throws NoSuchAlgorithmException {
        //TODO átírni gradleproperties-be címet collections stb + tesztelni a titkosítást
        MongoClientURI connectionstring = new MongoClientURI("mongodb://192.168.2.156:27017");
        MongoClient mongoClient = new MongoClient(connectionstring);
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("users");
        Document myDoc = collection.find(eq("username",email)).first();
        if(myDoc==null)
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            String myHash = Arrays.toString(digest).toUpperCase();
            myDoc= new Document("name",name);
            myDoc
                    .append("username",email)
                    .append("password",myHash)
                    .append("role", "admin");
            collection.insertOne(myDoc);
            Toast.makeText(getApplicationContext(),"User has been successfully registered!",Toast.LENGTH_SHORT).show();
            Intent login = new Intent(getApplicationContext(),LoginService.class);
            startActivity(login);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"User already exists,please try to log in!",Toast.LENGTH_SHORT).show();
        }
    }
}