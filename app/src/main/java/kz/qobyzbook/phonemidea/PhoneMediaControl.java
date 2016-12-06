/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.phonemidea;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import kz.qobyzbook.AppController;
import kz.qobyzbook.ApplicationDMPlayer;
import kz.qobyzbook.dbhandler.FavoritePlayTableHelper;
import kz.qobyzbook.dbhandler.MostAndRecentPlayTableHelper;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.models.SongDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class PhoneMediaControl {

    private Context context;
    private Cursor cursor = null;
    private static volatile PhoneMediaControl Instance = null;
    ArrayList<SongDetail> songsList = new ArrayList<>();
    public static enum SonLoadFor {
        AllSongs, Gener, LessonAudio, Artists, Musicintent, MostPlay, Favorite, ResecntPlay
    }

    /**
     *
     * @return
     */
    public static PhoneMediaControl getInstance() {
        PhoneMediaControl localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PhoneMediaControl();
                }
            }
        }
        return localInstance;
    }

    /**
     *
     * @param context
     * @param id
     * @param sonloadfor
     * @param path
     */
    public void loadMusicList(final Context context, final long id, final SonLoadFor sonloadfor, final String path) {

        songsList = getList(context, id, sonloadfor, path);
        if (phonemediacontrolinterface != null) {
            phonemediacontrolinterface.loadSongsComplete(songsList);
        }

//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//            ArrayList<SongDetail> songsList = null;
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//
//                try {
//                    songsList = getList(context, id, sonloadfor, path);
//                } catch (Exception e) {
//                    closeCrs();
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (phonemediacontrolinterface != null) {
//                    phonemediacontrolinterface.loadSongsComplete(songsList);
//                }
//            }
//        };
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
//            task.execute();
//        }
    }

    /**
     *
     * @param context
     * @param id
     * @param sonloadfor
     * @param path
     * @return
     */
    public ArrayList<SongDetail> getList(final Context context, final long id, final SonLoadFor sonloadfor, final String path) {

        songsList.clear();
        String sortOrder = "";
        switch (sonloadfor) {
            case AllSongs:
//                getSongsFromServer(context,new JsonObjectListener() {
//                    @Override
//                    public void onDone(ArrayList<SongDetail> arrayList) {
//                       // songsList = arrayList;
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, "http://api.kobyzbook.kz/api/Audios");
                getSongsFromServer(context, new JsonObjectListener() {
                    @Override
                    public void onDone(ArrayList<SongDetail> arrayList) {
                        songsList = arrayList;
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                }, "http://api.kobyzbook.kz/api/Default/" + String.valueOf(id));
                break;

            case Gener:
                Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", id);
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = ((Activity) context).getContentResolver().query(uri, projectionSongs, null, null, null);
                songsList = getSongsFromCursor(cursor);
                break;

            case Artists:
                getArtistsFromServer(context, new JsonObjectListener() {
                    @Override
                    public void onDone(ArrayList<SongDetail> arrayList) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                }, "http://api.kobyzbook.kz/api/Default");
//                getSongsFromServer(context, new JsonObjectListener() {
//                    @Override
//                    public void onDone(ArrayList<SongDetail> arrayList) {
//                        songsList = arrayList;
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, "http://api.kobyzbook.kz/api/Default" + String.valueOf(id));
                break;

            case LessonAudio:

                break;

            case Musicintent:
                String condition = MediaStore.Audio.Media.DATA + "='" + path + "' AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = ((Activity) context).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionSongs, condition, null, sortOrder);
                songsList = getSongsFromCursor(cursor);
                break;

            case MostPlay:
                cursor = MostAndRecentPlayTableHelper.getInstance(context).getMostPlay();
                songsList = getSongsFromSQLDBCursor(cursor);
                break;

            case Favorite:
                cursor = FavoritePlayTableHelper.getInstance(context).getFavoriteSongList();
                songsList = getSongsFromSQLDBCursor(cursor);
                break;
        }
        return songsList;
    }

    /**
     *
     * @param cursor
     * @return
     */
    private ArrayList<SongDetail> getSongsFromCursor(Cursor cursor) {
        ArrayList<SongDetail> generassongsList = new ArrayList<SongDetail>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                while (cursor.moveToNext()) {

                    int ID = cursor.getInt(_id);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);

                    SongDetail mSongDetail = new SongDetail(ID, album_id, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION);
                    generassongsList.add(mSongDetail);
                }
            }
            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
        return generassongsList;
    }

    /**
     *
     * @param cursor
     * @return
     */
    private ArrayList<SongDetail> getSongsFromSQLDBCursor(Cursor cursor) {
        ArrayList<SongDetail> generassongsList = new ArrayList<SongDetail>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {

                while (cursor.moveToNext()) {
                    long ID = cursor.getLong(cursor.getColumnIndex(FavoritePlayTableHelper.ID));
                    long album_id = cursor.getLong(cursor.getColumnIndex(FavoritePlayTableHelper.ALBUM_ID));
                    String ARTIST = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.ARTIST));
                    String TITLE = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.TITLE));
                    String DISPLAY_NAME = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.DISPLAY_NAME));
                    String DURATION = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.DURATION));
                    String Path = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.PATH));

                    SongDetail mSongDetail = new SongDetail((int) ID, (int) album_id, ARTIST, TITLE, Path, DISPLAY_NAME, "" + (Long.parseLong(DURATION) * 1000));
                    generassongsList.add(mSongDetail);
                }
            }
            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
        return generassongsList;
    }

    /**
     *
     * @param context
     * @param listener
     * @param url
     */
    private void getArtistsFromServer(Context context, final JsonObjectListener listener, String url){
        Log.d("url","phone med url: " + url);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String lang = preferences.getString("lang", "kk");
        final ArrayList<SongDetail> musicList = new ArrayList<SongDetail>();
        JsonArrayRequest addReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                long ID = obj.getInt("id");
                                long album_id = obj.getInt("id");

                                String IMAGE_URL = convertURL(obj.getString("photo"));
                                String DURATION = "200";
                                String ARTIST;
                                String TITLE;
                                if (lang.equals("kk")){
                                    TITLE = obj.getString("namekz");
                                }
                                else {
                                    TITLE = obj.getString("nameen");
                                }
                                SongDetail mSongDetail = new SongDetail((int) ID, (int) album_id, "", TITLE, "", IMAGE_URL, "" + (Long.parseLong(DURATION) * 1000));
                                musicList.add(mSongDetail);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (phonemediacontrolinterface != null) {
                            phonemediacontrolinterface.loadSongsComplete(musicList);
                        }
                        if (listener!=null){
                            listener.onDone(musicList);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(addReq);
    }

    /**
     *
     * @param context
     * @param listener
     * @param url
     */
    private void getSongsFromServer(Context context, final JsonObjectListener listener, String url){
        Log.d("url","phone med url: " + url);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String lang = preferences.getString("lang", "kk");
        final ArrayList<SongDetail> musicList = new ArrayList<SongDetail>();
        JsonArrayRequest addReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                long ID = obj.getInt("id");
                                long album_id = obj.getInt("id");

                                String IMAGE_URL = convertURL(obj.getString("photo"));
                                String DURATION = "200";
                                String Path = convertURL(obj.getString("audio1"));
                                String ARTIST;
                                String TITLE;
                                if (lang.equals("kk")){
                                     ARTIST = obj.getString("orindaushikz");
                                     TITLE = obj.getString("namekz");
                                }
                                else {
                                     ARTIST = obj.getString("orindaushien");
                                     TITLE = obj.getString("nameen");
                                }
                                SongDetail mSongDetail = new SongDetail((int) ID, (int) album_id, ARTIST, TITLE, Path, IMAGE_URL, "" + (Long.parseLong(DURATION) * 1000));
                                musicList.add(mSongDetail);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (phonemediacontrolinterface != null) {
                            phonemediacontrolinterface.loadSongsComplete(musicList);
                        }
                        if (listener!=null){
                            listener.onDone(musicList);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(addReq);
    }



    private void closeCrs() {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationDMPlayer.applicationHandler.post(runnable);
        } else {
            ApplicationDMPlayer.applicationHandler.postDelayed(runnable, delay);
        }
    }

    private final String[] projectionSongs = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION};

    public static PhoneMediaControlINterface phonemediacontrolinterface;

    public static PhoneMediaControlINterface getPhonemediacontrolinterface() {
        return phonemediacontrolinterface;
    }

    public static void setPhonemediacontrolinterface(PhoneMediaControlINterface phonemediacontrolinterface) {
        PhoneMediaControl.phonemediacontrolinterface = phonemediacontrolinterface;
    }

    public interface PhoneMediaControlINterface {
        public void loadSongsComplete(ArrayList<SongDetail> songsList);
    }


    public interface JsonObjectListener {

        void onDone(ArrayList<SongDetail> arrayList);

        void onError(String error);
    }

    private String convertURL(String url){
        if (url!=null){
        url = url.replace("~/", "/");
        url = url.replace(" ", "%20");
        url = "http://admin.kobyzbook.kz"+url;
        return url;}
        else return null;
    }

}
