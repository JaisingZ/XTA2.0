package com.larryhowell.xunta.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.bean.Person;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.UtilBox;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BindListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private Context mContext;

    public BindListAdapter(Context context) {
        this.mContext = context;

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_body_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(viewHolder instanceof ViewHolder)) {
            return;
        }

        ViewHolder holder = (ViewHolder) viewHolder;
        final Person person = Config.bindList.get(position);
        holder.nicknameTextView.setText(person.getNickname());
        ImageLoader.getInstance().displayImage(
                UtilBox.getThumbnailImageName(person.getPortraitUrl(),
                        UtilBox.dip2px(mContext, 45),
                        UtilBox.dip2px(mContext, 45))
                , holder.portraitImageView);

        holder.bodyRippleViewLayout.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(holder.bodyRippleViewLayout, person);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getItemCount() {
        return Config.bindList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_item_body_member)
        ImageView portraitImageView;
        @Bind(R.id.rl_item_body_member)
        MaterialRippleLayout bodyRippleViewLayout;
        @Bind(R.id.tv_nickname)
        TextView nicknameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }

    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Person person);
    }
}
