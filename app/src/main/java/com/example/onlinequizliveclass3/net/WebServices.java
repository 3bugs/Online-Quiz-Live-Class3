package com.example.onlinequizliveclass3.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.onlinequizliveclass3.model.Question;
import com.example.onlinequizliveclass3.model.Quiz;
import com.example.onlinequizliveclass3.model.ResponseStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

    private static ResponseStatus mResponseStatus;
    private static ArrayList<Quiz> mQuizArrayList;
    private static ArrayList<Question> mQuestionArrayList;


    public interface GetQuizzesCallback {
        void onFailure(IOException e);
        void onResponse(ResponseStatus status, ArrayList<Quiz> quizArrayList);
    }


    public static void getQuizzes(final GetQuizzesCallback callback) {
        Request request = new Request.Builder()
                .url(GET_QUIZZES_URL)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResult = response.body().string();
                Log.d(TAG, jsonResult);

                delay(2);

                try {
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    int success = jsonObject.getInt("success");

                    if (success == 1) {
                        mResponseStatus = new ResponseStatus(true, null);
                        mQuizArrayList = new ArrayList<>();

                        parseJsonQuizData(jsonObject.getJSONArray("quiz_data"));
                        
                    } else if (success == 0) {
                        String message = jsonObject.getString("message");
                        mResponseStatus = new ResponseStatus(false, message);
                        mQuizArrayList = null;
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON.");
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(mResponseStatus, mQuizArrayList);
                    }
                });
            }
        });
    }

    private static void parseJsonQuizData(JSONArray jsonArrayQuizData) throws JSONException {
        for (int i = 0; i < jsonArrayQuizData.length(); i++) {
            JSONObject jsonQuiz = jsonArrayQuizData.getJSONObject(i);

            int quizId = jsonQuiz.getInt("quiz_id");
            String title = jsonQuiz.getString("title");
            String detail = jsonQuiz.getString("detail");
            int numberOfQuestions = jsonQuiz.getInt("number_of_questions");

            Quiz quiz = new Quiz(quizId, title, detail, numberOfQuestions);
            mQuizArrayList.add(quiz);
        }
    }

    private static void delay(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
