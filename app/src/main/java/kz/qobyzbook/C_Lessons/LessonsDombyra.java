package kz.qobyzbook.C_Lessons;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import kz.qobyzbook.R;


/**
 * Created by Orenk on 22.08.2016.
 */
public class LessonsDombyra extends Fragment {

    private final String URL_KZ = "http://zhaksy-adam.azurewebsites.net/api/Requisitions/GetAll?id=2&page=1&count=10"; // myrzadan alasyn
    private final String URL_EN = "http://zhaksy-adam.azurewebsites.net/api/Requisitions/GetAll?id=3&page=1&count=10";

    public static final String NAME = "FullName"; // json - nan alasyn
    public static final String ID = "ID";

    //Variables
    ArrayList<Qobyz> qobyzArrayList;
    //QobyzAdapter mAdapter;

    //Components
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.c_lessons_song, container, false);
        setHasOptionsMenu(true);

        /*//Initialising components...
        qobyzArrayList = new ArrayList<>();
        rl_downloading = (RelativeLayout) view.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) view.findViewById(R.id.rl_internet);
        btn_update = (Button) view.findViewById(R.id.btn_update);

        //Recycler View
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new QobyzAdapter(getActivity(), qobyzArrayList, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        //fill content from Server
        fillContent();


        //Button click hundler...
        btn_update.setOnClickListener(onClickListener);*/

        return view;
    }

    //Fill list with data from Server using JSON
    void fillContent(){
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String lang = preferences.getString("lang", "kk");
        String URL;
        if (lang.equals("kk")){
            URL = URL_KZ;
        }
        else URL = URL_EN;

        JsonArrayRequest addReq = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        rl_downloading.setVisibility(View.INVISIBLE);

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Qobyz add = new Qobyz();

                                add.setName(obj.getString(NAME));

                                qobyzArrayList.add(add);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //mAdapter.notifyDataSetChanged();

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
            if (v.getId()==R.id.btn_update){
                if (isOnline())
                    fillContent();
                else
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_connect), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}

