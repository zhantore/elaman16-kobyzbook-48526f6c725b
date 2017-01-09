package kz.qobyzbook.H_Project;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import kz.qobyzbook.AppController;
import kz.qobyzbook.R;

/**
 * Created by zhan on 12/30/16.
 */

public class FragmentAboutProject extends Fragment {

    private static final String TAG = "FragmentAboutProject";
    ProjectModel project = new ProjectModel();
    private int color = 0xFFFFFF;
    private FloatingActionButton fab_button;
    //Constants
    private final String URL = "http://api.kobyzbook.kz/api/Autors/1";
    int id = 0;
    //Components
    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;
    WebView zhoba;
    RelativeLayout relativeLayoutZhoba,relativeLayoutOmirOner;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.h_fragment_about_project, null);
        ((AppCompatActivity)getActivity()).setTitle(getResources().getString(R.string.project_info));
        ((AppCompatActivity)getActivity()).setTitleColor(getResources().getColor(R.color.white));

        initialize(view);
        makeJsonObjectRequest();
        return view;
    }

    /**
     *
     */
    private void initialize(View view) {
        Log.d(TAG, "initialize()");

        // Setup RecyclerView inside drawer
        final TypedValue typedValue = new TypedValue();
        ((AppCompatActivity)getActivity()).getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

//        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, color));

        relativeLayoutZhoba = (RelativeLayout) view.findViewById(R.id.relativeLayoutZhoba);
        relativeLayoutOmirOner = (RelativeLayout) view.findViewById(R.id.relativeLayoutOmirOner);

        zhoba = (WebView) view.findViewById(R.id.zhoba);
        zhoba.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        zhoba.setLongClickable(false);

        rl_downloading = (RelativeLayout)view.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) view.findViewById(R.id.rl_internet);
        btn_update = (Button)view.findViewById(R.id.btn_update);


        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        try {
            fab_button = (FloatingActionButton) view.findViewById(R.id.fab_button);
            fab_button.setColorFilter(color);
            if (Build.VERSION.SDK_INT > 15) {
                fab_button.setImageAlpha(255);
            } else {
                fab_button.setAlpha(255);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void makeJsonObjectRequest() {

        Log.d(TAG, "makeJsonObjectRequest()");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String lang = preferences.getString("lang", "kk");

        JsonObjectRequest addReq = new JsonObjectRequest(URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {

                        rl_downloading.setVisibility(View.INVISIBLE);

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
                        fillContent();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                rl_downloading.setVisibility(View.GONE);
            }
        });
        AppController.getInstance().addToRequestQueue(addReq);
    }


    private void fillContent() {
        Log.d(TAG, "fillContent()");
        zhoba.loadData(project.getZhoba(), "text/html; charset=UTF-8", null);
//        zhoba.setText(Html.fromHtml(project.getZhoba()));
//        omir.setText(Html.fromHtml(project.getOmirbayan()+project.getOnerbayan()+project.getZhetistik()+project.getEnbek()
//                +project.getGilim()+project.getShigarma()));
//        anyktama.setText(project.getAniktama());
//        mail.setText(Html.fromHtml(project.getMail()));
    }
}
