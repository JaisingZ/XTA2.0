package com.larryhowell.xunta.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.adapter.LocationListAdapter;
import com.larryhowell.xunta.bean.Location;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.ILocationPresenter;
import com.larryhowell.xunta.presenter.LocationPresenterImpl;
import com.larryhowell.xunta.widget.DividerItemDecoration;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberLocationListFragment extends Fragment implements ILocationPresenter.ILocationView {
    @Bind(R.id.recyclerView)
    SuperRecyclerView mRecyclerView;

    private LocationListAdapter mAdapter;
    public boolean loaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_location_list, container, false);

        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        mRecyclerView.setRefreshListener(this::refresh);

        ((ImageView) mRecyclerView.getEmptyView().findViewById(R.id.imageView)).setImageResource(R.drawable.no_location);
        ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView)).setText("ta还没有上传过位置信息");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
    }

    public void refresh() {
        if (!Config.isConnected) {
            UtilBox.showSnackbar(getActivity(), R.string.cant_access_network);

            mRecyclerView.setRefreshing(false);
            return;
        }

        mRecyclerView.setRefreshing(true);

        new LocationPresenterImpl(this).getLocationList(((MemberMainActivity) getActivity()).mPerson.getTelephone());
    }

    @Override
    public void onGetLocationListResult(Boolean result, String info, List<Location> locationList) {
        mRecyclerView.setRefreshing(false);

        if (result) {
            if (mAdapter == null) {
                mAdapter = new LocationListAdapter();
                mAdapter.setLocationList(locationList);

                if (locationList != null && locationList.size() != 0) {
                    ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
                    layoutParams.height = locationList.size() * (UtilBox.dip2px(getActivity(), 64) + 1) + 20;
                    mRecyclerView.setLayoutParams(layoutParams);
                }

                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setLocationList(locationList);
                mAdapter.notifyDataSetChanged();
            }

        } else {
            UtilBox.showSnackbar(getActivity(), info);
        }
    }

    @Override
    public void onGetLocationResult(Boolean result, PoiInfo location) {
    }
}
