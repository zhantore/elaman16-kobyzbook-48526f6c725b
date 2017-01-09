package kz.qobyzbook.a_author;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import kz.qobyzbook.H_Project.ProjectModel;
import kz.qobyzbook.R;

/**
 * Created by zhan on 11/28/16.
 */

public class FragmentAuthor extends Fragment {

    private  final String URL = "http://api.kobyzbook.kz/api/Autors/1";
    private static final String TAG = "FragmentAuthor";
    TextView omir, anyktama, mail;
    ProjectModel project = new ProjectModel();
    ImageView autorPhoto;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View v = inflater.inflate(R.layout.fragment_author, container, false);
        omir = (TextView) v.findViewById(R.id.omir);
        anyktama = (TextView) v.findViewById(R.id.anyktama);
        mail = (TextView) v.findViewById(R.id.mail);
        autorPhoto = (ImageView)v.findViewById(R.id.author_photo);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        makeJsonObjectRequest();
        return v;
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
                                project.setPhoto(obj.getString("photo"));
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
                                project.setPhoto(obj.getString("photo"));
                            }
                            omir.setText(stripHtml(project.getOmirbayan()+project.getOnerbayan()+project.getZhetistik()+project.getEnbek()
                                    +project.getGilim()+project.getShigarma()));
                            anyktama.setText(project.getAniktama());
                            mail.setText(project.getMail());
                            imageLoader.displayImage(convertURL(project.getPhoto()), autorPhoto);
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

    /**
     *
     * @param url
     * @return
     */
    private String convertURL(String url){
        if (url!=null){
            url = url.replace("~/", "/");
            url = url.replace(" ", "%20");
            url = "http://admin.kobyzbook.kz"+url;
            return url;}
        else return null;
    }

    /**
     *
     * @param html
     * @return
     */
    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }
}
