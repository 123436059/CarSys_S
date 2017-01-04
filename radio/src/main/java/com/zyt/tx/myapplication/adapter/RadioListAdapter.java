package com.zyt.tx.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyt.tx.myapplication.R;
import com.zyt.tx.myapplication.entity.RadioBean;

import java.util.List;

/**
 * Created by MJS on 2016/12/30.
 */

public class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.ViewHolder> {

    private final List<RadioBean> mDatas;

    public interface onItemClick {
        void onItemClickListener(View view, int position);
    }

    private onItemClick mListener;

    public void setOnItemClickListener(onItemClick listener) {
        mListener = listener;
    }


    public RadioListAdapter(List<RadioBean> list) {
        this.mDatas = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_radio, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RadioBean radioBean = mDatas.get(position);
        holder.tvContent.setText(radioBean.getContent());
        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClickListener(view, holder.getPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }
}

