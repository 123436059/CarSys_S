package com.zyt.tx.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.INotificationSideChannel;

import com.zyt.tx.myapplication.service.ICarService;
import com.zyt.tx.myapplication.service.ServiceManager;

/**
 * Created by MJS on 2017/1/4.
 */

public abstract class BaseActivity extends Activity {


    protected Handler H;
    private ICarService carService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        H = new Handler(handlerCallback);


    }


    final Handler.Callback handlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            handlerMessageInternal(message);
            return handleMessage(message);
        }
    };

    private void bindCarService() {
        IBinder binder = ServiceManager.checkService("my.service");
        if (binder != null) {
            this.carService = (ICarService) INotificationSideChannel.Stub.asInterface(binder);
        }

    }


    /**
     * 基类处理message
     * @param msg
     * @return
     */
    protected boolean handleMessage(Message msg) {
        return false;
    }

    /**
     * 内部处理消息
     * @param msg
     * @return
     */
    private boolean handlerMessageInternal(Message msg) {
        return false;
    }
}
