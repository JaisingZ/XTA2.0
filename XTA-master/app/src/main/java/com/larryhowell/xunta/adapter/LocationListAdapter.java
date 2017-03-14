package com.larryhowell.xunta.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.larryhowell.xunta.R;
import com.larryhowell.xunta.bean.Location;
import com.larryhowell.xunta.common.UtilBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LocationListAdapter extends RecyclerView.Adapter {
    private List<Location> locationList;

    public LocationListAdapter() {
        locationList = new ArrayList<>();
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(viewHolder instanceof ViewHolder)) {
            return;
        }

        ViewHolder holder = (ViewHolder) viewHolder;
        final Location location = locationList.get(position);
        String address = location.getPoiInfo().name;
        holder.locationTextView.setText(address.substring(address.indexOf("市") + 1, address.length()));
        holder.timeTextView.setText(UtilBox.getDateToString(Long.valueOf(location.getTime()), "MM-dd HH:mm"));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_location_name)
        TextView locationTextView;

        @Bind(R.id.tv_location_time)
        TextView timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }
}
