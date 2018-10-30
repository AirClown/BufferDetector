package com.example.yushichao.bufferdetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorControl sensorControl;
    private SensorControl.SensorCallback sensorCallback=new SensorControl.SensorCallback() {
        @Override
        public void refreshAcc(float[] accs) {
            tv.setText(accs[0]+"");
            if(accControl!=null){
                accControl.refreshAcc(accs);
            }
        }
    };

    private AccControl accControl;
    private AccControl.AccCallback accCallback=new AccControl.AccCallback() {
        @Override
        public void BufferDetector(float speed) {
            mv.setTitle("速度:"+speed);
        }

        @Override
        public void Drew(float[] data1, float[] data2) {
            if (mv==null) return;
            mv.setRange(data1.length);
            mv.setData(data1,data2);
        }
    };

    private MyView mv;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mv=(MyView)findViewById(R.id.myview);
        tv=(TextView)findViewById(R.id.textView);

        sensorControl=new SensorControl((SensorManager) getSystemService(Context.SENSOR_SERVICE),sensorCallback);
        if(!sensorControl.registerSensor(Sensor.TYPE_LINEAR_ACCELERATION,SensorManager.SENSOR_DELAY_GAME)){
            tv.setText("fail");
        }

        accControl=new AccControl(accCallback);
        //accControl.saveData(this.getExternalFilesDir(null)+"");
    }
}
