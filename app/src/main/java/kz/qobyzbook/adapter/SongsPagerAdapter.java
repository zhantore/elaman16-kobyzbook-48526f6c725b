/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kz.qobyzbook.manager.MusicPreferance;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SongsPagerAdapter extends PagerAdapter {

    private static final String TAG = "SongsPagerAdapter";
    private Context context;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public SongsPagerAdapter(Context context_) {
        Log.d(TAG, "SongsPagerAdapter()");
        this.context = context_;
        this.inflater = LayoutInflater.from(context_);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(kz.qobyzbook.R.drawable.bg_default_album_art)
                .showImageForEmptyUri(kz.qobyzbook.R.drawable.bg_default_album_art).showImageOnFail(kz.qobyzbook.R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount()");
        if (MusicPreferance.playlist == null) {
            return 0;
        } else {
            return MusicPreferance.playlist.size();
        }
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        Log.d(TAG, "instantiateItem()");
        View infV = inflater.inflate(kz.qobyzbook.R.layout.inflate_albumart_pager, container, false);
        ImageView artImage = (ImageView) infV.findViewById(kz.qobyzbook.R.id.image_songAlbumbg_mid);
        String contentURI = "content://media/external/audio/media/" + MusicPreferance.playlist.get(position).getId() + "/albumart";
        imageLoader.displayImage(contentURI, artImage, options, animateFirstListener);
        container.addView(infV);
        return infV;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem()");
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Log.d(TAG, "isViewFromObject()");
        return view == object;
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
                    FadeInBitmapDisplayer.animate(imageView, 1000);
                    displayedImages.add(imageUri);
                }
            }
        }

    }
}
