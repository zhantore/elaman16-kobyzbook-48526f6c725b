/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.D_AudioLesson;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import kz.qobyzbook.R;
import kz.qobyzbook.activities.DMPlayerBaseActivity;
import kz.qobyzbook.childfragment.DialogAlert;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.models.SongDetail;
import kz.qobyzbook.phonemidea.DMPlayerUtility;
import kz.qobyzbook.phonemidea.PhoneMediaControl;
import kz.qobyzbook.phonemidea.PhoneMediaControl.PhoneMediaControlINterface;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentAllSongs extends Fragment implements DialogAlert {

    private static final String TAG = "FragmentAllSongs";
    private ListView recycler_songslist;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();
    private ProgressDialog progress;
    MediaController mediaController = new MediaController();

    RelativeLayout rl_internet,rl_downloading;
    Button btn_update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View v = inflater.inflate(kz.qobyzbook.R.layout.fragment_allsongs, container, false);
        progress = new ProgressDialog(getActivity());
        setupInitialViews(v);
        fillContent();

        mediaController.setOnEventListener(this);
        return v;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    /**
     * Initializing components
     * @param v
     */
    private void setupInitialViews(View v) {
        Log.d(TAG, "setupInitialViews()");
        recycler_songslist = (ListView) v.findViewById(kz.qobyzbook.R.id.recycler_allSongs);
        mAllSongsListAdapter = new AllSongsListAdapter(getActivity());
        recycler_songslist.setAdapter(mAllSongsListAdapter);
        rl_downloading = (RelativeLayout)v.findViewById(R.id.circle_bg);
        rl_internet = (RelativeLayout) v.findViewById(R.id.rl_internet);
        btn_update = (Button)v.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_update){
                    if (isOnline())
                        fillContent();
                    else
                        Toast.makeText(getActivity() , getResources().getString(R.string.no_connect),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *
     */
    private void loadAllSongs() {
        Log.d(TAG, "loadAllSongs()");
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (!songsList_.isEmpty())
                    rl_downloading.setVisibility(View.INVISIBLE);
                else
                    rl_downloading.setVisibility(View.INVISIBLE);

            }
        });
        mPhoneMediaControl.loadMusicList(getActivity(), -1, PhoneMediaControl.SonLoadFor.Artists, "");
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
     *
     * @return
     */
    public boolean isOnline() {
        Log.d(TAG, "isOnline()");
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


    /**
     *
     */
    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions options;
        ImageLoader imageLoader = ImageLoader.getInstance();

        public AllSongsListAdapter(Context mContext) {
            Log.d(TAG, "AllSongsListAdapter()");
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
            this.options = new DisplayImageOptions.Builder().showImageOnLoading(kz.qobyzbook.R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(kz.qobyzbook.R.drawable.bg_default_album_art).showImageOnFail(kz.qobyzbook.R.drawable.bg_default_album_art).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
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
                convertView = layoutInflater.inflate(kz.qobyzbook.R.layout.inflate_allsongsitem, null);
                mViewHolder.song_row = (LinearLayout) convertView.findViewById(kz.qobyzbook.R.id.inflate_allsong_row);
                mViewHolder.textViewSongName = (TextView) convertView.findViewById(kz.qobyzbook.R.id.inflate_allsong_textsongname);
                mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(kz.qobyzbook.R.id.inflate_allsong_textsongArtisName_duration);
                mViewHolder.imageSongThm = (ImageView) convertView.findViewById(kz.qobyzbook.R.id.inflate_allsong_imgSongThumb);
                mViewHolder.imagemore = (ImageView) convertView.findViewById(kz.qobyzbook.R.id.img_moreicon);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            final SongDetail mDetail = songList.get(position);
//            String audioDuration = "";
//            try {
//                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
            mViewHolder.textViewSongArtisNameAndDuration.setText(mDetail.getArtist());
            mViewHolder.textViewSongName.setText(mDetail.getTitle());
            imageLoader.displayImage(mDetail.getImage_url(), mViewHolder.imageSongThm, options);


            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AudioActivity.class)
                            .putExtra("id", mDetail.getId())
                            .putExtra("title", mDetail.getTitle()));
//                    SongDetail mDetail = songList.get(position);
//                    ((DMPlayerBaseActivity) getActivity()).updateImages(mDetail);
//                    ((DMPlayerBaseActivity) getActivity()).updateTitles(mDetail);
//
//                    if (mDetail != null) {
//                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
//                            MediaController.getInstance().pauseAudio(mDetail);
//                        } else {
//                            MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.AllSongs.ordinal(), -1);
//                        }
//                    }
                }
            });

            mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
            if (Build.VERSION.SDK_INT > 15) {
                mViewHolder.imagemore.setImageAlpha(255);
            } else {
                mViewHolder.imagemore.setAlpha(255);
            }

            mViewHolder.imagemore.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(kz.qobyzbook.R.menu.list_item_option, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case kz.qobyzbook.R.id.playnext:
                                        break;
                                    case kz.qobyzbook.R.id.addtoque:
                                        break;
                                    case kz.qobyzbook.R.id.addtoplaylist:
                                        break;
                                    case kz.qobyzbook.R.id.gotoartis:
                                        break;
                                    case kz.qobyzbook.R.id.gotoalbum:
                                        break;
                                    case kz.qobyzbook.R.id.delete:
                                        break;
                                    default:
                                        break;
                                }

                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

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

    /**
     *
     * @param img
     * @return
     */
    public String imageConvert(String img) {
        Log.d(TAG, "imageConvert()");
        if (img != null) {
            img = "http://admin.unimax.kz" + img;
            img = img.replace("~/", "/");
            img = img.replace(" ", "%20");
        }
        return img;
    }
}


