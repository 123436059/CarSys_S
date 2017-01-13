package com.zyt.tx.myapplication;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by MJS on 2017/1/13.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<MusicItemInfo> mList;
    private int index;

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    onItemClickListener mListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    public ListAdapter(List<MusicItemInfo> list) {
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_list
                , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == index) {
            holder.tvIndex.setText("播放中");
            holder.tvIndex.setTextColor(Color.RED);
            holder.tvContent.setTextColor(Color.RED);

        } else {
            holder.tvIndex.setTextColor(Color.BLACK);
            holder.tvContent.setTextColor(Color.BLACK);
            holder.tvIndex.setText(String.valueOf(position + 1));
        }
        holder.tvContent.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCurrent(int index) {
        if (index < 0 || index >= mList.size()) {
            return;
        }
        this.index = index;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        TextView tvContent;
        public ViewHolder(final View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tvIndex);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            if (mList != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index = getAdapterPosition();
                        mListener.onItemClick(itemView, index);
                    }
                });
            }
        }
    }
}

