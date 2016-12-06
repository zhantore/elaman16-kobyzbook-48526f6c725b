/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */

package kz.qobyzbook.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kz.qobyzbook.C_Lessons.LessonNote;
import kz.qobyzbook.R;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.manager.MusicPreferance;
import kz.qobyzbook.manager.NotificationManager;
import kz.qobyzbook.models.SongDetail;
import kz.qobyzbook.observablelib.ObservableScrollView;
import kz.qobyzbook.observablelib.ObservableScrollViewCallbacks;
import kz.qobyzbook.observablelib.ScrollState;
import kz.qobyzbook.observablelib.ScrollUtils;
import kz.qobyzbook.phonemidea.DMPlayerUtility;
import kz.qobyzbook.phonemidea.PhoneMediaControl;
import kz.qobyzbook.slidinguppanelhelper.SlidingUpPanelLayout;
import kz.qobyzbook.uicomponent.ExpandableHeightListView;
import kz.qobyzbook.uicomponent.PlayPauseView;
import kz.qobyzbook.uicomponent.Slider;
import kz.qobyzbook.utility.LogWriter;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AlbumAndArtisDetailsActivity extends ActionBarActivity implements View.OnClickListener, ObservableScrollViewCallbacks, Slider.OnValueChangedListener,
        NotificationManager.NotificationCenterDelegate {

    private static final String TAG = "AlbumAndArtisDActivity";
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private SharedPreferences sharedPreferences;
    private int color = 0xFFFFFF;
    private Context context;

    private long id = -1;
    private long tagFor = -1;
    private String albumname = "";
    private String title_one = "";
    private String title_sec = "";
    private String image = "";
    private ImageView banner;
    private FloatingActionButton fab_button;
    private TextView tv_albumname, tv_title_fst, tv_title_sec;
    private ExpandableHeightListView recycler_songslist;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();


    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        //Set your theme first
        context = AlbumAndArtisDetailsActivity.this;
        theme();
        //Set your Layout view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumandartisdetails);

        initialize();
        getBundleValuse();

        initiSlidingUpPanel();
        loadAlreadyPaing();
        addObserver();
        fabanim();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
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
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        Log.d(TAG, "onScrollChanged()");
        int baseColor = color;
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(banner, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
        Log.d(TAG, "onDownMotionEvent()");
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.d(TAG, "onUpOrCancelMotionEvent()");
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarView = findViewById(R.id.toolbar);

        // Setup RecyclerView inside drawer
        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, color));
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        banner = (ImageView) findViewById(R.id.banner);
        tv_albumname = (TextView) findViewById(R.id.tv_albumname);
        tv_title_fst = (TextView) findViewById(R.id.tv_title_frst);
        tv_title_sec = (TextView) findViewById(R.id.tv_title_sec);
        recycler_songslist = (ExpandableHeightListView) findViewById(R.id.recycler_allSongs);
        mAllSongsListAdapter = new AllSongsListAdapter(context);

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
        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList.size()>0) {
                    SongDetail mDetail = songList.get(0);
                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, (int) tagFor, (int) id);
                        }
                    }
                }
            }
        });
    }

    private void getBundleValuse() {
        Log.d(TAG, "getBundleValuse()");
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            id = mBundle.getLong("id");
            tagFor = mBundle.getLong("tagfor");
            albumname = mBundle.getString("albumname");
            title_one = mBundle.getString("title_one");
            title_sec = mBundle.getString("title_sec");
            image = mBundle.getString("image");
        }

        if (tagFor == PhoneMediaControl.SonLoadFor.Gener.ordinal()) {
            loadGenersSongs(id);
        } else if (tagFor == PhoneMediaControl.SonLoadFor.Artists.ordinal()) {
            loadArtistSongs(id);
        } else if (tagFor == PhoneMediaControl.SonLoadFor.LessonAudio.ordinal()) {
            loadArtisSongs(id);
        } else {
        }

        tv_albumname.setText(albumname);
        tv_title_fst.setText(title_one);
        tv_title_sec.setText(title_sec+" "+getResources().getString(R.string.count_kui));
    }


    private void loadArtistSongs(long id) {
        Log.d(TAG, "loadArtistSongs()");
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    tv_title_sec.setText(songList.size() + getResources().getString(R.string.count_kui));
                }

                recycler_songslist.setAdapter(mAllSongsListAdapter);
            }
        });
        mPhoneMediaControl.loadMusicList(context, id, PhoneMediaControl.SonLoadFor.Artists, "");
        imageLoader.displayImage(image, banner, options);
    }

    private void loadArtisSongs(long id) {
        Log.d(TAG, "loadArtisSongs()");
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    imageLoader.displayImage(songList.get(0).getImage_url(), banner, options);
                }
            }
        });
        mPhoneMediaControl.loadMusicList(context, id, PhoneMediaControl.SonLoadFor.LessonAudio, "");
    }

    private void loadGenersSongs(long id) {
        Log.d(TAG, "loadGenersSongs()");
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    String contentURI = "content://media/external/audio/media/" + songList.get(0).getId() + "/albumart";
                    imageLoader.displayImage(contentURI, banner, options);
                    tv_title_sec.setText(songList.size() + getResources().getString(R.string.count_kui));
                }
            }
        });
        mPhoneMediaControl.loadMusicList(context, id, PhoneMediaControl.SonLoadFor.Gener, "");
    }


    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;

        public AllSongsListAdapter(Context mContext) {
            Log.d(TAG, "AllSongsListAdapter()");
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView()");

            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.inflate_allsongsitem, null);
                mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
                mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
                mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
                mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);

                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            SongDetail mDetail = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mViewHolder.textViewSongArtisNameAndDuration.setText(mDetail.getArtist());
            mViewHolder.textViewSongName.setText(mDetail.getTitle());
            imageLoader.displayImage(mDetail.getImage_url(), mViewHolder.imageSongThm, options);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    SongDetail mDetail = songList.get(position);
                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, (int) tagFor, (int) id);
                        }
                    }

                }
            });
//            mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
//            if (Build.VERSION.SDK_INT > 15) {
//                mViewHolder.imagemore.setImageAlpha(255);
//            } else {
//                mViewHolder.imagemore.setAlpha(255);
//            }
//
//            mViewHolder.imagemore.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    try {
//                        PopupMenu popup = new PopupMenu(context, v);
//                        popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
//                        popup.show();
//                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//
//                                switch (item.getItemId()) {
//                                    case R.id.playnext:
//                                        break;
//                                    case R.id.addtoque:
//                                        break;
//                                    case R.id.addtoplaylist:
//                                        break;
//                                    case R.id.gotoartis:
//                                        break;
//                                    case R.id.gotoalbum:
//                                        break;
//                                    case R.id.delete:
//                                        break;
//                                    default:
//                                        break;
//                                }
//
//                                return true;
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            return convertView;
        }

        @Override
        public int getCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder {
            TextView textViewSongName;
            ImageView imageSongThm, imagemore;
            TextView textViewSongArtisNameAndDuration;
            LinearLayout song_row;
        }
    }



    /*-----------------AllSongs Work Related to Slide Panel-----------------*/

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
            Log.d(TAG, "onLoadingComplete()");
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


    private void fabanim() {
        Log.d(TAG, "fabanim()");
        ObjectAnimator anim = ObjectAnimator.ofFloat(fab_button, "scaleX", 0.0f, 1.0f);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(fab_button, "scaleY", 0.0f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(fab_button, "alpha", 0.0f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim, anim1, anim2);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

}
