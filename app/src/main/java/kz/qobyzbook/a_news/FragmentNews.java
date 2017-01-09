package kz.qobyzbook.a_news;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kz.qobyzbook.A_AboutQobyz.Qobyz;
import kz.qobyzbook.AppController;
import kz.qobyzbook.B_Persons.PersonAdapter;
import kz.qobyzbook.R;

/**
 * Created by zhan on 11/29/16.
 */

public class FragmentNews extends Fragment {

    private  final String URL = "http://api.kobyzbook.kz/api/Novosti";
    private static final String TAG = "FragmentNews";
    public static final String TITLE_KZ = "titlekz"; // json - nan alasyn
    public static final String TITLE_EN = "titleen"; // json - nan alasyn
    public static final String CONTENT_KZ = "contentkz"; // json - nan alasyn
    public static final String CONTENT_EN = "contenten"; // json - nan alasyn
    TextView news;

    //Variables
    ArrayList<Qobyz> qobyzArrayList;
    PersonAdapter mAdapter;

    //Components
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.b_old_persons, null);
        news = (TextView)rootview.findViewById(R.id.textView14);
        setHasOptionsMenu(true);

        //Initialising components...
        qobyzArrayList = new ArrayList<>();
        rl_downloading = (RelativeLayout) rootview.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) rootview.findViewById(R.id.rl_internet);
        btn_update = (Button) rootview.findViewById(R.id.btn_update);

        //Recycler View
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PersonAdapter(getActivity(), qobyzArrayList, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        //fill content from Server
        fillContent();


        //Button click hundler...
        btn_update.setOnClickListener(onClickListener);

        return rootview;
    }

    //Fill list with data from Server using JSON
    void fillContent() {
        Log.d(TAG, "fillContent()");
        if (isOnline()) {
            rl_downloading.setVisibility(View.VISIBLE);
            rl_internet.setVisibility(View.INVISIBLE);
            if(qobyzArrayList.isEmpty()) {
                makeJsonObjectRequest();
            }
        }
        else {
            rl_downloading.setVisibility(View.INVISIBLE);
            rl_internet.setVisibility(View.VISIBLE);
        }
    }

    private void makeJsonObjectRequest() {
        Log.d(TAG, "makeJsonObjectRequest()");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String lang = preferences.getString("lang", "kk");



        JsonArrayRequest addReq = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        rl_downloading.setVisibility(View.INVISIBLE);

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Qobyz qobyz = new Qobyz();

                                if (lang.equals("kk")) {
                                    qobyz.setName(obj.getString(TITLE_KZ));
                                    qobyz.setDescription(obj.getString(CONTENT_KZ));
                                    qobyz.setPhoto(obj.getString("photo"));
                                    qobyzArrayList.add(qobyz);
                                }
                                else  if (lang.equals("en")) {
                                    qobyz.setName(obj.getString(TITLE_EN));
                                    qobyz.setDescription(obj.getString(CONTENT_EN));
                                    qobyz.setPhoto(obj.getString("photo"));
                                    qobyzArrayList.add(qobyz);
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(addReq);
    }

    //Click handler
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
