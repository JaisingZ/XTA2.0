package com.larryhowell.xunta.presenter;

import android.content.SharedPreferences;
import android.util.Log;

import com.larryhowell.xunta.App;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.Urls;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LoginPresenterImpl implements ILoginPresenter {
    private ILoginView iLoginView;

    public LoginPresenterImpl(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
    }

    @Override
    public void login(String telephone) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "getUserInfo");
        params.put("id", telephone);
        params.put("operation", "get");
        params.put("name", "");
        params.put("head_img", "");
        params.put("device_token", Config.device_token);

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iLoginView.onLoginResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.i("haha", response);

                    JSONObject jsonObject = new JSONObject(response);

                    int resultCode = jsonObject.getInt("result");

                    if (resultCode == 0) {
                        iLoginView.onLoginResult(false, "非法请求");
                    } else if (resultCode == -1) {
                        iLoginView.onLoginResult(false, "操作失败");
                    } else {
                        Config.time = UtilBox.getCurrentTime();
                        Config.telephone = telephone;
                        Config.nickname = jsonObject.getString("name");
                        Config.portrait = Urls.MEDIA_CENTER_PORTRAIT + Config.telephone + ".jpg" + "?t=" + Config.time;

                        if (Config.nickname == null || "".equals(Config.nickname)) {
                            Config.nickname = "昵称";
                        }

                        SharedPreferences.Editor editor = App.sp.edit();
                        editor.putString(Constants.SP_KEY_TELEPHONE, Config.telephone);
                        editor.putString(Constants.SP_KEY_NICKNAME, Config.nickname);
                        editor.putString(Constants.SP_KEY_PORTRAIT, Config.portrait);
                        editor.putString(Constants.SP_KEY_TIME, Config.time);
                        editor.apply();

                        iLoginView.onLoginResult(true, "");
                    }

                } catch (JSONException e) {
                    iLoginView.onLoginResult(false, "登录失败,请重试");
                    e.printStackTrace();
                }

            }
        });
    }
}
