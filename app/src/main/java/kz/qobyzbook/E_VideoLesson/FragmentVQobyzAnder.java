package kz.qobyzbook.E_VideoLesson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kz.qobyzbook.R;

/**
 * Created by zhan on 11/22/16.
 */

public class FragmentVQobyzAnder extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_v_qobyz_song, null);
        return v;
    }
}
