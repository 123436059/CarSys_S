package com.zyt.tx.myapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MJS on 2017/1/12.
 */

public class PlayCollections {

    public static final int MODE_SINGLE = 0x01;
    public static final int MODE_CIRCLE = 0x02;
    public static final int MODE_NORMAL = 0x04;
    public static final int MODE_RANDOM = 0x03;


    private int playMode = MODE_SINGLE;

    private Random random = new Random();

    private PlayCollections() {
    }

    private static PlayCollections singleTon;

    public static PlayCollections getInstance() {
        if (singleTon == null) {
            singleTon = new PlayCollections();
        }
        return singleTon;
    }

    private List<MusicItemInfo> mMusicList = new ArrayList<>();

    private MusicItemInfo mCurrent;

    private int mCurrentIndex;

    public List<MusicItemInfo> getMusicList() {
        return mMusicList;
    }

    public void setMusicList(List<MusicItemInfo> mMusicList) {
        this.mMusicList = mMusicList;
    }


    public MusicItemInfo getCurrent() {
        return mCurrent;
    }

    public void setCurrent(MusicItemInfo mCurrent) {
        this.mCurrent = mCurrent;
    }

    public void setCurrentIndex(int position) {
        if (position < 0 || position >= mMusicList.size()) {
            return;
        }
        setCurrent(mMusicList.get(position));
    }

    public int getCurrentIndex() {
        return mMusicList.indexOf(mCurrent);
    }


    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }


    public void next() {
        int index = mMusicList.indexOf(mCurrent);
        if (index >= 0) {
            index = getChooseIndex(index, true);
            mCurrent = mMusicList.get(index);
        }
    }

    public void prev() {
        int index = mMusicList.indexOf(mCurrent);
        if (index >= 0) {
            index = getChooseIndex(index, false);
            mCurrent = mMusicList.get(index);
        }
    }

    private int getChooseIndex(int index, boolean isNext) {
        switch (playMode) {
            case MODE_CIRCLE:
                if (isNext) {
                    index = ++index > mMusicList.size() - 1 ? 0 : index;
                } else {
                    index = --index < 0 ? mMusicList.size() - 1 : index;
                }
                break;

            case MODE_NORMAL:
                if (isNext) {
                    index = ++index > mMusicList.size() - 1 ? mMusicList.size() - 1 : index;
                } else {
                    index = --index < 0 ? 0 : index;
                }
                break;

            case MODE_RANDOM:
                index = random.nextInt(mMusicList.size());
                break;

            case MODE_SINGLE:
                break;
        }
        return index;
    }

    private int progress;

    private int max;

    public void setProgress(int progress) {
        this.progress = progress;
    }


    public void setMax(int max) {
        this.max = max;
    }


    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }
}
