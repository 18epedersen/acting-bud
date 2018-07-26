package com.example.yangning.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    EditText inputSearch;
    ArrayAdapter<String> mAdapter;
    CognitoCachingCredentialsProvider credentialsProvider;
    //Script kaka;
    PaginatedScanList<Script> result;
    HashMap<String, String> scriptChosen = new HashMap<String,String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select A Script");
        initActingClient();
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentialsProvider);

        final DynamoDBMapper mapper = new DynamoDBMapper(client);

        Runnable runnable = new Runnable() {
            public void run() {
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                result = mapper.scan(Script.class, scanExpression);
                for (Script ka : result) {
                    Log.d("result", ka.getName());
                    Log.d("resultdata", ka.getData());
                    scriptChosen.put(ka.getName(), ka.getData());

                }
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

        final ArrayList<String> Columns  = new ArrayList<String>();
        for (Script ka : result) {
            Log.d("result", ka.getName());
            if (ka.getName().equals("status")) {
                continue;
            }
            Columns.add(ka.getName());
        }
        //final String[] fromColumns = {"Romeo and Juliet","A Midsummer's night dream","Othello","Macbeth"};

         class CustomListAdapter extends ArrayAdapter <String> {
            public CustomListAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
                super(context, resource, textViewResourceId, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {


                View view =  super.getView(position, convertView, parent);
                final Typeface tvFont = Typeface.createFromAsset(getAssets(), "Playfair.ttf");

                TextView tv = (TextView) view.findViewById(R.id.produjct_name);
                tv.setTypeface(tvFont);

                return view;
            }


        }

        final String[] fromColumns = Columns.toArray(new String[Columns.size()]);
        mAdapter = new CustomListAdapter(this, R.layout.list_item, R.id.produjct_name,fromColumns);
        ListView listView = (ListView) findViewById(R.id.list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                String message = fromColumns[position];
                intent.putExtra("strname", message);
                intent.putExtra("database", scriptChosen.get(message));
                Log.d("choosenscript", scriptChosen.get(message));
                startActivity(intent);
            }
        });




        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MainActivity.this.mAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });


        Typeface font = Typeface.createFromAsset(getAssets(),"Playfair.ttf");
        TextView tv=(TextView) findViewById(R.id.inputSearch);
        tv.setTypeface(font);
        Button tv1=(Button) findViewById(R.id.upload);
        tv1.setTypeface(font);
    }


    void initActingClient() {
        // Initialize the Amazon Cognito credentials provider.
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:78edd1a1-c8ab-4c09-b84f-3b1ed7101cfe", // Identity Pool ID
                Regions.US_EAST_1
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }
    public void onClick(View view)
    {
        startActivity(new Intent("android.intent.SecondActivity"));
    }



}
