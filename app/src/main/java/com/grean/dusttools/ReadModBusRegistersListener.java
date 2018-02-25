package com.grean.dusttools;

/**
 * 接收需要读取的ModBus寄存器的回调函数
 * Created by weifeng on 2018/2/25.
 */

public interface ReadModBusRegistersListener {
    void onComplete(byte[] value,int size);
}
