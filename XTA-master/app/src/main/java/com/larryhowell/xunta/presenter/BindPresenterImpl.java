package com.larryhowell.xunta.presenter;

import android.os.Handler;

import com.larryhowell.xunta.bean.Person;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Urls;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class BindPresenterImpl implements IBindPresenter {
    private IBindView iBindView;

    public BindPresenterImpl(IBindView iBindView) {
        this.iBindView = iBindView;
    }

    @Override
    public void bind(String telephone) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "bind");
        params.put("id", Config.telephone);
        params.put("bindid", telephone);
        params.put("operation", "add");
        params.put("device_token", "");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iBindView.onBindResult(false, "获取绑定列表失败");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int resultCode = jsonObject.getInt("result");

                    if (resultCode == 0) {
                        iBindView.onBindResult(false, "非法请求");
                    } else if (resultCode == -1) {
                        iBindView.onBindResult(false, "操作失败");
                    } else if (resultCode == -2) {
                        iBindView.onBindResult(false, "不存在该用户");
                    } else {
                        iBindView.onBindResult(true, "");
                    }
                } catch (JSONException e) {
                    iBindView.onBindResult(false, "绑定失败");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void bindConfirm(boolean accept, String telephone) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "bindConfirm");
        params.put("id", Config.telephone);
        params.put("bindid", telephone);
        params.put("operation", "confirm");
        params.put("agree", accept ? "1" : "0");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iBindView.onBindConfirmResult(true, "");
            }

            @Override
            public void onResponse(String response, int id) {
                iBindView.onBindConfirmResult(true, "");
            }
        });
    }

    @Override
    public void getBindList() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "getBindList");
        params.put("id", Config.telephone);
        params.put("operation", "get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iBindView.OnGetBindListResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int resultCode = jsonObject.getInt("result");

                    if (resultCode == 0) {
                        iBindView.OnGetBindListResult(false, "非法请求");
                    } else if (resultCode == -1) {
                        iBindView.OnGetBindListResult(false, "操作失败");
                    } else if (resultCode == -2) {
                        iBindView.OnGetBindListResult(false, "不存在该用户");
                    } else {
                        Config.bindList.clear();

                        JSONArray jsonArray = jsonObject.getJSONArray("bindList");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String nickname = object.getString("name");

                            if (nickname == null || "".equals(nickname)) {
                                nickname = "昵称";
                            }

                            String telephone = object.getString("id");

                            Config.bindList.add(new Person(
                                    nickname,
                                    telephone,
                                    Urls.MEDIA_CENTER_PORTRAIT + telephone + ".jpg" + "?t=" + UtilBox.getCurrentTime()
                            ));
                        }

                        iBindView.OnGetBindListResult(true, "");
                    }
                } catch (JSONException e) {
                    iBindView.OnGetBindListResult(false, "获取绑定列表失败");
                    e.printStackTrace();
                }
            }
        });
    }
}
