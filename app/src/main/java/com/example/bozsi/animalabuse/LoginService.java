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
        Button register = findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().contains("@") || !email.getText().toString().contains("."))
                {email.setError("Error! Probably you mistyped your email address.");return;}
                if(password.getText().toString().length()<5) {password.setError("Error! Password is min. 5 characters long!");return;}
                else{
                checkCredentials(email.getText().toString(),password.getText().toString());
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent register = new Intent(getApplicationContext(),RegisterService.class);
                startActivity(register);
            }
        });
    }

    private void checkCredentials(String email, String password){
        MongoClientURI connectionstring = new MongoClientURI("mongodb://192.168.2.156:27017");
        MongoClient mongoClient = new MongoClient(connectionstring);
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("users");
        Document myDoc = collection.find(eq("username",email)).first();
        if(myDoc == null) {Toast.makeText(getApplicationContext(), "Error! Can't find User!", Toast.LENGTH_SHORT).show();return;}
        if(!password.equals(myDoc.getString("password")))
            Toast.makeText(getApplicationContext(), "Error! Wrong password.", Toast.LENGTH_SHORT).show();
        if(myDoc.getString("role")=="admin"){
            Intent adminservice = new Intent(getApplicationContext(),AdminService.class);
            startActivity(adminservice);
        }
        else{
            Intent animalservice = new Intent(getApplicationContext(),AnimalAbuseService.class);
            animalservice.putExtra(EMAIL,email);
            startActivity(animalservice);}
    }
}
