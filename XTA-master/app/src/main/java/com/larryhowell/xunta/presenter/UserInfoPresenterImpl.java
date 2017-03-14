package com.larryhowell.xunta.presenter;

import android.content.SharedPreferences;

import com.larryhowell.xunta.App;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.Urls;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class UserInfoPresenterImpl implements IUserInfoPresenter {
    public IUserInfoView iGetUserInfoView;

    public UserInfoPresenterImpl(IUserInfoView iGetUserInfoView) {
        this.iGetUserInfoView = iGetUserInfoView;
    }

    @Override
    public void getUserInfo(String telephone) {
        if (telephone == null || "".equals(telephone)) {
            iGetUserInfoView.onGetUserInfoResult(true, "");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("type", "getUserInfo");
        params.put("id", telephone);
        params.put("operation", "get");
        params.put("name", Config.nickname);
        params.put("head_img", "");
        params.put("device_token", Config.device_token);

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iGetUserInfoView.onGetUserInfoResult(false, "获取用户信息失败");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int resultCode = jsonObject.getInt("result");

                    if (resultCode == 0) {
                        iGetUserInfoView.onGetUserInfoResult(false, "非法请求");
                    } else if (resultCode == -1) {
                        iGetUserInfoView.onGetUserInfoResult(false, "操作失败");
                    } else {
                        Config.nickname = jsonObject.getString("name");
                        Config.portrait = Urls.MEDIA_CENTER_PORTRAIT + telephone + ".jpg" + "?t=" + Config.time;

                        if ("".equals(Config.nickname)) {
                            Config.nickname = "昵称";
                        }

                        SharedPreferences.Editor editor = App.sp.edit();
                        editor.putString(Constants.SP_KEY_TELEPHONE, Config.telephone);
                        editor.putString(Constants.SP_KEY_NICKNAME, Config.nickname);
                        editor.putString(Constants.SP_KEY_PORTRAIT, Config.portrait);
                        editor.apply();

                        iGetUserInfoView.onGetUserInfoResult(true, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void updateNickname(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "updateUserInfo");
        params.put("id", Config.telephone);
        params.put("operation", "update");
        params.put("name", name);
        params.put("head_img", "");
        params.put("device_token", Config.device_token);

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iGetUserInfoView.onUpdateNickname(false, "修改失败");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int resultCode = jsonObject.getInt("result");

                    if (resultCode == 0) {
                        iGetUserInfoView.onUpdateNickname(false, "非法请求");
                    } else if (resultCode == -1) {
                        iGetUserInfoView.onUpdateNickname(false, "操作失败");
                    } else if (resultCode == -2) {
                        iGetUserInfoView.onUpdateNickname(false, "不存在该用户");
                    } else {
                        Config.nickname = name;

                        SharedPreferences.Editor editor = App.sp.edit();
                        editor.putString(Constants.SP_KEY_NICKNAME, Config.nickname);
                        editor.apply();

                        iGetUserInfoView.onUpdateNickname(true, name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}


