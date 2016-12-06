package kz.qobyzbook.B_Persons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kz.qobyzbook.A_AboutQobyz.AboutPage;
import kz.qobyzbook.A_AboutQobyz.Qobyz;
import kz.qobyzbook.R;
import kz.qobyzbook.phonemidea.PhoneMediaControl;
import kz.qobyzbook.utility.LogWriter;
import kz.qobyzbook.utility.RecyclerItemClickListener;


/**
 * Created by Orenk on 14.09.2016.
 */
public class PersonAdapter  extends RecyclerView.Adapter<PersonAdapter.MyViewHolder>   {
    private List<Qobyz> aboutQobyzList;
    private Context mContext;
    private RecyclerView recyclerView;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.song_title);
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
                .inflate(R.layout.item_about, parent, false);

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
    }


    @Override
    public int getItemCount() {
        return aboutQobyzList.size();
    }
}
