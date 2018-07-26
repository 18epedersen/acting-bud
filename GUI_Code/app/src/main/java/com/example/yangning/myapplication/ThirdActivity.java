package com.example.yangning.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class ThirdActivity extends AppCompatActivity {
    EditText inputSearch;
    ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("strname")!= null)
        {
            actionBar.setTitle(bundle.getString("strname"));
        }
        final String kk = bundle.getString("strname");

        final String database = bundle.getString("database");

        JsonObject jsonObject = new JsonParser().parse(database).getAsJsonObject();
        JsonObject acts = jsonObject.get("acts").getAsJsonObject();
        Log.d("acts", acts.toString());

        TreeSet<String> sceneNums  = new TreeSet<String>();
        for (Map.Entry<String, JsonElement> scene: acts.entrySet()){
            Log.d("key", scene.getKey());
            String sceneNum = scene.getKey().split(",")[1];
            Log.d("sceneNum", sceneNum.toString());
            sceneNums.add(sceneNum);
            Log.d("treeset", sceneNums.toString());
        }


        final String[] fromColumns = sceneNums.toArray(new String[sceneNums.size()]);
        Log.d("fromColumns", fromColumns.toString());

        for (int i = 0; i < fromColumns.length; i++) {
            fromColumns[i] = "Scene " + fromColumns[i];
            Log.d("entered for loop", fromColumns[i].toString());
        }

        //final String[] fromColumns = {"Scene I","Scene II","Scene III","Scene IV","Scene V"};
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
        mAdapter = new CustomListAdapter(this, R.layout.list_item, R.id.produjct_name,fromColumns);
        ListView listView = (ListView) findViewById(R.id.list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                Intent intent = new Intent(ThirdActivity.this, ThreePointFiveActivity.class);
                String message = fromColumns[position];
                intent.putExtra("strname", message + "/" + kk);
                intent.putExtra("database", database);
                startActivity(intent);
            }
        });
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ThirdActivity.this.mAdapter.getFilter().filter(cs);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }

}
