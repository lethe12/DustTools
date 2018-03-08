package com.grean.dusttools.devices;

import android.util.Log;

import com.grean.dusttools.ComManager;
import com.grean.dusttools.ReadModBusRegistersListener;
import com.tools;

/**
 * 步进电机驱动器
 * Created by weifeng on 2018/2/27.
 */

public class StepMotor {
    private static final String tag = "StepMotor";
    private OnStepMotorDriverSettingListener listener;
    private Driver driver;
    private StepMotorDriverSettingFormat format;

    public StepMotor(int comNumber,OnStepMotorDriverSettingListener listener){
        driver = new Driver(comNumber);
        this.listener = listener;
    }

    public void readSetting(){
        Log.d(tag,"查询");
        driver.readSetting();
    }

    public void writeSetting(int starting,int max,int plus){
        if(format!=null) {
            format.setStartingSpeed(starting);
            format.setMaxSpeed(max);
            format.setPlus(plus);
            driver.writeSetting();
        }
    }

    public void stop(){
        driver.stop();
    }

    public void scram(){
        driver.scram();
    }

    public void move(){
        driver.move();
    }

    private class Driver implements ReadModBusRegistersListener{
        private int comNumber,state;
        private static final int READ_SETTING=1,WRITE_MOVE=2;
        public Driver(int num){
            this.comNumber = num;
        }

        protected void readSetting(){
            state = READ_SETTING;
            ComManager.getInstance().readRegister(comNumber, (byte) 0x01,0x0020,7,this);
        }

        protected void writeSetting(){
            int [] values = new int[6];
            values[0] = format.getStartingSpeed();
            values[1] = 100;
            values[2] = 100;
            values[3] = format.getMaxSpeed();
            values[4] = format.getPlus()>>16;//高位
            values[5] = format.getPlus()&0x0000ffff;//低位
            ComManager.getInstance().writeRegisters(comNumber,(byte)0x01,0x0020,values);
        }

        protected void move(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0x01,0x0027,0x01);
        }

        protected void stop(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0x01,0x0028,0x00);
        }

        protected void scram(){
            ComManager.getInstance().writeRegister(comNumber,(byte)0x01,0x0028,0x01);
        }

        @Override
        public void onComplete(byte[] value, int size) {
            switch (state){
                case READ_SETTING:
                    StepMotorDriverSettingFormat.SettingBuilder builder = new StepMotorDriverSettingFormat.SettingBuilder();
                    format = builder.setStartingSpeed(tools.byte2int(value,3))
                            .setMaxSpeed(tools.byte2int(value,9)).setPlus(tools.byte2IntWith4(value,11)).build();
                    listener.onResult(format);
                    break;
                case WRITE_MOVE:

                    break;
                default:

                    break;
            }
        }
    }

}
