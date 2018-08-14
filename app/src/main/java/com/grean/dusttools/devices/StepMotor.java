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
    private int allSteps = 0,subdivide = 1000;//总步数 细分
    //步进电机减速器 1:100 同步带 1:3 螺杆 2mm/r 即 150r/mm

    public void setDustGeneration(float speed,int path){
        if(format!=null) {
            int max = (int) (speed*150f);
            subdivide = getSubdivide(speed);
            allSteps = path*150*subdivide;
            setSubdivide(subdivide);
            format.setStartingSpeed(2);
            format.setMaxSpeed(max);
            format.setPlus(allSteps);
            driver.writeSetting();
        }
    }

    private int getSubdivide(float speed){
        if(speed > 0.1){
            return 200;
        }else{
            return 1000;
        }
    }

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

    /**
     * 设置细分
     * @param subdivide
     */
    public void setSubdivide(int subdivide){
        byte sub=0x03;
        switch (subdivide){
            case 200:
                sub=0;
                break;
            case 400:
                sub=1;
                break;
            case 800:
                sub = 2;
                break;
            case 1600:
                sub = 3;
                break;
            case 3200:
                sub = 4;
                break;
            case 6400:
                sub = 5;
                break;
            case 12800:
                sub = 6;
                break;
            case 25600:
                sub = 7;
                break;
            case 1000:
                sub = 8;
                break;
            case 2000:
                sub = 9;
                break;
            case 4000:
                sub = 10;
                break;
            case 5000:
                sub = 11;
                break;
            case 8000:
                sub = 12;
                break;
            case 10000:
                sub = 13;
                break;
            case 20000:
                sub = 14;
                break;
            case 40000:
                sub = 15;
                break;
            default:
                break;
        }
        driver.setSubdivide(sub);
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

        protected void setSubdivide(byte subdivide){
            ComManager.getInstance().writeRegister(comNumber, (byte) 0x01,0x0011,subdivide);
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
