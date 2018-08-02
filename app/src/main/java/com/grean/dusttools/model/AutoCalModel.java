package com.grean.dusttools.model;

import android.util.Log;

import com.SaveDataToExcel;
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

public class AutoCalModel implements SaveDataToExcel.OnWriteContentListener{
    private static final String tag = "AutoCalModel";
    public static final int MAX=4;
    private OnAutoCalListener listener;
    private boolean run;
    private DustMainBoard mainBoard,device1,device2,device3;
    private AutoCalThread autoCalThread;
    private List<DustMainBoardCalInfoFormat> list = new ArrayList<>();
    private CalListener[] calListeners = new CalListener[MAX];
    private SaveDataToExcel excel;

    public AutoCalModel(OnAutoCalListener listener){
        this.listener = listener;
        autoCalThread = new AutoCalThread();
        calListeners[0] = new CalListener(0);
        calListeners[1] = new CalListener(1);
        calListeners[2] = new CalListener(2);
        calListeners[3] = new CalListener(3);
        mainBoard = new DustMainBoard(0,1400,20,calListeners[0]);
        device1 = new DustMainBoard(1,1400,20,calListeners[1]);
        device2 = new DustMainBoard(2,1400,20,calListeners[2]);
        device3 = new DustMainBoard(3,1400,20,calListeners[3]);
    }

    public String getDate(int position){
        return list.get(position).getDateString();
    }

    public void stopRun(){
        run = false;
    }

    public boolean saveDataToFile(){
        excel = new SaveDataToExcel("自动校准老化",list.size(),5);
        excel.setOnWriteContentListener(this);
        return excel.exportData2File();
    }

    public String getFileName(){
        if(excel!=null){
            return excel.getFileName();
        }else{
            return " ";
        }
    }

    public void startAutoCal(String stepString,String timeString){
        if(!run){
            int step = Integer.valueOf(stepString);
            int time = Integer.valueOf(timeString);
            mainBoard.setMotorRounds(step);
            mainBoard.setMotorTime(time);
            device1.setMotorRounds(step);
            device1.setMotorTime(time);
            device2.setMotorRounds(step);
            device2.setMotorTime(time);
            device3.setMotorRounds(step);
            device3.setMotorTime(time);
            autoCalThread.start();
        }
    }


    @Override
    public String getTitleName(int line) {
        switch (line){
            case 0:
                return "时间";
            case 1:
                return "设备1";
            case 2:
                return "设备2";
            case 3:
                return "设备3";
            case 4:
                return "设备4";
            default:
                return "-";
        }
    }

    @Override
    public String getContent(int line, int row) {
        DustMainBoardCalInfoFormat format = list.get(row);
        switch (line){
            case 0:
                return format.getDateString();
            case 1:
            case 2:
            case 3:
            case 4:
                return format.getResult(line-1);
            default:
                return "-";

        }
    }

    private class CalListener implements OnMainBoardListener{
        private int num;
        private boolean bgOk,spanOk,spanPos,measurePos;



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

        public boolean isSpanPos() {
            return spanPos;
        }

        public boolean isMeasurePos() {
            return measurePos;
        }

        @Override
        public void onBgResult(boolean key) {
            bgOk = key;
        }

        @Override
        public void onSpanResult(boolean key) {
            spanOk = key;
        }

        @Override
        public void onPos(boolean span, boolean measure) {
            spanPos = span;
            measurePos = measure;
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
                device1.ctrl.stopDustIndicator();
                device2.ctrl.stopDustIndicator();
                device3.ctrl.stopDustIndicator();
                for(int i=0;i<MAX;i++){
                    states[i] = "停止测量,开始校准";
                }
               // Log.d(tag,"停止测量,开始校准"+String.valueOf(states.length));
                listener.onRealTimeState(states);
                delay(1);
                mainBoard.ctrl.ctrlRelay(true);
                device1.ctrl.ctrlRelay(true);
                device3.ctrl.ctrlRelay(true);
                device2.ctrl.ctrlRelay(true);
                delay(10);//等待管阀
                for(int i=0;i<MAX;i++){
                    states[i] = "正在校零";
                }
                listener.onRealTimeState(states);
                mainBoard.ctrl.startDustIndicatorBg();
                device1.ctrl.startDustIndicatorBg();
                device2.ctrl.startDustIndicatorBg();
                device3.ctrl.startDustIndicatorBg();
                delay(100);//等待校零点
                mainBoard.ctrl.readDustIndicatorBgResult();
                device1.ctrl.readDustIndicatorBgResult();
                device2.ctrl.readDustIndicatorBgResult();
                device3.ctrl.readDustIndicatorBgResult();
                delay(10);
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
                device1.ctrl.stopDustIndicatorBg();
                device2.ctrl.stopDustIndicatorBg();
                device3.ctrl.stopDustIndicatorBg();
                delay(1);
                mainBoard.ctrl.setMotorSetting(DustMainBoard.MOTOR_FORWARD);
                device1.ctrl.setMotorSetting(DustMainBoard.MOTOR_FORWARD);
                device2.ctrl.setMotorSetting(DustMainBoard.MOTOR_FORWARD);
                device3.ctrl.setMotorSetting(DustMainBoard.MOTOR_FORWARD);
                delay(mainBoard.getMotorTime()/100+2);//等待散光板到位
                mainBoard.ctrl.startDustIndicatorSpan();
                device1.ctrl.startDustIndicatorSpan();
                device2.ctrl.startDustIndicatorSpan();
                device3.ctrl.startDustIndicatorSpan();
                delay(80);//等待校跨
                mainBoard.ctrl.readDustIndicatorSpanResult();
                device1.ctrl.readDustIndicatorSpanResult();
                device2.ctrl.readDustIndicatorSpanResult();
                device3.ctrl.readDustIndicatorSpanResult();
                delay(1);
                for(int i=0;i<MAX;i++){
                    span[i] = calListeners[i].isSpanOk();
                    if(span[i]){
                        states[i] = "校跨成功";
                    }else{
                        states[i] = "校跨失败";
                    }
                    states[i] += ",结束校准";
                }
                listener.onRealTimeState(states);
                mainBoard.ctrl.stopDustIndicatorSpan();
                device1.ctrl.stopDustIndicatorSpan();
                device2.ctrl.stopDustIndicatorSpan();
                device3.ctrl.stopDustIndicatorSpan();
                delay(1);
                mainBoard.ctrl.setMotorSetting(DustMainBoard.MOTOR_BACK);
                device1.ctrl.setMotorSetting(DustMainBoard.MOTOR_BACK);
                device2.ctrl.setMotorSetting(DustMainBoard.MOTOR_BACK);
                device3.ctrl.setMotorSetting(DustMainBoard.MOTOR_BACK);
                delay(1);
                mainBoard.ctrl.ctrlRelay(false);
                device1.ctrl.ctrlRelay(false);
                device2.ctrl.ctrlRelay(false);
                device3.ctrl.ctrlRelay(false);
                delay(mainBoard.getMotorTime()/100+1);
                mainBoard.ctrl.readMainBoardState();
                device1.ctrl.readMainBoardState();
                device2.ctrl.readMainBoardState();
                device3.ctrl.readMainBoardState();
                delay(1);
                boolean[] spanPos = new boolean[4],measurePos = new boolean[4];
                for(int i=0;i<4;i++){
                    spanPos[i] = calListeners[i].isSpanPos();
                    measurePos[i] = calListeners[i].isMeasurePos();
                }
                DustMainBoardCalInfoFormat format = new DustMainBoardCalInfoFormat(tools.nowtime2timestamp(),bg,span,spanPos,measurePos);
                list.add(format);
                listener.onInsertItem(format);
                mainBoard.ctrl.startDustIndicator();
                for(int i=0;i<MAX;i++){
                    states[i] = "等待下次测试";
                }
                for(int i=0;i<MAX;i++){
                    bg[i] = false;
                    span[i] = false;
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
