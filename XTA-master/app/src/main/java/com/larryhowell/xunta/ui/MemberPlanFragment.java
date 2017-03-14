package com.larryhowell.xunta.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.IPlanPresenter;
import com.larryhowell.xunta.presenter.PlanPresenterImpl;

import java.text.ParseException;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberPlanFragment extends Fragment implements IPlanPresenter.IPlanView {
    @Bind(R.id.ripple)
    MaterialRippleLayout mRippleLayout;

    @Bind(R.id.ll_running)
    LinearLayout mRunningLinearLayout;

    @Bind(R.id.ll_empty)
    LinearLayout mEmptyLinearLayout;

    @Bind(R.id.tv_time)
    TextView mTimeTextView;

    @Bind(R.id.tv_departure)
    TextView mDepartureTextView;

    @Bind(R.id.tv_terminal)
    TextView mTerminalTextView;

    @Bind(R.id.tv_start_time)
    TextView mStartTimeTextView;

    @Bind(R.id.tv_arrival)
    TextView mArrivalTextView;

    @Bind(R.id.ripple_find)
    MaterialRippleLayout mFindRippleLayout;

    @Bind(R.id.ripple_detail)
    MaterialRippleLayout mDetailRippleLayout;

    @Bind(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    private ProgressDialog mProgressDialog;
    public boolean loaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_plan, container, false);

        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        mRunningLinearLayout.setVisibility(View.GONE);
        mEmptyLinearLayout.setVisibility(View.VISIBLE);
        mFloatingActionButton.setVisibility(View.GONE);

        mRippleLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), MakePlanActivity.class);
                    intent.putExtra("person", ((MemberMainActivity) getActivity()).mPerson);
                    startActivityForResult(intent, Constants.CODE_MAKE_PLAN,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    getActivity(),
                                    getActivity().findViewById(R.id.appbar), "appBar").toBundle());
                }
        );

        mFindRippleLayout.setOnClickListener(view -> {
            ((MemberMainActivity) getActivity()).mMapFragment.refresh();
            ((MemberMainActivity) getActivity()).mViewPager.setCurrentItem(0, true);
        });

        mDetailRippleLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
            //intent.putExtra("plan", Config.plan);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(),
                    getActivity().findViewById(R.id.appbar), "appBar").toBundle()
            );
        });

        mFloatingActionButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
            AlertDialog dialog = builder.setMessage("真的要中止这个计划吗")
                    .setCancelable(true)
                    .setNegativeButton("假的", null)
                    .setPositiveButton("真的", (dialogInterface, i) -> {
                        mProgressDialog.show();
                        new PlanPresenterImpl(this).cancelPlan(((MemberMainActivity) getActivity()).mPerson.getTelephone());
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        });

        mRunningLinearLayout.setVisibility(View.GONE);
        mEmptyLinearLayout.setVisibility(View.GONE);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("获取计划中...");
        mProgressDialog.setCancelable(false);
    }

    public void refresh() {
        mProgressDialog.show();

        new PlanPresenterImpl(this).getPlan(((MemberMainActivity) getActivity()).mPerson.getTelephone());
    }

    @Override
    public void onGetPlanResult(Boolean result, String info) {
        if (mProgressDialog.isShowing()) {
            new Handler().postDelayed(() -> mProgressDialog.dismiss(), 500);
        }

        if (result) {
            if ("".equals(info)) {
                mRunningLinearLayout.setVisibility(View.VISIBLE);
                mEmptyLinearLayout.setVisibility(View.GONE);
                mFloatingActionButton.setVisibility(View.VISIBLE);

                mDepartureTextView.setText(Config.plan.getDeparture().name);
                mTerminalTextView.setText(Config.plan.getTerminal().name);
                mStartTimeTextView.setText(UtilBox.getDateToString(Long.valueOf(Config.plan.getStartTime()), UtilBox.DATE_TIME));
                mArrivalTextView.setText(UtilBox.getDateToString(Long.valueOf(Config.plan.getArrival()), UtilBox.DATE_TIME));

                if (Config.plan.getStartTime().compareTo(UtilBox.getCurrentTime()) < 0) {
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                    valueAnimator.addUpdateListener(
                            animation -> {
                                try {
                                    mTimeTextView.setText(
                                            UtilBox.dateDifference(Config.plan.getStartTime(), UtilBox.getCurrentTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                    valueAnimator.setDuration(300000000);
                    valueAnimator.start();

                    Calendar.getInstance().setTimeInMillis(Long.valueOf(UtilBox.getCurrentTime()));
                } else {
                    mTimeTextView.setText("00:00:00");
                }
            } else {
                mRunningLinearLayout.setVisibility(View.GONE);
                mEmptyLinearLayout.setVisibility(View.VISIBLE);
                mFloatingActionButton.setVisibility(View.GONE);
            }
        } else {
            mRunningLinearLayout.setVisibility(View.GONE);
            mEmptyLinearLayout.setVisibility(View.VISIBLE);
            mFloatingActionButton.setVisibility(View.GONE);

            UtilBox.showSnackbar(getActivity(), info);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CODE_MAKE_PLAN:
                if (resultCode == Activity.RESULT_OK) {
                    refresh();
                }
                break;
        }
    }

    @Override
    public void onMakePlanResult(Boolean result, String info) {
    }

    @Override
    public void onCancelPlanResult(Boolean result, String info) {
        if (mProgressDialog.isShowing()) {
            new Handler().postDelayed(() -> mProgressDialog.dismiss(), 500);
        }

        if (result) {
            refresh();
        } else {
            UtilBox.showSnackbar(getActivity(), info);
        }
    }
}
