package com.grean.dusttools.presenter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grean.dusttools.R;
import com.grean.dusttools.devices.ComparativeDustData;
import com.grean.dusttools.model.DustBinModel;
import com.tools;
import com.view.ItemFragment;
import com.view.NoscrollListView;
import com.view.SyncHorizontalScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by weifeng on 2018/2/9.
 */

public class DustBinActivity extends Activity implements DustBinScanResultListener,View.OnClickListener{
    private static final String tag = "DustBinActivity";
    private static final int msgUpdateRealTimeResult = 1,msgInsertItem = 2;

    private NoscrollListView mLeft;
    private LeftAdapter mLeftAdapter;
    private SyncHorizontalScrollView mHeaderHorizontal;
    private SyncHorizontalScrollView mDataHorizontal;
    private List<String> mListData;
    private int index=0;
    //private Button btnSave2File;

    private ComparativeDustData dustData;

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
                case msgInsertItem:
                    String[] stirngs= new String[DustBinModel.INDICATOR_MAX];
                    stirngs[0] = tools.float2String4(dustData.getReference());
                    stirngs[1] = tools.float2String4(dustData.getTest()[0]);
                    stirngs[2] = tools.float2String4(dustData.getTest()[1]);
                    stirngs[3] = tools.float2String4(dustData.getTest()[2]);
                    stirngs[4] = tools.float2String4(dustData.getTest()[3]);
                    stirngs[5] = tools.float2String4(dustData.getTest()[4]);
                    ItemFragment mFOne = new ItemFragment(stirngs);
                    FragmentTransaction tx =getFragmentManager().beginTransaction();

                    tx.add(R.id.lv_data,mFOne).commit();
                    index++;
                    mListData.add(String.valueOf(index));

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
    protected void onDestroy() {
        model.stopScan();
        super.onDestroy();
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
        }
    }

    class LeftAdapter extends BaseAdapter {

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
