package kz.qobyzbook.C_Lessons;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kz.qobyzbook.R;
import kz.qobyzbook.utility.RecyclerItemClickListener;


public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.MyViewHolder>  {

    public static java.util.List<Lesson> List;
    public static int pos;
    private Context mContext;
    private RecyclerView recyclerView;
    DialogLessons dialogLessons = new DialogLessons();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.song_title);
        }
    }

    public LessonAdapter(Context mContext, java.util.List<Lesson> qobyzModelList, RecyclerView recyclerView) {
        this.List = qobyzModelList;
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
                        pos = position;
                        dialogLessons.show(((AppCompatActivity)mContext).getSupportFragmentManager(),"lang");

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Lesson lesson = List.get(position);
        holder.title.setText(lesson.getName());
    }


    @Override
    public int getItemCount() {
        return List.size();
    }
}