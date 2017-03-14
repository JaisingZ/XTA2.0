package com.larryhowell.xunta.bean;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.baidu.mapapi.search.core.PoiInfo;

public class LocationSuggestion implements SearchSuggestion {
    private PoiInfo info;
    private boolean mIsHistory = false;

    public LocationSuggestion(PoiInfo suggestion) {
        this.info = suggestion;
    }

    public LocationSuggestion(Parcel parcel) {
        info = (PoiInfo) parcel.readValue(PoiInfo.class.getClassLoader());
    }

    public PoiInfo getInfo() {
        return info;
    }

    public void setInfo(PoiInfo info) {
        this.info = info;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return info.name;
    }

    public static final Creator<LocationSuggestion> CREATOR = new Creator<LocationSuggestion>() {
        @Override
        public LocationSuggestion createFromParcel(Parcel in) {
            return new LocationSuggestion(in);
        }

        @Override
        public LocationSuggestion[] newArray(int size) {
            return new LocationSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(info);
    }
}
