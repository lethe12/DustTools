package com.grean.dusttools.devices;

import com.grean.dusttools.model.DustBinModel;
import com.tools;

/**
 * Created by weifeng on 2018/3/1.
 */

public class ComparativeDustData {
    private float reference,backup;
    private float [] test = new float[DustBinModel.INDICATOR_MAX];
    private long date;
    private String dateString;

    public float getReference() {
        return reference;
    }

    public long getDate() {
        return date;
    }

    public float getBackup() {
        return backup;
    }

    public String getDateString() {
        return dateString;
    }

    public float[] getTest() {
        return test;
    }

    public ComparativeDustData(long date,float reference,float [] data,float backup){
        this.date = date;
        this.reference = reference;
        this.backup = backup;
        this.dateString = tools.timestamp2stringWithSecond(date);
        if(data.length == DustBinModel.INDICATOR_MAX){
            System.arraycopy(data,0,test,0,DustBinModel.INDICATOR_MAX-1);
        }
    }
}
