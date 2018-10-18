package com.grean.dusttools.presenter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.grean.dusttools.R;
import com.grean.dusttools.SystemConfig;
import com.grean.dusttools.devices.DustMainBoardCalInfoFormat;
import com.grean.dusttools.model.AutoCalModel;
import com.tools;
import com.view.ItemFragment;
import com.view.NoscrollListView;
import com.view.SyncHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2018/2/9.
 */

public class AutoCalActivity extends Activity implements OnAutoCalListener,View.OnClickListener{
    private static final String tag = "AutoCalActivity";
    private AutoCalModel model;

    private boolean backEnable = true;

    private NoscrollListView mLeft;
    private LeftAdapter mLeftAdapter;
    private SyncHorizontalScrollView mHeaderHorizontal;
    private SyncHorizontalScrollView mDataHorizontal;
    private List<String> mListData;
    private int index=0;

    private WaitingFragment waitingFragment;
    private TextView tvMotor1State;
    private EditText etMotor1Steps,etMotor1Time;
    private Switch swAutoTest;
    private String[] stateStrings = new String[AutoCalModel.MAX];
    private DustMainBoardCalInfoFormat format;
    private static final int MSG_REAL_TIME=1,MSG_INSERT_ITEM =2,MSG_END=3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_REAL_TIME:
                    String string = stateStrings[0];
                    for(int i=1;i<AutoCalModel.MAX;i++){
                        string+=" | "+stateStrings[i];
                    }
                    tvMotor1State.setText(string);
                    break;
                case MSG_INSERT_ITEM:
                    String[] strings= new String[7];
                    for(int i=0;i<AutoCalModel.MAX;i++) {
                        strings[i] = format.getResult(i);
                    }
                    /*for(int i=0;i<6;i++) {
                        strings[i] = tools.float2String3(format.getValues()[i]);
                    }*/
                    strings[5] = "-";
                    strings[6] = "-";
                    ItemFragment mFOne = new ItemFragment(strings);
                    FragmentTransaction tx =getFragmentManager().beginTransaction();
                    tx.add(R.id.lv_data,mFOne).commit();
                    index++;
                    mListData.add(String.valueOf(index));
                    break;
                case MSG_END:
                    if(waitingFragment!=null){
                        waitingFragment.dismiss();
                    }
                    etMotor1Steps.setEnabled(true);
                    etMotor1Time.setEnabled(true);
                    Toast.makeText(AutoCalActivity.this,"停止测试!",Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_cal);

        mLeft = (NoscrollListView) findViewById(R.id.lv_left);
        mDataHorizontal = (SyncHorizontalScrollView) findViewById(R.id.data_horizontal);
        mHeaderHorizontal = (SyncHorizontalScrollView) findViewById(R.id.header_horizontal);


        mDataHorizontal.setScrollView(mHeaderHorizontal);
        mHeaderHorizontal.setScrollView(mDataHorizontal);

        mListData = new ArrayList<>();
        mLeftAdapter= new LeftAdapter();
        mLeft.setAdapter(mLeftAdapter);

        tvMotor1State = findViewById(R.id.tvMainBoard1RealTime);
        etMotor1Steps = findViewById(R.id.etMotor1Steps);
        etMotor1Time = findViewById(R.id.etMotor1Time);
        findViewById(R.id.btnSaveAutoCalToFile).setOnClickListener(this);
        swAutoTest = findViewById(R.id.swCountinueTest);
        swAutoTest.setOnClickListener(this);

        model = new AutoCalModel(this,new SystemConfig(this));
        etMotor1Steps.setText(model.getSteps());
        etMotor1Time.setText(model.getTime());
    }

    @Override
    public void onBackPressed() {
        Log.d(tag,"拦截返回键");
        //拦截返回键
        if(backEnable) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRealTimeState(String[] state) {
       // Log.d(tag,"onRealTimeState");
        for(int i=0;i<AutoCalModel.MAX;i++){
           // Log.d(tag,"onRealTimeState"+String.valueOf(i));
            stateStrings[i] = state[i];
        }
        //Log.d(tag,"onRealTimeState end");
        handler.sendEmptyMessage(MSG_REAL_TIME);
    }

    @Override
    public void onInsertItem(DustMainBoardCalInfoFormat format) {
        this.format = format;
        handler.sendEmptyMessage(MSG_INSERT_ITEM);
    }

    @Override
    public void endCal() {
        backEnable = true;
        handler.sendEmptyMessage(MSG_END);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.swCountinueTest:
                if(swAutoTest.isChecked()){//启动
                    etMotor1Time.setEnabled(false);
                    etMotor1Steps.setEnabled(false);
                    backEnable = false;
                    model.startAutoCal(etMotor1Steps.getText().toString(),etMotor1Time.getText().toString());
                }else{//停止
                    waitingFragment = new WaitingFragment();
                    waitingFragment.show(getFragmentManager(),"WaitingFragment");
                    model.stopRun();
                }
                break;
            case R.id.btnSaveAutoCalToFile:
                if(model.saveDataToFile()){
                    Toast.makeText(this,"导出成功!文件路径为:"+model.getFileName(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"导出失败",Toast.LENGTH_SHORT).show();
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
                convertView = LayoutInflater.from(AutoCalActivity.this).inflate(R.layout.item_left, null);
                holder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvLeft.setText(model.getDate(position));
            //holder.tvLeft.setText("123");
            return convertView;
        }

        class ViewHolder {
            TextView tvLeft;
        }
    }
}
