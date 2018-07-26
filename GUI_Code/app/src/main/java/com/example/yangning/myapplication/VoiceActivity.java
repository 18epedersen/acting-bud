package com.example.yangning.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.Context;
import android.app.SearchManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.gson.JsonObject;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

public class VoiceActivity extends AppCompatActivity {
    String character;
    int sceneNum;
    int actNum;
    String Script;
    CognitoCachingCredentialsProvider credentialsProvider;
    Script result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direct_to_voice);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("strname")!= null)
        {
            actionBar.setTitle(bundle.getString("strname"));

        }

        final String kk = bundle.getString("strname");
        character = kk.split("/")[0];
        sceneNum = Integer.parseInt(kk.split("/")[1].substring(6));
        actNum = Integer.parseInt(kk.split("/")[2].substring(4));
        Script =kk.split("/")[3];

        //Log.d("kk", kk);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("scriptName", Script);
        jsonObject.addProperty("actNum", actNum);
        jsonObject.addProperty("sceneNum", sceneNum);
        jsonObject.addProperty("roleName", character);

        Log.d("jsonobject", jsonObject.toString());

        initActingClient();
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentialsProvider);

        final DynamoDBMapper mapper = new DynamoDBMapper(client);

        Runnable runnable = new Runnable() {
            public void run() {
                result = mapper.load(Script.class, "status");
                result.setData(jsonObject.toString());
                mapper.save(result);
                //DynamoDB calls go here
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final String database = bundle.getString("database");
        TextView TextView = (TextView) findViewById(R.id.question);
        TextView.setText("Start practicing your role as "+ kk.split("/")[0]+ " with Alexa");
        Button back = (Button) findViewById(R.id.back4);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.activity_threepointfive);
                Intent intent = new Intent(VoiceActivity.this, ChooseActivity.class);
                intent.putExtra("strname",kk);
                intent.putExtra("database", database);
                startActivity(intent);
            }
        });
    }
    void initActingClient() {
        // Initialize the Amazon Cognito credentials provider.
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:78edd1a1-c8ab-4c09-b84f-3b1ed7101cfe", // Identity Pool ID
                Regions.US_EAST_1
        );
    }

}
