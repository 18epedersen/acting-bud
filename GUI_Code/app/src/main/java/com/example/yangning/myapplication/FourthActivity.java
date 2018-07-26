package com.example.yangning.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FourthActivity extends AppCompatActivity {
    Button next;
    Button back;
    Button flip;
    String kk;
    Integer beginLine;
    Integer endLine;
    Integer counter;
    TextView lines;
    JsonArray scriptLines;
    String ourCharacter;
    Button prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Bundle bundle = getIntent().getExtras();
        kk = bundle.getString("strname");
        final String database = bundle.getString("database");
        TextView tv=(TextView) findViewById(R.id.actscene);
        tv.setText("Practicing as "+kk.split("/")[0] + " in " + kk.split("/")[1] + ", " +kk.split("/")[2] + ", " + kk.split("/")[3]);
        next = (Button) findViewById(R.id.next4);
        prev = (Button) findViewById(R.id.prev);
        back = (Button) findViewById(R.id.back4);
        flip = (Button) findViewById(R.id.flip);
        lines = (TextView) findViewById(R.id.line);
        lines.setMovementMethod(new ScrollingMovementMethod());

        if (bundle.getString("strname") != null) {
            actionBar.setTitle(bundle.getString("strname"));

        }

        ourCharacter = kk.split("/")[0].toLowerCase();
        Log.d("ourCharacter", ourCharacter);

        String scene = kk.split("/")[1].substring(6);
        String act = kk.split("/")[2].substring(4);
        String actScene = act + "," + scene;
        Log.d("actScene", actScene);

        JsonObject jsonObject = new JsonParser().parse(database).getAsJsonObject();
        scriptLines = jsonObject.get("lines").getAsJsonArray();
        Log.d("scriptLines", scriptLines.toString());

        JsonObject actSceneKey = jsonObject.get("acts").getAsJsonObject();
        Log.d("actSceneKey", actSceneKey.toString());
        JsonArray lineNums = actSceneKey.get(actScene).getAsJsonArray();
        Log.d("lineNums", lineNums.toString());
        beginLine = lineNums.get(0).getAsInt();
        endLine = lineNums.get(1).getAsInt();

        counter = beginLine;


        final JsonArray scriptLines = jsonObject.get("lines").getAsJsonArray();
        Log.d("lines", scriptLines.toString());

        JsonObject currentLine = scriptLines.get(counter).getAsJsonObject();
        String currentChar = currentLine.get("character").getAsString();
        String charLine = currentLine.get("line").getAsString();
        lines.setText(currentChar + ": " + charLine);
        //counter++;
        flip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                counter ++;
                Log.d("counter", counter.toString());
                if (counter <= endLine) {
                    JsonObject currentLine = scriptLines.get(counter).getAsJsonObject();
                    String currentChar = currentLine.get("character").getAsString();
                    Log.d("currentLine", currentLine.toString());
                    String charLine = currentLine.get("line").getAsString();
                    if (currentChar.equals(ourCharacter)) {
                        lines.setText(currentChar + ": " + charLine);
                    } else {
                        lines.setText("Your character is not next to speak. Click Next to proceed.");
                    }
                } else {
                    lines.setText("Great job! You are done practicing as " + ourCharacter);
                }
                counter--;
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.activity_threepointfive);
                Intent intent = new Intent(FourthActivity.this, ChooseActivity.class);
                intent.putExtra("strname", kk);
                intent.putExtra("database",database);
                startActivity(intent);
            }
        });
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                counter ++;
                if (counter > endLine) {
                    lines.setText("Great job! You are done practicing as " + ourCharacter);
                    return;
                }
                JsonObject currentLine = scriptLines.get(counter).getAsJsonObject();
                String currentChar = currentLine.get("character").getAsString();
                String charLine = currentLine.get("line").getAsString();
                boolean end = false;
                while (currentChar.equals(ourCharacter)) {
                    if (counter <= endLine) {
                        counter ++;
                        currentLine = scriptLines.get(counter).getAsJsonObject();
                        currentChar = currentLine.get("character").getAsString();
                        charLine = currentLine.get("line").getAsString();
                    } else {
                        lines.setText("Great job! You are done practicing as " + ourCharacter);
                        end = true;
                        break;
                    }
                }
                if (!end) {
                    lines.setText(currentChar + ": " + charLine);

                }

//                if (counter <= endLine) {
//                    currentLine = scriptLines.get(counter).getAsJsonObject();
//                    String currentChar = currentLine.get("character").getAsString();
//                    String charLine = currentLine.get("line").getAsString();
//                    lines.setText(currentChar + ": " + charLine);
//                    counter++;
//                } else {
//                    lines.setText("Great job! You are done practicing as " + ourCharacter);
//                }
            }
        });

        prev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldCounter = counter;
                counter --;
                if (counter < beginLine) {
                    counter = oldCounter;
                    return;

                }
                JsonObject currentLine = scriptLines.get(counter).getAsJsonObject();
                String currentChar = currentLine.get("character").getAsString();
                String charLine = currentLine.get("line").getAsString();
                boolean end = false;
                while (currentChar.equals(ourCharacter)) {
                    counter --;
                    if (counter >= beginLine ) {
                        currentLine = scriptLines.get(counter).getAsJsonObject();
                        currentChar = currentLine.get("character").getAsString();
                        charLine = currentLine.get("line").getAsString();
                    } else {
                        counter = oldCounter;
                        currentLine = scriptLines.get(counter).getAsJsonObject();
                        currentChar = currentLine.get("character").getAsString();
                        charLine = currentLine.get("line").getAsString();
                        break;
                    }
                }
                if (!end) {
                    lines.setText(currentChar + ": " + charLine);

                }

//                if (counter <= endLine) {
//                    currentLine = scriptLines.get(counter).getAsJsonObject();
//                    String currentChar = currentLine.get("character").getAsString();
//                    String charLine = currentLine.get("line").getAsString();
//                    lines.setText(currentChar + ": " + charLine);
//                    counter++;
//                } else {
//                    lines.setText("Great job! You are done practicing as " + ourCharacter);
//                }
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}