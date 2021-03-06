/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */

package kz.qobyzbook.H_Project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import kz.qobyzbook.C_Lessons.LessonNote;
import kz.qobyzbook.R;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.manager.MusicPreferance;
import kz.qobyzbook.manager.NotificationManager;
import kz.qobyzbook.models.SongDetail;
import kz.qobyzbook.observablelib.ScrollUtils;
import kz.qobyzbook.phonemidea.DMPlayerUtility;
import kz.qobyzbook.slidinguppanelhelper.SlidingUpPanelLayout;
import kz.qobyzbook.uicomponent.PlayPauseView;
import kz.qobyzbook.uicomponent.Slider;
import kz.qobyzbook.utility.LogWriter;

public class ProjectPage extends AppCompatActivity implements View.OnClickListener, Slider.OnValueChangedListener,
        NotificationManager.NotificationCenterDelegate {

    private static final String TAG = "ProjectPage";
    private SharedPreferences sharedPreferences;
    private int color = 0xFFFFFF;
    private Context context;
    Toolbar toolbar;
    private FloatingActionButton fab_button;
    //Constants
    private final String URL = "http://api.kobyzbook.kz/api/Infoes/1";
    String objectJson;
    int id = 0;
    private String name = "";
    //Components
    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;
    TextView zhoba,omir, anyktama, mail;
    RelativeLayout relativeLayoutZhoba,relativeLayoutOmirOner;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        //Set your theme first
        context = ProjectPage.this;
        theme();
        //Set your Layout view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h_project_page);

        initialize();
        getBundleValue();

        initiSlidingUpPanel();
        loadAlreadyPaing();
        addObserver();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
//            case R.id.back:
//                onBackPressed();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        if (isExpand) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        removeObserver();
    }

    @Override
    public void onClick(View v) {

        Log.d(TAG, "onClick()");
        switch (v.getId()) {
            case R.id.bottombar_play:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
                break;

            case R.id.btn_play:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
                break;

            case R.id.btn_forward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playNextSong();
                break;

            case R.id.btn_backward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playPreviousSong();
                break;

            case R.id.btn_suffel:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().shuffleMusic = v.isSelected() ? true : false;
                MusicPreferance.setShuffel(context, (v.isSelected() ? true : false));
                MediaController.getInstance().shuffleList(MusicPreferance.playlist);
                DMPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case R.id.btn_toggle:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().repeatMode = v.isSelected() ? 1 : 0;
                MusicPreferance.setRepeat(context, (v.isSelected() ? 1 : 0));
                DMPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case R.id.bottombar_img_Favorite:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    MediaController.getInstance().storeFavoritePlay(context, MediaController.getInstance().getPlayingSongDetail(), v.isSelected() ? 0 : 1);
                    v.setSelected(v.isSelected() ? false : true);
                    DMPlayerUtility.animateHeartButton(v);
                    findViewById(R.id.ivLike).setSelected(v.isSelected() ? true : false);
                    DMPlayerUtility.animatePhotoLike(findViewById(R.id.vBgLike), findViewById(R.id.ivLike));
                }
                break;
            case R.id.bottombar_noteicon:
                SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
                if (mSongDetail != null) {
                    try {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        String lang = preferences.getString("lang", "kk");

                        Intent mIntent = new Intent(this, LessonNote.class);
                        Bundle mBundle = new Bundle();

                        if (lang.equals("kk"))
                            mBundle.putString("name",getResources().getStringArray(R.array.lessons_array_kz)[4]);
                        else  mBundle.putString("name",getResources().getStringArray(R.array.lessons_array_en)[4]);

                        mBundle.putString("description", mSongDetail.getImage_url());
                        mIntent.putExtras(mBundle);
                        startActivity(mIntent);
                        overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogWriter.info("albumdetails", e.toString());
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }



    //Catch  theme changed from settings
    public void theme() {
        Log.d(TAG, "theme()");
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("THEME", 0);
        DMPlayerUtility.settingTheme(context, theme);
    }

    private void initialize() {
        Log.d(TAG, "initialize()");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup RecyclerView inside drawer
        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

//        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, color));

        relativeLayoutZhoba = (RelativeLayout) findViewById(R.id.relativeLayoutZhoba);
        relativeLayoutOmirOner = (RelativeLayout) findViewById(R.id.relativeLayoutOmirOner);

        zhoba = (TextView) findViewById(R.id.zhoba);
        omir = (TextView) findViewById(R.id.omir);
        anyktama = (TextView) findViewById(R.id.anyktama);
        mail = (TextView) findViewById(R.id.mail);

        rl_downloading = (RelativeLayout)findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) findViewById(R.id.rl_internet);
        btn_update = (Button)findViewById(R.id.btn_update);


        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        try {
            fab_button = (FloatingActionButton) findViewById(R.id.fab_button);
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

    private void getBundleValue() {
        Log.d(TAG, "getBundleValue()");
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            name = mBundle.getString("name");
            toolbar.setTitle(name);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));

            switch (mBundle.getInt("id")){
                case 0:
                    relativeLayoutOmirOner.setVisibility(View.VISIBLE);
                    relativeLayoutZhoba.setVisibility(View.INVISIBLE);


                    omir.setText(Html.fromHtml(mBundle.getString("omir")+mBundle.getString("oner")+mBundle.getString("zhetistik")+mBundle.getString("enbek")
                            +mBundle.getString("gilim")+mBundle.getString("shigarma")));
                    anyktama.setText(mBundle.getString("aniktama"));
                    mail.setText(Html.fromHtml(mBundle.getString("mail")));
                    break;
                case 1:
                    relativeLayoutOmirOner.setVisibility(View.INVISIBLE);
                    relativeLayoutZhoba.setVisibility(View.VISIBLE);
                    zhoba.setText(Html.fromHtml(mBundle.getString("zhoba")));
                    break;
            }
        }


    }


    /*-----------------AllSongs Work Related to Slide Panel-----------------*/

//    private static final String TAG = "ActivityDMPlayerBase";
    private SlidingUpPanelLayout mLayout;
    private RelativeLayout slidepanelchildtwo_topviewone;
    private RelativeLayout slidepanelchildtwo_topviewtwo;
    private boolean isExpand = false;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageView songAlbumbg;
    private ImageView img_bottom_slideone;
    private ImageView img_bottom_slidetwo;
    private TextView txt_playesongname;
    private TextView txt_songartistname;

    private TextView txt_playesongname_slidetoptwo;
    private TextView txt_songartistname_slidetoptwo;

    private TextView txt_timeprogress;
    private TextView txt_timetotal;
    private ImageView imgbtn_backward;
    private ImageView imgbtn_forward;
    private ImageView imgbtn_toggle;
    private ImageView imgbtn_suffel;
    private ImageView img_Favorite;
    private ImageView img_Note;
    private PlayPauseView btn_playpause;
    private PlayPauseView btn_playpausePanel;
    private Slider audio_progress;
    private boolean isDragingStart = false;
    private int TAG_Observer;

    private void initiSlidingUpPanel() {
        Log.d(TAG, "initiSlidingUpPanel()");
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        // songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg);
        songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg_mid);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(R.id.img_bottom_slidetwo);
        txt_timeprogress = (TextView) findViewById(R.id.slidepanel_time_progress);
        txt_timetotal = (TextView) findViewById(R.id.slidepanel_time_total);
        imgbtn_backward = (ImageView) findViewById(R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(R.id.btn_forward);
        imgbtn_toggle = (ImageView) findViewById(R.id.btn_toggle);
        imgbtn_suffel = (ImageView) findViewById(R.id.btn_suffel);
        btn_playpause = (PlayPauseView) findViewById(R.id.btn_play);
        audio_progress = (Slider) findViewById(R.id.audio_progress_control);
        btn_playpausePanel = (PlayPauseView) findViewById(R.id.bottombar_play);
        img_Favorite = (ImageView) findViewById(R.id.bottombar_img_Favorite);
        img_Note = (ImageView) findViewById(R.id.bottombar_noteicon);


        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;
        audio_progress.setBackgroundColor(coloraccent);
        audio_progress.setValue(0);

        audio_progress.setOnValueChangedListener(this);
        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);
        imgbtn_toggle.setOnClickListener(this);
        imgbtn_suffel.setOnClickListener(this);
        img_Favorite.setOnClickListener(this);
        img_Note.setOnClickListener(this);

        btn_playpausePanel.Pause();
        btn_playpause.Pause();

        txt_playesongname = (TextView) findViewById(R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewtwo);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);

        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });

        slidepanelchildtwo_topviewtwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        ((PlayPauseView) findViewById(R.id.bottombar_play)).setOnClickListener(this);
        ((PlayPauseView) findViewById(R.id.btn_play)).setOnClickListener(this);

        imgbtn_toggle.setSelected((MusicPreferance.getRepeat(context) == 1) ? true : false);
        MediaController.getInstance().shuffleMusic = imgbtn_toggle.isSelected() ? true : false;
        DMPlayerUtility.changeColorSet(context, (ImageView) imgbtn_toggle, imgbtn_toggle.isSelected());

        imgbtn_suffel.setSelected(MusicPreferance.getShuffel(context) ? true : false);
        MediaController.getInstance().repeatMode = imgbtn_suffel.isSelected() ? 1 : 0;
        DMPlayerUtility.changeColorSet(context, (ImageView) imgbtn_suffel, imgbtn_suffel.isSelected());

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(TAG, "onPanelSlide, offset " + slideOffset);

                if (slideOffset == 0.0f) {
                    isExpand = false;
                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    // if (isExpand) {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f -
                    // slideOffset);
                    // } else {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f -
                    // slideOffset);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f);
                    // }

                } else {
                    isExpand = true;
                    slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.d(TAG, "onPanelExpanded");
                isExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.d(TAG, "onPanelCollapsed");
                isExpand = false;
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.d(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.d(TAG, "onPanelHidden");
            }
        });

    }

    private void loadAlreadyPaing() {
        Log.d(TAG, "loadAlreadyPaing()");
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail != null) {
            updateImages(mSongDetail);
            updateTitles(mSongDetail);
            updateSongsDetails(false);
            MediaController.getInstance().checkIsFavorite(context, mSongDetail, img_Favorite);
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Log.d(TAG, "AnimateFirstDisplayListener->onLoadingComplete()");
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }

    }

    public void addObserver() {
        Log.d(TAG, "addObserver()");
        TAG_Observer = MediaController.getInstance().generateObserverTag();
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.newaudioloaded);
    }

    public void removeObserver() {
        Log.d(TAG, "removeObserver()");
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.newaudioloaded);
    }

    public void updateImages(SongDetail mDetail) {
        Log.d(TAG, "updateImages()");
        imageLoader.displayImage(mDetail.getImage_url(), songAlbumbg, options, animateFirstListener);
        imageLoader.displayImage(mDetail.getImage_url(), img_bottom_slideone, options, animateFirstListener);
        imageLoader.displayImage(mDetail.getImage_url(), img_bottom_slidetwo, options, animateFirstListener);
    }

    public void updateTitles(SongDetail mSongDetail) {
        Log.d(TAG, "updateTitles()");
        txt_playesongname.setText(mSongDetail.getTitle());
        txt_songartistname.setText(mSongDetail.getArtist());
        txt_playesongname_slidetoptwo.setText(mSongDetail.getTitle());
        txt_songartistname_slidetoptwo.setText(mSongDetail.getArtist());

        if (txt_timetotal != null) {
            long duration = Long.valueOf(mSongDetail.getDuration());
            txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        Log.d(TAG, "didReceivedNotification()");
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();

        if (id == NotificationManager.audioDidStarted) { //  || id == NotificationManager.audioPlayStateChanged || id == NotificationManager.audioDidReset
            updateSongsDetails(id == NotificationManager.audioDidReset && (Boolean) args[1]);
            updateImages(mSongDetail);
            updateTitles(mSongDetail);
        }
        else if (id == NotificationManager.audioPlayStateChanged) {
            updateSongsDetails(id == NotificationManager.audioDidReset && (Boolean) args[1]);
        }
        else if (id == NotificationManager.audioDidReset) {
            updateSongsDetails(id == NotificationManager.audioDidReset && (Boolean) args[1]);
            updateImages(mSongDetail);
            updateTitles(mSongDetail);
        }
        else if (id == NotificationManager.audioProgressDidChanged) {
            updateProgress(mSongDetail);
        }
    }

    @Override
    public void newSongLoaded(Object... args) {
        Log.d(TAG, "newSongLoaded(" +args+ ")");
        MediaController.getInstance().checkIsFavorite(context, (SongDetail) args[0], img_Favorite);
    }


    private void updateSongsDetails(boolean shutdown) {
        Log.d(TAG, "updateSongsDetails()");
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail == null && shutdown) {
            return;
        } else {
            updateProgress(mSongDetail);
            if (MediaController.getInstance().isAudioPaused()) {
                btn_playpausePanel.Pause();
                btn_playpause.Pause();
            } else {
                btn_playpausePanel.Play();
                btn_playpause.Play();
            }
        }
    }

    private void updateProgress(SongDetail mSongDetail) {
        Log.d(TAG, "didReceivedNotification(" +mSongDetail+ ")");
        if (audio_progress != null && mSongDetail != null) {
            // When SeekBar Draging Don't Show Progress
            if (!isDragingStart) {
                // Progress Value comming in point it range 0 to 1
                audio_progress.setValue((int) (mSongDetail.audioProgress * 100));
            }
            String timeString = String.format("%d:%02d", mSongDetail.audioProgressSec / 60, mSongDetail.audioProgressSec % 60);
            txt_timeprogress.setText(timeString);
        }
    }

    private void PlayPauseEvent(View v) {
        Log.d(TAG, "didReceivedNotification()");
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Play();
        } else {
            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Pause();
        }
    }

    @Override
    public void onValueChanged(int value) {
        Log.d(TAG, "didReceivedNotification(" +value+ ")");
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingSongDetail(), (float) value / 100);
    }

}
