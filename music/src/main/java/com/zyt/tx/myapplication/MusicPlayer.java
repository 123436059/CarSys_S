package com.zyt.tx.myapplication;

import android.media.MediaPlayer;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

/**
 * Created by MJS on 2017/1/12.
 */

public class MusicPlayer {

    MediaPlayer mMediaPlayer = new MediaPlayer();

    public interface playControl {

    }

    public void play(String path) {
        try {
            Log.d("taxi", "music path=" + path);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            // play failed
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resume() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    public int getCurrentPos() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }


    public void setPlayProgress(int progress) {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.seekTo(progress);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

}
