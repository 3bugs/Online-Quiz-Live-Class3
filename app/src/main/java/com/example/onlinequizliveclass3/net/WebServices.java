package com.example.onlinequizliveclass3.net;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Promlert on 3/19/2016.
 */
public class WebServices {

    private static final String TAG = WebServices.class.getSimpleName();

    private static final String BASE_URL = "http://10.0.3.2/online_quiz/";

    private static final String GET_QUIZZES_URL = BASE_URL + "quiz_index.php";
    private static final String GET_QUESTIONS_URL = BASE_URL + "get_questions.php?quiz_id=%d";
    private static final String SET_USER_GUESSES_URL = BASE_URL + "set_user_guesses.php";

    private static final OkHttpClient mClient = new OkHttpClient();


    public static void getQuizzes() {
        Request request = new Request.Builder()
                .url(GET_QUIZZES_URL)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResult = response.body().string();
                Log.d(TAG, jsonResult);
            }
        });
    }


}
