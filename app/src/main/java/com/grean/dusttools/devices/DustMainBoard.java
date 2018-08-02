package com.grean.dusttools.devices;

import android.util.Log;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * 扬尘主板
 * Created by weifeng on 2018/2/27.
 */

public class DustMainBoard {
    public static final int MOTOR_STOP=0,MOTOR_FORWARD=1,MOTOR_BACK=2;
    private int motorRounds,motorTime;
    public MainBoardCtrl ctrl;
    private OnMainBoardListener listener;

    public void setMotorRounds(int motorRounds) {
        this.motorRounds = motorRounds;
    }

    public void setMotorTime(int motorTime) {
        this.motorTime = motorTime*100;
    }

    public DustMainBoard(int comNumber, int rounds, int time, OnMainBoardListener listener){
        this.listener = listener;
        ctrl = new MainBoardCtrl(comNumber);
        this.motorRounds = rounds;
        this.motorTime = time * 100;
    }

    public int getMotorTime() {
        return motorTime;
    }

    public class MainBoardCtrl implements ReadModBusRegistersListener{
        private int comNumber,state;
        public MainBoardCtrl(int comNumber){
            this.comNumber = comNumber;
            state = 0;
        }

        public void stopDustIndicator(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0xdd,0x0003,0x00);
        }

        public void startDustIndicator(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0xdd,0x0003,0x01);
        }

        public void startDustIndicatorBg(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0xdd,0x0006,0x01);
        }

        public void stopDustIndicatorBg(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0xdd,0x0006,0x00);
        }

        public void readDustIndicatorBgResult(){
            state = 1;
            ComManager.getInstance().readRegister(comNumber,(byte)0xdd,0x0007,0x01,this);
        }

        public void startDustIndicatorSpan(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0xdd,0x0008,0x01);
        }

        public void stopDustIndicatorSpan(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0xdd,0x0008,0x00);
        }

        public void readDustIndicatorSpanResult(){
            state = 2;
            ComManager.getInstance().readRegister(comNumber,(byte)0xdd,0x0009,0x01,this);
        }

        public void readMainBoardState(){
            state = 3;
            ComManager.getInstance().readRegister(comNumber, (byte) 0x55,0x2001,0x1f,this);
        }

        public void ctrlRelay(boolean key){
            if(key){
                ComManager.getInstance().writeRegister(comNumber,(byte)0x55,0x1001,0x01);
            }else{
                ComManager.getInstance().writeRegister(comNumber,(byte)0x55,0x1002,0x01);
            }
        }

        /**
         *
         * @param fun =0停止; =1正向; =2反向;
         */
        public void setMotorSetting(int fun){
            ComManager.getInstance().writeRegister(comNumber,(byte)0x55,0x1003,motorRounds);
            ComManager.getInstance().writeRegister(comNumber,(byte)0x55,0x1004,motorTime);
            ComManager.getInstance().writeRegister(comNumber,(byte)0x55,0x1005,fun);
        }


        @Override
        public void onComplete(byte[] value, int size) {
            if(state == 1){//bg
                if(value[4]==0x01){//success
                    listener.onBgResult(true);
                }else{//fail
                    listener.onBgResult(false);
                }
            }else if(state == 2){//span
                if(value[4]==0x01){
                    listener.onSpanResult(true);
                }else{
                    listener.onSpanResult(false);
                }
            }else if(state == 3){//查询参数
                boolean span = false,measure = false;
                if(value[37]!=0x00){
                    span = true;
                }
                if(value[39]!=0x00){
                    measure = true;
                }
                listener.onPos(span,measure);
            }
        }
    }

}
