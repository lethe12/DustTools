package com.grean.dusttools.model;

import com.SaveDataToExcel;
import com.grean.dusttools.devices.MultiTemperatureDataFormat;
import com.grean.dusttools.devices.MultiTemperatureTransducer;
import com.grean.dusttools.devices.OnReadMultiTemperatureListener;
import com.grean.dusttools.presenter.OnMultiTemperatureTestListener;
import com.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2018/3/9.
 */

public class MultiTemperatureModel implements SaveDataToExcel.OnWriteContentListener{
    private MultiTemperatureTransducer transducer;
    private boolean run=false;
    private ReadTemperatureThread readTemperatureThread;
    private OnMultiTemperatureTestListener listener;
    private SaveDataToExcel excel;
    private List<MultiTemperatureDataFormat> data = new ArrayList<>();

    public MultiTemperatureModel(OnMultiTemperatureTestListener listener){
        this.listener = listener;
        readTemperatureThread = new ReadTemperatureThread();
        transducer = new MultiTemperatureTransducer(0,readTemperatureThread);
        readTemperatureThread.start();

    }

    public boolean saveDataToFile(){
        excel = new SaveDataToExcel("多通道温度检测",data.size(),7);
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

    public void stopRun(){
        run = false;
    }

    public String getDateString(int position){
        return data.get(position).getDateString();
    }

    @Override
    public String getTitleName(int line) {
        switch (line){
            case 0:
                return "时间";
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return "通道"+String.valueOf(line);
            default:
                return " ";
        }
    }

    @Override
    public String getContent(int line, int row) {
        MultiTemperatureDataFormat value = data.get(row);
        switch (line){
            case 0:
                return value.getDateString();
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return tools.float2String3(value.getValues()[line-1]);
            default:
                return " ";
        }
    }

    private class ReadTemperatureThread extends Thread implements OnReadMultiTemperatureListener{
        private float[] sumValues = new float[MultiTemperatureTransducer.MAX_CHANNEL];
        private int times;
        @Override
        public void run() {
            for(int i=0;i<MultiTemperatureTransducer.MAX_CHANNEL;i++){
                sumValues[i] = 0f;
            }
            times = 0;
            run = true;
            while (run&&(!interrupted())){
                transducer.readTemperature();

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                times++;
                if(times >=30){
                    float[] result = new float[MultiTemperatureTransducer.MAX_CHANNEL];
                    for(int i=0;i<MultiTemperatureTransducer.MAX_CHANNEL;i++){
                        result[i] = sumValues[i]/times;
                    }
                    MultiTemperatureDataFormat format = new MultiTemperatureDataFormat(tools.nowtime2timestamp(),result);
                    data.add(format);
                    listener.onInsertItem(format);
                    for(int i=0;i<MultiTemperatureTransducer.MAX_CHANNEL;i++){
                        sumValues[i] = 0f;
                    }
                    times = 0;
                }
            }
        }

        @Override
        public void onResult(float[] values) {
            for(int i=0;i<MultiTemperatureTransducer.MAX_CHANNEL;i++){
                sumValues[i] += values[i];
            }
            listener.onRealTimeData(values);
        }
    }

}
