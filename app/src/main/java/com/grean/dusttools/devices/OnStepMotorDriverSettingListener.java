package com.grean.dusttools.devices;

/**
 * Created by weifeng on 2018/3/8.
 */

public interface OnStepMotorDriverSettingListener {
    void onResult(StepMotorDriverSettingFormat format);
    void onProcess(String content,int process);
    void onStatus(boolean run);
}
