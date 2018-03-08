package com.grean.dusttools.presenter;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grean.dusttools.R;

/**
 * Created by weifeng on 2018/3/8.
 */

public class ProcessDialogFragment extends DialogFragment implements View.OnClickListener{
    private TextView tvContent;
    private ProgressBar pb;
    private String content;
    private int process;
    public interface HandleListener{
        void onFirstButton();
        void onSecondButton();
    }

    private HandleListener listener;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    tvContent.setText(content);
                    pb.setProgress(process);
                    break;
                default:
                    break;
            }
        }
    };

    public void setProcess(String content,int process){
        this.content = content;
        this.process = process;
        handler.sendEmptyMessage(1);
    }

    public void setOnHandleListener(HandleListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_process, container);
        tvContent = view.findViewById(R.id.tvProcessContent);
        pb = view.findViewById(R.id.pbProcessDialog);
        view.findViewById(R.id.btnStopMotor).setOnClickListener(this);
        view.findViewById(R.id.btnScramMotor).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStopMotor:
                listener.onFirstButton();
                break;
            case R.id.btnScramMotor:
                listener.onSecondButton();
                break;
            default:
                break;
        }
    }
}
