package com.grean.dusttools.devices;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * Created by weifeng on 2018/11/14.
 */

public class OriginalSibataDustIndicator {
    private static final String tag = "SibataDustIndicator";
    private ReadDust readDust;
    private OnReadDustResultListener listener;

    public OriginalSibataDustIndicator(int comNumber,OnReadDustResultListener listener){
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
            return ComManager.getInstance().readRegister(comNumber, (byte) 0x01,1,1,this);
        }

        @Override
        public void onComplete(byte[] value, int size) {
            //Log.d(tag,"收到"+tools.bytesToHexString(value, size));
            listener.onResult(tools.byte2int(value, 3));
        }
    }
}
