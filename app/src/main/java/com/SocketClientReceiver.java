package com;

/**
 * Created by weifeng on 2018/2/26.
 */

public interface SocketClientReceiver {
    boolean isComplete(byte [] buff,int length);
    void handleReceive(byte [] buff,int length);
}
