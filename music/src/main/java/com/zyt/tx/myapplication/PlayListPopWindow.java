package com.zyt.tx.myapplication;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by MJS on 2017/1/13.
 */

public class PlayListPopWindow extends PopupWindow {

    private final ListAdapter mAdapter;
    private final RecyclerView mRecyclerView;

    public interface onListItemClickListener {
        void onListItemClick(int position);
    }

    onListItemClickListener mListener;

    public void setOnListItemClickListener(onListItemClickListener listener) {
        mListener = listener;
    }

    public PlayListPopWindow(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.pop_music_list, null);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(DensityUtils.dip2px(context, 400));
        setOutsideTouchable(true);
//        setBackgroundDrawable(new BitmapDrawable());
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        setAnimationStyle(R.style.pop_anim_style);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ListAdapter(PlayCollections.getInstance().getMusicList());
        mAdapter.setOnItemClickListener(new ListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //播放当前选择。
                if (mListener != null) {
                    mListener.onListItemClick(position);
                }
                hide();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setCurrentIndex(int index) {
        mAdapter.setCurrent(index);
    }

    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mRecyclerView.smoothScrollToPosition(mAdapter.getCurrent());
        mAdapter.notifyDataSetChanged();
    }

    public void hide() {
        dismiss();
    }

}
