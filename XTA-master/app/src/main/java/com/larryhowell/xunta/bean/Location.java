package com.larryhowell.xunta.bean;

import com.baidu.mapapi.search.core.PoiInfo;

public class Location {
    private PoiInfo poiInfo;
    private String time;

    public Location(PoiInfo poiInfo, String time) {
        this.poiInfo = poiInfo;
        this.time = time;
    }

    public PoiInfo getPoiInfo() {
        return poiInfo;
    }

    public void setPoiInfo(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
