package kz.qobyzbook.E_VideoLesson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.List;

import kz.qobyzbook.AppController;
import kz.qobyzbook.R;

/**
 * Created by Orenk on 27.09.2016.
 */

public class FragmentVideoQobyz extends Fragment {


    private static final String TAG = "FragmentVideoQobyz";
    ListView listView;
    VideoAdapter videoAdapter;
    String idVideo;

    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;

    public String imgYoutubePictureFinally;
    private List<VideoModel> videList = new ArrayList<VideoModel>();
    private static final String urlJsonLink = "http://api.kobyzbook.kz/api/Videos";
    private static final String urlPicture = "http://img.youtube.com/vi/";
    private static final String endUrlPicture = "/mqdefault.jpg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.e_video_fragment, null);

        rl_downloading = (RelativeLayout) view.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) view.findViewById(R.id.rl_internet);
        btn_update = (Button) view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(onClickListener);

        listView = (ListView)view.findViewById(R.id.listView);
        videoAdapter = new VideoAdapter(getActivity(), videList);
        listView.setAdapter(videoAdapter);
        fillContent();
        listViewClickHandler();
        return view;
    }

    void fillContent() {
        Log.d(TAG, "fillContent()");
        if (isOnline()) {
            rl_downloading.setVisibility(View.VISIBLE);
            rl_internet.setVisibility(View.INVISIBLE);
            if(videList.isEmpty()) {
                jsonRequest();
            }
        }
        else {
            rl_downloading.setVisibility(View.INVISIBLE);
            rl_internet.setVisibility(View.VISIBLE);
        }
    }

    private void jsonRequest() {
        Log.d(TAG, "jsonRequest()");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String lang = preferences.getString("lang", "kk");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(urlJsonLink, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                rl_downloading.setVisibility(View.INVISIBLE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        idVideo = obj.getString("video1");
                        imgYoutubePictureFinally = (urlPicture+idVideo+endUrlPicture);
                        VideoModel videoModel = new VideoModel();
                        videoModel.setVideo(imgYoutubePictureFinally);
                        if (lang.equals("kk"))
                            videoModel.setName(obj.getString("namekz"));
                        else videoModel.setName(obj.getString("nameen"));

                        videList.add(videoModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                videoAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
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
        }
    };

    public boolean isOnline() {
        Log.d(TAG, "isOnline()");
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void listViewClickHandler() {
        Log.d(TAG, "listViewClickHandler()");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Intent myIntent = new Intent(getActivity(), VideoPage.class);
                myIntent.putExtra("youtubeId",idVideo);
                myIntent.putExtra("title", videList.get(position).getName());
                getActivity().startActivity(myIntent);

            }
        });
    }

}
