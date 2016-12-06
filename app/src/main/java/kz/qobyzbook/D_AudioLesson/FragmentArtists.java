/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.D_AudioLesson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import kz.qobyzbook.AppController;
import kz.qobyzbook.R;
import kz.qobyzbook.activities.AlbumAndArtisDetailsActivity;
import kz.qobyzbook.utility.LogWriter;
import kz.qobyzbook.phonemidea.PhoneMediaControl;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentArtists extends Fragment {

    private static final String TAG = "FragmentArtists";
    private static Context context;
    private RecyclerView recyclerView;
    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;
    private AlbumRecyclerAdapter mAdapter;

    ArrayList<ArtistModel> musicList = new ArrayList<ArtistModel>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(kz.qobyzbook.R.layout.fragmentchild_album, null);
        setupView(view);
        getDataFromServer();
        return view;
    }

     private void setupView(View v) {
         Log.d(TAG, "setupView()");
        recyclerView = (RecyclerView) v.findViewById(kz.qobyzbook.R.id.recyclerview_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rl_downloading = (RelativeLayout)v.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) v.findViewById(R.id.rl_internet);
        btn_update = (Button)v.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(onClickListener);
        populateData();
    }

    void getDataFromServer() {
        Log.d(TAG, "getDataFromServer()");

        if (isOnline()) {
            rl_downloading.setVisibility(View.VISIBLE);
            rl_internet.setVisibility(View.INVISIBLE);
            getSongsFromServer();
        }
        else {
            rl_downloading.setVisibility(View.INVISIBLE);
            rl_internet.setVisibility(View.VISIBLE);
        }
    }

    private void getSongsFromServer() {
        Log.d(TAG, "getSongsFromServer()");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String lang = preferences.getString("lang", "kk");

        JsonArrayRequest addReq = new JsonArrayRequest("http://api.kobyzbook.kz/api/Default",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        rl_downloading.setVisibility(View.INVISIBLE);
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                int ID = obj.getInt("id");
                                String NAME;
                                if (lang.equals("kk")){
                                    NAME = obj.getString("namekz");
                                }
                                else NAME = obj.getString("nameen");

                                String IMAGE_BIG = convertURL(obj.getString("photo"));
                                String IMAGE_SMALL = convertURL(obj.getString("sphoto"));
                                int COUNT = obj.getInt("count");

                                ArtistModel artistModel = new ArtistModel( ID, NAME, COUNT, IMAGE_BIG, IMAGE_SMALL);

                                musicList.add(artistModel);

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

    public boolean isOnline() {
        Log.d(TAG, "isOnline()");
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private String convertURL(String url) {
        Log.d(TAG, "convertURL(" +url+ ")");
        if (url!=null){
        url = url.replace("~/", "/");
        url = url.replace(" ", "%20");
        url = "http://admin.kobyzbook.kz/"+url;
        return url;}
        else return null;
    }


    private void populateData() {
        Log.d(TAG, "populateData()");
        if (mAdapter == null) {
            mAdapter = new AlbumRecyclerAdapter();
            recyclerView.setAdapter(mAdapter);
        } else {
            recyclerView.setAdapter(mAdapter);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickListener()");
            if (v.getId()==R.id.btn_update){
                if (isOnline())
                    getDataFromServer();
                else
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_connect), Toast.LENGTH_SHORT).show();
            }
        }
    };


    public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.ViewHolder> {

        private ImageLoader imageLoader = ImageLoader.getInstance();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int position) {
            Log.d(TAG, "onCreateViewHolder()");
            return new ViewHolder(LayoutInflater.from(viewgroup.getContext()).inflate(kz.qobyzbook.R.layout.inflate_grid_item, viewgroup, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder()");
            ArtistModel artistModel = musicList.get(position);
            holder.artistName.setText(artistModel.getName());
            holder.soundsCount.setText(String.valueOf(artistModel.getCount())+" "+getActivity().getResources().getString(R.string.count_kui));
            imageLoader.displayImage(artistModel.getImg_big(), holder.icon);
        }

        @Override
        public int getItemCount() {
            return musicList.size() ;
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView artistName;
            TextView soundsCount;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                Log.d(TAG, "ViewHolder()");
                artistName = (TextView) itemView.findViewById(kz.qobyzbook.R.id.line1);
                soundsCount = (TextView) itemView.findViewById(kz.qobyzbook.R.id.line2);
                icon = (ImageView) itemView.findViewById(kz.qobyzbook.R.id.icon);
                icon.setScaleType(ScaleType.CENTER_CROP);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick()");
                try {

                    ArtistModel artistModel = musicList.get(getAdapterPosition());


                    Intent mIntent = new Intent(getActivity(), AlbumAndArtisDetailsActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putLong("id", artistModel.getID());
                    mBundle.putLong("tagfor", PhoneMediaControl.SonLoadFor.Artists.ordinal());
                    mBundle.putString("albumname", ((TextView) view.findViewById(kz.qobyzbook.R.id.line1)).getText().toString().trim());
                    mBundle.putString("title_one", getActivity().getResources().getString(R.string.allsons));
                    mBundle.putString("title_sec", String.valueOf(artistModel.getCount()));
                    mBundle.putString("image", artistModel.getImg_big());
                    mIntent.putExtras(mBundle);
                    getActivity().startActivity(mIntent);
                    getActivity().overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }
            }
        }


    }
}
