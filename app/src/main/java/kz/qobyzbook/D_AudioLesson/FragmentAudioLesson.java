/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.D_AudioLesson;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kz.qobyzbook.R;
import kz.qobyzbook.tablayout.SlidingTabLayout;

public class FragmentAudioLesson extends Fragment {

    private final String[] TITLES = {""};
    private TypedValue typedValueToolbarHeight = new TypedValue();

    private FragmentArtists fragmentArtists;
    private FragmentAllSongs fragmentAllSongs;
    private FragmentAQobyzAnder fragmentAQobyzAnder;

    private MyPagerAdapter pagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private int tabsPaddingTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_library, null);
//        TITLES[1] = getActivity().getResources().getString(R.string.kuishiler);
        TITLES[0] = getActivity().getResources().getString(R.string.kuiler);
//        TITLES[2] = getActivity().getResources().getString(R.string.lessons_song);
        setupView(v);
        return v;
    }


    private void setupView(View view) {
        pager = (ViewPager) view.findViewById(R.id.pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);
        // Tab indicator color
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });
//        tabs.setViewPager(pager);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
//                case 0:
//                    fragmentArtists = new FragmentArtists();
//                    return fragmentArtists;
                case 0:
                    fragmentAllSongs = new FragmentAllSongs();
                    return fragmentAllSongs;
//                case 2:
//                    fragmentAQobyzAnder = new FragmentAQobyzAnder();
//                    return fragmentAQobyzAnder;
            }
            return null;
        }
    }

    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
