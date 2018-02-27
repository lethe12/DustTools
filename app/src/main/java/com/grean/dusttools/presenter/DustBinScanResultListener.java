package com.grean.dusttools.presenter;

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
}
