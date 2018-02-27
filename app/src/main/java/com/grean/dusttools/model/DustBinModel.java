package com.grean.dusttools.model;

import android.util.Log;

import com.grean.dusttools.devices.OnReadDustResultListener;
import com.grean.dusttools.devices.SibataDustIndicator;
import com.grean.dusttools.presenter.DustBinScanResultListener;

/**
 * Created by weifeng on 2018/2/27.
 */

public class DustBinModel {
    private static final String tag = "DustBinModel";
    private DustBinScanResultListener listener;
    private boolean run = false;
    private SibataDustIndicator [] indicators = new SibataDustIndicator[5];

    public DustBinModel(DustBinScanResultListener listener){
        this.listener = listener;
        for(int i=0;i<5;i++){
            indicators[i] = new SibataDustIndicator(i+4,new TestIndicator(i));
        }
        Log.d(tag,"开始扫描");
        new ScanThread().start();
    }

    public void stopScan(){
        run = false;
    }

    private class TestIndicator implements OnReadDustResultListener{
        private int num;
        public TestIndicator(int num){
            this.num = num;
        }

        @Override
        public void onResult(float value) {
            listener.onTestResult(num,value);
        }
    }

    /**
     * 扫描结果
     */
    private class ScanThread extends Thread{

        @Override
        public void run() {
            run = true;
            while (run&&(!interrupted())){
                for(int i=0;i<5;i++){
                    if(!indicators[i].readDust()){
                        break;
                    }
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            run = false;
            listener.onErrorCommunication();
        }
    }
}
