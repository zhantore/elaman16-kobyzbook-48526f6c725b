package kz.qobyzbook.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kz.qobyzbook.R;

public class SplashActivity extends AppCompatActivity {


    private static final String TAG = "SplashActivity";
    private static final long INTERVAL2 = 3000;
    Handler hand = new Handler();
    TextView textView;
    ImageView imageView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView = (TextView)findViewById(R.id.textView13);
        imageView = (ImageView)findViewById(R.id.imageView2);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "kk");
        if (lang.equals("kk")) {
            textView.setText("Ұстазым – \n" +
                    "Әбдіманап Жұмабекұлына \n" +
                    "мың алғыс!\n");
            imageView.setImageResource(R.drawable.kb_kaz);
        } else {
            textView.setText("I express my gratitude to my teacher \nAbdimanap Zhumabekuly!");
            imageView.setImageResource(R.drawable.kb_en);
        }

        if ( !isOnline() ){
            Toast.makeText(this, "Подключите интернет !", Toast.LENGTH_LONG).show();
        } else {
            registerInBackground();
        }
    }

    private void registerInBackground() {
        Log.d(TAG, "registerInBackground()");
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    Log.d(TAG,"doInBackground");
                    String msg = "";
                    try {
                            hand.postDelayed(run1, INTERVAL2);

                    } catch (Exception ex) {
                        msg = "Error :" + ex.getMessage();
                    }

                    Log.d(TAG,msg);
                    return msg;
                }

            }.execute(null, null, null);
    }

    Runnable run1 = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Runnable()");
            startActivity(new Intent(SplashActivity.this, DMPlayerBaseActivity.class));
            finish();

        }
    };

    protected boolean isOnline() {
        Log.d(TAG, "isOnline()");
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);

        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return  true;
        }
    }
}
