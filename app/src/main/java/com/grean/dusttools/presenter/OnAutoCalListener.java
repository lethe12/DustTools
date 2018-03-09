package com.grean.dusttools.presenter;

import com.grean.dusttools.devices.DustMainBoardCalInfoFormat;

/**
 * Created by weifeng on 2018/3/9.
 */

public interface OnAutoCalListener {
    void onRealTimeState(String[] state);
    void onInsertItem(DustMainBoardCalInfoFormat format);
    void endCal();
}
