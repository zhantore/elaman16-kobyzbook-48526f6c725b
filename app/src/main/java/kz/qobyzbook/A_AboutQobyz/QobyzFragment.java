package kz.qobyzbook.A_AboutQobyz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

import kz.qobyzbook.F_Test.FragmentTest;
import kz.qobyzbook.R;

public class QobyzFragment extends Fragment {

    private static final String TAG = "QobyzFragment";
    //Variables
    ArrayList<Qobyz> qobyzArrayList;
    ArrayList<String> qobyzList;
    AboutAdapter mAdapter;

    //Components
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.a_about_qobyz, null);
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.about_qobyz));


        //Initialising components...
        qobyzArrayList = new ArrayList<>();
        fillData();

        //Recycler View
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AboutAdapter(getActivity(), qobyzList, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        return view;

    }

    void fillData(){
        Log.d(TAG, "fillData()");
        String aboutQobyzA = getResources().getString(R.string.about_qobyz_a);
        String aboutQobyzB = getResources().getString(R.string.about_qobyz_b);
        String aboutQobyzC = getResources().getString(R.string.about_qobyz_c);
        String aboutQobyzD = getResources().getString(R.string.about_qobyz_d);
        String aboutQobyzE = getResources().getString(R.string.about_qobyz_e);
        String aboutQobyzF = getResources().getString(R.string.about_qobyz_f);
        String aboutQobyzG = getResources().getString(R.string.about_qobyz_g);

        qobyzList = new ArrayList<String>();
        qobyzList.add(aboutQobyzA);
        qobyzList.add(aboutQobyzB);
        qobyzList.add(aboutQobyzC);
        qobyzList.add(aboutQobyzD);
        qobyzList.add(aboutQobyzE);
        qobyzList.add(aboutQobyzF);
        qobyzList.add(aboutQobyzG);
    }
    //Fill list with data from Server using JSON


}