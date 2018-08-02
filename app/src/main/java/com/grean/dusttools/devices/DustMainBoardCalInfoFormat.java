package com.grean.dusttools.devices;

import android.util.Log;

import com.tools;

/**
 * Created by weifeng on 2018/3/9.
 */

public class DustMainBoardCalInfoFormat {

    private boolean[] bg,span,spanPos,measurePos;
    private long date;

    public DustMainBoardCalInfoFormat(long date,boolean bg[],boolean span[],boolean spanPos[],boolean measurePos[]){
        this.date = date;
        this.bg = new boolean[bg.length];
        System.arraycopy(bg,0,this.bg,0,bg.length);
        this.span = new boolean[span.length];
        System.arraycopy(span,0,this.span,0,span.length);
        this.spanPos = new boolean[spanPos.length];
        System.arraycopy(spanPos,0,this.spanPos,0,spanPos.length);
        this.measurePos = new boolean[measurePos.length];
        System.arraycopy(measurePos,0,this.measurePos,0,measurePos.length);
        //Log.d("CalInfoFormat",String.valueOf(bg[0])+String.valueOf(span[0]));
    }

    public String getDateString(){
        return tools.timestamp2stringWithSecond(date);
    }

    public String getResult(int num){
        if((num>=0)&&(num < bg.length)){
            return "校零"+String.valueOf(bg[num])+" 校跨"+String.valueOf(span[num])+"限位"+String.valueOf(spanPos[num])+"测量"+String.valueOf(measurePos[num]);
        }else{
            return "-";
        }
    }
}
