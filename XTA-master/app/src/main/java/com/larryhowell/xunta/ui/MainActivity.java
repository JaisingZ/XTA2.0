package com.larryhowell.xunta.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.bean.Location;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.ILocationPresenter;
import com.larryhowell.xunta.presenter.IShareLocationPresenter;
import com.larryhowell.xunta.presenter.IUpdatePresenter;
import com.larryhowell.xunta.presenter.LocationPresenterImpl;
import com.larryhowell.xunta.presenter.ShareLocationPresenterImpl;
import com.larryhowell.xunta.presenter.UpdatePresenterImpl;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BDLocationListener,
        View.OnClickListener, IUpdatePresenter.IUpdateView, ILocationPresenter.ILocationView,
        IShareLocationPresenter.IShareLocationView {

    @Bind(R.id.mapView)
    MapView mMapView;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.ripple)
    MaterialRippleLayout mRippleLayout;

    @Bind(R.id.button)
    Button mButton;

    @Bind(R.id.appBar)
    AppBarLayout mAppBarLayout;

    private CircleImageView mNavigationPortraitImageView;
    private TextView mNicknameTextView;
    private int versionCode = 2;
    private boolean isFirstLoc = true;

    private MaterialDialog mDialog;
    private TextView mTextView;

    public LocationClient mLocationClient = null;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initView();

        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(this);

        mLocationClient.start();

        new UpdatePresenterImpl(this).getVersion();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        initNavigationView();

        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("请稍候...");
        mProgressDialog.setCancelable(false);

        mRippleLayout.setOnClickListener(v -> {
            if (Config.telephone == null || "".equals(Config.telephone)) {
                showLoginDialog();
            } else {
                startActivity(
                        new Intent(MainActivity.this, BindListActivity.class)
                        , ActivityOptions.makeSceneTransitionAnimation(
                                MainActivity.this,
                                Pair.create(mAppBarLayout, "appBar")
                        ).toBundle());
                //startActivity(new Intent(MainActivity.this, BindListActivity.class));
            }
        });

        PackageManager pkgManager = getPackageManager();

        boolean fineLocationPermission = pkgManager.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION, getPackageName())
                == PackageManager.PERMISSION_GRANTED;

        boolean coarseLocationPermission = pkgManager.checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName())
                == PackageManager.PERMISSION_GRANTED;

        if (!fineLocationPermission || !coarseLocationPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
        builder.setMessage("需要登录才能使用这个功能哦")
                .setCancelable(true)
                .setPositiveButton("登录", (dialog, which) -> {
                    startActivityForResult(
                            new Intent(MainActivity.this, LoginActivity.class),
                            Constants.CODE_LOGIN,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    MainActivity.this,
                                    Pair.create(mButton, "button"),
                                    Pair.create(mAppBarLayout, "appBar")
                            ).toBundle());
                })
                .setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @SuppressLint("InflateParams")
    private void showShareLocationDialog() {
        final View contentView = getLayoutInflater().inflate(R.layout.dialog_input, null);

        TextInputLayout til = (TextInputLayout) contentView.findViewById(R.id.til_input);
        til.setHint("对方手机号");

        mTextView = (TextView) contentView.findViewById(R.id.tv_input);

        final EditText editText = ((EditText) contentView
                .findViewById(R.id.edt_input));
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.requestFocus();

        mDialog = new MaterialDialog(this);
        mDialog.setPositiveButton("查找", v -> {
            if (TextUtils.isEmpty(editText.getText())) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("请输入对方手机号");
            } else if (!Config.isConnected) {
                Toast.makeText(this, R.string.cant_access_network,
                        Toast.LENGTH_SHORT).show();
            } else if (!UtilBox.isTelephoneNumber(editText.getText().toString())) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("请输入11位手机号");
            } else {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("查找中...");

                new ShareLocationPresenterImpl(this).requestLocation(editText.getText().toString());
            }
        }).setNegativeButton("取消", v -> {
            mDialog.dismiss();
        }).setContentView(contentView)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    @Override
    public void requestLocationResult(Boolean result, String info) {
        if (result) {
            mDialog.dismiss();

            UtilBox.showSnackbar(this, "请求成功,正在等待对方确认");
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(info);
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        //option.setScanSpan(1000); // 定位次数
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        BaiduMap mBaiduMap = mMapView.getMap();

        // map view 销毁后不再处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(location.getDirection())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        mBaiduMap.setMyLocationData(locData);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(new LatLng(location.getLatitude(), location.getLongitude()));
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                String address = reverseGeoCodeResult.getAddress();

                if (address == null || "".equals(address)) {
                    return;
                }

                if (address.contains("省")) {
                    Config.currentCity = address.substring(address.indexOf("省") + 1, address.indexOf("市"));
                } else {
                    Config.currentCity = address.substring(0, address.indexOf("市"));
                }

                Config.currentCityDetail = address.substring(address.indexOf("市") + 1, address.length());

                PoiInfo location = new PoiInfo();
                location.name = address;
                location.location = new LatLng(locData.latitude, locData.longitude);

                new LocationPresenterImpl(MainActivity.this).sendLocation(location);
            }
        });
        geoCoder.reverseGeoCode(option);

        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    private void initNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        //mNavigationView.setItemIconTintList(null);

        // 设置昵称
        mNicknameTextView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.tv_navigation_nickname);
        mNicknameTextView.setText(Config.nickname);

        // 获取抽屉的头像
        mNavigationPortraitImageView = (CircleImageView) mNavigationView.getHeaderView(0).findViewById(R.id.iv_navigation);
        if (!"".equals(Config.portrait)) {
            ImageLoader.getInstance().displayImage
                    (Config.portrait + "&w=" + UtilBox.dip2px(this, 80) + "&h=" + UtilBox.dip2px(this, 80),
                            mNavigationPortraitImageView);
        }

        mNavigationView.getHeaderView(0).findViewById(R.id.ll_nvHeader).setOnClickListener(this);
    }

    @Override
    public void onGetVersionResult(Boolean result, String info) {
        if (result) {
            int currentVersion = UtilBox.getPackageInfo(this).versionCode;

            versionCode = Integer.valueOf(info);

            if (currentVersion >= versionCode) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
            builder.setTitle("是否更新")
                    .setMessage("当前版本:" + versionCode + "\n最新版本:" + versionCode)
                    .setCancelable(true)
                    .setPositiveButton("更新", (dialog, which) -> {
                        Uri uri = Uri.parse("http://xunta.file.alimmdn.com/xunta_" + versionCode + ".apk");
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    })
                    .setNegativeButton("取消", null);

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            UtilBox.reportBug(info);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nvHeader:
            case R.id.iv_navigation:
            case R.id.tv_navigation_nickname:
                if (Config.telephone == null || "".equals(Config.telephone)) {
                    showLoginDialog();
                } else {
                    startActivityForResult(new Intent(this, UserInfoActivity.class),
                            Constants.CODE_USER_INFO,
                            ActivityOptions.makeSceneTransitionAnimation(this, mAppBarLayout, "appBar").toBundle());
                    mDrawer.closeDrawer(GravityCompat.START);
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (Config.telephone == null || "".equals(Config.telephone)) {
            showLoginDialog();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.nav_user_info:
                startActivityForResult(new Intent(this, UserInfoActivity.class), Constants.CODE_USER_INFO,
                        ActivityOptions.makeSceneTransitionAnimation(this, mAppBarLayout, "appBar").toBundle());
                break;

            case R.id.nav_bind_list:
                startActivity(new Intent(MainActivity.this, BindListActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(
                                MainActivity.this,
                                Pair.create(mAppBarLayout, "appBar")).toBundle());
                break;

            case R.id.nav_share_location:
                showShareLocationDialog();
                break;

            case R.id.nav_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain"); // 纯文本
                intent.putExtra(Intent.EXTRA_TEXT, "寻ta下载地址: http://xunta.file.alimmdn.com/xunta_" + versionCode + ".apk");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
                break;

            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
                builder.setMessage("真的要注销吗")
                        .setNegativeButton("假的", (dialog, which) -> {

                        })
                        .setPositiveButton("真的", (dialog, which) -> {
                            UtilBox.clearAllData(MainActivity.this);

                            mNavigationPortraitImageView.setImageResource(R.drawable.portrait_default);

                            mNicknameTextView.setText(Config.nickname);

                            Toast.makeText(MainActivity.this, "已注销", Toast.LENGTH_SHORT).show();
                        })
                        .setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CODE_LOGIN:
            case Constants.CODE_USER_INFO:
                if (resultCode == RESULT_OK) {
                    ImageLoader.getInstance().displayImage(
                            Config.portrait + "&w=" + UtilBox.dip2px(this, 80) + "&h=" + UtilBox.dip2px(this, 80),
                            mNavigationPortraitImageView);
                    mNicknameTextView.setText(Config.nickname);

                    if (requestCode == Constants.CODE_LOGIN) {
                        mDrawer.openDrawer(GravityCompat.START);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mMapView.setVisibility(View.VISIBLE);

        isFirstLoc = true;
        mLocationClient.start();

        MobclickAgent.onResume(this);

        if (Config.confirmMessage != null && !"".equals(Config.confirmMessage)) {
            try {
                JSONObject jsonObject = new JSONObject(Config.confirmMessage);

                String nickname = jsonObject.getString("name");
                String telephone = jsonObject.getString("telephone");

                AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);

                builder.setMessage("用户 " + nickname + " (手机号" + telephone + ")想查看你的位置")
                        .setCancelable(false)
                        .setPositiveButton("同意", (dialog, which) -> {
                            mProgressDialog.show();

                            new ShareLocationPresenterImpl(this).confirmShare(true, nickname, telephone);
                        })
                        .setNegativeButton("拒绝", (dialog, which) -> {
                            mProgressDialog.show();

                            new ShareLocationPresenterImpl(this).confirmShare(false, nickname, telephone);
                        })
                        .show();

                Config.confirmMessage = "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (Config.locationMessage != null && !"".equals(Config.locationMessage)) {
            try {
                JSONObject jsonObject = new JSONObject(Config.locationMessage);

                String nickname = jsonObject.getString("name");
                String telephone = jsonObject.getString("telephone");

                AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);

                builder.setMessage("用户 " + nickname + " (手机号" + telephone + ")已经同意与你共享位置")
                        .setCancelable(false)
                        .setPositiveButton("现在去看", (dialog, which) -> {
                            Intent intent = new Intent(this, MapActivity.class);
                            intent.putExtra("location", decodeLocation(Config.locationMessage));
                            startActivity(intent,
                                    ActivityOptions.makeSceneTransitionAnimation(
                                            MainActivity.this,
                                            Pair.create(mAppBarLayout, "appBar")
                                    ).toBundle());
                            Config.locationMessage = "";
                        })
                        .setNegativeButton("不看了", (dialogInterface, i) -> {
                            Config.locationMessage = "";
                        })
                        .show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void confirmShareResult(Boolean result, String info) {
        if (mProgressDialog.isShowing()) {
            new Handler().postDelayed(() -> mProgressDialog.dismiss(), 500);
        }
    }

    private PoiInfo decodeLocation(String message) {
        PoiInfo location = new PoiInfo();
        try {
            JSONObject object = new JSONObject(message);
            location.location = new LatLng(object.getDouble("lat"), object.getDouble("lot"));
            location.name = object.getString("space");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mMapView.setVisibility(View.GONE);
    }

    @Override
    public void onGetLocationResult(Boolean result, PoiInfo location) {
    }

    @Override
    public void onGetLocationListResult(Boolean result, String info, List<Location> locationList) {
    }
}