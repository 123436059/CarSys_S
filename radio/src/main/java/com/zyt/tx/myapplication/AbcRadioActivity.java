package com.zyt.tx.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import com.zyt.tx.myapplication.service.IRadioService;

/**
 * Created by MJS on 2017/1/4.
 */

public abstract class AbcRadioActivity extends Activity {

    abstract void onCreate2(Bundle saveInstanceState);

    abstract void onListChanged();

    abstract void onFavouritesChanged();

    abstract void onFreqChanged();

    abstract void onStateChanged();

    abstract void onDebugTextChanged();



    private Runnable runWhenStartRadio = null;
    IRadioService radioService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate2(savedInstanceState);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra("channel")) {
            final String band = intent.getStringExtra("bind");
            final int channel = intent.getIntExtra("channel", 8750);
            runWhenStartRadio = new Runnable() {
                @Override
                public void run() {
                    if (radioService != null) {
                        try {
                            radioService.setBindFreq(band,channel);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }
}
