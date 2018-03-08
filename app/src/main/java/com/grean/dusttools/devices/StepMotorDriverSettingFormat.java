package com.grean.dusttools.devices;

import android.util.Log;

/**
 * Created by weifeng on 2018/3/8.
 */

public class StepMotorDriverSettingFormat {
    private int startingSpeed,maxSpeed,plus;

   public StepMotorDriverSettingFormat(SettingBuilder builder){
       this.startingSpeed = builder.startingSpeed;
       this.maxSpeed = builder.maxSpeed;
       this.plus = builder.plus;
   }

   public float getTime(){
       return (float) (0.2+(float)(Math.abs(plus) - startingSpeed*16/3)*6/((float)maxSpeed*160));
   }

    public void setStartingSpeed(int startingSpeed) {
        this.startingSpeed = startingSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setPlus(int plus) {
        this.plus = plus;
    }

    public int getStartingSpeed() {
        return startingSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getPlus() {
        return plus;
    }

    public static class SettingBuilder{
        private int startingSpeed,maxSpeed,plus;

        public SettingBuilder setStartingSpeed(int speed){
            startingSpeed = speed;
            return this;
        }

        public SettingBuilder setMaxSpeed(int speed){
            if(speed==0){
                maxSpeed = 60;
            }else {
                maxSpeed = speed;
            }
            return this;
        }

        public SettingBuilder setPlus(int plus){

            if(plus == 0){
                this.plus = 16000;
            }else{
                this.plus = plus;
            }
            return this;
        }

        public StepMotorDriverSettingFormat build (){
            return new StepMotorDriverSettingFormat(this);
        }
    }
}
