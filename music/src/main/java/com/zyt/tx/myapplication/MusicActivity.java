package com.zyt.tx.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.health.TimerStat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MusicActivity extends AppCompatActivity {

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

    private final static int UPDATE_PROGRESS = 0x01;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    int index = progressBar.getProgress() + 5;
                    if (index <= progressBar.getMax()) {
                        progressBar.setProgress(index);
                    } else {
                        Toast.makeText(MusicActivity.this, "Next", Toast.LENGTH_SHORT).show();
                        timer.cancel();
                    }
                    break;
            }
        }
    };
    private Timer timer;
    private PlayListPopWindow popList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);
        initPop();
        progressBar.setMax(100);
        progressBar.setProgress(0);
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.obtainMessage(UPDATE_PROGRESS).sendToTarget();
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private void initPop() {
        popList = new PlayListPopWindow(this);
        popList.setOnListItemClickListener(new PlayListPopWindow.onListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                Toast.makeText(MusicActivity.this
                        , "当前点击：" + PlayCollections.getInstance().getCurrent().getName()
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.ivCover, R.id.ivNext, R.id.ivPlay, R.id.ivAll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCover:
                break;
            case R.id.ivNext:
                break;
            case R.id.ivPlay:
                break;

            case R.id.ivAll:
                popList.show(progressBar);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popList != null) {
            popList.dismiss();
        }
    }
}
