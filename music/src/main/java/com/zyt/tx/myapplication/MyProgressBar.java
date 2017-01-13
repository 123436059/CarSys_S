package com.zyt.tx.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by MJS on 2017/1/11.
 */

public class MyProgressBar extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    private TextView tvStart;
    private TextView tvEnd;
    private SeekBar seekBar;


    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_my_progressbar, this, true);
        tvStart = (TextView) findViewById(R.id.tvStart);
        tvEnd = (TextView) findViewById(R.id.tvEnd);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    //---------seeKbar--------------
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setStartTv(progress);
        if (mListener != null) {
            mListener.onProgressChanged(MyProgressBar.this, progress, fromUser);
        }
    }

    private String getFormatTime(int progress) {
        int res = progress / 1000;
        return String.format(Locale.CHINESE, "%02d:%02d", res / 60, res % 60);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    //-------------operator progressBar------------------------
    private void setStartTv(int progress) {
        String current = getFormatTime(progress);
        tvStart.setText(current);
    }

    private void setEndTv(int progress) {
        String current = getFormatTime(progress);
        tvEnd.setText(current);
    }


    //设置max初始值=0.
    public void updateProgress(int progress, int duration) {
        if (seekBar == null || seekBar.getProgress() == progress) {
            return;
        }
        seekBar.setProgress(progress);
        setStartTv(progress);
        if (duration != seekBar.getMax()) {
            seekBar.setMax(duration);
            setEndTv(duration);
        }
    }

    //-----inter---------
    public interface OnProgressChangeListener {
        void onProgressChanged(MyProgressBar bar, int progress, boolean isFromUser);
    }

    OnProgressChangeListener mListener;

    public void setOnProgressChangedListener(OnProgressChangeListener listener) {
        this.mListener = listener;
    }
}

