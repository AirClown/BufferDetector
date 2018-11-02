package com.example.yushichao.bufferdetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by yushi on 2018/10/25.
 */

public class SensorControl implements SensorEventListener {

    private SensorManager manager;

    public interface SensorCallback{
        void refreshAcc(float[] accs);
    }

    private SensorCallback callback;

    public SensorControl(SensorManager manager,SensorCallback callback){
        this.manager=manager;
        this.callback=callback;
    }

    public boolean registerSensor(int type,int speed){
        if (manager==null) return false;

        Sensor sensor=manager.getDefaultSensor(type);
        manager.registerListener(this,sensor,speed);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (callback==null) return;
        switch (event.sensor.getType()){
            case Sensor.TYPE_LINEAR_ACCELERATION:
                callback.refreshAcc(event.values);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
