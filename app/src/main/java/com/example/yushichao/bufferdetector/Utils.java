package com.example.yushichao.bufferdetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushi on 2018/9/19.
 */

public class Utils {
    public static float[] smoothFilter(float[] data,int strength){
        if (strength>data.length/2){
            strength=data.length/2;
        }

        float[] back=new float[data.length];

        back[0]=data[0];
        for (int i=1;i<data.length;i++){
            if (i<strength){
                back[i]=back[i-1]+data[i];
            }else{
                back[i]=back[i-1]-data[i-strength]+data[i];
            }
        }

        for (int i=1;i<back.length;i++){
            if (i<strength){
                back[i]/=(i+1);
            }else{
                back[i]/=strength;
            }
        }

        return back;
    }

    public static float[] diff(float[] values){
        float[] data=new float[values.length-1];

        for(int i=0;i<data.length;i++){
            data[i]=values[i]-values[i+1];
        }

        return data;
    }

    public static float[] abs(float[] values){
        for (int i=0;i<values.length;++i){
            values[i]=Math.abs(values[i]);
        }
        return values;
    }
}
