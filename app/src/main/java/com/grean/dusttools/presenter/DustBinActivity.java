package com.grean.dusttools.presenter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.grean.dusttools.R;
import com.grean.dusttools.SystemConfig;
import com.grean.dusttools.devices.ComparativeDustData;
import com.grean.dusttools.model.DustBinModel;
import com.tools;
import com.view.ItemFragment;
import com.view.NoscrollListView;
import com.view.SyncHorizontalScrollView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by weifeng on 2018/2/9.
 */

public class DustBinActivity extends Activity implements DustBinScanResultListener,View.OnClickListener{
    private static final String tag = "DustBinActivity";
    private static final int msgUpdateRealTimeResult = 1,msgInsertItem = 2,msgDustGenerateEnd =3,msgMotorRunning=4;

    private NoscrollListView mLeft;
    private LeftAdapter mLeftAdapter;
    private SyncHorizontalScrollView mHeaderHorizontal;
    private SyncHorizontalScrollView mDataHorizontal;
    private List<String> mListData;
    private int index=0;
    private boolean backEnable = true;//返回键使能
    private boolean motorRunning = false;
    //private Button btnSave2File;
    private Switch swBrush,swDustGenerate,swMotorRunning;
    private EditText etScrewSpeed,etScrewPath,etParameter;
    private TextView realTime,tvDustInfo;
    private Button btnSaveParameter;
    private String[] realTimeString = new String[DustBinModel.INDICATOR_MAX];

    private DustBinModel model;
    private SystemConfig config;

    private Timer dustGenerationTimer;

    private ComparativeDustData dustData;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgMotorRunning:
                    swMotorRunning.setChecked(motorRunning);
                    break;
                case msgUpdateRealTimeResult:
                    String string="";
                    for(int i=0;i<DustBinModel.INDICATOR_MAX;i++){
                        if(realTimeString[i]!=null) {
                            string += realTimeString[i] + "\n";
                        }
                    }
                    realTime.setText(string);
                    break;
                case msgInsertItem:
                    String[] stirngs= new String[DustBinModel.INDICATOR_MAX];
                    stirngs[0] = tools.float2String4(dustData.getReference());
                    stirngs[1] = tools.float2String4(dustData.getTest()[0]);
                    stirngs[2] = tools.float2String4(dustData.getTest()[1]);
                    stirngs[3] = tools.float2String4(dustData.getTest()[2]);
                    stirngs[4] = tools.float2String4(dustData.getTest()[3]);
                    stirngs[5] = tools.float2String4(dustData.getTest()[4]);
                    stirngs[6] = tools.float2String3(dustData.getBackup());
                    ItemFragment mFOne = new ItemFragment(stirngs);
                    FragmentTransaction tx =getFragmentManager().beginTransaction();

                    tx.add(R.id.lv_data,mFOne).commit();
                    index++;
                    mListData.add(String.valueOf(index));

                    break;
                case msgDustGenerateEnd:
                    swDustGenerate.setChecked(false);
                    swBrush.setChecked(false);
                    Toast.makeText(DustBinActivity.this,"发尘结束",Toast.LENGTH_SHORT).show();
                    etScrewPath.setEnabled(true);
                    etScrewSpeed.setEnabled(true);
                    etParameter.setEnabled(true);
                    btnSaveParameter.setEnabled(true);
                    model.onStopDustGenerate();
                    backEnable = true;
                    break;
                default:

                    break;
            }

        }
    };


    private class DustGenerateTimerTask extends TimerTask{

        @Override
        public void run() {
            handler.sendEmptyMessage(msgDustGenerateEnd);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust_bin);
        initView();
        config = new SystemConfig(this);
        model = new DustBinModel(this,config);
        etParameter.setText(String.valueOf(model.getDustParameter()));
        etScrewSpeed.setText(String.valueOf(model.getScrewSpeed()));
        etScrewPath.setText(String.valueOf(model.getScrewPath()));
        /*FragmentManager manager =getFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();
        fragments= new ArrayList<>();
        for (int i = 0; i < mListData.size(); i++) {
            ItemFragment mFOne = new ItemFragment();
            fragments.add(mFOne);
        }
        Log.i("TAG", "fragment.size=="+fragments.size());
        for (int i = 0; i < fragments.size(); i++) {
            tx.add(R.id.lv_data, fragments.get(i));
        }
        tx.commit();*/
    }

    private void initView(){
        realTime = findViewById(R.id.tvDustBinRealtime);
        tvDustInfo = findViewById(R.id.tvDustInfo);
        etParameter = findViewById(R.id.etDustParameter);
        etScrewPath = findViewById(R.id.etScrewPath);
        etScrewSpeed = findViewById(R.id.etScrewSpeed);
        findViewById(R.id.swBrush).setOnClickListener(this);
        findViewById(R.id.swDustGenerate).setOnClickListener(this);
        btnSaveParameter = findViewById(R.id.btnSaveParaMeter);
        btnSaveParameter.setOnClickListener(this);
        swBrush = findViewById(R.id.swBrush);
        swDustGenerate = findViewById(R.id.swDustGenerate);
        swMotorRunning = findViewById(R.id.swMotorRun);
        mLeft = (NoscrollListView) findViewById(R.id.lv_left);
        mDataHorizontal = (SyncHorizontalScrollView) findViewById(R.id.data_horizontal);
        mHeaderHorizontal = (SyncHorizontalScrollView) findViewById(R.id.header_horizontal);

        mDataHorizontal.setScrollView(mHeaderHorizontal);
        mHeaderHorizontal.setScrollView(mDataHorizontal);

        mListData = new ArrayList<>();
       /* mListData.add("1");
        mListData.add("2");
        mListData.add("3");
        mListData.add("4");
        mListData.add("5");
        mListData.add("6");
        mListData.add("7");
        mListData.add("8");
        mListData.add("9");
        mListData.add("10");
        mListData.add("11");
        mListData.add("12");
        mListData.add("13");*/

        mLeftAdapter= new LeftAdapter();
        mLeft.setAdapter(mLeftAdapter);
        findViewById(R.id.btnSaveDustBin).setOnClickListener(this);
        swMotorRunning.setOnClickListener(this);
        //setData();
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
    public void insertItem(ComparativeDustData data) {
        this.dustData = data;
        handler.sendEmptyMessage(msgInsertItem);
    }

    @Override
    public void onDustGenerationStart(long endTime) {
        dustGenerationTimer = new Timer();
        Date when = new Date(endTime);
        dustGenerationTimer.schedule(new DustGenerateTimerTask(),when);
    }

    @Override
    public void onMotorStatus(boolean run) {
        motorRunning = run;
        handler.sendEmptyMessage(msgMotorRunning);
    }

    @Override
    protected void onStop() {
        /*if(isFinishing()) {
            model.stopScan();
        }*/
        model.onStopDustGenerate();
        model.stopScan();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (backEnable) {
            super.onBackPressed();
        }else{
            Toast.makeText(this,"发尘中，请勿退出!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSaveDustBin:
                if(model.export2File()){
                    Toast.makeText(this,"导出成功!路径:"+model.getFileName(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"导出失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSaveParaMeter:
                if(Float.valueOf(etScrewSpeed.getText().toString()) > 18f){
                    Toast.makeText(this,"设置速度超范围", Toast.LENGTH_SHORT).show();
                    etScrewSpeed.setError("最大值 18");
                    break;
                }
                if((Integer.valueOf(etScrewPath.getText().toString()) > 250)||(Integer.valueOf(etScrewPath.getText().toString()) < -250)){
                    Toast.makeText(this,"设置速度超范围", Toast.LENGTH_SHORT).show();
                    etScrewPath.setError("最大行程 250mm");
                    break;
                }

                model.setDustGenerateSetting(Float.valueOf(etScrewSpeed.getText().toString()),
                        Integer.valueOf(etScrewPath.getText().toString()),Float.valueOf(etParameter.getText().toString()));
                tvDustInfo.setText(model.getDustGenerateInfo());
                break;
            case R.id.swBrush:
                model.switchBrush(swBrush.isChecked());
                break;
            case R.id.swMotorRun:
                if(swMotorRunning.isChecked()){

                }else{
                    model.onStopDustGenerate();
                }
                break;
            case R.id.swDustGenerate:
                if(swDustGenerate.isChecked()){
                    if(Float.valueOf(etScrewSpeed.getText().toString()) > 18f){
                        Toast.makeText(this,"设置速度超范围", Toast.LENGTH_SHORT).show();
                        etScrewSpeed.setError("最大值 18");
                        break;
                    }
                    if((Integer.valueOf(etScrewPath.getText().toString()) > 250)||(Integer.valueOf(etScrewPath.getText().toString()) < -250)){
                        Toast.makeText(this,"设置速度超范围", Toast.LENGTH_SHORT).show();
                        etScrewPath.setError("最大行程 250mm");
                        break;
                    }
                    btnSaveParameter.setEnabled(false);
                    etScrewPath.setEnabled(false);
                    etScrewSpeed.setEnabled(false);
                    etParameter.setEnabled(false);
                    backEnable = false;
                    model.startDustGenerate(Float.valueOf(etScrewSpeed.getText().toString()),
                            Integer.valueOf(etScrewPath.getText().toString()),Float.valueOf(etParameter.getText().toString()));
                    tvDustInfo.setText(model.getDustGenerateInfo());

                }else{
                    if(dustGenerationTimer!=null){
                        dustGenerationTimer.cancel();
                        dustGenerationTimer = null;
                    }
                    swBrush.setChecked(false);
                    btnSaveParameter.setEnabled(true);
                    etScrewPath.setEnabled(true);
                    etScrewSpeed.setEnabled(true);
                    etParameter.setEnabled(true);
                    model.onStopDustGenerate();
                    backEnable = true;
                }
                break;
            default:
                break;
        }
    }

    private class LeftAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(DustBinActivity.this).inflate(R.layout.item_left, null);
                holder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //holder.tvLeft.setText("第" + position + "条");
            holder.tvLeft.setText(model.getDustDate(position));
            return convertView;
        }

        class ViewHolder {
            TextView tvLeft;
        }
    }

    /*private void setData() {
        if (data==null) {
            data=new ArrayList<>();
        }
        for (int i = 0; i <mListData.size(); i++) {
            HashMap<String, String> map=new HashMap<String, String>();
            map.put("姓名"+i, i+"姓名");
            map.put("年龄"+i, i+"年龄");
            map.put("体温"+i, i+"体温");
            map.put("脉搏"+i, i+"脉搏");
            map.put("呼吸"+i, i+"呼吸");
            data.add(map);

        }
    }*/
}
