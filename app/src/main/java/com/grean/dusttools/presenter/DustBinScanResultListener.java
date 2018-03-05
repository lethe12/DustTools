package com.grean.dusttools.presenter;

import com.grean.dusttools.devices.ComparativeDustData;

/**
 * Created by weifeng on 2018/2/27.
 */

public interface DustBinScanResultListener {
    /**
     * 显示比较的柴田粉尘仪结果
     * @param num COM口编号
     * @param result 结果
     */
    void onTestResult(int num ,float result);

    void onErrorCommunication();

    /**
     * 插入一条显示记录
     * @param data
     */
    void insertItem(ComparativeDustData data);
}
