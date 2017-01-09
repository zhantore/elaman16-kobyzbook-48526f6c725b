package kz.qobyzbook.D_AudioLesson;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import kz.qobyzbook.C_Lessons.LessonNote;
import kz.qobyzbook.R;
import kz.qobyzbook.childfragment.DialogAlert;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.manager.MusicPreferance;
import kz.qobyzbook.manager.NotificationManager;
import kz.qobyzbook.models.SongDetail;
import kz.qobyzbook.phonemidea.DMPlayerUtility;
import kz.qobyzbook.phonemidea.PhoneMediaControl;
import kz.qobyzbook.slidinguppanelhelper.SlidingUpPanelLayout;
import kz.qobyzbook.uicomponent.PlayPauseView;
import kz.qobyzbook.uicomponent.Slider;
import kz.qobyzbook.utility.LogWriter;

public class AudioActivity extends AppCompatActivity implements DialogAlert, View.OnClickListener, Slider.OnValueChangedListener,
        NotificationManager.NotificationCenterDelegate {

    private static final String TAG = "AudioActivity";
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();
    AllSongsListAdapter mAllSongsListAdapter;
    private ProgressDialog progress;
    Integer artistId;
    RelativeLayout rl_internet, rl_downloading;
    MediaController mediaController = new MediaController();
    Button btn_update;
    ImageLoader imageLoader = ImageLoader.getInstance();
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_audio_page);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            artistId = b.getInt("id");
            title = b.getString("title");
        }
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mediaController.setOnEventListener(this);
        initiSlidingUpPanel();
        loadAlreadyPaing();
        addObserver();
        setupInitialViews();
        fillContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     */
    private void loadAllSongs() {
        Log.d(TAG, "loadAllSongs()");
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (!songsList_.isEmpty())
                    rl_downloading.setVisibility(View.INVISIBLE);
                else
                    rl_downloading.setVisibility(View.INVISIBLE);           }
        });
        mPhoneMediaControl.loadMusicList(AudioActivity.this, artistId, PhoneMediaControl.SonLoadFor.AllSongs, "");
    }

    /**
     *
     */
    void fillContent(){
        Log.d(TAG, "fillContent()");
        if (isOnline()) {
            rl_downloading.setVisibility(View.VISIBLE);
            rl_internet.setVisibility(View.INVISIBLE);
            loadAllSongs();
        }
        else {
            rl_downloading.setVisibility(View.INVISIBLE);
            rl_internet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Initializing components
     */
    private void setupInitialViews() {
        Log.d(TAG, "setupInitialViews()");
        ListView songslist = (ListView) findViewById(R.id.recycler_allSongs);
        mAllSongsListAdapter = new AllSongsListAdapter(AudioActivity.this);
        songslist.setAdapter(mAllSongsListAdapter);
        rl_downloading = (RelativeLayout)findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) findViewById(R.id.rl_internet);
        btn_update = (Button)findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_update){
                    if (isOnline())
                        fillContent();
                    else
                        Toast.makeText(AudioActivity.this, getResources().getString(R.string.no_connect),Toast.LENGTH_SHORT).show();
                }
            }
        });
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }



    /**
     * Adapter for music list
     */
    public class AllSongsListAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        public AllSongsListAdapter(Context mContext) {
            Log.d(TAG, "AllSongsListAdapter()");
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

            AllSongsListAdapter.ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new AllSongsListAdapter.ViewHolder();
                convertView = layoutInflater.inflate(R.layout.activity_audio, parent, false);
                mViewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
                mViewHolder.songName = (TextView) convertView.findViewById(R.id.songName);
//                mViewHolder.downloadSong = (ImageView) convertView.findViewById(kz.qobyzbook.R.id.inflate_allsong_imgSongThumb);
//                mViewHolder.playSong = (ImageView) convertView.findViewById(kz.qobyzbook.R.id.img_moreicon);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (AllSongsListAdapter.ViewHolder) convertView.getTag();
            }

            final SongDetail mDetail = songList.get(position);
//            String audioDuration = "";
//            try {
//                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//            mViewHolder.duration.setText(audioDuration);
            mViewHolder.songName.setText(mDetail.getTitle());


            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    SongDetail mDetail = songList.get(position);

                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.AllSongs.ordinal(), -1);
                        }
                    }
                }
            });

//            mViewHolder.songName.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    try {
//                        PopupMenu popup = new PopupMenu(context, v);
//                        popup.getMenuInflater().inflate(kz.qobyzbook.R.menu.list_item_option, popup.getMenu());
//                        popup.show();
//                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//
//                                switch (item.getItemId()) {
//                                    case kz.qobyzbook.R.id.playnext:
//                                        break;
//                                    case kz.qobyzbook.R.id.addtoque:
//                                        break;
//                                    case kz.qobyzbook.R.id.addtoplaylist:
//                                        break;
//                                    case kz.qobyzbook.R.id.gotoartis:
//                                        break;
//                                    case kz.qobyzbook.R.id.gotoalbum:
//                                        break;
//                                    case kz.qobyzbook.R.id.delete:
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
            TextView duration, songName;
            ImageView downloadSong, playSong;
        }
    }


    /**
     *
     * @return
     */
    public boolean isOnline() {
        Log.d(TAG, "isOnline()");
        ConnectivityManager cm =
                (ConnectivityManager)AudioActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void setDialog() {
        Log.d(TAG, "setDialog()");
    }

    @Override
    public void showDialog() {
        Log.d(TAG, "showDialog()");
        progress.setMessage("Buffering...");
        progress.show();
    }

    @Override
    public void hideDialog() {
        Log.d(TAG, "hideDialog()");
        if (progress.isShowing()) {
            progress.cancel();
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
    private DisplayImageOptions options;

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

        imgbtn_toggle.setSelected((MusicPreferance.getRepeat(AudioActivity.this) == 1) ? true : false);
        MediaController.getInstance().shuffleMusic = imgbtn_toggle.isSelected() ? true : false;
        DMPlayerUtility.changeColorSet(AudioActivity.this, (ImageView) imgbtn_toggle, imgbtn_toggle.isSelected());

        imgbtn_suffel.setSelected(MusicPreferance.getShuffel(AudioActivity.this) ? true : false);
        MediaController.getInstance().repeatMode = imgbtn_suffel.isSelected() ? 1 : 0;
        DMPlayerUtility.changeColorSet(AudioActivity.this, (ImageView) imgbtn_suffel, imgbtn_suffel.isSelected());

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
                MusicPreferance.setShuffel(AudioActivity.this, (v.isSelected() ? true : false));
                MediaController.getInstance().shuffleList(MusicPreferance.playlist);
                DMPlayerUtility.changeColorSet(AudioActivity.this, (ImageView) v, v.isSelected());
                break;

            case R.id.btn_toggle:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().repeatMode = v.isSelected() ? 1 : 0;
                MusicPreferance.setRepeat(AudioActivity.this, (v.isSelected() ? 1 : 0));
                DMPlayerUtility.changeColorSet(AudioActivity.this, (ImageView) v, v.isSelected());
                break;

            case R.id.bottombar_img_Favorite:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    MediaController.getInstance().storeFavoritePlay(AudioActivity.this, MediaController.getInstance().getPlayingSongDetail(), v.isSelected() ? 0 : 1);
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
                            mBundle.putString("name",getResources().getStringArray(R.array.lessons_array_kz)[3]);
                        else  mBundle.putString("name",getResources().getStringArray(R.array.lessons_array_en)[3]);

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


    private void loadAlreadyPaing() {
        Log.d(TAG, "loadAlreadyPaing()");
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail != null) {
            updateImages(mSongDetail);
            updateTitles(mSongDetail);
            updateSongsDetails(false);
            MediaController.getInstance().checkIsFavorite(AudioActivity.this, mSongDetail, img_Favorite);
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
        MediaController.getInstance().checkIsFavorite(AudioActivity.this, (SongDetail) args[0], img_Favorite);
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
}
