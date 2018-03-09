package com.grean.dusttools.model;

import android.util.Log;

import com.grean.dusttools.devices.ComparativeDustData;
import com.grean.dusttools.devices.LvlinDustIndicator;
import com.grean.dusttools.devices.OnReadDustResultListener;
import com.grean.dusttools.devices.SibataDustIndicator;
import com.grean.dusttools.devices.ZetianDustIndicator;
import com.grean.dusttools.presenter.DustBinScanResultListener;
import com.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by weifeng on 2018/2/27.
 */

public class DustBinModel {
    public static final int INDICATOR_MAX = 7;
    private static final String tag = "DustBinModel";
    private DustBinScanResultListener listener;
    private boolean run = false;
    private SibataDustIndicator [] indicators = new SibataDustIndicator[5];
    private LvlinDustIndicator referenceIndicator;
    private ZetianDustIndicator backUpIndicator;
    private List<ComparativeDustData> list = new ArrayList<>();
    private float lastReference = 0f,nowReference = 0f;
    private float [] sumTest = new float[INDICATOR_MAX];
    private float backupTest=0f,sumBackupTest = 0f;
    float[] test = new float[INDICATOR_MAX];

    private String fileName,pathName;

    public DustBinModel(DustBinScanResultListener listener){
        this.listener = listener;
        for(int i=0;i<5;i++){
            indicators[i] = new SibataDustIndicator(i+4,new TestIndicator(i+1));
        }
        referenceIndicator = new LvlinDustIndicator(1,new TestIndicator(0));
        backUpIndicator = new ZetianDustIndicator(3,new TestIndicator(6));
        Log.d(tag,"开始扫描");
        new ScanThread().start();
    }

    public void stopScan(){
        run = false;
    }

    private class TestIndicator implements OnReadDustResultListener{
        private int num;
        public TestIndicator(int num){
            this.num = num;
        }

        @Override
        public void onResult(float value) {
            if(num == 0){//参比粉尘仪
                nowReference = value;
            }else if(num == 6){
                sumBackupTest += value;
            }else{
                sumTest[num-1] += value;
            }
            listener.onTestResult(num,value);
        }
    }

    public String getDustDate(int position){
        if(position < list.size()){
            return list.get(position).getDateString();
        }else {
            return "";
        }
    }

    public String getFileName(){
        return pathName+fileName;
    }

    public boolean export2File(){
        return exportData2File();
    }

    /**
     * 扫描结果
     */
    private class ScanThread extends Thread{

        @Override
        public void run() {
            run = true;
            int times = 0;
            while (run&&(!interrupted())){
                referenceIndicator.readDust();
                for(int i=0;i<5;i++){
                    if(!indicators[i].readDust()){
                        break;
                    }
                }
                backUpIndicator.readDust();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                times++;
                if(lastReference!=nowReference){
                    if(times !=0) {
                        saveDustData(times);//存储数据
                    }
                    times = 0;
                }else if(times >=120){
                    saveDustData(times);//存储数据
                    times = 0;
                }else{

                }
            }
            run = false;
            listener.onErrorCommunication();
        }
    }

    private void saveDustData(int times){
        Log.d(tag,"存储数据");
        for(int i=0;i<INDICATOR_MAX;i++){
            test[i] = sumTest[i] / times;
        }
        backupTest = sumBackupTest / times;
        ComparativeDustData data = new ComparativeDustData(tools.nowtime2timestamp(),nowReference,test,backupTest);
        lastReference = nowReference;
        list.add(data);
        listener.insertItem(data);
        sumBackupTest = 0;
        for(int i=0;i<INDICATOR_MAX;i++){
            sumTest[i] = 0f;
        }

    }

    private boolean exportData2File() {
        boolean exportDataResult=true;
        pathName = "/mnt/usbhost/Storage01/GREAN/";//"/storage/emulated/0/GREAN/";
        fileName = "粉尘仪比对"+ tools.nowTime2FileString()+"导出.xls";
        File path = new File(pathName);
        File file = new File(path,fileName);

        try{
            if (!path.exists()) {
                //Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if (!file.exists()) {
                //Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }

            WritableWorkbook wwb;
            OutputStream os = new FileOutputStream(file);
            wwb = Workbook.createWorkbook(os);

            //ArrayList<HistoryDataFormat> list = exportDataFormat(start,end);
            WritableSheet sheet;
            //每个sheet最多65534行
            int elementMax = list.size();
            if(elementMax > 0) {
                int sheetMax = elementMax / 65534;
                sheetMax += 1;
                int index = 0;
                for(int i=0;i<sheetMax;i++){
                    sheet = wwb.createSheet("Sheet"+String.valueOf(i+1),i);
                    addTitle(sheet);
                    if((elementMax-index)>= 65534){
                        addOneSheet(sheet,list,index,index+65534);
                        index += 65534;
                    }else{
                        addOneSheet(sheet,list,index,elementMax);
                        break;
                    }
                }
            }else{
                sheet = wwb.createSheet("Sheet1",0);
                addTitle(sheet);
            }

            wwb.write();
            os.flush();
            wwb.close();
            //需要关闭输出流，结束占用，否则系统会 结束 app
            os.close();

        }catch (IOException e) {
            e.printStackTrace();
            exportDataResult = false;
        } catch (RowsExceededException e) {
            e.printStackTrace();
            exportDataResult = false;
        } catch (WriteException e) {
            e.printStackTrace();
            exportDataResult = false;
        }
        return exportDataResult;

    }

    private void addTitle(WritableSheet sheet) throws WriteException {
        Label label;
        label = new Label(0,0,"时间");
        sheet.addCell(label);
        label = new Label(1,0,"参比");
        sheet.addCell(label);
        label = new Label(2,0,"测试1");
        sheet.addCell(label);
        label = new Label(3,0,"测试2");
        sheet.addCell(label);
        label = new Label(4,0,"测试3");
        sheet.addCell(label);
        label = new Label(5,0,"测试4");
        sheet.addCell(label);
        label = new Label(6,0,"测试5");
        sheet.addCell(label);
        label = new Label(7,0,"备用");
        sheet.addCell(label);
        label = new Label(8,0,"时间戳");
        sheet.addCell(label);
    }

    private void addOneSheet(WritableSheet sheet,List<ComparativeDustData> dataList,int index,int max)throws WriteException{
        int row=1;
        //List<String> element;
        ComparativeDustData data;
        for(int i=index;i<max;i++){
            Label label;
            data = dataList.get(i);
            label = new Label(0,row,data.getDateString());
            sheet.addCell(label);
            label = new Label(1,row,tools.float2String3(data.getReference()));
            sheet.addCell(label);
            for (int j=0;j<5;j++){
                label = new Label(j+2,row,tools.float2String3(data.getTest()[j]));
                sheet.addCell(label);
            }
            label = new Label(7,row,tools.float2String3(data.getBackup()));
            sheet.addCell(label);
            label = new Label(8,row,String.valueOf(data.getDate()));
            sheet.addCell(label);
            row++;
        }
    }

    private void addOneSheet(WritableSheet sheet, List<String> date,List<List<String>> data, int index, int max) throws WriteException {
        int row=1;
        List<String> element;
        for(int i=index;i<max;i++){
            element = data.get(i);
            Label label;
            label = new Label(0,row,date.get(i));
            sheet.addCell(label);
            for(int j=0;j<6;j++){
                label = new Label(j+1,row,element.get(j));
                sheet.addCell(label);
            }
            row++;
        }
    }
}
