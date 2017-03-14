package com.larryhowell.xunta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.baidu.mapapi.SDKInitializer;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;


public class App extends Application {
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getApplicationContext().getSharedPreferences(Constants.SP_FILENAME, Context.MODE_PRIVATE);
    }
}
