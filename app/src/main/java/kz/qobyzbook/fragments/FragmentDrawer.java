package kz.qobyzbook.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import kz.qobyzbook.R;

public class FragmentDrawer extends Fragment {


    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private FragmentDrawerListener drawerListener;


    //Components...
    RelativeLayout rl_zero, rl_one_lost,rl_two_found,rl_three_free,rl_four_help,rl_five_add,
            rl_six_share, rl_seven_settings, rl_eight_about,rl_nine_settings, rl_ten;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating view layout...
        View layout = inflater.inflate(R.layout.fr_nav_drawer, container, false);

        //Initilizing
        initializeComponents(layout);

        //Click menu item
        setClickEvent();

        return layout;
    }

    View.OnClickListener item_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_zero:{
                    drawerListener.onDrawerItemSelected(1);
                }
                break;
                case R.id.rl_one:{
                    drawerListener.onDrawerItemSelected(2);
                } break;
                case R.id.rl_two:{
                    drawerListener.onDrawerItemSelected(3);
                }break;
                case R.id.rl_three:{
                    drawerListener.onDrawerItemSelected(4);
                } break;
                case R.id.rl_four:{
                    drawerListener.onDrawerItemSelected(5);
                }break;
                case R.id.rl_five:{
                    drawerListener.onDrawerItemSelected(6);
                } break;
                case R.id.rl_six:{
                    drawerListener.onDrawerItemSelected(7);
                }break;
                case R.id.rl_seven:{
                    drawerListener.onDrawerItemSelected(8);
                } break;
                case R.id.rl_eight:{
                    drawerListener.onDrawerItemSelected(9);
                }break;
                case R.id.rl_nine:{
                    drawerListener.onDrawerItemSelected(10);
                }   break;
                case R.id.rl_ten:{
                    drawerListener.onDrawerItemSelected(11);
                }
                break;

            }
        }
    };

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    void initializeComponents(View view){

        //RelativeLayouts for control choosing item
        rl_zero = (RelativeLayout)view.findViewById(R.id.rl_zero);
        rl_one_lost = (RelativeLayout) view.findViewById(R.id.rl_one);
        rl_two_found = (RelativeLayout) view.findViewById(R.id.rl_two);
        rl_three_free = (RelativeLayout) view.findViewById(R.id.rl_three);
        rl_four_help = (RelativeLayout) view.findViewById(R.id.rl_four);
        rl_five_add = (RelativeLayout) view.findViewById(R.id.rl_five);
        rl_six_share = (RelativeLayout) view.findViewById(R.id.rl_six);
        rl_seven_settings = (RelativeLayout) view.findViewById(R.id.rl_seven);
        rl_eight_about = (RelativeLayout) view.findViewById(R.id.rl_eight);
        rl_nine_settings = (RelativeLayout) view.findViewById(R.id.rl_nine);
        rl_ten = (RelativeLayout)view.findViewById(R.id.rl_ten);
    }

    void setClickEvent(){

        //Click event setting
        rl_zero.setOnClickListener(item_clicked);
        rl_one_lost.setOnClickListener(item_clicked);
        rl_two_found.setOnClickListener(item_clicked);
        rl_three_free.setOnClickListener(item_clicked);
        rl_four_help.setOnClickListener(item_clicked);
        rl_five_add.setOnClickListener(item_clicked);
        rl_six_share.setOnClickListener(item_clicked);
        rl_seven_settings.setOnClickListener(item_clicked);
        rl_eight_about.setOnClickListener(item_clicked);
        rl_nine_settings.setOnClickListener(item_clicked);
        rl_ten.setOnClickListener(item_clicked);
    }

    // Some methods...
    public FragmentDrawer() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(int position);
    }


}
