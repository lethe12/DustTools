package com.grean.dusttools.model;

import android.util.Log;

import com.grean.dusttools.devices.DustMainBoard;
import com.grean.dusttools.devices.DustMainBoardCalInfoFormat;
import com.grean.dusttools.devices.OnMainBoardListener;
import com.grean.dusttools.presenter.OnAutoCalListener;
import com.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2018/3/9.
 */

public class AutoCalModel {
    private static final String tag = "AutoCalModel";
    public static final int MAX=4;
    private OnAutoCalListener listener;
    private boolean run;
    private DustMainBoard mainBoard;
    private AutoCalThread autoCalThread;
    private List<DustMainBoardCalInfoFormat> list = new ArrayList<>();
    private CalListener[] calListeners = new CalListener[MAX];

    public AutoCalModel(OnAutoCalListener listener){
        this.listener = listener;
        autoCalThread = new AutoCalThread();
        calListeners[0] = new CalListener(0);
        calListeners[1] = new CalListener(1);
        calListeners[2] = new CalListener(2);
        calListeners[3] = new CalListener(3);
        mainBoard = new DustMainBoard(0,1400,14,calListeners[0]);
    }

    public String getDate(int position){
        return String.valueOf(position);
    }

    public void stopRun(){
        run = false;
    }

    public void startAutoCal(){
        if(!run){
            autoCalThread.start();
        }
    }

    public boolean saveDataToFile(){
        return true;
    }

    public String getFileName(){
        return "-";
    }

    private class CalListener implements OnMainBoardListener{
        private int num;
        private boolean bgOk,spanOk;

        public CalListener(int num){
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public boolean isBgOk() {
            return bgOk;
        }

        public boolean isSpanOk() {
            return spanOk;
        }

        @Override
        public void onBgResult(boolean key) {
            bgOk = key;
        }

        @Override
        public void onSpanResult(boolean key) {
            spanOk = key;
        }
    }

    private class AutoCalThread extends Thread {
        private String[] states = new String[MAX];
        private boolean [] bg=new boolean[MAX],span=new boolean[MAX];
        @Override
        public void run() {
            run = true;
            while(run&&(!interrupted())){
                //停止粉尘仪
                mainBoard.ctrl.stopDustIndicator();
                for(int i=0;i<MAX;i++){
                    states[i] = "停止测量,开始校准";
                }
               // Log.d(tag,"停止测量,开始校准"+String.valueOf(states.length));
                listener.onRealTimeState(states);
                delay(1);
                mainBoard.ctrl.ctrlRelay(true);
                delay(10);
                for(int i=0;i<MAX;i++){
                    states[i] = "正在校零";
                }
                listener.onRealTimeState(states);
                mainBoard.ctrl.startDustIndicatorBg();
                delay(100);
                mainBoard.ctrl.readDustIndicatorBgResult();
                delay(1);
                for(int i=0;i<MAX;i++){
                    bg[i] = calListeners[i].isBgOk();
                    if(bg[i]){
                        states[i] = "校零成功";
                    }else{
                        states[i] = "校零失败";
                    }
                    states[i] += ",正在校跨";
                }
                listener.onRealTimeState(states);
                mainBoard.ctrl.stopDustIndicatorBg();
                delay(1);
                mainBoard.ctrl.setMotorSetting(DustMainBoard.MOTOR_FORWARD);
                delay(mainBoard.getMotorTime()/100+2);
                mainBoard.ctrl.startDustIndicatorSpan();
                delay(80);
                mainBoard.ctrl.readDustIndicatorSpanResult();
                delay(1);
                for(int i=0;i<MAX;i++){
                    span[i] = calListeners[i].isSpanOk();
                    if(bg[i]){
                        states[i] = "校跨成功";
                    }else{
                        states[i] = "校跨失败";
                    }
                    states[i] += ",结束校准";
                }
                listener.onRealTimeState(states);
                mainBoard.ctrl.stopDustIndicatorSpan();
                delay(1);
                mainBoard.ctrl.setMotorSetting(DustMainBoard.MOTOR_BACK);
                delay(1);
                mainBoard.ctrl.ctrlRelay(false);
                delay(mainBoard.getMotorTime()/100+1);
                DustMainBoardCalInfoFormat format = new DustMainBoardCalInfoFormat(tools.nowtime2timestamp(),bg,span);
                list.add(format);
                listener.onInsertItem(format);
                mainBoard.ctrl.startDustIndicator();
                for(int i=0;i<MAX;i++){
                    states[i] = "等待下次测试";
                }
                delay(20);
            }
            listener.endCal();
        }

    }


    private void delay(int n){
        try {
            Thread.sleep(1000*n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
