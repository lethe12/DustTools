package com.grean.dusttools.devices;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * Created by weifeng on 2018/10/18.
 */

public class Cld50DustIndicator {
    private static final byte[] ReadCpm = {0x55,0x01,0x00,0x00, (byte) 0xF2};
    private ReadDust readDust;
    private OnReadDustResultListener listener;
    public Cld50DustIndicator(int comNumber,OnReadDustResultListener listener){
        readDust = new ReadDust(comNumber);
        this.listener = listener;
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
            return ComManager.getInstance().readBytes(comNumber, ReadCpm,this);
        }

        @Override
        public void onComplete(byte[] value, int size) {
            //Log.d(tag,"收到"+tools.bytesToHexString(value, size));
            listener.onResult(tools.byte2int(value,2));
        }
    }
}
