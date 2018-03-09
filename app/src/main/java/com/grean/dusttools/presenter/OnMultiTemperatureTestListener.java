package com.grean.dusttools.presenter;

import com.grean.dusttools.devices.MultiTemperatureDataFormat;

/**
 * Created by weifeng on 2018/3/9.
 */

public interface OnMultiTemperatureTestListener {
    /**
     * 显示实时数据
     * @param values
     */
    void onRealTimeData(float[] values);

    /**
     * 插入一条数据值表格
     * @param format
     */
    void onInsertItem(MultiTemperatureDataFormat format);
}
