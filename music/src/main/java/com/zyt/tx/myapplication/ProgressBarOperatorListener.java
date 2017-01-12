package com.zyt.tx.myapplication;

/**
 * Created by MJS on 2017/1/11.
 */

public interface ProgressBarOperatorListener {
    void setStartTv(String content);

    void setEndTv(String content);

    void setSeekBarProgress(int progress);

    void setSeekBarMax(int max);

    int getCurrentProgress();
}
