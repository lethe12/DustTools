package com.grean.dusttools.devices;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;

/**
 * 台达PLC驱动
 * Created by weifeng on 2018/3/7.
 */

public class PlcDriver {
    private OnReadIoListener listener;

    public PlcDriver(int comNumber,OnReadIoListener listener){
        this.listener = listener;
    }



    private class CtrlIo implements ReadModBusRegistersListener{
        private int comNumber;

        public CtrlIo (int comNumber){
            this.comNumber = comNumber;
        }

        /**
         * 直接控制PLC的Yn口值
         * @param num 0~5
         * @param key On/Off
         */
        protected void writeIo(int num,boolean key){
            if((num>=0)&&(num<=5)) {
                if(key) {
                    ComManager.getInstance().writeIO(comNumber, (byte) 0x01, 0x0500 + num, 0xff00);
                }else{
                    ComManager.getInstance().writeIO(comNumber, (byte) 0x01, 0x0500 + num, 0x0000);
                }
            }
        }

        protected void readIo(int num){
            if((num>=0)&&(num<=7)){
                ComManager.getInstance().readIO(comNumber,(byte)0x01,0x0400+num,this);
            }
        }
        @Override
        public void onComplete(byte[] value, int size) {
            if(value[3]==0x01) {
                listener.onResult(true);
            }else{
                listener.onResult(false);
            }
        }
    }
}
