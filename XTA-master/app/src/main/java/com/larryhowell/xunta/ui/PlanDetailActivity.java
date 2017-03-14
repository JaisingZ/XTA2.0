package com.larryhowell.xunta.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.larryhowell.xunta.R;
import com.larryhowell.xunta.bean.Plan;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.UtilBox;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanDetailActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.edt_desc)
    EditText mDescEditText;

    @Bind(R.id.seekBar)
    SeekBar mSeekBar;

    @Bind(R.id.tv_grade)
    TextView mGradeTextView;

    @Bind(R.id.edt_startTime)
    EditText mStartTimeEditText;

    @Bind(R.id.edt_arrival)
    EditText mArrivalEditText;

    @Bind(R.id.edt_departure)
    EditText mDepartureEditText;

    @Bind(R.id.edt_terminal)
    EditText mTerminalEditText;

    @Bind(R.id.textInputLayout)
    TextInputLayout mTextInputLayout;

    @Bind(R.id.appBar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    private Plan mPlan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_make_plan);

        ButterKnife.bind(this);

        mPlan = Config.plan;

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDescEditText.setHint(mPlan.getDesc());
        mDescEditText.setText(mPlan.getDesc());
        mDescEditText.requestLayout();
        mTextInputLayout.requestLayout();
        mToolbar.requestLayout();
        mAppBarLayout.requestLayout();
        mCollapsingToolbarLayout.requestLayout();
        //mDescEditText.setOnTouchListener((view, motionEvent) -> true);

        mStartTimeEditText.setText(UtilBox.getDateToString(Long.valueOf(mPlan.getStartTime()), UtilBox.DATE_TIME));
        mArrivalEditText.setText(UtilBox.getDateToString(Long.valueOf(mPlan.getArrival()), UtilBox.DATE_TIME));

        mDepartureEditText.setText(mPlan.getDeparture().name);
        mTerminalEditText.setText(mPlan.getTerminal().name);

        mSeekBar.setOnTouchListener((view, motionEvent) -> true);

        switch (mPlan.getGrade()) {
            case 0:
                mGradeTextView.setText("D");
                mSeekBar.setProgress(4);
                break;
            case 1:
                mGradeTextView.setText("C");
                mSeekBar.setProgress(3);
                break;
            case 2:
                mGradeTextView.setText("B");
                mSeekBar.setProgress(2);
                break;
            case 3:
                mGradeTextView.setText("A");
                mSeekBar.setProgress(1);
                break;
            case 4:
                mGradeTextView.setText("S");
                mSeekBar.setProgress(0);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
