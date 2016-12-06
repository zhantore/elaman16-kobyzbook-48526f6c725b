/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kz.qobyzbook.D_AudioLesson.MenuItemClickListener;
import kz.qobyzbook.R;
import kz.qobyzbook.models.DrawerItem;
import kz.qobyzbook.phonemidea.DMPlayerUtility;

import java.util.ArrayList;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
    private static final String TAG = "DrawerAdapter";
    private ArrayList<DrawerItem> drawerItems;
    Context context;
    SharedPreferences sharedPreferences;
    int color;
    private int theme;
    MenuItemClickListener menuItemClickListener;

    public DrawerAdapter(ArrayList<DrawerItem> drawerItems, Context context) {
        Log.d(TAG, "DrawerAdapter()");
        this.drawerItems = drawerItems;
        this.context = context;

        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(kz.qobyzbook.R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

        sharedPreferences = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);
        DMPlayerUtility.settingTheme(context, theme);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView icon;
        RelativeLayout relativeLayoutDrawerItem;
        public ViewHolder(View view) {
            super(view);
            Log.d(TAG, "ViewHolder()");
            title = (TextView) view.findViewById(R.id.textViewDrawerItemTitle);
            icon = (ImageView) view.findViewById(R.id.imageViewDrawerIcon);
            relativeLayoutDrawerItem = (RelativeLayout) view.findViewById(R.id.relativeLayoutDrawerItem);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick()");
            if (true) {
                icon.setColorFilter(color);
                if (Build.VERSION.SDK_INT > 15) {
                    icon.setImageAlpha(255);
                } else {
                    icon.setAlpha(255);
                }
                title.setTextColor(color);

                TypedValue typedValueDrawerSelected = new TypedValue();
                context.getTheme().resolveAttribute(kz.qobyzbook.R.attr.colorPrimary, typedValueDrawerSelected, true);
                int colorDrawerItemSelected = typedValueDrawerSelected.data;
                colorDrawerItemSelected = (colorDrawerItemSelected & 0x00FFFFFF) | 0x30000000;
                relativeLayoutDrawerItem.setBackgroundColor(colorDrawerItemSelected);

            } else {
                icon.setColorFilter(context.getResources().getColor(kz.qobyzbook.R.color.md_text));
                if (Build.VERSION.SDK_INT > 15) {
                    icon.setImageAlpha(138);
                } else {
                    icon.setAlpha(138);
                }
                title.setTextColor(context.getResources().getColor(kz.qobyzbook.R.color.md_text));
                relativeLayoutDrawerItem.setBackgroundColor(context.getResources().getColor(kz.qobyzbook.R.color.md_white_1000));
            }

            Log.d(TAG,"click menu");
            menuItemClickListener.menuItemClicked(getAdapterPosition());
        }
    }


    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder()");
        holder.title.setText(drawerItems.get(position).getTitle());
        holder.icon.setImageDrawable(drawerItems.get(position).getIcon());

        if (position == sharedPreferences.getInt("FRAGMENT", 0)) {

            holder.icon.setColorFilter(color);
            if (Build.VERSION.SDK_INT > 15) {
                holder.icon.setImageAlpha(255);
            } else {
                holder.icon.setAlpha(255);
            }
            holder.title.setTextColor(color);

            TypedValue typedValueDrawerSelected = new TypedValue();
            context.getTheme().resolveAttribute(kz.qobyzbook.R.attr.colorPrimary, typedValueDrawerSelected, true);
            int colorDrawerItemSelected = typedValueDrawerSelected.data;
            colorDrawerItemSelected = (colorDrawerItemSelected & 0x00FFFFFF) | 0x30000000;
            holder.relativeLayoutDrawerItem.setBackgroundColor(colorDrawerItemSelected);

        } else {
            holder.icon.setColorFilter(context.getResources().getColor(kz.qobyzbook.R.color.md_text));
            if (Build.VERSION.SDK_INT > 15) {
                holder.icon.setImageAlpha(138);
            } else {
                holder.icon.setAlpha(138);
            }
            holder.title.setTextColor(context.getResources().getColor(kz.qobyzbook.R.color.md_text));
            holder.relativeLayoutDrawerItem.setBackgroundColor(context.getResources().getColor(kz.qobyzbook.R.color.md_white_1000));
        }


        //click handler
          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

              }
          });


    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    public void setOnMenuItemClickListener(MenuItemClickListener menuItemClickListener){
        this.menuItemClickListener = menuItemClickListener;
    }
}


