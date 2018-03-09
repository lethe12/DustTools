package com;

import com.grean.dusttools.devices.ComparativeDustData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by weifeng on 2018/3/9.
 */

public class SaveDataToExcel {
    private String fileName,pathName,name;
    int size,line;
    private OnWriteContentListener listener;
    public interface OnWriteContentListener{
        String getTitleName(int line);
        String getContent(int line,int row);
    }

    public void setOnWriteContentListener(OnWriteContentListener listener){
        this.listener = listener;
    }

    /**
     *
     * @param name 文件类型
     * @param size 数据的行数
     * @param line 数据的列数
     */
    public SaveDataToExcel(String name,int size,int line){
        this.name = name;
        this.size = size;
        this.line = line;
    }

    public String getFileName(){
        return pathName+fileName;
    }

    public boolean exportData2File() {
        boolean exportDataResult=true;
        pathName = "/mnt/usbhost/Storage01/GREAN/";//"/storage/emulated/0/GREAN/";
        fileName = name+ com.tools.nowTime2FileString()+"导出.xls";
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
            int elementMax = size;
            if(elementMax > 0) {
                int sheetMax = elementMax / 65534;
                sheetMax += 1;
                int index = 0;
                for(int i=0;i<sheetMax;i++){
                    sheet = wwb.createSheet("Sheet"+String.valueOf(i+1),i);
                    addTitle(sheet);
                    if((elementMax-index)>= 65534){
                        addOneSheet(sheet,index,index+65534);
                        index += 65534;
                    }else{
                        addOneSheet(sheet,index,elementMax);
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
        for(int i=0;i<line;i++){
            label = new Label(i,0,listener.getTitleName(i));
            sheet.addCell(label);
        }

        /*label = new Label(0,0,"时间");
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
        sheet.addCell(label);*/
    }

    private void addOneSheet(WritableSheet sheet, int index, int max)throws WriteException{
        int row=1;
        for(int i=index;i<max;i++){
            Label label;
            for(int j=0;j<line;j++) {
                label = new Label(j, row, listener.getContent(j,i));
                sheet.addCell(label);
            }
            row++;
        }

        //List<String> element;

        /*ComparativeDustData data;
        for(int i=index;i<max;i++){
            Label label;
            data = dataList.get(i);
            label = new Label(0,row,data.getDateString());
            sheet.addCell(label);
            label = new Label(1,row, com.tools.float2String3(data.getReference()));
            sheet.addCell(label);
            for (int j=0;j<5;j++){
                label = new Label(j+2,row, com.tools.float2String3(data.getTest()[j]));
                sheet.addCell(label);
            }
            label = new Label(7,row, com.tools.float2String3(data.getBackup()));
            sheet.addCell(label);
            label = new Label(8,row,String.valueOf(data.getDate()));
            sheet.addCell(label);
            row++;
        }*/
    }

}
