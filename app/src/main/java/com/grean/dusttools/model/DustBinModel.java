package com.grean.dusttools.model;

import android.util.Log;

import com.grean.dusttools.SystemConfig;
import com.grean.dusttools.devices.Cld50DustIndicator;
import com.grean.dusttools.devices.ComparativeDustData;
import com.grean.dusttools.devices.LvlinDustIndicator;
import com.grean.dusttools.devices.OnReadDustResultListener;
import com.grean.dusttools.devices.OnReadIoListener;
import com.grean.dusttools.devices.OnStepMotorDriverSettingListener;
import com.grean.dusttools.devices.PlcDriver;
import com.grean.dusttools.devices.SibataDustIndicator;
import com.grean.dusttools.devices.StepMotor;
import com.grean.dusttools.devices.StepMotorDriverSettingFormat;
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

public class DustBinModel implements OnReadIoListener ,OnStepMotorDriverSettingListener {
    public static final int INDICATOR_MAX = 7;
    private static final String tag = "DustBinModel";
    private DustBinScanResultListener listener;
    private boolean run = false;
    private SibataDustIndicator [] indicators = new SibataDustIndicator[5];
    private LvlinDustIndicator referenceIndicator;
    private ZetianDustIndicator backUpIndicator;
    private Cld50DustIndicator compareIndicator;
    private PlcDriver plcDriver;
    private StepMotor stepMotor;
    private List<ComparativeDustData> list = new ArrayList<>();
    private float lastReference = 0f,nowReference = 0f;
    private float [] sumTest = new float[INDICATOR_MAX];
    private float backupTest=0f,sumBackupTest = 0f;
    float[] test = new float[INDICATOR_MAX];
    private float screwSpeed,dustParameter;
    private int screwPath;
    private SystemConfig config;


    private String fileName,pathName;

    public DustBinModel(DustBinScanResultListener listener, SystemConfig config){
        this.listener = listener;
        for(int i=0;i<5;i++){
            indicators[i] = new SibataDustIndicator(i+3,new TestIndicator(i+1));
        }
        referenceIndicator = new LvlinDustIndicator(1,new TestIndicator(0));
        backUpIndicator = new ZetianDustIndicator(8,new TestIndicator(6));

        Log.d(tag,"开始扫描");
        new ScanThread().start();
        plcDriver = new PlcDriver(2,this);
        stepMotor = new StepMotor(0,this);
        stepMotor.readSetting();

        this.config = config;
        screwPath = config.getConfigInt("ScrewPath");
        screwSpeed = config.getConfigFloat("ScrewSpeed");
        dustParameter = config.getConfigFloat("DustParameter");
        if(screwPath == 0){
            screwPath = 10;
        }
        if(screwSpeed == 0f){
            screwSpeed = 10;
        }
        if(dustParameter == 0f){
            dustParameter = 1374.296875f;
        }
    }

    public  void setDustGenerateSetting(float speed,int path,float parameter){
        screwSpeed = speed;
        screwPath = path;
        dustParameter = parameter;
        config.saveConfig("ScrewPath",path);
        config.saveConfig("ScrewSpeed",speed);
        config.saveConfig("DustParameter",parameter);
    }

    public void startDustGenerate(float speed,int path,float parameter){
        screwSpeed = speed;
        screwPath = path;
        dustParameter = parameter;
        //计算细分，转速，总步数并设置
        stepMotor.setDustGeneration(speed,path);
        //stepMotor.move();
        stepMotor.triggerSpeed();
        float time = Math.abs(screwPath);
        time = time / screwSpeed;
        long stamp = tools.nowtime2timestamp()+(long) time*60000l;
        listener.onDustGenerationStart(stamp);
    }

    public String getDustGenerateInfo(){
        if(screwPath > 0){
            float time = screwPath;
            time = time / screwSpeed;
            long stamp = tools.nowtime2timestamp()+(long) time*60000l;
            float mass = screwSpeed / 60f * 897.5f*.049f*3.14f;// mg/s
            float volume = 0.2f*.16f*3.14f;//m³/s
            return "发尘时间:"+String.valueOf(time)+"min 预计结束时间:"+tools.timestamp2string(stamp) +" 发尘浓度:"+String.valueOf(mass/volume)+"mg/m³";
        }else{
            float time = Math.abs(screwPath);
            time = time / screwSpeed;
            long stamp = tools.nowtime2timestamp()+(long) time*60000l;
            return "预计推动时间:"+String.valueOf(time)+"min 预计结束时间:"+tools.timestamp2string(stamp);
        }
    }

    public void stopScan(){
        run = false;
    }

    @Override
    public void onResult(boolean key) {

    }

    public void switchBrush(boolean key){
        plcDriver.setIo(0,key);
    }

    public void onStopDustGenerate(){
        plcDriver.setIo(0,false);
        stepMotor.stop();
    }

    @Override
    public void onResult(StepMotorDriverSettingFormat format) {

    }

    @Override
    public void onProcess(String content, int process) {

    }

    @Override
    public void onStatus(boolean run) {
        listener.onMotorStatus(run);
    }

    public float getScrewSpeed() {
        return screwSpeed;
    }

    public float getDustParameter() {
        return dustParameter;
    }

    public int getScrewPath() {
        return screwPath;
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
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stepMotor.readStatus();
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
