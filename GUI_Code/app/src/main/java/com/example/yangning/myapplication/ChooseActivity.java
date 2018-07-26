package com.example.yangning.myapplication;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Select The Way To Practice");
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }
        final String kk = bundle.getString("strname");
        final String database = bundle.getString("database");
        Button voice = (Button) findViewById(R.id.button2);
        Button flashcard = (Button) findViewById(R.id.button3);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, VoiceActivity.class);
                intent.putExtra("strname",kk);
                intent.putExtra("database", database);
                startActivity(intent);
            }
        });
        flashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, FourthActivity.class);
                intent.putExtra("strname",kk);
                intent.putExtra("database", database);
                startActivity(intent);
            }
        });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
