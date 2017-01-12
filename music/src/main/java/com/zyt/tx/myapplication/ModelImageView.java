package com.zyt.tx.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by admin on 2017/1/12.
 */

public class ModelImageView extends ImageView implements View.OnClickListener {

    private int currentIndex;
    private int[] mods = {PlayCollections.MODE_SINGLE, PlayCollections.MODE_CIRCLE
            , PlayCollections.MODE_RANDOM};

    private int[] bgIds = {R.drawable.play_single, R.drawable.play_all, R.drawable.play_random};

    public ModelImageView(Context context) {
        super(context);
    }

    public ModelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        currentIndex = ++currentIndex > bgIds.length ? 0 : currentIndex;
        setImageResource(bgIds[currentIndex]);
        if (mListener != null) {
            mListener.onSelected(mods[currentIndex]);
        }

    }

    public void setCurrentMode(int mode) {
        for (int i = 0; i < mods.length; i++) {
            if (mode == mods[i]) {
                currentIndex = i;
                break;
            }
        }
        setImageResource(bgIds[currentIndex]);
    }

    public interface onModeSelectedListener {
        void onSelected(int mode);
    }

    onModeSelectedListener mListener;

    public void onSetModeSelectedListener(onModeSelectedListener listener) {
        mListener = listener;
    }
}
