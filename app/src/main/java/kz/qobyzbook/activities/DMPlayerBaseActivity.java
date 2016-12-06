/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kz.qobyzbook.A_AboutQobyz.QobyzFragment;
import kz.qobyzbook.ApplicationDMPlayer;
import kz.qobyzbook.C_Lessons.LessonFragment;
import kz.qobyzbook.C_Lessons.LessonNote;
import kz.qobyzbook.D_AudioLesson.FragmentAudioLesson;
import kz.qobyzbook.E_VideoLesson.FragmentVideoLessons;
import kz.qobyzbook.F_Test.FragmentTest;
import kz.qobyzbook.H_Project.FragmentProject;
import kz.qobyzbook.I_Settings.DialogLanguage;
import kz.qobyzbook.I_Settings.FragmentSettings;
import kz.qobyzbook.R;
import kz.qobyzbook.a_author.FragmentAuthor;
import kz.qobyzbook.a_kirispe.FragmentKirispe;
import kz.qobyzbook.a_news.FragmentNews;
import kz.qobyzbook.adapter.DrawerAdapter;
import kz.qobyzbook.fragments.FragmentDrawer;
import kz.qobyzbook.fragments.FragmentFavorite;
import kz.qobyzbook.B_Persons.PersonsFragment;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.manager.MusicPreferance;
import kz.qobyzbook.manager.NotificationManager;
import kz.qobyzbook.models.SongDetail;
import kz.qobyzbook.phonemidea.DMPlayerUtility;
import kz.qobyzbook.slidinguppanelhelper.SlidingUpPanelLayout;
import kz.qobyzbook.uicomponent.CircleImageView;
import kz.qobyzbook.uicomponent.PlayPauseView;
import kz.qobyzbook.uicomponent.Slider;
import kz.qobyzbook.utility.LogWriter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static kz.qobyzbook.manager.NotificationManager.audioDidStarted;


public class DMPlayerBaseActivity extends AppCompatActivity implements View.OnClickListener, Slider.OnValueChangedListener,
        NotificationManager.NotificationCenterDelegate, FragmentDrawer.FragmentDrawerListener {

    private static final String TAG = "DMPlayerBaseActivity";
    private Context context;
    private SharedPreferences sharedPreferences;
    private ActionBarDrawerToggle mDrawerToggle;
    private int theme;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    private RecyclerView recyclerViewDrawer;
    private DrawerAdapter adapterDrawer;

    private SlidingUpPanelLayout mLayout;
    private RelativeLayout slidepanelchildtwo_topviewone;
    private RelativeLayout slidepanelchildtwo_topviewtwo;
    private boolean isExpand = false;

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private CircleImageView songAlbumbg;
    private ImageView img_bottom_slideone;
    private ImageView img_bottom_slidetwo;
    private ImageView language;
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
    public static Bundle myBundle = new Bundle();

    private FragmentDrawer drawerFragment;
    RelativeLayout rl_zero, rl_one, rl_two, rl_three, rl_four, rl_five, rl_six, rl_seven, rl_eight, rl_nine, rl_ten;
    TextView tv_zero, tv_one, tv_two, tv_three, tv_four, tv_five, tv_six, tv_seven, tv_eight, tv_nine, tv_ten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        //Set your theme first
        context = DMPlayerBaseActivity.this;
        theme();
        //Set your Layout view
        super.onCreate(savedInstanceState);
        setContentView(kz.qobyzbook.R.layout.activity_dmplayerbase);

        //System bar color set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            systembartiteniam();
        }

        toolbarStatusBar();
        initiSlidingUpPanel();


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);
        drawerFragment.setDrawerListener(this);

        initDrawerComponents();
        getIntentData();
        displayView(1);
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        addObserver();
        loadAlreadyPlayng();


    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        removeObserver();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        removeObserver();
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().cleanupPlayer(context, true, true);
        }
        super.onDestroy();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        if (item.getItemId()==android.R.id.home){
            Log.d("url","back");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick()");

        switch (v.getId()) {
            case kz.qobyzbook.R.id.bottombar_play:
                    if (MediaController.getInstance().getPlayingSongDetail() != null)
                        PlayPauseEvent(v);
                break;

            case kz.qobyzbook.R.id.btn_play:

                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
                break;

            case kz.qobyzbook.R.id.btn_forward:

                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playNextSong();
                break;

            case kz.qobyzbook.R.id.btn_backward:

                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playPreviousSong();
                break;

            case kz.qobyzbook.R.id.btn_suffel:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().shuffleMusic = v.isSelected() ? true : false;
                MusicPreferance.setShuffel(context, (v.isSelected() ? true : false));
                MediaController.getInstance().shuffleList(MusicPreferance.playlist);
                DMPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case kz.qobyzbook.R.id.btn_toggle:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().repeatMode = v.isSelected() ? 1 : 0;
                MusicPreferance.setRepeat(context, (v.isSelected() ? 1 : 0));
                DMPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case kz.qobyzbook.R.id.bottombar_img_Favorite:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    MediaController.getInstance().storeFavoritePlay(context, MediaController.getInstance().getPlayingSongDetail(), v.isSelected() ? 0 : 1);
                    v.setSelected(v.isSelected() ? false : true);
                    DMPlayerUtility.animateHeartButton(v);
                    findViewById(kz.qobyzbook.R.id.ivLike).setSelected(v.isSelected() ? true : false);
                    DMPlayerUtility.animatePhotoLike(findViewById(kz.qobyzbook.R.id.vBgLike), findViewById(kz.qobyzbook.R.id.ivLike));
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

    /**
     * Get intent data from music choose option
     */
    private void getIntentData() {
        Log.d(TAG, "getIntentData()");
        try {
            Uri data = getIntent().getData();
            if (data != null) {
                if (data.getScheme().equalsIgnoreCase("file")) {
                    String path = data.getPath().toString();
                    if (!TextUtils.isEmpty(path)) {
                        MediaController.getInstance().cleanupPlayer(context, true, true);
                        MusicPreferance.getPlaylist(context, path);
                        updateSongsDetails(false);
                        MediaController.getInstance().playAudio(MusicPreferance.playingSongDetail);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                }
                if (data.getScheme().equalsIgnoreCase("http"))
                    LogWriter.info(TAG, data.getPath().toString());
                if (data.getScheme().equalsIgnoreCase("content"))
                    LogWriter.info(TAG, data.getPath().toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toolbarStatusBar() {
        Log.d(TAG, "toolbarStatusBar()");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initDrawerComponents(){
        Log.d(TAG, "initDrawerComponents()");
        rl_zero = (RelativeLayout) findViewById(R.id.rl_zero);
        rl_one = (RelativeLayout) findViewById(R.id.rl_one);
        rl_two = (RelativeLayout) findViewById(R.id.rl_two);
        rl_three = (RelativeLayout) findViewById(R.id.rl_three);
        rl_four = (RelativeLayout) findViewById(R.id.rl_four);
        rl_five = (RelativeLayout) findViewById(R.id.rl_five);
        rl_six = (RelativeLayout) findViewById(R.id.rl_six);
        rl_seven = (RelativeLayout) findViewById(R.id.rl_seven);
        rl_eight = (RelativeLayout) findViewById(R.id.rl_eight);
        rl_nine = (RelativeLayout) findViewById(R.id.rl_nine);
        rl_ten = (RelativeLayout) findViewById(R.id.rl_ten);

        tv_zero = (TextView) findViewById(R.id.tv_zero);
        tv_one = (TextView) findViewById(R.id.tv_one);
        tv_two = (TextView) findViewById(R.id.tv_two);
        tv_three = (TextView) findViewById(R.id.tv_three);
        tv_four = (TextView) findViewById(R.id.tv_four);
        tv_five = (TextView) findViewById(R.id.tv_five);
        tv_six = (TextView) findViewById(R.id.tv_six);
        tv_seven = (TextView) findViewById(R.id.tv_seven);
        tv_eight = (TextView) findViewById(R.id.tv_eight);
        tv_nine = (TextView) findViewById(R.id.tv_nine);
        tv_ten = (TextView) findViewById(R.id.tv_ten);

    }

    @Override
    public void onDrawerItemSelected(int position) {
        Log.d(TAG, "onDrawerItemSelected(" +position+ ")");
        displayView(position);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void displayView(int position) {
        Log.d(TAG, "displayView(" +position+ ")");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        Fragment fragment = null;
        Class fragmentClass = null;

        switch (position) {
            case 1: {
                fragmentClass = FragmentKirispe.class;
                toolbar.setTitle(getResources().getString(R.string.kirispe));
//                drawer.setBackground(getResources().getDrawable(R.drawable.qobyz_turaly));

                tv_zero.setTypeface(Typeface.DEFAULT_BOLD);
                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_zero.setTextColor(Color.WHITE);
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
            }
            break;
            case 2:
                fragmentClass = QobyzFragment.class;
                toolbar.setTitle(getResources().getString(R.string.about_qobyz));
//                drawer.setBackground(getResources().getDrawable(R.drawable.qobyz_turaly));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.list_row_hover_start_color));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT_BOLD);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);


                tv_one.setTextColor(Color.WHITE);
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
                break;

            case 3:
                fragmentClass = PersonsFragment.class;
                toolbar.setTitle(getResources().getString(R.string.persons));
//                drawer.setBackground(getResources().getDrawable(R.drawable.qobyzshylar));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT_BOLD);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_two.setTextColor(Color.WHITE);
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
                break;

            case 4:
                fragmentClass = LessonFragment.class;
                toolbar.setTitle(getResources().getString(R.string.lessons));
//                drawer.setBackground(getResources().getDrawable(R.drawable.lessons));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT_BOLD);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_three.setTextColor(Color.WHITE);
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
                break;
            case 5: {
                fragmentClass = FragmentAudioLesson.class;
                toolbar.setTitle(getResources().getString(R.string.audio));
//                drawer.setBackground(getResources().getDrawable(R.drawable.audio));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT_BOLD);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_four.setTextColor(Color.WHITE);
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
            }
            break;

            case 6:{
                fragmentClass = FragmentVideoLessons.class;
                toolbar.setTitle(getResources().getString(R.string.beine_sabaqtar));
//                drawer.setBackground(getResources().getDrawable(R.drawable.video));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT_BOLD);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_five.setTextColor(Color.WHITE);
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
            }
            break;

            case 7: {
                fragmentClass = FragmentFavorite.class;
                toolbar.setTitle(getResources().getString(R.string.playlist));
//                drawer.setBackground(getResources().getDrawable(R.drawable.test));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT_BOLD);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_six.setTextColor(Color.WHITE);
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
            }
                break;

            case 8: {
                fragmentClass = FragmentTest.class;
                toolbar.setTitle(getResources().getString(R.string.test));
//                drawer.setBackground(getResources().getDrawable(R.drawable.test));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT_BOLD);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_seven.setTextColor(Color.WHITE);
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
            }
            break;

            case 9: {
                fragmentClass = FragmentAuthor.class;
                toolbar.setTitle(getResources().getString(R.string.autor));
//                drawer.setBackground(getResources().getDrawable(R.drawable.test));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT_BOLD);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(Color.WHITE);
            }
                break;

            case 10: {
                fragmentClass = FragmentNews.class;
                toolbar.setTitle(getResources().getString(R.string.news));
//                drawer.setBackground(getResources().getDrawable(R.drawable.settings));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT_BOLD);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT);

                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_ten.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(Color.WHITE);
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
            }
            break;

            case 11: {
                fragmentClass = FragmentProject.class;
                toolbar.setTitle(getResources().getString(R.string.zhoba_turaly));
//                drawer.setBackground(getResources().getDrawable(R.drawable.about_project));
//                rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_four.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_five.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_six.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
//                rl_seven.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_eight.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                rl_nine.setBackgroundColor(ContextCompat.getColor(this, R.color.list_row_hover_start_color));

                tv_one.setTypeface(Typeface.DEFAULT);
                tv_two.setTypeface(Typeface.DEFAULT);
                tv_three.setTypeface(Typeface.DEFAULT);
                tv_four.setTypeface(Typeface.DEFAULT);
                tv_five.setTypeface(Typeface.DEFAULT);
                tv_six.setTypeface(Typeface.DEFAULT);
                tv_seven.setTypeface(Typeface.DEFAULT);
                tv_eight.setTypeface(Typeface.DEFAULT);
                tv_nine.setTypeface(Typeface.DEFAULT);
                tv_zero.setTypeface(Typeface.DEFAULT);
                tv_ten.setTypeface(Typeface.DEFAULT_BOLD);

                tv_ten.setTextColor(Color.WHITE);
                tv_two.setTextColor(getResources().getColor(R.color.text_color));
                tv_three.setTextColor(getResources().getColor(R.color.text_color));
                tv_four.setTextColor(getResources().getColor(R.color.text_color));
                tv_five.setTextColor(getResources().getColor(R.color.text_color));
                tv_six.setTextColor(getResources().getColor(R.color.text_color));
                tv_seven.setTextColor(getResources().getColor(R.color.text_color));
                tv_eight.setTextColor(getResources().getColor(R.color.text_color));
                tv_one.setTextColor(getResources().getColor(R.color.text_color));
                tv_zero.setTextColor(getResources().getColor(R.color.text_color));
                tv_nine.setTextColor(getResources().getColor(R.color.text_color));
            }
                break;
            default:
                break;
        }
        if (fragmentClass!=null){
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit();
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * Method for changing language
     */
    public void changeLanguageDialog() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DMPlayerBaseActivity.this);
        String lang = preferences.getString("lang", "kk");
        AlertDialog.Builder builder = new AlertDialog.Builder(DMPlayerBaseActivity.this);
        if (lang.equals("kk")) {
            builder.setItems(R.array.language_array_kz, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        preferences.edit().putString("lang", "en").commit();
                        ApplicationDMPlayer.setLocale(DMPlayerBaseActivity.this);
                        restartActivity();
                    } else {
                        preferences.edit().putString("lang", "kk").commit();
                        ApplicationDMPlayer.setLocale(DMPlayerBaseActivity.this);
                        restartActivity();
                    }
                }
            });
        } else {
            builder.setItems(R.array.language_array_en, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        preferences.edit().putString("lang", "en").commit();
                        ApplicationDMPlayer.setLocale(DMPlayerBaseActivity.this);
                        restartActivity();
                    } else {
                        preferences.edit().putString("lang", "kk").commit();
                        ApplicationDMPlayer.setLocale(DMPlayerBaseActivity.this);
                        restartActivity();
                    }
                }
            });
        }
        builder.create();
        builder.show();
    }

    /**
     *
     */
    private void restartActivity() {
        Intent intent = new Intent(DMPlayerBaseActivity.this, DMPlayerBaseActivity.class);
        startActivity(intent);
        finish();
    }

    private void initiSlidingUpPanel() {
        Log.d(TAG, "initiSlidingUpPanel()");
        mLayout = (SlidingUpPanelLayout) findViewById(kz.qobyzbook.R.id.sliding_layout);
        songAlbumbg = (CircleImageView) findViewById(kz.qobyzbook.R.id.image_songAlbumbg_mid);
        img_bottom_slideone = (ImageView) findViewById(kz.qobyzbook.R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(kz.qobyzbook.R.id.img_bottom_slidetwo);
        language = (ImageView)findViewById(R.id.langImage);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguageDialog();
            }
        });

        txt_timeprogress = (TextView) findViewById(kz.qobyzbook.R.id.slidepanel_time_progress);
        txt_timetotal = (TextView) findViewById(kz.qobyzbook.R.id.slidepanel_time_total);
        imgbtn_backward = (ImageView) findViewById(kz.qobyzbook.R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(kz.qobyzbook.R.id.btn_forward);
        imgbtn_toggle = (ImageView) findViewById(kz.qobyzbook.R.id.btn_toggle);
        imgbtn_suffel = (ImageView) findViewById(kz.qobyzbook.R.id.btn_suffel);
        btn_playpause = (PlayPauseView) findViewById(kz.qobyzbook.R.id.btn_play);
        audio_progress = (Slider) findViewById(kz.qobyzbook.R.id.audio_progress_control);
        btn_playpausePanel = (PlayPauseView) findViewById(kz.qobyzbook.R.id.bottombar_play);
        img_Favorite = (ImageView) findViewById(kz.qobyzbook.R.id.bottombar_img_Favorite);
        img_Note = (ImageView) findViewById(R.id.bottombar_noteicon);

        loadImageLoaderOption();

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(kz.qobyzbook.R.attr.colorAccent, typedvaluecoloraccent, true);
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

        txt_playesongname = (TextView) findViewById(kz.qobyzbook.R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(kz.qobyzbook.R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = (TextView) findViewById(kz.qobyzbook.R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(kz.qobyzbook.R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = (RelativeLayout) findViewById(kz.qobyzbook.R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = (RelativeLayout) findViewById(kz.qobyzbook.R.id.slidepanelchildtwo_topviewtwo);

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

        ((PlayPauseView) findViewById(kz.qobyzbook.R.id.bottombar_play)).setOnClickListener(this);
        ((PlayPauseView) findViewById(kz.qobyzbook.R.id.btn_play)).setOnClickListener(this);

        imgbtn_toggle.setSelected((MusicPreferance.getRepeat(context) == 1) ? true : false);
        MediaController.getInstance().repeatMode = imgbtn_toggle.isSelected() ? 1 : 0;
        DMPlayerUtility.changeColorSet(context, (ImageView) imgbtn_toggle, imgbtn_toggle.isSelected());

        imgbtn_suffel.setSelected(MusicPreferance.getShuffel(context) ? true : false);
        MediaController.getInstance().shuffleMusic = imgbtn_suffel.isSelected() ? true : false;
        DMPlayerUtility.changeColorSet(context, (ImageView) imgbtn_suffel, imgbtn_suffel.isSelected());

        MediaController.getInstance().shuffleList(MusicPreferance.playlist);

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



    //Catch  theme changed from settings
    public void theme() {
        Log.d(TAG, "theme()");
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);
        DMPlayerUtility.settingTheme(context, theme);
    }

    private void loadImageLoaderOption() {
        Log.d(TAG, "loadImageLoaderOption()");
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(kz.qobyzbook.R.drawable.bg_default_album_art)
                .showImageForEmptyUri(kz.qobyzbook.R.drawable.bg_default_album_art).showImageOnFail(kz.qobyzbook.R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }






    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Log.d(TAG, "onLoadingComplete( " +imageUri+ ")");
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

    /**
     *
     */
    private void loadAlreadyPlayng() {
        Log.d(TAG, "loadAlreadyPlayng()");
        SongDetail mSongDetail = MusicPreferance.getLastSong(context);
        ArrayList<SongDetail> playlist = MusicPreferance.getPlaylist(context);
        if (mSongDetail != null) {
            updateSongsDetails(false);
        }
        MediaController.getInstance().checkIsFavorite(context, mSongDetail, img_Favorite);
    }

    /**
     *
     */
    public void addObserver() {
        Log.d(TAG, "addObserver()");
        TAG_Observer = MediaController.getInstance().generateObserverTag();
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().addObserver(this, audioDidStarted);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.newaudioloaded);
    }

    /**
     *
     */
    public void removeObserver() {
        Log.d(TAG, "removeObserver()");
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.newaudioloaded);
    }

    /**
     * Method for update music image when the music is starting play
     * @param mDetail
     */
    public void updateImages(SongDetail mDetail) {
        Log.d(TAG, "updateImages()");
        imageLoader.displayImage(mDetail.getImage_url(), songAlbumbg, options, animateFirstListener);
        imageLoader.displayImage(mDetail.getImage_url(), img_bottom_slideone, options, animateFirstListener);
        imageLoader.displayImage(mDetail.getImage_url(), img_bottom_slidetwo, options, animateFirstListener);
    }

    /**
     * Method for update music title when the music is starting play
     * @param mSongDetail
     */
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
        Log.d(TAG, "newSongLoaded()");
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
        Log.d(TAG, "updateProgress()");
        if (audio_progress != null) {
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
        Log.d(TAG, "PlayPauseEvent()");
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
        Log.d(TAG, "onValueChanged()");
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingSongDetail(), (float) value / 100);
    }




    private void setTranslucentStatus(boolean on) {
        Log.d(TAG, "setTranslucentStatus()");
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
