package kz.qobyzbook.C_Lessons;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kz.qobyzbook.R;

/**
 * Created by zhan on 12/26/16.
 */

public class LessonQobyzAnder extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qobyz_song, null);
        return v;
    }
}
