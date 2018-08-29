package com.grean.dusttools.presenter;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grean.dusttools.R;
import com.grean.dusttools.devices.OnStepMotorDriverSettingListener;
import com.grean.dusttools.devices.StepMotorDriverSettingFormat;
import com.grean.dusttools.model.StepMotorTestModel;

/**
 * Created by weifeng on 2018/2/9.
 */

public class StepMotorTesterActivity extends Activity implements View.OnClickListener, OnStepMotorDriverSettingListener,ProcessDialogFragment.HandleListener{
    private static final String tag = "StepMotorTesterActivity";
    private EditText etStarting,etMax,etPlus;
    private TextView tvRemandTime;
    private StepMotorTestModel model;
    private StepMotorDriverSettingFormat format;
    ProcessDialogFragment dialogFragment;
    private static final int MSG_SHOW_SETTING=1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SHOW_SETTING:
                    etStarting.setText(String.valueOf(format.getStartingSpeed()));
                    etMax.setText(String.valueOf(format.getMaxSpeed()));
                    etPlus.setText(String.valueOf(format.getPlus()));
                    tvRemandTime.setText("运行一次预计需要"+String.valueOf(format.getTime())+"S");
                    break;
                default:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_motor_tester);
        etMax = findViewById(R.id.etMaxSpeed);
        etPlus = findViewById(R.id.etPlus);
        etStarting = findViewById(R.id.etStepMotorStartingSpeed);
        tvRemandTime = findViewById(R.id.tvRemaindTime);
        findViewById(R.id.btnStepMotorSaveSetting).setOnClickListener(this);
        findViewById(R.id.btnStepMotorMove).setOnClickListener(this);
        findViewById(R.id.btnMotorBackward).setOnClickListener(this);
        findViewById(R.id.btnMotorForward).setOnClickListener(this);
        model = new StepMotorTestModel(this);
        model.getStepMotorSetting();

    }

    @Override
    public void onResult(StepMotorDriverSettingFormat format) {
        this.format = format;
        handler.sendEmptyMessage(MSG_SHOW_SETTING);
    }

    @Override
    public void onProcess(String content, int process) {
        if(process < 100) {
            dialogFragment.setProcess(content, process);
        }else{
            dialogFragment.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStepMotorSaveSetting:
                model.setStepMotorSetting(Integer.valueOf(etStarting.getText().toString()),
                        Integer.valueOf(etMax.getText().toString()),Integer.valueOf(etPlus.getText().toString()));
                break;
            case R.id.btnStepMotorMove:
                model.setStepMotorSetting(Integer.valueOf(etStarting.getText().toString()),
                        Integer.valueOf(etMax.getText().toString()),Integer.valueOf(etPlus.getText().toString()));
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.setOnHandleListener(this);
                dialogFragment.show(getFragmentManager(),"ProcessDialogFragment");
                model.setStepMotorMove((int) format.getTime()+1);
                break;
            case R.id.btnMotorBackward:
                model.setStepMotorSetting(5,20,-1600);
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.setOnHandleListener(this);
                dialogFragment.show(getFragmentManager(),"ProcessDialogFragment");
                model.setStepMotorMove((int) format.getTime()+1);
                break;
            case R.id.btnMotorForward:
                model.setStepMotorSetting(5,20,1600);
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.setOnHandleListener(this);
                dialogFragment.show(getFragmentManager(),"ProcessDialogFragment");
                model.setStepMotorMove((int) format.getTime()+1);
                break;
            default:

                break;
        }
    }

    @Override
    public void onFirstButton() {
        model.stopRun();
        model.setStepMotorScram();
        dialogFragment.dismiss();
    }

    @Override
    public void onSecondButton() {
        model.stopRun();
        model.setStepMotorStop();
        dialogFragment.dismiss();
    }
}
