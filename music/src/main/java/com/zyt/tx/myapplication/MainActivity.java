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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity implements IMusicUpdateOperator {

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
    @BindView(R.id.modelImageView)
    ModelImageView modelImageView;
    @BindView(R.id.tvLyric)
    TextView tvLyric;

    private MusicPlayControl musicPlayer;

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
            musicPlayer.registerUpdateUIListener(MainActivity.this);
            //更新当前界面,不做其他处理
            updateViewInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicPlayer = null;
        }
    };

    private PlayListPopWindow popList;

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
            public void onProgressChanged(MyProgressBar bar, int progress, boolean fromUser) {
                //这里加一个是否是用户操作，不然当seekbar更新时，都会调用seekto操作，造成卡顿。
                if (fromUser) {
                    if (musicPlayer != null) {
                        musicPlayer.setCurrentProgress(progress);
                    }
                }
            }
        });

        modelImageView.setCurrentMode(PlayCollections.getInstance().getPlayMode());
        modelImageView.onSetModeSelectedListener(new ModelImageView.onModeSelectedListener() {
            @Override
            public void onSelected(int mode) {
                PlayCollections.getInstance().setPlayMode(mode);
            }
        });
        initPop();

        testLyric();
    }

    private void testLyric() {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "ZztaxiDatas");
            File file = new File(dir, "告白气球.lrc");
            LyricInfo lyricInfo = LyricUtils.setupLyricResource(new FileInputStream(file), "utf-8");
            if (lyricInfo != null && lyricInfo.songLines != null) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < lyricInfo.songLines.size(); i++) {
                    sb.append(lyricInfo.songLines.get(i).content + "\n");
                }
                tvLyric.setText(sb.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicPlayer != null) {
            musicPlayer.registerUpdateUIListener(this);
        }
        myProgressBar.updateProgress(PlayCollections.getInstance().getProgress()
                , PlayCollections.getInstance().getMax());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicPlayer != null) {
            musicPlayer.unregisterUpdateUIListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popList != null) {
            popList.dismiss();
        }
        unbindService(mConn);
    }

    private void initPop() {
        popList = new PlayListPopWindow(this);
        popList.setOnListItemClickListener(new PlayListPopWindow.onListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                PlayCollections.getInstance().setCurrentIndex(position);
                play();
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
    }

    @PermissionFail(requestCode = 100)
    public void onRequestFail() {
    }


    @OnClick({R.id.btnList, R.id.cover, R.id.btnPlay, R.id.bntPause, R.id.btnPrev, R.id.btnNext
            , R.id.btnPlayList, R.id.ivExit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnList:
                break;
            case R.id.cover:
                break;
            case R.id.btnPlay:
                resume();
                break;
            case R.id.bntPause:
                if (musicPlayer == null) {
                    return;
                }
                musicPlayer.onPause();
                break;
            case R.id.btnPrev:
                if (musicPlayer == null) {
                    return;
                }
                musicPlayer.onPrev();
                break;
            case R.id.btnNext:
                if (musicPlayer == null) {
                    return;
                }
                musicPlayer.onNext();
                break;

            case R.id.btnPlayList:
                showListPop();
                break;

            case R.id.ivExit:
                setResult(100);
                finish();
                break;
        }
    }

    private void showListPop() {
        if (!isFinishing() && !popList.isShowing()) {
            popList.setCurrentIndex(PlayCollections.getInstance().getCurrentIndex());
            popList.show(findViewById(R.id.rl_control));
        }
    }

    private void resume() {
        if (musicPlayer == null) {
            return;
        }
        if (!musicPlayer.isPlaying()) {
            musicPlayer.onResume();
        }
        updateViewInfo();
        btnPlay.setVisibility(View.INVISIBLE);
        btnPause.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onBackPressed() {
        if (popList != null && popList.isShowing()) {
            popList.hide();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void musicPlay() {
        updateViewInfo();
        btnPlay.setVisibility(View.INVISIBLE);
        btnPause.setVisibility(View.VISIBLE);
    }

    @Override
    public void musicPause() {
        updateViewInfo();
        btnPlay.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
    }

    @Override
    public void musicStop() {
        btnPlay.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
    }

    @Override
    public void musicProgress(int progress, int max) {
        myProgressBar.updateProgress(progress, max);
    }
}