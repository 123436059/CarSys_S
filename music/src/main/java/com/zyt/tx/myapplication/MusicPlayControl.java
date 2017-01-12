package com.zyt.tx.myapplication;

import java.io.IOException;

/**
 * Created by MJS on 2017/1/12.
 */

public interface MusicPlayControl {
    MusicItemInfo onPlay() throws IOException;

    void onPrev();

    void onNext();

    void onPause();

    void onResume();

    void onStop();

    void onError();

    void setCurrentProgress(int progress);

    boolean isPlaying();

    int getCurrentPosition();

    int getDuration();

    MusicItemInfo getCurrentMusicInfo();
}
