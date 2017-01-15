package com.zyt.tx.myapplication;

/**
 * Created by admin on 2017/1/15.
 */

public interface IMusicUpdateOperator {

    void musicPlay();

    void musicPause();

    void musicStop();

    void musicProgress(int progress, int max);

}
