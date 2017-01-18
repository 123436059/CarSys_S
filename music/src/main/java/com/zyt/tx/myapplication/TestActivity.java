package com.zyt.tx.myapplication;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class TestActivity extends AppCompatActivity {

    private LyricView mLyricView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mLyricView = (LyricView) findViewById(R.id.lyricView);

        findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(Environment.getExternalStorageDirectory(), "ZztaxiDatas");
                File file = new File(dir, "告白气球.lrc");
                mLyricView.setLyricFile(file,"utf-8");
            }
        });

    }
}
