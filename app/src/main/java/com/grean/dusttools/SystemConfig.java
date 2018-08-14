package com.grean.dusttools;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by weifeng on 2018/8/14.
 */

public class SystemConfig {
    private Context context;

    public SystemConfig(Context context){
        this.context = context;
    }


    public void saveConfig(String key,float data){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key,data);
        editor.apply();
    }

    public void saveConfig(String key,long data){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key,data);
        editor.apply();
    }

    public void saveConfig(String key,int data){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key,data);
        editor.apply();
    }

    public void saveConfig(String key,String data){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,data);
        editor.apply();
    }

    public void saveConfig(String key,boolean data){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key,data);
        editor.apply();
    }

    public boolean getConfigBoolean(String key){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        return sp.getBoolean(key,false);
    }

    public float getConfigFloat(String key){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        return sp.getFloat(key,0f);
    }

    public int getConfigInt(String key){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        return sp.getInt(key,0);
    }

    public long getConfigLong(String key){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        return sp.getLong(key,0);
    }

    public String getConfigString(String key){
        SharedPreferences sp = context.getSharedPreferences("config",MODE_PRIVATE);
        return sp.getString(key," ");
    }

}
