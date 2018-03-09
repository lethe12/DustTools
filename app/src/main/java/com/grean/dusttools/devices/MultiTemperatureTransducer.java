package com.grean.dusttools.devices;

import android.util.Log;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * 多通道温度变送器
 * Created by weifeng on 2018/2/27.
 */

public class MultiTemperatureTransducer {
    private static final String tag= "Transducer";
    public static final int MAX_CHANNEL = 6;//最大通道数
    private OnReadMultiTemperatureListener listener;
    private ReadTransducer transducer;

    public MultiTemperatureTransducer(int comNumber,OnReadMultiTemperatureListener listener){
        this.listener = listener;
        transducer = new ReadTransducer(comNumber);
    }

    public void readTemperature(){
        transducer.readTemperature();
    }

    private class ReadTransducer implements ReadModBusRegistersListener{
        private int comNumber;

        public ReadTransducer(int comNumber){
            this.comNumber = comNumber;
        }

        protected void readTemperature(){
            //Log.d(tag,"read temperature");
            ComManager.getInstance().readRegister(comNumber, (byte) 0x01,0x00,6,this);
        }

        @Override
        public void onComplete(byte[] value, int size) {
            //Log.d(tag,"calc temperature");
            float[] values = new float[MAX_CHANNEL];
            for(int i=0;i<MAX_CHANNEL;i++){
                values[i] = (float) tools.byte2int(value,3+i*2)/10;
            }
            listener.onResult(values);
        }
    }
}
