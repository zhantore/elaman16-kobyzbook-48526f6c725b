package kz.qobyzbook.C_Lessons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;


import java.util.ArrayList;

import kz.qobyzbook.B_Persons.PersonPage;
import kz.qobyzbook.E_VideoLesson.VideoPage;
import kz.qobyzbook.R;
import kz.qobyzbook.activities.DMPlayerBaseActivity;
import kz.qobyzbook.manager.MediaController;
import kz.qobyzbook.models.SongDetail;
import kz.qobyzbook.phonemidea.PhoneMediaControl;
import kz.qobyzbook.utility.LogWriter;


/**
 * Created by Orenk on 22.08.2016.
 */
public class DialogLessons extends android.support.v4.app.DialogFragment {

    SharedPreferences preferences;
    AlertDialog.Builder builder;

    public static final String TITLE_LESSON = "name";
    public static final String TITLE_NOTE = "TITLE_NOTE";
    public static final String DESCRIPTION_LESSON = "description";
    public static final String DESCRIPTION_NOTE = "DESCRIPTION_NOTE";

    Intent intent_text,intent_note;
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String lang = preferences.getString("lang", "kk");
        builder = new AlertDialog.Builder(getActivity());

        if (lang.equals("kk")) {

        builder.setItems(R.array.lessons_array_kz, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent(getActivity(), VideoPage.class);
                        intent.putExtra("title",getResources().getStringArray(R.array.lessons_array_kz)[0]);
                        intent.putExtra("youtubeId",LessonAdapter.List.get(LessonAdapter.pos).getVideo());
                        getActivity().startActivity(intent);
                        break;
                    case 1:
                        try {
                            Intent mIntent = new Intent(getActivity(), PersonPage.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_kz)[1]);
                            mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getMif());
                            mIntent.putExtras(mBundle);
                            getActivity().startActivity(mIntent);
                            getActivity().overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogWriter.info("dialogLesson", e.toString());
                        }
                        break;
                    case 2:
                        try {

                            Intent mIntent = new Intent(getActivity(), PersonPage.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_kz)[2]);
                            mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getUkazanie());
                            mIntent.putExtras(mBundle);
                            getActivity().startActivity(mIntent);
                            getActivity().overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogWriter.info("dialogLesson", e.toString());
                        }
                        break;
                    case 3:
                        ArrayList<SongDetail> musicList = new ArrayList<SongDetail>();
                        long ID = LessonAdapter.List.get(LessonAdapter.pos).getId();
                        long album_id = LessonAdapter.List.get(LessonAdapter.pos).getId();

                        String IMAGE_URL = "http://kazgazeta.kz/wp-content/uploads//IMG_4158.jpg";
                        String DURATION = "200";
                        String Path = LessonAdapter.List.get(LessonAdapter.pos).getAudio();
                        String ARTIST = "Медеубек Мақсат Сағатбекұлы";
                        String TITLE = LessonAdapter.List.get(LessonAdapter.pos).getName();
                        SongDetail mDetail = new SongDetail((int) ID, (int) album_id, ARTIST, TITLE, Path, IMAGE_URL, "" + (Long.parseLong(DURATION) * 1000));
                        musicList.add(mDetail);

                        ((DMPlayerBaseActivity) getActivity()).updateImages(mDetail);
                        ((DMPlayerBaseActivity) getActivity()).updateTitles(mDetail);
                        if (mDetail != null) {
                            if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                                MediaController.getInstance().pauseAudio(mDetail);

                            } else {
                                MediaController.getInstance().setPlaylist(musicList, mDetail, PhoneMediaControl.SonLoadFor.LessonAudio.ordinal(), -1);

                            }
                        }
                        break;
                    case 4:
                        try {
                            Intent mIntent = new Intent(getActivity(), LessonNote.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_kz)[4]);
                            mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getNote());
                            mIntent.putExtras(mBundle);
                            getActivity().startActivity(mIntent);
                            getActivity().overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogWriter.info("dialogLesson", e.toString());
                        }
                        break;
                    case 5:
                        try {
                            Intent mIntent = new Intent(getActivity(), PersonPage.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_kz)[5]);
                            mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getLiter());
                            mIntent.putExtras(mBundle);
                            getActivity().startActivity(mIntent);
                            getActivity().overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogWriter.info("dialogLesson", e.toString());
                        }
                        break;
                }
            }
        });
    }

        else {
            builder.setItems(R.array.lessons_array_en, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            Intent intent = new Intent(getActivity(), VideoPage.class);
                            intent.putExtra("title",getResources().getStringArray(R.array.lessons_array_en)[0]);
                            intent.putExtra("youtubeId",LessonAdapter.List.get(LessonAdapter.pos).getVideo());
                            getActivity().startActivity(intent);
                            break;
                        case 1:
                            try {
                                Intent mIntent = new Intent(getActivity(), PersonPage.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_en)[1]);
                                mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getMif());
                                mIntent.putExtras(mBundle);
                                getActivity().startActivity(mIntent);
                                getActivity().overridePendingTransition(0, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogWriter.info("dialogLesson", e.toString());
                            }
                            break;
                        case 2:
                            try {

                                Intent mIntent = new Intent(getActivity(), PersonPage.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_en)[2]);
                                mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getUkazanie());
                                mIntent.putExtras(mBundle);
                                getActivity().startActivity(mIntent);
                                getActivity().overridePendingTransition(0, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogWriter.info("dialogLesson", e.toString());
                            }
                            break;
                        case 3:
                            break;
                        case 4:
                            try {

                                Intent mIntent = new Intent(getActivity(), LessonNote.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_en)[4]);
                                mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getNote());
                                mIntent.putExtras(mBundle);
                                getActivity().startActivity(mIntent);
                                getActivity().overridePendingTransition(0, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogWriter.info("dialogLesson", e.toString());
                            }
                            break;
                        case 5:
                            try {
                                Intent mIntent = new Intent(getActivity(), PersonPage.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putString(TITLE_LESSON,getResources().getStringArray(R.array.lessons_array_en)[5]);
                                mBundle.putString(DESCRIPTION_LESSON, LessonAdapter.List.get(LessonAdapter.pos).getLiter());
                                mIntent.putExtras(mBundle);
                                getActivity().startActivity(mIntent);
                                getActivity().overridePendingTransition(0, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogWriter.info("dialogLesson", e.toString());
                            }
                            break;

                    }
                }
            });
        }


        return builder.create();

    }
}
