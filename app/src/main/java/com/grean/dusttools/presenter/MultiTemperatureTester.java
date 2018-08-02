package com.grean.dusttools.presenter;

import android.app.Activity;
import android.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.grean.dusttools.R;
import com.grean.dusttools.devices.MultiTemperatureDataFormat;
import com.grean.dusttools.devices.MultiTemperatureTransducer;
import com.grean.dusttools.devices.OnReadMultiTemperatureListener;
import com.grean.dusttools.model.DustBinModel;
import com.grean.dusttools.model.MultiTemperatureModel;
import com.tools;
import com.view.ItemFragment;
import com.view.NoscrollListView;
import com.view.SyncHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2018/2/9.
 */

public class MultiTemperatureTester extends Activity implements OnMultiTemperatureTestListener,View.OnClickListener{
    private static final String tag = "MultiTemperatureTester";
    private TextView tvRealTime;
    private MultiTemperatureModel model;
    private String realTimeString;

    private NoscrollListView mLeft;
    private LeftAdapter mLeftAdapter;
    private SyncHorizontalScrollView mHeaderHorizontal;
    private SyncHorizontalScrollView mDataHorizontal;
    private List<String> mListData;
    private int index=0;
    private MultiTemperatureDataFormat format;

    private static final int MSG_REAL_TIME=1,MSG_INSERT_ITEM=2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_REAL_TIME:
                    tvRealTime.setText(realTimeString);
                    break;
                case MSG_INSERT_ITEM:
                    String[] strings= new String[7];
                    for(int i=0;i<6;i++) {
                        strings[i] = tools.float2String3(format.getValues()[i]);
                    }
                    strings[6] = "-";
                    ItemFragment mFOne = new ItemFragment(strings);
                    FragmentTransaction tx =getFragmentManager().beginTransaction();
                    tx.add(R.id.lv_data,mFOne).commit();
                    index++;
                    mListData.add(String.valueOf(index));
                   // Log.d(tag,"show");
                    break;
                default:

                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_temperature_tester);
        tvRealTime = findViewById(R.id.tvMultiTempRealtimeData);

        mLeft = (NoscrollListView) findViewById(R.id.lv_left);
        mDataHorizontal = (SyncHorizontalScrollView) findViewById(R.id.data_horizontal);
        mHeaderHorizontal = (SyncHorizontalScrollView) findViewById(R.id.header_horizontal);
        findViewById(R.id.btnSaveMultiTempToFile).setOnClickListener(this);

        mDataHorizontal.setScrollView(mHeaderHorizontal);
        mHeaderHorizontal.setScrollView(mDataHorizontal);

        mListData = new ArrayList<>();
        mLeftAdapter= new LeftAdapter();
        mLeft.setAdapter(mLeftAdapter);

        model = new MultiTemperatureModel(this);
    }

    @Override
    protected void onStop() {
        model.stopRun();
        super.onStop();
    }

    @Override
    public void onRealTimeData(float[] values) {
        realTimeString="";
        for (int i=0;i<MultiTemperatureTransducer.MAX_CHANNEL;i++){
            realTimeString += "Ch"+String.valueOf(i)+":"+String.valueOf(values[i])+"; ";
        }
        handler.sendEmptyMessage(MSG_REAL_TIME);
    }

    @Override
    public void onInsertItem(MultiTemperatureDataFormat format) {
        this.format = format;
        handler.sendEmptyMessage(MSG_INSERT_ITEM);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSaveMultiTempToFile:
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
                convertView = LayoutInflater.from(MultiTemperatureTester.this).inflate(R.layout.item_left, null);
                holder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvLeft.setText(model.getDateString(position));
            //holder.tvLeft.setText("123");
            return convertView;
        }

        class ViewHolder {
            TextView tvLeft;
        }
    }
}
