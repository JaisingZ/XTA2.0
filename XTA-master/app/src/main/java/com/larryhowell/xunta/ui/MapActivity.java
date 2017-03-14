package com.larryhowell.xunta.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.mapView)
    MapView mMapView;

    private PoiInfo location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        location = getIntent().getParcelableExtra("location");

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);

        LatLng cenpt = new LatLng(location.location.latitude, location.location.longitude);
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        mMapView.getMap().setMapStatus(mMapStatusUpdate);

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.location);
        MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(cenpt).title(location.name);

        mMapView.getMap().addOverlay(markerOptions);
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
