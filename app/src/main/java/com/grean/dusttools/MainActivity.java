package com.grean.dusttools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.grean.dusttools.presenter.AutoCalActivity;
import com.grean.dusttools.presenter.DustBinActivity;
import com.grean.dusttools.presenter.MultiTemperatureTester;
import com.grean.dusttools.presenter.StepMotorTesterActivity;
import com.tools;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String tag="MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnAutoCalTest).setOnClickListener(this);
        findViewById(R.id.btnDustMeterTester).setOnClickListener(this);
        findViewById(R.id.btnMultiTemperature).setOnClickListener(this);
        findViewById(R.id.btnStepMotorTestTools).setOnClickListener(this);

        findViewById(R.id.btnTest).setOnClickListener(this);
        ComManager.SocketComBuilder builder = new ComManager.SocketComBuilder();
        builder.context(this).ip("192.168.1.18").ports(new int[]{50001, 50002, 50003, 50004, 50005});
        ComManager.getInstance().openSocketCom(builder);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnAutoCalTest:
                intent.setClass(this, AutoCalActivity.class);
                startActivity(intent);
                break;
            case R.id.btnDustMeterTester:
                intent.setClass(this, DustBinActivity.class);
                startActivity(intent);
                break;
            case R.id.btnMultiTemperature:
                intent.setClass(this, MultiTemperatureTester.class);
                startActivity(intent);
                break;
            case R.id.btnStepMotorTestTools:
                intent.setClass(this, StepMotorTesterActivity.class);
                startActivity(intent);
                break;
            case R.id.btnTest:

                break;
            default:

                break;
        }
    }
}
