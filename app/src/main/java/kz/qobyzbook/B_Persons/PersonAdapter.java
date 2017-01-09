package kz.qobyzbook.B_Persons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kz.qobyzbook.A_AboutQobyz.AboutPage;
import kz.qobyzbook.A_AboutQobyz.Qobyz;
import kz.qobyzbook.R;
import kz.qobyzbook.phonemidea.PhoneMediaControl;
import kz.qobyzbook.utility.LogWriter;
import kz.qobyzbook.utility.RecyclerItemClickListener;

import static kz.qobyzbook.R.id.imageView;


/**
 * Created by Orenk on 14.09.2016.
 */
public class PersonAdapter  extends RecyclerView.Adapter<PersonAdapter.MyViewHolder>   {
    private List<Qobyz> aboutQobyzList;
    private Context mContext;
    private RecyclerView recyclerView;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        ImageView imageView;
        private ImageLoader imageLoader;
        private DisplayImageOptions options;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.song_title);
            imageView = (ImageView)view.findViewById(R.id.inflate_allsong_imgSongThumb);
            imageLoader = ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }
    }

    public PersonAdapter(Context mContext, List<Qobyz> qobyzModelList, RecyclerView recyclerView) {
        this.aboutQobyzList = qobyzModelList;
        this.mContext = mContext;
        this.recyclerView = recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kobyz_player, parent, false);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, recyclerView , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {

                            Intent mIntent = new Intent(mContext, PersonPage.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putLong("id", position);
                            mBundle.putString("name", aboutQobyzList.get(position).getName());
                            mBundle.putString("description", aboutQobyzList.get(position).getDescription());
                            mBundle.putString("photo", aboutQobyzList.get(position).getPhoto());
                            mBundle.putString("sphoto", aboutQobyzList.get(position).getSphoto());
                            mIntent.putExtras(mBundle);
                            ((Activity) mContext).startActivity(mIntent);
                            ((Activity) mContext).overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogWriter.info("personAdapter", e.toString());
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Qobyz album = aboutQobyzList.get(position);
        holder.title.setText(album.getName());
        holder.imageLoader.displayImage(convertURL(album.getPhoto()), holder.imageView, holder.options);
    }

    @Override
    public int getItemCount() {
        return aboutQobyzList.size();
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
