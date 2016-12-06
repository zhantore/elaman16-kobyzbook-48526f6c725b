package kz.qobyzbook.I_Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import kz.qobyzbook.ApplicationDMPlayer;
import kz.qobyzbook.R;
import kz.qobyzbook.activities.DMPlayerBaseActivity;

/**
 * Created by Orenk on 22.08.2016.
 */
public class DialogLanguage extends Dialog {

    SharedPreferences preferences;

    public DialogLanguage(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getOwnerActivity());
        String lang = preferences.getString("lang", "kk");

        AlertDialog.Builder builder = new AlertDialog.Builder(getOwnerActivity());
        if (lang.equals("kk")) {
            builder.setItems(R.array.language_array_kz, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        preferences.edit().putString("lang", "en").commit();
                        ApplicationDMPlayer.setLocale(getOwnerActivity());
                        restartActivity();
                    } else {
                        preferences.edit().putString("lang", "kk").commit();
                        ApplicationDMPlayer.setLocale(getOwnerActivity());
                        restartActivity();
                    }
                }
            });
        } else {
            builder.setItems(R.array.language_array_en, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        preferences.edit().putString("lang", "en").commit();
                        ApplicationDMPlayer.setLocale(getOwnerActivity());
                        restartActivity();
                    } else {
                        preferences.edit().putString("lang", "kk").commit();
                        ApplicationDMPlayer.setLocale(getOwnerActivity());
                        restartActivity();
                    }
                }
            });
        }
        builder.create();
    }

//    @Override
//    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        return builder.create();
//    }

    private void restartActivity() {
        Intent intent = new Intent(getOwnerActivity(), DMPlayerBaseActivity.class);
        getOwnerActivity().startActivity(intent);
        getOwnerActivity().finish();
    }
}
