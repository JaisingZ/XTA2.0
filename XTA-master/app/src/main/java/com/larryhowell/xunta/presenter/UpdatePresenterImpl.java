package com.larryhowell.xunta.presenter;

import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class UpdatePresenterImpl implements IUpdatePresenter {
    private IUpdateView iUpdateView;

    public UpdatePresenterImpl(IUpdateView iUpdateView) {
        this.iUpdateView = iUpdateView;
    }

    @Override
    public void getVersion() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "version");
        params.put("operation", "get");
        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iUpdateView.onGetVersionResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int version = jsonObject.getInt("version");

                    if (version == -1) {
                        iUpdateView.onGetVersionResult(false, "非法请求");
                    } else {
                        iUpdateView.onGetVersionResult(true, String.valueOf(version));
                    }
                } catch (JSONException e) {
                    iUpdateView.onGetVersionResult(false, e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
