package com.grean.dusttools.devices;

import com.tools;

/**
 * Created by weifeng on 2018/3/9.
 */

public class DustMainBoardCalInfoFormat {
    private boolean[] bg,span;
    private long date;

    public DustMainBoardCalInfoFormat(long date,boolean bg[],boolean span[]){
        this.date = date;
        this.bg = new boolean[bg.length];
        System.arraycopy(bg,0,this.bg,0,bg.length);
        this.span = new boolean[span.length];
        System.arraycopy(span,0,this.bg,0,span.length);
    }

    public String getDateString(){
        return tools.timestamp2stringWithSecond(date);
    }

    public String getResult(int num){
        if((num>=0)&&(num < bg.length)){
            return "校零"+String.valueOf(bg[num])+" 校跨"+String.valueOf(span[num]);
        }else{
            return "-";
        }
    }
}
