package kz.qobyzbook.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import kz.qobyzbook.R;

public class SplashActivity extends AppCompatActivity {


    private static final String TAG = "SplashActivity";
    private static final long INTERVAL2 = 2000;
    Handler hand = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
