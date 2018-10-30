package com.example.yushichao.bufferdetector;

import android.util.Log;

/**
 * Created by yushi on 2018/10/25.
 */

public class AccControl {

    private int dataNum=800;

    private float[] Accs;
    private int count;

    public interface AccCallback{
        void BufferDetector(float speed);
        void Drew(float[] data1,float[] data2);
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

        float[] diff=Utils.abs(Utils.diff(data));

        int r=100;
        float[] ave=new float[diff.length];
        float sum=0;

        for (int i=0;i<diff.length;i++){
            if (i<r*2){
                sum+=diff[i];
            }else{
                sum+=diff[i];
                sum-=diff[i-2*r];

                ave[i-r]=sum/(r*2);
            }
        }

        for (int i=r;i<diff.length-r;i++){
            if (diff[i]<0.5||diff[i]<ave[i]*2){
                diff[i]=0;
            }else{
                diff[i]*=diff[i];
            }
        }

        diff=Utils.smoothFilter(diff,50);

        float[] sign=new float[diff.length];
        for (int i=r;i<sign.length-r;i++){
            if (diff[i]>ave[i]&&diff[i]>0.2){
                sign[i]=1;
            }
        }

        callback.Drew(diff,sign);

        count=(count+1)%Accs.length;
    }
}
