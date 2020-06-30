package com.example.andy.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE = 100;
    EditText textWidget;
    Button sendButton;
    Button deleteText;
    RadioButton Messenger,Whatsapp,Mail,Google;
    RadioButton French,English,Spanish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textWidget = findViewById(R.id.text);
        sendButton = findViewById(R.id.sendMessage);
        deleteText = findViewById(R.id.deleteMessage);
        Messenger = (RadioButton)findViewById(R.id.messenger);
        Whatsapp = (RadioButton)findViewById(R.id.whatsapp);
        Mail = (RadioButton)findViewById(R.id.mail);
        Google = (RadioButton)findViewById(R.id.google);
        French = (RadioButton)findViewById(R.id.french);
        English = (RadioButton)findViewById(R.id.english);
        Spanish = (RadioButton)findViewById(R.id.spanish);
        ImageView speak = findViewById(R.id.speak);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //
                if(French.isChecked()) {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr");
                } else if (English.isChecked()) {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                } else if (Spanish.isChecked()) {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es");
                }
                else {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                }

                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Messenger.isChecked()) {
                    sendMessenger();
                } else if (Whatsapp.isChecked()) {
                    sendWhatsApp();
                } else if (Mail.isChecked()) {
                    sendEmail();
                } else if (Google.isChecked()) {
                    googleQuery();
                }
            }
        });

        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWidget.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textWidget.setText(result.get(0));
                }
                break;
            }
        }
    }

    public void sendMessenger() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent
                .putExtra(Intent.EXTRA_TEXT,
                        textWidget.getText().toString());
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");
        try {
            startActivity(sendIntent);
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(),"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
        }
    }

    public void sendWhatsApp() {

        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = textWidget.getText().toString();

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "some@email.address" });
        //intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
        String text = textWidget.getText().toString();
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, ""));
    }

    public void googleQuery() {
        String text = textWidget.getText().toString();
        String escapedQuery = null;
        try {
            escapedQuery = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}