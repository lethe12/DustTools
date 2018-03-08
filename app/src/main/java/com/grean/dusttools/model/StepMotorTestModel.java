package com.grean.dusttools.model;

import com.grean.dusttools.devices.OnStepMotorDriverSettingListener;
import com.grean.dusttools.devices.StepMotor;

/**
 * Created by weifeng on 2018/3/8.
 */

public class StepMotorTestModel {
    private StepMotor stepMotor;
    private boolean run=false;
    private OnStepMotorDriverSettingListener listener;

    public void stopRun(){
        run = false;
    }
    public StepMotorTestModel(OnStepMotorDriverSettingListener listener){
        this.listener = listener;
        stepMotor = new StepMotor(0,listener);
    }

    public void setStepMotorSetting(int starting,int max,int plus){
        stepMotor.writeSetting(starting,max,plus);
    }

    public void setStepMotorMove(int max){
        new ProcessRun(max).start();
        stepMotor.move();
    }

    public void getStepMotorSetting(){
        stepMotor.readSetting();
    }

    public void setStepMotorStop(){
        stepMotor.stop();
    }

    public void setStepMotorScram(){
        stepMotor.scram();
    }



    private class ProcessRun extends Thread{
        private int max=10;

        public ProcessRun(int max){
            this.max = max;
        }
        @Override
        public void run() {
            int step = max;
            String content;
            float process = 0;
            run = true;
            while (run&&(!interrupted())&&(step > 0)){
                step--;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                content = "剩余时间:"+String.valueOf(step)+"S";
                process = (float) (max -step)/max*100f;
                listener.onProcess(content, (int) process);
            }
        }
    }
}
