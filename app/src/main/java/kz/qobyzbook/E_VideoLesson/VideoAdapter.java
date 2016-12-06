package kz.qobyzbook.E_VideoLesson;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import kz.qobyzbook.AppController;
import kz.qobyzbook.R;

public class VideoAdapter extends BaseAdapter {
    private static final String TAG = "FragmentVideoQobyz";
   private Activity activity;
   private LayoutInflater inflater;
   private List<VideoModel> listItems = new ArrayList<VideoModel>();
   private TextView nameKz;

   private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

   public VideoAdapter(Activity activity, List<VideoModel> listItems) {
       Log.d(TAG, "VideoAdapter()");
       this.activity = activity;
       this.listItems = listItems;
   }

   @Override
   public int getCount() {
       return listItems.size();
   }

   @Override
   public Object getItem(int position) {
       return listItems.get(position);
   }

   @Override
   public long getItemId(int position) {
       return position;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       Log.d(TAG, "getView()");
       VideoModel videoModel = listItems.get(position);

       if (inflater == null)
           inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       if (convertView == null)
           convertView = inflater.inflate(R.layout.e_item_adapter, null);

       if (imageLoader == null)
           imageLoader = AppController.getInstance().getImageLoader();

       nameKz = (TextView) convertView.findViewById(R.id.textViewNameKz);
       nameKz.setText(videoModel.getName());
//       NetworkImageView networkImage = (NetworkImageView) convertView.findViewById(R.id.networkImage);
       // getting movie data for the row
       // thumbnail image
//       networkImage.setImageUrl(videoModel.getVideo(), imageLoader);
//  networkImage.setImageUrl(videoModel.getNetworkImage(), imageLoader);
//        nameKz.setText(videoModel.getName());
       return convertView;
   }



}
