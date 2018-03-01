package com.grean.dusttools.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.grean.dusttools.R;
import com.grean.dusttools.model.DustBinModel;

/**
 * Created by weifeng on 2018/2/9.
 */

public class DustBinActivity extends Activity implements DustBinScanResultListener{
    private static final String tag = "DustBinActivity";
    private static final int msgUpdateRealTimeResult = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgUpdateRealTimeResult:
                    String string="";
                    for(int i=0;i<DustBinModel.INDICATOR_MAX;i++){
                        if(realTimeString[i]!=null) {
                            string += realTimeString[i] + "\n";
                        }
                    }
                    realTime.setText(string);
                    break;
                default:

                    break;
            }

        }
    };

    private TextView realTime;
    private String[] realTimeString = new String[DustBinModel.INDICATOR_MAX];

    private DustBinModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust_bin);
        initView();
        model = new DustBinModel(this);
    }

    private void initView(){
        realTime = findViewById(R.id.tvDustBinRealtime);
    }

    @Override
    public void onTestResult(int num, float result) {
        realTimeString[num] = "粉尘仪"+String.valueOf(num)+":"+String.valueOf(result);
        handler.sendEmptyMessage(msgUpdateRealTimeResult);
    }

    @Override
    public void onErrorCommunication() {
        Log.d(tag,"结束扫描");
    }

    @Override
    protected void onDestroy() {
        Log.d(tag,"onDestroy");
        model.stopScan();
        super.onDestroy();
    }
}
