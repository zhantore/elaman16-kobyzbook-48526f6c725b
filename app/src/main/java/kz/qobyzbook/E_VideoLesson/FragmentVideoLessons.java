package kz.qobyzbook.E_VideoLesson;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kz.qobyzbook.D_AudioLesson.FragmentAllSongs;
import kz.qobyzbook.D_AudioLesson.FragmentArtists;
import kz.qobyzbook.D_AudioLesson.FragmentAudioLesson;
import kz.qobyzbook.R;
import kz.qobyzbook.tablayout.SlidingTabLayout;

/**
 * Created by zhan on 11/22/16.
 */

public class FragmentVideoLessons extends Fragment {

    private final String[] TITLES = {"", "", ""};
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private FragmentVideoQobyz fragmentVideoQobyz;
    private FragmentVideoDombyra fragmentVideoDombyra;
    private FragmentVQobyzAnder fragmentVQobyzAnder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, null);
        TITLES[0] = getActivity().getResources().getString(R.string.lessons_qobyz);
        TITLES[1] = getActivity().getResources().getString(R.string.lessons_dombyra);
        TITLES[2] = getActivity().getResources().getString(R.string.lessons_song);
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
        tabs.setViewPager(pager);
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
                case 0:
                    fragmentVideoQobyz = new FragmentVideoQobyz();
                    return fragmentVideoQobyz;
                case 1:
                    fragmentVideoDombyra = new FragmentVideoDombyra();
                    return fragmentVideoDombyra;
                case 2:
                    fragmentVQobyzAnder = new FragmentVQobyzAnder();
                    return fragmentVQobyzAnder;
            }
            return null;
        }
    }
}
