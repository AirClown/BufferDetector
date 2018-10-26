package com.example.yushichao.bufferdetector;

import android.util.Log;

/**
 * Created by yushi on 2018/10/25.
 */

public class AccControl {


    private float[] Accs;
    private int count;
    private static final int smooth=60;

    private int dataNum=2*smooth+200;

    public interface AccCallback{
        void BufferDetector(float speed);
        void Drew(float[] data1,int[] data2);
    }
    private AccCallback callback;

    public AccControl(AccCallback callback){
        this.callback=callback;

        Accs=new float[dataNum];
        count=0;
    }

    public void refreshAcc(float[] values){
        float acc=(float)Math.sqrt(values[0]*values[0]+values[1]*values[1]+values[2]*values[2]);

        Log.e("Acc",""+acc);
        Accs[count]=acc;

        float[] data=new float[Accs.length];
        for(int i=0,j=count;i<Accs.length;i++,j--){
            if(j<0){
                j=Accs.length-1;
            }
            data[i]=Accs[j];
        }

        float[] diff=Utils.diff(data);

        for(int i=0;i<diff.length;i++){
            diff[i]*=diff[i];
        }

        diff=Utils.smoothFilter(data,smooth);
        float max=0;
        for (int i=smooth;i<diff.length;i++){
            if(diff[i]>max){
                max=diff[i];
            }
        }

        max/=5;
        int[] sign=new int[diff.length];
        for (int i=0;i<diff.length;i++){
            if(max>0.2&&diff[i]>max){
                sign[i]=1;
            }
        }
        if (sign[smooth]==0&&sign[smooth+1]==1){
            int num=0;
            for(int i=smooth+1;i<sign.length;i++){
                if(sign[i]==1){
                    ++num;
                }else{
                    break;
                }
            }

            num-=(smooth+1);
            if (num>20&&num<120){
                float speed=(120-num)*0.27f+3;
                callback.BufferDetector(speed);
            }
        }

        callback.Drew(diff,sign);

        count=(count+1)%Accs.length;
    }
}
