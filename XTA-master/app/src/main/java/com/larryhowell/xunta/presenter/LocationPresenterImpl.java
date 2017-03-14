package com.larryhowell.xunta.presenter;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.larryhowell.xunta.bean.Location;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class LocationPresenterImpl implements ILocationPresenter {
    private ILocationView iLocationView;

    public LocationPresenterImpl(ILocationView iLocationView) {
        this.iLocationView = iLocationView;
    }

    @Override
    public void getLocation(String telephone) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "getCurrentLocation");
        params.put("id", telephone);
        params.put("operation", "get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.i("haha", response);

                    JSONObject jsonObject = new JSONObject(response);

                    int result = jsonObject.getInt("result");

                    PoiInfo location = new PoiInfo();

                    if (result != 1) {
                        iLocationView.onGetLocationResult(false, location);
                    } else {
                        JSONObject object = jsonObject.getJSONObject("currentLocation");

                        location.location = new LatLng(object.getDouble("lat"), object.getDouble("lot"));
                        location.name = object.getString("space");

                        iLocationView.onGetLocationResult(true, location);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void getLocationList(String telephone) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "getHistoryLocation");
        params.put("id", telephone);
        params.put("operation", "get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (e != null && e.getMessage() != null) {
                    Log.i("haha", e.getMessage());
                }
                iLocationView.onGetLocationListResult(false, "获取位置列表失败", null);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("haha", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int result = jsonObject.getInt("result");

                    if (result == -2) {
                        iLocationView.onGetLocationListResult(false, "还没有上传过位置信息", null);
                    } else if (result != 1) {
                        iLocationView.onGetLocationListResult(false, "获取位置列表失败", null);
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("locationList");

                        List<Location> locationList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            PoiInfo poiInfo = new PoiInfo();
                            poiInfo.name = object.getString("space");
                            poiInfo.location = new LatLng(object.getDouble("lat"), object.getDouble("lot"));

                            locationList.add(0, new Location(poiInfo, object.getString("time")));
                        }

                        iLocationView.onGetLocationListResult(true, "", locationList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        PoiInfo location = new PoiInfo();
//        location.location = new LatLng(30.663791, 104.07281);
//        location.name = "南京市江宁区南京航空航天大学江宁校区";
//
//        List<Location> locationList = new ArrayList<>();
//        locationList.add(new Location(location, "1487654678087"));
//        locationList.add(new Location(location, "1487654678087"));
//        locationList.add(new Location(location, "1487654678087"));
//        locationList.add(new Location(location, "1487654678087"));
//
//        new Handler().postDelayed(() -> iLocationView.onGetLocationListResult(true, "", locationList), 2000);
    }

    @Override
    public void sendLocation(PoiInfo location) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "sendLocation");
        params.put("id", Config.telephone);
        params.put("operation", "add");
        params.put("space", location.name);
        params.put("lat", String.valueOf(location.location.latitude));
        params.put("lot", String.valueOf(location.location.longitude));
        params.put("time", UtilBox.getCurrentTime());

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
            }
        });
    }
}
