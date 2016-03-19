package com.example.onlinequizliveclass3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.onlinequizliveclass3.net.WebServices;

public class QuizListActivity extends AppCompatActivity {

    private static final String TAG = QuizListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        WebServices.getQuizzes();
    }
}
