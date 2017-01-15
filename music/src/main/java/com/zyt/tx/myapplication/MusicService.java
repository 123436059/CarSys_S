package com.zyt.tx.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.example.utillib.Log.L;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MusicService extends Service implements MusicPlayControl {

    private MediaPlayer mediaPlayer;
    private PlayCollections playControl;

    private boolean isPausing = false;

    private CopyOnWriteArrayList<IMusicUpdateOperator> mUpdates = new CopyOnWriteArrayList<>();
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            for (IMusicUpdateOperator mUpdate : mUpdates) {
                mUpdate.musicProgress(getCurrentPosition(), getDuration());
                PlayCollections.getInstance().setProgress(getCurrentPosition());
                PlayCollections.getInstance().setMax(getDuration());
                L.d("推送音乐");
            }
            if (isPlaying()) {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.isNeedLog = true;
        L.d("onCreate");
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d("onDestroy");
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
        L.d("onBind");
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.d("onUnbind");
        return super.onUnbind(intent);
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
            updateUIOperator(UPDATE_PLAY);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    onNext();
                }
            });
            mHandler.removeCallbacks(updateRunnable);
            mHandler.post(updateRunnable);
        } catch (IOException e) {
            e.printStackTrace();
            onError();
        }
        return playControl.getCurrent();
    }

    private static final int UPDATE_PLAY = 0;
    private static final int UPDATE_STOP = 1;
    private static final int UPDATE_PAUSE = 2;

    private void updateUIOperator(int i) {
        for (IMusicUpdateOperator mUpdate : mUpdates) {
            switch (i) {
                case UPDATE_PLAY:
                    mUpdate.musicPlay();
                    break;

                case UPDATE_PAUSE:
                    mUpdate.musicPause();
                    break;

                case UPDATE_STOP:
                    mUpdate.musicStop();
                    break;
            }
        }
    }

    @Override
    public void onPrev() {
        playControl.prev();
        onPlay();
    }

    @Override
    public void onNext() {
        playControl.next();
        onPlay();
    }

    @Override
    public void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPausing = true;
            mHandler.removeCallbacks(updateRunnable);
            updateUIOperator(UPDATE_PAUSE);
        }
    }

    @Override
    public void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        updateUIOperator(UPDATE_STOP);
        mHandler.removeCallbacks(updateRunnable);
        isPausing = false;
    }

    @Override
    public void onResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isPausing) {
            mediaPlayer.start();
            isPausing = false;
            updateUIOperator(UPDATE_PLAY);
            mHandler.removeCallbacks(updateRunnable);
            mHandler.post(updateRunnable);
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
        return isPausing;
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

    @Override
    public void registerUpdateUIListener(IMusicUpdateOperator listener) {
        if (!mUpdates.contains(listener))
            mUpdates.add(listener);
    }

    @Override
    public void unregisterUpdateUIListener(IMusicUpdateOperator listener) {
        int i = mUpdates.indexOf(listener);
        if (i >= 0) {
            mUpdates.remove(i);
        }
    }

    class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
