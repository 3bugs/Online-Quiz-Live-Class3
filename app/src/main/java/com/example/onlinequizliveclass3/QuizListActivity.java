package com.example.onlinequizliveclass3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.onlinequizliveclass3.model.Quiz;
import com.example.onlinequizliveclass3.model.ResponseStatus;
import com.example.onlinequizliveclass3.net.WebServices;

import java.io.IOException;
import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity {

    private static final String TAG = QuizListActivity.class.getSimpleName();

    private View mMainLayout;
    private ListView mQuizzesListView;
    private ProgressBar mProgressBar;
    private View mRetryLayout;
    private TextView mErrorMessageTextView;
    private Button mRetryButton;

    private ArrayAdapter<Quiz> mAdapter;
    private ArrayList<Quiz> mQuizArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        setupViews();
        loadQuizzes();
    }

    private void setupViews() {
        mMainLayout = findViewById(R.id.main_layout);
        mQuizzesListView = (ListView) findViewById(R.id.quizzes_list_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRetryLayout = findViewById(R.id.retry_layout);
        mErrorMessageTextView = (TextView) findViewById(R.id.error_message);
        mRetryButton = (Button) findViewById(R.id.retry_button);

        mAdapter = new ArrayAdapter<Quiz>(
                this,
                R.layout.quiz_item,
                mQuizArrayList
        );
        mQuizzesListView.setAdapter(mAdapter);
        mQuizzesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Quiz selectedQuiz = mQuizArrayList.get(position);

                Intent intent = new Intent(QuizListActivity.this, QuizActivity.class);
                intent.putExtra("quiz_id", selectedQuiz.quizId);
                startActivity(intent);
            }
        });
    }

    private void loadQuizzes() {
        mMainLayout.setBackgroundResource(0);
        mQuizzesListView.setVisibility(View.GONE);
        mRetryLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        WebServices.getQuizzes(new WebServices.GetQuizzesCallback() {
            @Override
            public void onFailure(IOException e) {
                mMainLayout.setBackgroundResource(0);
                mQuizzesListView.setVisibility(View.GONE);
                mRetryLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                String msg = "Network Connection Error:\n" + e.getMessage();
                mErrorMessageTextView.setText(msg);
            }

            @Override
            public void onResponse(ResponseStatus status, ArrayList<Quiz> quizArrayList) {
                if (status.success) {
                    mMainLayout.setBackgroundResource(R.drawable.background);
                    mQuizzesListView.setVisibility(View.VISIBLE);
                    mRetryLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);

                    mAdapter.addAll(quizArrayList);
                } else {
                    mMainLayout.setBackgroundResource(0);
                    mQuizzesListView.setVisibility(View.GONE);
                    mRetryLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    mErrorMessageTextView.setText(status.message);
                }
            }
        });
    }
}
