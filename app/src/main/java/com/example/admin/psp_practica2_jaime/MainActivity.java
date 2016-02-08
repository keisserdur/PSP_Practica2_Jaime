package com.example.admin.psp_practica2_jaime;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.admin.psp_practica2_jaime.chatterbox.ChatterBot;
import com.example.admin.psp_practica2_jaime.chatterbox.ChatterBotFactory;
import com.example.admin.psp_practica2_jaime.chatterbox.ChatterBotSession;
import com.example.admin.psp_practica2_jaime.chatterbox.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private  ChatterBotFactory factory;
    private ChatterBot bot1,bot2;
    private ChatterBotSession bot1session,bot2session;
    private TextView tv1,tv2;
    private TextToSpeech tts;
    private final int HABLAR=1,CTE=2;
    private ArrayList<String> textos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, CTE);
    }

    public void init (){
        factory = new ChatterBotFactory();
        tv1= (TextView) findViewById(R.id.textView);
        tv2= (TextView) findViewById(R.id.textView2);
    }

    public void hablar(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, HABLAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CTE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
                tts.setLanguage(Locale.getDefault());
            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        if (requestCode == HABLAR) {
            try {
                textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tv1.setText(textos.get(textos.size() - 1));
                Hebra t = new Hebra();
                String mifrase = tv1.getText().toString();
                t.execute(mifrase);
                tv2.setText(mifrase);
            }catch (NullPointerException e){
                tv2.setText("No te he entendido bien");
            }
        }
    }

    @Override
    public void onInit(int status) {

    }

    public class Hebra extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String mifrase = params[0];
            try {
                bot1 = factory.create(ChatterBotType.CLEVERBOT);
                bot1session = bot1.createSession();
                return bot1session.think(mifrase);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String out) {
            tts.setLanguage(Locale.getDefault());
            tts.setPitch((float) 0.8);
            tts.setSpeechRate((float) 1.1);
            tts.speak(out, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
