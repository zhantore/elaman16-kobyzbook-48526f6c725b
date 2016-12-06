package kz.qobyzbook.H_Project;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import kz.qobyzbook.AppController;
import kz.qobyzbook.R;
import kz.qobyzbook.utility.LogWriter;


/**
 * Created by Orenk on 27.09.2016.
 */

public class FragmentProject extends Fragment {
    private  final String URL = "http://api.kobyzbook.kz/api/Autors/1";
    private static final String TAG = "FragmentProject";
    //Variables

    ArrayList<String> qobyzList;
    ProjectModel project = new ProjectModel();

    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;

    LinearLayout ll_author_project;
    TextView tv_author,tv_project;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.h_fragment_project, null);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setTitle(getResources().getString(R.string.project_info));

        //Initialising components...
        qobyzList = new ArrayList<String>();

        ll_author_project = (LinearLayout) view.findViewById(R.id.ll_auhor_project);
        ll_author_project.setVisibility(View.INVISIBLE);
        tv_author = (TextView) view.findViewById(R.id.author);
        tv_author.setOnClickListener(onClickListener);
        tv_project = (TextView) view.findViewById(R.id.project);
        tv_project.setOnClickListener(onClickListener);

        rl_downloading = (RelativeLayout) view.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) view.findViewById(R.id.rl_internet);
        btn_update = (Button) view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(onClickListener);


        fillContent();

        return view;

    }


    //Fill list with data from Server using JSON

    void fillContent() {
        Log.d(TAG, "onCreateView()");
        if (isOnline()) {
            rl_downloading.setVisibility(View.VISIBLE);
            rl_internet.setVisibility(View.INVISIBLE);
                makeJsonObjectRequest();

        }
        else {
            rl_downloading.setVisibility(View.INVISIBLE);
            rl_internet.setVisibility(View.VISIBLE);
        }
    }

    private void makeJsonObjectRequest() {

        Log.d(TAG, "makeJsonObjectRequest()");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String lang = preferences.getString("lang", "kk");

        JsonObjectRequest addReq = new JsonObjectRequest(URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {

                        rl_downloading.setVisibility(View.INVISIBLE);
                        ll_author_project.setVisibility(View.VISIBLE);

                            try {
                                if (lang.equals("kk")){
                                    project.setOmirbayan(obj.getString("omirbayankz"));
                                    project.setOnerbayan(obj.getString("onerbayankz"));
                                    project.setAniktama(obj.getString("aniktamakz"));
                                    project.setEnbek(obj.getString("enbekkz"));
                                    project.setGilim(obj.getString("gilimikz"));
                                    project.setZhetistik(obj.getString("zhetistikkz"));
                                    project.setMail(obj.getString("mailkz"));
                                    project.setShigarma(obj.getString("shigarmakz"));
                                    project.setZhoba(obj.getString("zhobakz"));
                                }
                                else {
                                    project.setOmirbayan(obj.getString("omirbayanen"));
                                    project.setOnerbayan(obj.getString("onerbayanen"));
                                    project.setAniktama(obj.getString("aniktamakz"));
                                    project.setEnbek(obj.getString("enbeken"));
                                    project.setGilim(obj.getString("gilimien"));
                                    project.setZhetistik(obj.getString("zhetistiken"));
                                    project.setMail(obj.getString("mailkz"));
                                    project.setShigarma(obj.getString("shigarmaen"));
                                    project.setZhoba(obj.getString("zhobaen"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(addReq);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickListener()");
            if (v.getId()==R.id.btn_update){
                if (isOnline())
                    fillContent();
                else
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_connect), Toast.LENGTH_SHORT).show();
            }

            if (v.getId()==R.id.author){
                try {

                    Intent mIntent = new Intent(getActivity(), ProjectPage.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("id", 0);
                    mBundle.putString("name", getActivity().getResources().getString(R.string.project_author_life));
                    mBundle.putString("omir",project.getOmirbayan());
                    mBundle.putString("oner",project.getOnerbayan());
                    mBundle.putString("zhetistik",project.getZhetistik());
                    mBundle.putString("enbek",project.getEnbek());
                    mBundle.putString("gilim",project.getGilim());
                    mBundle.putString("shigarma",project.getShigarma());
                    mBundle.putString("anyktama",project.getAniktama());
                    mBundle.putString("mail",project.getMail());
                    mIntent.putExtras(mBundle);
                    (getActivity()).startActivity(mIntent);
                    (getActivity()).overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogWriter.info("intent", e.toString());
                }
            }

            if (v.getId()==R.id.project){
                try {
                    Intent mIntent = new Intent(getActivity(), ProjectPage.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("id", 1);
                    mBundle.putString("name", getActivity().getResources().getString(R.string.project_info));
                    mBundle.putString("zhoba",project.getZhoba());
                    mIntent.putExtras(mBundle);
                    (getActivity()).startActivity(mIntent);
                    (getActivity()).overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogWriter.info("intent", e.toString());
                }
            }
        }
    };

    public boolean isOnline() {
        Log.d(TAG, "isOnline()");
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
