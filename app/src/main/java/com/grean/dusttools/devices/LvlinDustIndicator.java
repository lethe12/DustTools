package com.grean.dusttools.devices;

import android.util.Log;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * 绿林粉尘仪
 * Created by weifeng on 2018/2/27.
 */

public class LvlinDustIndicator {
    private static final String tag = "LvlinDustIndicator";
    private OnReadDustResultListener listener;
    private ReadDust readDust;

    public LvlinDustIndicator(int comNumber,OnReadDustResultListener listener){
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
           // Log.d(tag,String.valueOf(comNumber));
            return ComManager.getInstance().readRegister(comNumber, (byte) 0x02,0x0020,2,this);
        }

        @Override
        public void onComplete(byte[] value, int size) {
            listener.onResult(tools.getFloat(value,6));
        }
    }
}
