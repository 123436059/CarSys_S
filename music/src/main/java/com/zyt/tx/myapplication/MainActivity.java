package com.zyt.tx.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity {

    private static final int UPDATE_PROGRESS = 1;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.btnList)
    Button btnList;
    @BindView(R.id.cover)
    ImageView cover;
    @BindView(R.id.myProgressBar)
    MyProgressBar myProgressBar;
    @BindView(R.id.btnPlay)
    ImageButton btnPlay;
    @BindView(R.id.bntPause)
    ImageButton btnPause;
    private MusicService musicPlayer;
    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String music_path = rootPath + "/netease/cloudmusic/Music/周杰伦 - 告白气球.mp3";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    int progress = msg.arg1;
                    int duration = msg.arg2;
                    myProgressBar.updateProgress(progress, duration);
                    break;
            }
        }
    };


    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicPlayer = binder.getService();
            play();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicPlayer = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initRequestPermission();
        btnPlay.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        bindMusicService();

        myProgressBar.setOnProgressChangedListener(new MyProgressBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(MyProgressBar bar, int progress) {
                //更新实时的播放进度
                musicPlayer.setCurrentProgress(progress);
            }
        });
    }

    private void bindMusicService() {
        Intent musicService = new Intent(this, MusicService.class);
        bindService(musicService, mConn, Context.BIND_AUTO_CREATE);
    }

    @TargetApi(16)
    private void initRequestPermission() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }

    @PermissionSuccess(requestCode = 100)
    public void onRequestSuc() {
        Toast.makeText(this, "request permission success", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void onRequestFail() {
        Toast.makeText(this, "request permission fail", Toast.LENGTH_SHORT).show();
    }


    Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = musicPlayer.getCurrentPosition();
            int duration = musicPlayer.getDuration();
            mHandler.obtainMessage(UPDATE_PROGRESS, progress, duration).sendToTarget();
            mHandler.postDelayed(this, 1000);
        }
    };


    @OnClick({R.id.btnList, R.id.cover, R.id.btnPlay, R.id.bntPause, R.id.btnPrev, R.id.btnNext})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnList:
                break;
            case R.id.cover:
                break;
            case R.id.btnPlay:
                play();
                break;
            case R.id.bntPause:
                if (musicPlayer == null) {
                    return;
                }
                musicPlayer.onPause();
                mHandler.removeCallbacks(seekBarRunnable);
                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                break;
            case R.id.btnPrev:
                if (musicPlayer == null) {
                    return;
                }
                musicPlayer.onPrev();
                updateViewInfo();
                break;
            case R.id.btnNext:
                if (musicPlayer == null) {
                    return;
                }
                musicPlayer.onNext();
                updateViewInfo();
                break;
        }
    }

    private void play() {
        if (musicPlayer == null) {
            return;
        }
        musicPlayer.onPlay();
        updateViewInfo();
        btnPlay.setVisibility(View.INVISIBLE);
        btnPause.setVisibility(View.VISIBLE);
    }

    private void updateViewInfo() {
        if (musicPlayer == null) {
            return;
        }
        MusicItemInfo info = musicPlayer.getCurrentMusicInfo();
        if (info != null) {
            tvTitle.setText(info.getName());
        }
        //要移除，否则会重复加入到队列中。
        mHandler.removeCallbacks(seekBarRunnable);
        mHandler.post(seekBarRunnable);
    }
}