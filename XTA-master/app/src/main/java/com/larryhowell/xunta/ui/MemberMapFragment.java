package com.larryhowell.xunta.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.bean.Location;
import com.larryhowell.xunta.presenter.ILocationPresenter;
import com.larryhowell.xunta.presenter.LocationPresenterImpl;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberMapFragment extends Fragment implements ILocationPresenter.ILocationView {
    @Bind(R.id.mapView)
    MapView mMapView;

    @Bind(R.id.ll_empty)
    LinearLayout mEmptyLinearLayout;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_map, container, false);

        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("正在获取对方当前位置");
        mProgressDialog.setCancelable(false);

        refresh();
    }

    public void refresh() {
        mProgressDialog.show();

        new LocationPresenterImpl(this).getLocation(((MemberMainActivity) getActivity()).mPerson.getTelephone());
    }

    @Override
    public void onGetLocationResult(Boolean result, PoiInfo location) {
        if (mProgressDialog.isShowing()) {
            new Handler().postDelayed(() -> mProgressDialog.dismiss(), 500);
        }

        if (result) {
            LatLng cenpt = new LatLng(location.location.latitude, location.location.longitude);
            MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

            mMapView.getMap().setMapStatus(mMapStatusUpdate);

            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.location);
            MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(cenpt).title(location.name);

            mMapView.getMap().addOverlay(markerOptions);

            mMapView.setVisibility(View.VISIBLE);
            mEmptyLinearLayout.setVisibility(View.GONE);
        } else {
            mMapView.setVisibility(View.GONE);
            mEmptyLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetLocationListResult(Boolean result, String info, List<Location> locationList) {
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
}
