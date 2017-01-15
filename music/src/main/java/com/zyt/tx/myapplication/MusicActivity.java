package com.zyt.tx.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MusicActivity extends AppCompatActivity implements IMusicUpdateOperator {

    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAuthor)
    TextView tvAuthor;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ivNext)
    ImageView ivNext;
    @BindView(R.id.ivPlay)
    ImageView ivPlay;


    private PlayListPopWindow popList;
    private MusicPlayControl ImusicControl;


    private final static int SONG_DETAIL = 0x02;

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            ImusicControl = binder.getService();
            ImusicControl.registerUpdateUIListener(MusicActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ImusicControl = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);
        initPermission();
        initPop();
    }

    @TargetApi(16)
    private void initPermission() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();

    }

    @PermissionSuccess(requestCode = 100)
    public void onRequestSuc() {
        initService();
    }

    @PermissionFail(requestCode = 100)
    public void onRequestFail() {
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (ImusicControl != null) {
            ImusicControl.registerUpdateUIListener(this);
        }
        progressBar.setProgress(PlayCollections.getInstance().getProgress());
        progressBar.setMax(PlayCollections.getInstance().getMax());

        if (PlayCollections.getInstance().getCurrent() != null) {
            tvName.setText(PlayCollections.getInstance().getCurrent().getName());
            tvAuthor.setText(PlayCollections.getInstance().getCurrent().getAuthor());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ImusicControl != null) {
            ImusicControl.unregisterUpdateUIListener(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popList != null) {
            popList.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SONG_DETAIL) {
            if (resultCode == 100) {

            }
        }

    }

    private void initService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, mConn, Context.BIND_AUTO_CREATE);
    }

    private void initPop() {
        popList = new PlayListPopWindow(this);
        popList.setOnListItemClickListener(new PlayListPopWindow.onListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                PlayCollections.getInstance().setCurrentIndex(position);
                ImusicControl.onPlay();
            }
        });
    }

    @OnClick({R.id.ivCover, R.id.ivNext, R.id.ivPlay, R.id.ivAll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCover:
                Intent intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent, SONG_DETAIL);
                break;
            case R.id.ivNext:
                if (ImusicControl != null) {
                    ImusicControl.onNext();
                }
                break;
            case R.id.ivPlay:
                if (ImusicControl != null) {
                    if (ImusicControl.isPausing()) {
                        ImusicControl.onResume();
                    } else {
                        if (ImusicControl.isPlaying()) {
                            ImusicControl.onPause();
                        } else {
                            ImusicControl.onPlay();
                        }
                    }
                }
                break;

            case R.id.ivAll:
                if (!isFinishing() && !popList.isShowing()) {
                    popList.setCurrentIndex(PlayCollections.getInstance().getCurrentIndex());
                    popList.show(progressBar);
                }
                break;
        }
    }


    //------------更新ＵＩ操作
    @Override
    public void musicPlay() {
        ivPlay.setImageResource(R.drawable.play_pause);
        tvName.setText(PlayCollections.getInstance().getCurrent().getName());
        tvAuthor.setText(PlayCollections.getInstance().getCurrent().getAuthor());
    }

    @Override
    public void musicPause() {
        ivPlay.setImageResource(R.drawable.play);
    }

    @Override
    public void musicStop() {
        ivPlay.setImageResource(R.drawable.pause_n);
    }

    @Override
    public void musicProgress(int progress, int max) {
        if (progressBar.getMax() != max) {
            progressBar.setMax(max);
        }
        progressBar.setProgress(progress);
    }
}
