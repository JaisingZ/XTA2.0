package com.larryhowell.xunta.presenter;

import android.os.Handler;
import android.util.Log;

import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ShareLocationPresenterImpl implements IShareLocationPresenter {
    private IShareLocationView iShareLocationView;

    public ShareLocationPresenterImpl(IShareLocationView iShareLocationView) {
        this.iShareLocationView = iShareLocationView;
    }

    @Override
    public void requestLocation(String target) {
        Map<String, String> params = new HashMap<>();

        params.put("type", "requestLocation");
        params.put("id", Config.telephone);
        params.put("target", target);
        params.put("operation", "get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iShareLocationView.requestLocationResult(false, "请求位置失败");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.i("haha", response);

                    JSONObject jsonObject = new JSONObject(response);

                    int result = jsonObject.getInt("result");

                    if (result != 1) {
                        iShareLocationView.requestLocationResult(false, "请求位置失败");
                    } else {
                        iShareLocationView.requestLocationResult(true, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void confirmShare(boolean accept, String nickname, String sender) {
        Map<String, String> params = new HashMap<>();

        params.put("type", "confirmShare");
        params.put("id", Config.telephone);
        params.put("target", sender);
        params.put("operation", "confirm");
        params.put("accept", accept ? "1" : "0");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iShareLocationView.confirmShareResult(false, "");
            }

            @Override
            public void onResponse(String response, int id) {
                // 不关心返回值
                iShareLocationView.confirmShareResult(true, "");
            }
        });
    }
}
