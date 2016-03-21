package com.example.onlinequizliveclass3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.onlinequizliveclass3.model.Question;
import com.example.onlinequizliveclass3.model.ResponseStatus;
import com.example.onlinequizliveclass3.net.WebServices;

import java.io.IOException;
import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = QuizActivity.class.getSimpleName();

    private QuestionsPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private ProgressBar mProgressBar;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;

    private int mQuizId;
    protected ArrayList<Question> mQuestionArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        mQuizId = intent.getIntExtra("quiz_id", 0);

        setupViews();
        loadQuestions();
    }

    private void setupViews() {
        mAdapter = new QuestionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        final View coordinatorLayout = findViewById(R.id.main_layout);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isQuizComplete()) {
                    new AlertDialog.Builder(QuizActivity.this)
                            .setTitle("ต้องการส่งข้อมูล?")
                            .setPositiveButton("ส่ง", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    WebServices.setUserGuesses(1, mQuizId, mQuestionArrayList,
                                            new WebServices.SetUserGuessesCallback() {
                                                @Override
                                                public void onFailure(IOException e) {
                                                    String msg = "Network Connection Error:\n" + e.getMessage();
                                                    Log.e(TAG, msg);
                                                    Snackbar.make(
                                                            coordinatorLayout,
                                                            msg,
                                                            Snackbar.LENGTH_LONG
                                                    ).show();
                                                }

                                                @Override
                                                public void onResponse(ResponseStatus responseStatus) {
                                                    if (responseStatus.success) {
                                                        Toast.makeText(
                                                                QuizActivity.this,
                                                                responseStatus.message,
                                                                Toast.LENGTH_LONG
                                                        ).show();

                                                        finish();
                                                    } else {
                                                        Snackbar.make(
                                                                coordinatorLayout,
                                                                responseStatus.message,
                                                                Snackbar.LENGTH_LONG
                                                        ).show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("ยกเลิก", null)
                            .show();
                } else {
                    Snackbar.make(coordinatorLayout, "คุณยังทำแบบทดสอบไม่ครบทุกข้อ", Snackbar.LENGTH_LONG ).show();
                }
            }
        });
    }

    private void loadQuestions() {
        mProgressBar.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);

        WebServices.getQuestions(mQuizId, new WebServices.GetQuestionsCallback() {
            @Override
            public void onFailure(IOException e) {
                mProgressBar.setVisibility(View.GONE);
                mViewPager.setVisibility(View.GONE);

                String msg = "Network Connection Error:\n" + e.getMessage();
                Log.e(TAG, msg);
                Toast.makeText(QuizActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(ResponseStatus responseStatus, ArrayList<Question> questionArrayList) {
                if (responseStatus.success) {
                    mProgressBar.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);

                    //Collections.copy(mQuestionArrayList, questionArrayList);
                    mQuestionArrayList.addAll(questionArrayList);
                    mAdapter.notifyDataSetChanged();
                    mTabLayout.setupWithViewPager(mViewPager);

                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);

                    Log.e(TAG, responseStatus.message);
                    Toast.makeText(QuizActivity.this, responseStatus.message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void checkQuizComplete() {
        if (isQuizComplete()) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    private boolean isQuizComplete() {
        for (Question question : mQuestionArrayList) {
            if (question.getSelectedChoiceId() == Question.NO_CHOICE_SELECTED) {
                return false;
            }
        }
        return true;
    }

    public class QuestionsPagerAdapter extends FragmentPagerAdapter {

        public QuestionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mQuestionArrayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position + 1);
        }
    }
}
