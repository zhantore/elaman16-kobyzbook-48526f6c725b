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

import kz.qobyzbook.AppController;
import kz.qobyzbook.R;
import kz.qobyzbook.activities.DMPlayerBaseActivity;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.models.SongDetail;
import kz.qobyzbook.phonemidea.PhoneMediaControl;


/**
 * Created by Orenk on 22.08.2016.
 */
public class LessonsQobyz extends Fragment {
    private final String URL = "http://api.kobyzbook.kz/api/subjects"; // myrzadan alasyn


    public static final String ID = "id";
    public static final String VIDEO = "video";
    public static final String AUDIO = "audio";
    public static final String NOTE = "note";
    public static final String NAME_KZ = "namekz";
    public static final String MIF_KZ = "mifkz";
    public static final String UKAZANIE_KZ = "ukazaniekz";
    public static final String LITER_KZ = "literkz";
    public static final String NAME_EN = "nameen";
    public static final String MIF_EN = "mifen";
    public static final String UKAZANIE_EN = "ukazen";
    public static final String LITER_EN = "liten";

    //Variables
    ArrayList<Lesson> qobyzArrayList;
    ArrayList<SongDetail> songArray;
    LessonAdapter mAdapter;
    public static Context context;

    //Components
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;
    TextView empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.c_lessons_qobyz, container, false);

        setHasOptionsMenu(true);
        context  = getActivity();
        //Initialising components...
        qobyzArrayList = new ArrayList<>();
        songArray = new ArrayList<>();
        rl_downloading = (RelativeLayout) view.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) view.findViewById(R.id.rl_internet);
        btn_update = (Button) view.findViewById(R.id.btn_update);
        empty = (TextView)view.findViewById(R.id.emptyText);

        //Recycler View
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LessonAdapter(getActivity(), qobyzArrayList, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        //fill content from Server
        fillContent();


        //Button click hundler...
        btn_update.setOnClickListener(onClickListener);

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
        final String lang = preferences.getString("lang", "kk");

        JsonArrayRequest addReq = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        rl_downloading.setVisibility(View.INVISIBLE);

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Lesson lesson = new Lesson();

                                lesson.setId(obj.getInt(ID));
                                lesson.setVideo(obj.getString(VIDEO));
                                lesson.setNote(convertURL(obj.getString(NOTE)));
                                lesson.setAudio(convertURL(obj.getString(AUDIO)));
                                if (lang.equals("kk")){
                                    lesson.setName(obj.getString(NAME_KZ));
                                    lesson.setMif(obj.getString(MIF_KZ));
                                    lesson.setUkazanie(obj.getString(UKAZANIE_KZ));
                                    lesson.setLiter(obj.getString(LITER_KZ));
                                    qobyzArrayList.add(lesson);
                                }
                                else {
                                    lesson.setName(obj.getString(NAME_EN));
                                    lesson.setMif(obj.getString(MIF_EN));
                                    lesson.setUkazanie(obj.getString(UKAZANIE_EN));
                                    lesson.setLiter(obj.getString(LITER_EN));
                                    qobyzArrayList.add(lesson);
                                }
                                empty.setVisibility(View.GONE);

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
                empty.setVisibility(View.VISIBLE);
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

    private String convertURL(String url){
        if (url!=null){
            url = url.replace("~/", "/");
            url = url.replace(" ", "%20");
            url = "http://admin.kobyzbook.kz"+url;
            return url;}
        else return null;
    }

    /*public static void loadMusic(int position){

        *//*PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
             //   songList = songsList_;
              //  mAllSongsListAdapter.notifyDataSetChanged();
            }
        });
        mPhoneMediaControl.loadMusicList(context, -1, PhoneMediaControl.SonLoadFor.LessonAudio, url);*//*

        SongDetail mDetail = .get(position);
        ((DMPlayerBaseActivity) getActivity()).loadSongsDetails(mDetail);

        if (mDetail != null) {
            if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                MediaController.getInstance().pauseAudio(mDetail);
            } else {
                MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.AllSongs.ordinal(), -1);
            }
        }
    }*/

}

