package com.example.onlinequizliveclass3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.onlinequizliveclass3.model.Quiz;
import com.example.onlinequizliveclass3.model.ResponseStatus;
import com.example.onlinequizliveclass3.net.WebServices;

import java.io.IOException;
import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity {

    private static final String TAG = QuizListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        WebServices.getQuizzes(new WebServices.GetQuizzesCallback() {
            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onResponse(ResponseStatus status, ArrayList<Quiz> quizArrayList) {
                String text = "";

                for (Quiz q : quizArrayList) {
                    text += q.toString() + "\n";
                }

                Toast.makeText(QuizListActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
