package com.larryhowell.xunta.presenter;

import com.baidu.mapapi.search.core.PoiInfo;
import com.larryhowell.xunta.bean.Location;

import java.util.List;

public interface ILocationPresenter {
    void getLocation(String telephone);
    void getLocationList(String telephone);
    void sendLocation(PoiInfo location);

    interface ILocationView {
        void onGetLocationResult(Boolean result, PoiInfo location);
        void onGetLocationListResult(Boolean result, String info, List<Location> locationList);
    }
}
