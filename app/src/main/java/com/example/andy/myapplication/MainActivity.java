package com.example.andy.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    Button endRecording,sendButton,deleteAll,deleteLastSentence;
    RadioButton Messenger,Whatsapp,Mail,Google;
    RadioButton French,English,Spanish;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    boolean stopListering = false;
    static ArrayList<String> textToSend = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textWidget = findViewById(R.id.text);
        endRecording = findViewById(R.id.endRecording);
        sendButton = findViewById(R.id.sendMessage);
        deleteAll = findViewById(R.id.deleteMessage);
        deleteLastSentence = findViewById(R.id.deleteLastSentence);
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

                mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,20000000);

                if(French.isChecked()) {
                    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr");
                } else if (English.isChecked()) {
                    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                } else if (Spanish.isChecked()) {
                    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es");
                }
                else {
                    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                }

                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Need to speak");

                mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle bundle) {
                        Log.i("", "onReadyForSpeech");
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        Log.i("", "onBeginningOfSpeech");

                    }

                    @Override
                    public void onRmsChanged(float v) {
                        Log.i("", "onRmsChanged");

                    }

                    @Override
                    public void onBufferReceived(byte[] bytes) {
                        Log.i("", "onBufferReceived");

                    }

                    @Override
                    public void onEndOfSpeech() {
                        Log.i("", "onEndOfSpeech");
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

                    }

                    public void onError(int errorCode)
                    {
                        Log.i("", "onError");
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    }

                    @Override
                    public void onResults(Bundle bundle) {
                        Log.i("", "onResults");
                        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        String s = "";
                        for (String result:matches)
                            //s += result + "\n";
                            s = result;

                        if (Character.isLetter((char) s.charAt(s.length()-1)))
                            s = s + ". ";
                        else
                            s = s + " ";

                        s = s.substring(0, 1).toUpperCase() + s.substring(1);
                        textToSend.add(s);
                        textWidget.setText(arrayToString(textToSend));
                    }

                    @Override
                    public void onPartialResults(Bundle bundle) {
                        Log.i("", "onPartialResults");

                    }

                    @Override
                    public void onEvent(int i, Bundle bundle) {
                        Log.i("", "onEvent");

                    }
                });
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                /*try {
                    startActivityForResult(mSpeechRecognizerIntent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        endRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpeechRecognizer.stopListening();
                mSpeechRecognizer.destroy();
                mSpeechRecognizer.cancel();
                stopListering = true;
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
                } else if (Google.isChecked()) { // ACTION_WEB_SEARCH?
                    googleQuery();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please select where to send this",Toast.LENGTH_SHORT);
                }
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWidget.setText("");
                for (int i=textToSend.size()-1;i>=1;i++)
                    textToSend.remove(i);
                mSpeechRecognizer.stopListening();
                mSpeechRecognizer.destroy();
                mSpeechRecognizer.cancel();
                Toast.makeText(getApplicationContext(),"Click again on the mic to record yourself",Toast.LENGTH_SHORT);
            }
        });

        deleteLastSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSend.remove(textToSend.size()-1);
                textWidget.setText(arrayToString(textToSend));
                Toast.makeText(getApplicationContext(),"Click again on the mic to record yourself",Toast.LENGTH_SHORT);
            }
        });
    }

    public String arrayToString(ArrayList<String> text) {
        String concat = "";
        for (int i=0;i<=text.size()-1;i++)
            concat = concat + text.get(i);
        return concat;
    }

    public void sendMessenger() {
        Intent sendIntent = new Intent();
        String text = textWidget.getText().toString();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,text);
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
        Intent mSpeechRecognizerIntent = new Intent(Intent.ACTION_SEND);
        mSpeechRecognizerIntent.setType("plain/text");
        //mSpeechRecognizerIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "some@email.address" });
        //mSpeechRecognizerIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
        String text = textWidget.getText().toString();
        mSpeechRecognizerIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(mSpeechRecognizerIntent, ""));
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
        Intent mSpeechRecognizerIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(mSpeechRecognizerIntent);
    }

}