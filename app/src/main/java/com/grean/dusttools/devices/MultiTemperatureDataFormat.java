package com.grean.dusttools.devices;


import com.tools;

/**
 * Created by weifeng on 2018/3/9.
 */

public class MultiTemperatureDataFormat {
    private long date;
    private float[] values = new float[MultiTemperatureTransducer.MAX_CHANNEL];

    public long getDate() {
        return date;
    }

    public String getDateString(){
        return tools.timestamp2stringWithSecond(date);
    }

    public float[] getValues() {
        return values;
    }

    public MultiTemperatureDataFormat (long date,float[] values){
        this.date = date;
        if(values.length==MultiTemperatureTransducer.MAX_CHANNEL){
            System.arraycopy(values,0,this.values,0,MultiTemperatureTransducer.MAX_CHANNEL);
        }
    }
}
