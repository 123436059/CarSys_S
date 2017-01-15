package com.zyt.tx.myapplication;

/**
 * Created by MJS on 2017/1/12.
 */

public interface MusicPlayControl {
    MusicItemInfo onPlay();

    void onPrev();

    void onNext();

    void onPause();

    void onResume();

    void onStop();

    void onError();

    void setCurrentProgress(int progress);

    boolean isPlaying();

    boolean isPausing();

    int getCurrentPosition();

    int getDuration();

    MusicItemInfo getCurrentMusicInfo();

    void registerUpdateUIListener(IMusicUpdateOperator listener);

    void unregisterUpdateUIListener(IMusicUpdateOperator listener);
}
