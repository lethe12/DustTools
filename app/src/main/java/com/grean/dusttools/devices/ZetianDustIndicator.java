package com.grean.dusttools.devices;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * 泽天粉尘仪
 * Created by weifeng on 2018/2/27.
 */

public class ZetianDustIndicator {
    private ReadDust readDust;
    private OnReadDustResultListener listener;

    public ZetianDustIndicator(int comNumber,OnReadDustResultListener listener){
        this.listener = listener;
        readDust = new ReadDust(comNumber);
    }

    public boolean readDust(){
        return readDust.readCpm();
    }


    private class ReadDust implements ReadModBusRegistersListener {
        private int comNumber;

        public ReadDust(int num){
            this.comNumber = num;
        }

        private boolean readCpm(){
            //Log.d(tag,String.valueOf(comNumber));
            return ComManager.getInstance().readRegister(comNumber, (byte) 0x01,0x8C,6,this);
        }

        @Override
        public void onComplete(byte[] value, int size) {
            //Log.d(tag,"收到"+tools.bytesToHexString(value, size));
            listener.onResult(tools.getFloatWithLowByteFirst(value, 6));
        }
    }
}
