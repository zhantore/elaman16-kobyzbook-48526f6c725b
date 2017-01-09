package kz.qobyzbook.F_Test;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kz.qobyzbook.AppController;
import kz.qobyzbook.R;
import kz.qobyzbook.activities.DMPlayerBaseActivity;

/**
 * Created by Orenk on 26.09.2016.
 */

public class FragmentTest extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String urlJsonArray = "http://api.kobyzbook.kz/api/tests";
    private static final String TAG = "FragmentTest";
    private List<TestModel> testArray = new ArrayList<TestModel>();

    private String answ1 = "answ1", answ2 = "answ2", answ3 = "answ3", answ4 = "answ4", answ5 = "answ5", question = "question", right = "right";
    String answ1TextS = null, answ2TextS = null, answ3TextS = null, answ4TextS = null, answ5TextS = null, questionTextS = null, rightTextS = null, result = null, resultCounter = null;
    TextView answ1Text, answ2Text, answ3Text, answ4Text, answ5Text, questionText,counterText;
    private int j = 0; int testCount = 0; String countBall; int limitClick =1; int counter;
    Context context;
    private ProgressDialog pDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout relative_layout, relative_layoutG;
    Handler handler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.f_test, null);
        context=getActivity();
        initFindViewById(view);
        relative_layout.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        hidePDialog();
        if(isOnline()) {
            testCount = 0;
            limitClick =1;
            j = 0;

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.initialData));
            pDialog.setCancelable(false);
            pDialog.show();

            initDataVolley();
        }else {
            relative_layout.setVisibility(View.INVISIBLE);

            notConnectInternet();
            Log.d("Error connect","Error connect");
        }
        mSwipeRefreshLayout.setRefreshing(false);
        return view;
    }


    private void notConnectInternet(){
        Log.d(TAG, "notConnectInternet()");
        relative_layoutG.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder = builder.setMessage(R.string.no_connect)
                .setCancelable(true)
                .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        relative_layout.setVisibility(View.VISIBLE);
                        relative_layoutG.setVisibility(View.VISIBLE);
                        if(isOnline()) {
                            testCount = 0;
                            counter = 0;
                            limitClick =1;
                            j = 0;
                            initDataVolley();

                            relative_layout.setVisibility(View.INVISIBLE);
                        }else {
                            relative_layout.setVisibility(View.INVISIBLE);
                            notConnectInternet();
                            Log.d("Error connect","Error connect");
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        //отправляем объект в невидимость

    }

    public void initFindViewById(View view){
        Log.d(TAG, "initFindViewById()");
        answ1Text = (TextView) view.findViewById(R.id.answ1);
        answ2Text = (TextView) view.findViewById(R.id.answ2);
        answ3Text = (TextView) view.findViewById(R.id.answ3);
        answ4Text = (TextView) view.findViewById(R.id.answ4);
        answ5Text = (TextView) view.findViewById(R.id.answ5);
        counterText = (TextView) view.findViewById(R.id.counterText);

        questionText = (TextView) view.findViewById(R.id.question);
        relative_layout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        relative_layout.setVisibility(View.INVISIBLE);
        relative_layoutG = (RelativeLayout) view.findViewById(R.id.relative_layoutG);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //обнуляем все текстView
        answ1Text.setText(null);
        answ2Text.setText(null);
        answ3Text.setText(null);
        answ4Text.setText(null);
        answ5Text.setText(null);
        questionText.setText(null);
        //
        answ1Text.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (limitClick == 1) { //устанавливаем лимит на клик
                    backgroundTextView(v);
                    if (Objects.equals(answ1Text.getText().toString(), rightTextS)) {// сравниваем результат с ответом
                        testCount++; //счетчик ответа
                        Log.d("aq", String.valueOf(testCount));
                    }
                    handler.postDelayed(new Runnable() {//выполнить после задержки в 1 секунду
                        @Override
                        public void run() {
                            limitClick = (1);
                            clearTextViewBackGround();
                            listenerButton();
                        }
                    }, 1000);
                }
                limitClick++;
            }
        });
        answ2Text.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (limitClick == 1) { //устанавливаем лимит на клик
                    backgroundTextView(v);
                    if (Objects.equals(answ2TextS, rightTextS)) {// сравниваем результат с ответом
                        testCount++; //счетчик ответа
                        Log.d("aq", String.valueOf(testCount));
                    }

                    handler.postDelayed(new Runnable() {//выполнить после задержки в 1 секунду
                        @Override
                        public void run() {
                            limitClick = (1);
                            clearTextViewBackGround();
                            listenerButton();
                        }
                    }, 1000);
                }
                limitClick++;
            }
        });
        answ3Text.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (limitClick == 1) { //устанавливаем лимит на клик
                    backgroundTextView(v);
                    if (Objects.equals(answ3TextS, rightTextS)) {// сравниваем результат с ответом
                        testCount++; //счетчик ответа
                        Log.d("aq", String.valueOf(testCount));
                    }
                    handler.postDelayed(new Runnable() {//выполнить после задержки в 1 секунду
                        @Override
                        public void run() {
                            limitClick = (1);
                            clearTextViewBackGround();
                            listenerButton();
                        }
                    }, 1000);
                }
                limitClick++;
            }
        });
        answ4Text.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (limitClick == 1) { //устанавливаем лимит на клик
                    backgroundTextView(v);
                    if (Objects.equals(answ4TextS, rightTextS)) {// сравниваем результат с ответом
                        testCount++; //счетчик ответа
                        Log.d("aq", String.valueOf(testCount));
                    }
                    handler.postDelayed(new Runnable() {//выполнить после задержки в 1 секунду
                        @Override
                        public void run() {
                            limitClick = (1);
                            clearTextViewBackGround();
                            listenerButton();
                        }
                    }, 1000);
                }
                limitClick++;
            }
        });
        answ5Text.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (limitClick == 1) { //устанавливаем лимит на клик
                    backgroundTextView(v);
                    if (Objects.equals(answ5TextS, rightTextS)) {// сравниваем результат с ответом
                        testCount++; //счетчик ответа
                        Log.d("aq", String.valueOf(testCount));
                    }
                    handler.postDelayed(new Runnable() {//выполнить после задержки в 1 секунду
                        @Override
                        public void run() {
                            limitClick = (1);
                            clearTextViewBackGround();
                            listenerButton();
                        }
                    }, 1000);
                }
                limitClick++;
            }
        });
    }

    private void clearTextViewBackGround(){ //обнуляем цвет TextView
        Log.d(TAG, "clearTextViewBackGround()");
        answ1Text.setBackgroundResource(R.drawable.selector_card_background_white);
        answ2Text.setBackgroundResource(R.drawable.selector_card_background_white);
        answ3Text.setBackgroundResource(R.drawable.selector_card_background_white);
        answ4Text.setBackgroundResource(R.drawable.selector_card_background_white);
        answ5Text.setBackgroundResource(R.drawable.selector_card_background_white);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void backgroundTextView(View textV) {
        Log.d(TAG, "backgroundTextView()");
        String str = ((TextView)textV).getText().toString();
        if (Objects.equals(str, rightTextS)) {
            textV.setBackgroundResource(R.drawable.green_background);
        } else {
            textV.setBackgroundResource(R.drawable.red_background);
            if (Objects.equals(answ1TextS, rightTextS)) {
                answ1Text.setBackgroundResource(R.drawable.green_background);
            } else if (Objects.equals(answ2TextS, rightTextS)) {
                answ2Text.setBackgroundResource(R.drawable.green_background);
            } else if (Objects.equals(answ3TextS, rightTextS)) {
                answ3Text.setBackgroundResource(R.drawable.green_background);
            } else if (Objects.equals(answ4TextS, rightTextS)) {
                answ4Text.setBackgroundResource(R.drawable.green_background);
            } else if (Objects.equals(answ5TextS, rightTextS)) {
                answ5Text.setBackgroundResource(R.drawable.green_background);
            }
        }
        Log.d("aq", String.valueOf(testCount));
    }


    private void scoresCount(){ //определение слова для ответа
        Log.d(TAG, "scoresCount()");
        if (testCount == 1) {
            countBall = getString(R.string.mark);
        }
        else if(testCount == 2 || testCount == 3 || testCount == 4){
            countBall = getString(R.string.mark);
        }
        else if(testCount == 5 || testCount == 6 || testCount == 7| testCount == 8 || testCount == 9 || testCount == 10 || testCount == 0){
            countBall = getString(R.string.mark);
        }

    }



    public void initDataVolley() {
        Log.d(TAG, "initDataVolley()");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String lang = preferences.getString("lang", "kk");

        JsonArrayRequest jsonArrayRequeste = new JsonArrayRequest(urlJsonArray, //получаем url
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hidePDialog();
                        if (testArray != null) {
                            testArray.clear(); //очистка array list

                        }


                        Log.d(TAG, response.toString());
                        //
                        int i = 0;
                        while ( i < 10) {
                            try {
                                JSONObject obj = response.getJSONObject(i); //создаем объект и получаем ответ
                                TestModel testModel = new TestModel();
                                testModel.setAnsw1(obj.getString(answ1)); //получаем данные запросом и заносим в array list
                                testModel.setAnsw2(obj.getString(answ2));
                                testModel.setAnsw3(obj.getString(answ3));
                                testModel.setAnsw4(obj.getString(answ4));
                                testModel.setAnsw5(obj.getString(answ5));
                                testModel.setQuestion(obj.getString(question));
                                testModel.setRight(obj.getString(right));
                                testArray.add(testModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i++;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        listenerButton();


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Ошибка: " + error.getMessage());
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    mSwipeRefreshLayout.setRefreshing(false);
                    hidePDialog();
                }
                hidePDialog();
            }
        });
        jsonArrayRequeste.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequeste);

    }

    public void listenerButton() { //заносим данные в текстВью и переменные из Аррэй листа
        Log.d(TAG, "listenerButton()");
        if ((j < 10) && (testArray.size() != 0)) {
            answ1TextS = (String.valueOf(testArray.get(j).getAnsw1())); //получаем данные и заносит в TextView
            answ2TextS = (String.valueOf(testArray.get(j).getAnsw2()));
            answ3TextS = (String.valueOf(testArray.get(j).getAnsw3()));
            answ4TextS = (String.valueOf(testArray.get(j).getAnsw4()));
            answ5TextS = (String.valueOf(testArray.get(j).getAnsw5()));
            questionTextS = (String.valueOf(testArray.get(j).getQuestion()));
            rightTextS = (String.valueOf(testArray.get(j).getRight()));
            //
            answ1Text.setText(answ1TextS);
            answ2Text.setText(answ2TextS);
            answ3Text.setText(answ3TextS);
            answ4Text.setText(answ4TextS);
            answ5Text.setText(answ5TextS);
            questionText.setText(questionTextS);
            relative_layout.setVisibility(View.VISIBLE);

        } else if (j == 10) { //если дошли доконца , то выводим алерт диалог
            scoresCount();
            result = getString(R.string.scored)+ " " + testCount;
            //Диалог
            relative_layout.setVisibility(View.INVISIBLE);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(result)
                    .setCancelable(true);
            builder.setNeutralButton(R.string.anew, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (isOnline()) {
                        testCount = 0;
                        limitClick = 1;
                        counter = 0;
                        j = 0;
                        initDataVolley();
                        relative_layout.setVisibility(View.INVISIBLE);
                    } else {
                        relative_layout.setVisibility(View.INVISIBLE);
                        notConnectInternet();
                        Log.d("Error connect", "Error connect");
                    }
                }
            });



            AlertDialog alert = builder.create();
            alert.show();

        }

        j++;
        if (counter < 10) {//счетчик вопросов
            counter++;

            resultCounter = (counter + getString(R.string.score11));
            counterText.setText(resultCounter);
            Log.d("Счетчик проходов", String.valueOf(resultCounter));
        }
    }



    public boolean isOnline() {
        Log.d(TAG, "isOnline()");
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh()");
        // говорим о том, что собираемся начать
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (testArray.size() ==0) {
                    initDataVolley();
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, 3000);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        Log.d(TAG, "hidePDialog()");
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
