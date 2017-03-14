package com.larryhowell.xunta.presenter;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.larryhowell.xunta.bean.Plan;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class PlanPresenterImpl implements IPlanPresenter {
    private IPlanView iPlanView;

    public PlanPresenterImpl(IPlanView iPlanView) {
        this.iPlanView = iPlanView;
    }

    @Override
    public void makePlan(Plan plan) {
        if (plan == null) {
            iPlanView.onMakePlanResult(false, "计划错误");
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put("type", "makePlan");
        params.put("id", plan.getTargetTelephone());                         //自己手机号
        params.put("bindid", Config.telephone);          //对方手机号
        params.put("time_start", plan.getStartTime());                //开始时间戳
        params.put("remark", plan.getDesc());                          //备注
        params.put("grade", String.valueOf(plan.getGrade()));       //等级
        params.put("time_arrival", plan.getArrival());                    //结束时间戳
        params.put("lat_start", String.valueOf(plan.getDeparture().location.longitude));  //起始地点经度
        params.put("lot_start", String.valueOf(plan.getDeparture().location.latitude));  //起始地点纬度
        params.put("lot_arrival", String.valueOf(plan.getTerminal().location.longitude));   //终点经度
        params.put("lat_arrival", String.valueOf(plan.getTerminal().location.latitude));   //终点经度
        params.put("space_start", plan.getDeparture().name);
        params.put("space_arrival", plan.getTerminal().name);
        params.put("operation", "add");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iPlanView.onMakePlanResult(false, "计划制定失败");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int result = jsonObject.getInt("result");

                    if (result != 1) {
                        iPlanView.onMakePlanResult(false, "计划制定失败");
                    } else {
                        iPlanView.onMakePlanResult(true, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void cancelPlan(String telephone) {
        Map<String, String> params = new HashMap<>();

        params.put("type", "cancelPlan");
        params.put("id", telephone);
        params.put("operation", "delete");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iPlanView.onCancelPlanResult(false, "中止计划失败");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.i("haha", response);

                    JSONObject jsonObject = new JSONObject(response);

                    int result = jsonObject.getInt("result");

                    if (result != 1) {
                        iPlanView.onCancelPlanResult(false, "中止计划失败");
                    } else {
                        iPlanView.onCancelPlanResult(true, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void getPlan(String telephone) {
        Map<String, String> params = new HashMap<>();

        params.put("type", "getPlan");
        params.put("id", telephone);
        params.put("operation", "get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iPlanView.onGetPlanResult(false, "获取计划失败");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.i("haha", response);

                    JSONObject jsonObject = new JSONObject(response);

                    int result = jsonObject.getInt("result");

                    if (result == -2) {
                        iPlanView.onGetPlanResult(false, "没有正在运行的出行计划");
                    } else if (result != 1) {
                        iPlanView.onGetPlanResult(false, "获取计划失败");
                    } else {
                        JSONObject object = jsonObject.getJSONObject("planInfo");

                        Config.plan = null;

                        PoiInfo departure = new PoiInfo();
                        departure.name = object.getString("space_start");
                        departure.location = new LatLng(object.getDouble("lat_start"), object.getDouble("lot_start"));

                        PoiInfo terminal = new PoiInfo();
                        terminal.name = object.getString("space_arrival");
                        terminal.location = new LatLng(object.getDouble("lat_arrival"), object.getDouble("lot_arrival"));

                        Config.plan = new Plan(
                                object.getString("bindid"),
                                object.getString("remark"),
                                object.getInt("grade"),
                                object.getString("time_start"),
                                object.getString("time_arrival"),
                                departure,
                                terminal);

                        iPlanView.onGetPlanResult(true, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
