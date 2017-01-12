package com.zyt.tx.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaCrypto;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Telephony;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MusicPlayControl {

    private MediaPlayer mediaPlayer;
    private LocalBinder mBinder = new LocalBinder();

    private List<MusicItemInfo> mMusicList;

    private MusicItemInfo mCurrent;

    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        //应该开启线程来执行耗时操作。
        mMusicList = new ArrayList<>();
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String musicPath = rootPath + "/netease/cloudmusic/Music";
        File file = new File(musicPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                if (item.getName().endsWith(".mp3")) {
                    MusicItemInfo info = new MusicItemInfo();
                    setInfoDetail(info, item);
                    mMusicList.add(info);
                }
            }
            mCurrent = mMusicList.get(0);
            Log.d("taxi", "music list size=" + mMusicList.size());
        }
    }

    private void setInfoDetail(MusicItemInfo info, File file) {
        String name = file.getName();
        String[] split = name.trim().split("-");
        info.setPath(file.getAbsolutePath());
        info.setAuthor(split[0]);
        info.setName(split[1].substring(0, split[1].indexOf(".")));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //---------music control-----------------
    @Override
    public MusicItemInfo onPlay() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mCurrent.getPath());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    mp.start();
                    //TODO play next
                }
            });

            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
            onError();
        }
        return mCurrent;
    }

    @Override
    public void onPrev() {
        int index = mMusicList.indexOf(mCurrent);
        if (index >= 0) {
            // circle mode : order circle random single
            index = --index < 0 ? 0 : index;
            Log.d("taxi", "cur index =" + index);
            mCurrent = mMusicList.get(index);
            onStop();
            onPlay();
        }
    }

    @Override
    public void onNext() {
        int index = mMusicList.indexOf(mCurrent);
        if (index >= 0) {
            //TODO circle mode
            index = ++index > mMusicList.size() - 1 ? mMusicList.size() - 1 : index;
            mCurrent = mMusicList.get(index);
            onStop();
            onPlay();
        }
    }

    @Override
    public void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onError() {
        onStop();
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void setCurrentProgress(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public MusicItemInfo getCurrentMusicInfo() {
        return mCurrent;
    }

    class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
