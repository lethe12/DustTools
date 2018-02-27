package com.grean.dusttools.devices;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * 柴田粉尘仪
 * Created by weifeng on 2018/2/27.
 */

public class SibataDustIndicator {
    private ReadDust readDust;
    private boolean run;
    private OnReadDustResultListener listener;

    public SibataDustIndicator(int comNumber,OnReadDustResultListener listener){
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
            return ComManager.getInstance().readRegister(comNumber, (byte) 0x01,1,1,this);
        }

        @Override
        public void onComplete(byte[] value, int size) {
            listener.onResult(tools.byte2int(value, 3));
        }
    }
}
