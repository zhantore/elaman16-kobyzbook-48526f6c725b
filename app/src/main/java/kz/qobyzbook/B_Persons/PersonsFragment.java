/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package kz.qobyzbook.B_Persons;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kz.qobyzbook.R;
import kz.qobyzbook.tablayout.SlidingTabLayout;

public class PersonsFragment extends Fragment {

    private static final String TAG = "PersonsFragment";
    private final String[] TITLES = {"a","b"};
    private TypedValue typedValueToolbarHeight = new TypedValue();
    private NewPersons newPersons;
    private OldPersons oldPersons;
    private MyPagerAdapter pagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private int tabsPaddingTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        View v = inflater.inflate(kz.qobyzbook.R.layout.fragment_library, null);
        TITLES[0] = getActivity().getResources().getString(R.string.old_person);
        TITLES[1] = getActivity().getResources().getString(R.string.new_person);
        setupView(v);
        return v;
    }


    private void setupView(View view) {
        Log.d(TAG, "setupView()");
        pager = (ViewPager) view.findViewById(R.id.pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs = (SlidingTabLayout) view.findViewById(kz.qobyzbook.R.id.tabs);
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
            Log.d(TAG, "getItem()");
            switch (position) {
                case 0:
                    oldPersons = new OldPersons();
                    return oldPersons;
                case 1:
                    newPersons = new NewPersons();
                    return newPersons;
            }
            return null;
        }
    }

    public int convertToPx(int dp) {
        Log.d(TAG, "convertToPx()");
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
