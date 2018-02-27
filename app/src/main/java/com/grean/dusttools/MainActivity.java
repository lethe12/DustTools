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
    private double [] data= {17.8,
            17.9,
            18.0,
            18.2,
            18.2,
            18.3,
            18.4,
            18.4,
            18.4,
            18.4,
            18.5,
            18.5,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.7,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.6,
            18.5,
            18.5,
            18.5,
            18.6,
            18.6,
            18.6,
            18.6,
            18.9,
            19.3,
            19.7,
            19.8,
            19.9,
            19.9,
            20.9,
            20.0,
            20.1,
            20.1,
            20.2,
            20.0,
            20.4,
            20.5,
            20.5,
            20.2,
            19.9,
            19.9,
            20.1,
            20.1,
            20.2,
            20.1,
            19.9,
            19.8,
            20.0,
            20.3,
            20.3,
            20.3,
            20.2,
            19.8,
            19.9,
            20.1,
            20.2,
            20.1,
            19.8,
            19.7,
            19.8,
            20.0,
            20.1,
            20.1,
            20.0,
            19.7,
            19.5,
            19.8,
            20.0,
            20.0,
            19.9,
            19.8,
            20.0,
            20.1,
            20.1,
            20.1,
            20.0,
            20.0,
            20.2,
            20.4,
            20.4,
            20.3,
            20.2,
            20.1,
            20.1,
            20.2,
            20.3,
            20.2,
            20.0,
            19.9,
            20.0,
            20.0,
            20.1,
            20.2,
            20.1,
            20.1,
            20.4,
            20.6,
            20.7,
            20.6,
            20.5,
            20.6,
            20.8,
            21.0,
            21.1,
            21.2,
            21.2,
            21.1,
            21.1,
            21.2,
            21.2,
            21.1,
            21.0,
            20.8,
            21.0,
            21.1,
            21.1,
            21.1,
            19.9,
            19.8,
            21.0,
            21.1,
            21.2,
            21.2,
            21.3,
            21.3,
            21.3,
            21.2,
            21.2,
            21.1,
            21.0,
            20.9,
            20.9,
            21.1,
            21.2,
            21.2,
            21.2,
            21.2,
            21.2,
            21.4,
            21.4,
            21.4,
            21.5,
            21.4,
            21.5,
            21.6,
            21.6,
            21.5,
            21.4,
            21.3,
            21.4,
            21.5,
            21.5,
            21.4,
            21.3,
            21.2,
            21.3,
            21.5,
            21.6,
            21.6,
            21.5,
            21.3,
            21.5,
            21.7,
            21.8,
            21.9,
            21.8,
            21.6,
            21.5,
            21.5,
            21.4,
            21.3,
            21.2,
            21.1,
            21.1,
            21.2,
            21.3,
            21.3,
            21.2,
            21.2,
            21.3,
            21.5,
            21.7,
            21.7,
            21.7,
    };

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
        String string = "";
        for(int i=0;i<data.length;i++){
            string += tools.bytesToHexString(tools.float2byteBack((float) data[i]),4)+",";

        }
        Log.d(tag,string);
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
