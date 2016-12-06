package kz.qobyzbook.A_AboutQobyz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kz.qobyzbook.R;
import kz.qobyzbook.activities.DMPlayerBaseActivity;
import kz.qobyzbook.phonemidea.PhoneMediaControl;
import kz.qobyzbook.utility.LogWriter;
import kz.qobyzbook.utility.RecyclerItemClickListener;

import java.util.List;


public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.MyViewHolder>  {

    private List<String> aboutQobyzList;
    private Context mContext;
    private RecyclerView recyclerView;
    private static final String TAG = "AboutAdapter";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.song_title);
        }
    }

    public AboutAdapter(Context mContext, List<String> qobyzModelList, RecyclerView recyclerView) {
        this.aboutQobyzList = qobyzModelList;
        this.mContext = mContext;
        this.recyclerView = recyclerView;
    }

    public AboutAdapter(){}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_about, parent, false);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, recyclerView , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {

                            Intent mIntent = new Intent(mContext, AboutPage.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putInt("id", position);
                            mBundle.putString("name", aboutQobyzList.get(position));
                            mIntent.putExtras(mBundle);
                            ((Activity) mContext).startActivity(mIntent);
                            ((Activity) mContext).overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogWriter.info(TAG, e.toString());
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
        Log.d(TAG, "onBindViewHolder()");
        holder.title.setText(aboutQobyzList.get(position));
    }


    @Override
    public int getItemCount() {
        return aboutQobyzList.size();
    }

}