package com.carLib.common.base;

import android.app.Activity;
import android.os.Bundle;


public abstract class BaseActivity extends Activity {

    protected abstract int getLayoutId();

    protected abstract void initWork(Bundle savedInstanceState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initWork(savedInstanceState);
    }
}
