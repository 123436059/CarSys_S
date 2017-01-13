package com.zyt.tx.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MusicPlayControl {

    private MediaPlayer mediaPlayer;
    private PlayCollections playControl;

    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onStop();
    }

    private void initData() {
        //应该开启线程来执行耗时操作。
        playControl = PlayCollections.getInstance();
        List<MusicItemInfo> mMusicList = playControl.getMusicList();
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String musicPath = rootPath + "/netease/cloudmusic/Music";
        File file = new File(musicPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File item : files) {
                    if (item.getName().endsWith(".mp3")) {
                        MusicItemInfo info = new MusicItemInfo();
                        setInfoDetail(info, item);
                        mMusicList.add(info);
                    }
                }
                playControl.setCurrent(mMusicList.get(0));
            }
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
        return new LocalBinder();
    }

    //---------music control-----------------
    @Override
    public MusicItemInfo onPlay() {
        try {
            onStop();
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playControl.getCurrent().getPath());
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
                    onNext();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            onError();
        }
        return playControl.getCurrent();
    }

    @Override
    public void onPrev() {
        playControl.prev();
        onStop();
        onPlay();
    }

    @Override
    public void onNext() {
        playControl.next();
        onStop();
        onPlay();
    }

    @Override
    public void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
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
    public void onResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
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
    public boolean isPausing() {
        //增加一个标志位
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
        return playControl.getCurrent();
    }

    class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
