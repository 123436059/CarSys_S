package com.zyt.tx.myapplication.service;

/**
 * Created by MJS on 2017/1/4.
 */

public interface IRadioService {

    void setBindFreq(String var1, int var2) throws RuntimeException;

    void start();

    void stop();

    void setUp();

    void setDown();

}
