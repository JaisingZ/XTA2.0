package com.larryhowell.xunta;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mapapi.SDKInitializer;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.net.MediaServiceUtil;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.larryhowell.xunta.presenter.UserInfoPresenterImpl;
import com.larryhowell.xunta.presenter.IUserInfoPresenter;
import com.larryhowell.xunta.ui.MainActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class EntryActivity extends Activity implements IUserInfoPresenter.IUserInfoView {
    @Bind(R.id.ll_no_network)
    LinearLayout mNoNetworkLinearLayout;

    @Bind(R.id.ll_entry)
    LinearLayout mEntryLinearLayout;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_entry);

        ButterKnife.bind(this);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("连接中...");

        initService();
        getStart();
    }

    // 进入正式页面
    private void getStart() {
        // 初始化多媒体服务
        MediaServiceUtil.initMediaService(getApplicationContext(), new InitResultCallback() {
            @Override
            public void onSuccess() {
                entry();
            }

            @Override
            public void onFailure(int i, String s) {
                mEntryLinearLayout.setVisibility(View.GONE);
                mNoNetworkLinearLayout.setVisibility(View.VISIBLE);

                changeLoadingState("dismiss");

                UtilBox.showSnackbar(EntryActivity.this, "初始化失败，请重试");
            }
        });
    }

    private void entry() {
        if (!Config.isConnected) {
            changeLoadingState("dismiss");
            mEntryLinearLayout.setVisibility(View.GONE);

            mNoNetworkLinearLayout.setVisibility(View.VISIBLE);

            UtilBox.showSnackbar(EntryActivity.this, R.string.cant_access_network);
        } else {
            // 获取用户信息
            new UserInfoPresenterImpl(this).getUserInfo(Config.telephone);
        }
    }

    @Override
    public void onGetUserInfoResult(Boolean result, String info) {
        changeLoadingState("dismiss");
        if (result) {
            startActivity(new Intent(EntryActivity.this, MainActivity.class));

            finish();

            overridePendingTransition(R.anim.zoom_in_scale,
                    R.anim.zoom_out_scale);
        } else {
            mEntryLinearLayout.setVisibility(View.GONE);

            mNoNetworkLinearLayout.setVisibility(View.VISIBLE);

            UtilBox.showSnackbar(this, info);
        }
    }

    @Override
    public void onUpdateNickname(Boolean result, String info) {}

    @OnClick(R.id.btn_reconnect)
    public void onClick(View v) {
        initService();
        getStart();
        changeLoadingState("show");
    }

    /**
     * 显示或隐藏旋转进度条
     *
     * @param which show代表显示, dismiss代表隐藏
     */
    private void changeLoadingState(String which) {
        if ("show".equals(which)) {
            runOnUiThread(mDialog::show);
        } else if ("dismiss".equals(which)) {
            if (mDialog.isShowing()) {
                new Handler().postDelayed(() -> mDialog.dismiss(), 500);
            }
        }
    }

    private void initService() {
        // 启动崩溃统计
        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APP_ID, false);

        // 初始化请求
        OkHttpUtil.init(getApplicationContext());

        // 初始化网络状态
        getNetworkState();

        // 读取存储好的数据——cookie,公司信息,个人信息
        loadStorageData();

        // 初始化图片加载框架
        initImageLoader();

        // 开启推送服务
        initPushManager();

        // 初始化百度地图
        SDKInitializer.initialize(getApplicationContext());

        // 初始化数据统计
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(
                getApplicationContext(), Constants.UMENG_APP_KEY, "developer"));
    }

    private void initPushManager() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());

        Config.device_token = JPushInterface.getRegistrationID(getApplicationContext());
    }

    private void getNetworkState() {
        // 获取网络连接管理器对象（系统服务对象）
        ConnectivityManager cm
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // 获取网络状态
        NetworkInfo info = cm.getActiveNetworkInfo();

        Config.isConnected = info != null && info.isAvailable();
    }

    private void loadStorageData() {
        SharedPreferences sp = getSharedPreferences(Constants.SP_FILENAME, Context.MODE_PRIVATE);
        Config.telephone = sp.getString(Constants.SP_KEY_TELEPHONE, "");
        Config.nickname = sp.getString(Constants.SP_KEY_NICKNAME, "昵称");
        Config.portrait = sp.getString(Constants.SP_KEY_PORTRAIT, "");
        Config.time = sp.getString(Constants.SP_KEY_TIME, "");
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO).build();
        L.writeLogs(false);
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}