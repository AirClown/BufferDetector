package com.example.yushichao.bufferdetector;

import android.util.Log;

/**
 * Created by yushi on 2018/10/25.
 */

public class AccControl {

    private int dataNum=400;

    private float[] Accs;
    private int count;

    private MyFile file;

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

    public void saveData(String path){
        file=new MyFile(path,"Acc.txt");
        file.CreateFile();
    }

    public void refreshAcc(float[] values) {
        float acc = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);

        if (file!=null){
            file.WriteIntoFile(""+acc);
        }
        Accs[count] = acc;

        float[] data = new float[Accs.length];
        for (int i = 0, j = count; i < Accs.length; i++, j--) {
            if (j < 0) {
                j += Accs.length;
            }
            data[i] = Accs[j];
        }

        float[] diff = Utils.abs(Utils.diff(data));

        int r=10;
        float[] ave=new float[diff.length];

        float sum=0;
        for(int i=0;i<diff.length;i++){
            if(i<2*r){
                sum+=diff[i];
            }else{
                sum+=diff[i];
                sum-=diff[i-r*2];

                ave[i-r]=sum/(2*r);
            }
        }

        float[] sign=new float[diff.length];

        for(int i=0;i<diff.length;i++){
            if (ave[i]*2<diff[i]&&diff[i]>0.5){
                diff[i]*=diff[i];
            }else{
                diff[i]=0;
            }
        }

        diff=Utils.smoothFilter(diff,50);
        for (int i=r;i<diff.length-r;i++){
            if (diff[i]>ave[i]&&diff[i]>0.2){
                sign[i]=1;
            }
        }

        callback.Drew(diff, sign);

        count=(count+1)%Accs.length;
    }

    public void refreshAcc2(float[] values){
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

        diff=Utils.smoothFilter(data,50);
        float max=0;
        for (int i=0;i<diff.length;i++){
            if(diff[i]>max){
                max=diff[i];
            }
        }

        max/=10;
        float[] sign=new float[diff.length];
        for (int i=0;i<diff.length;i++){
            if(diff[i]>0.1&&diff[i]>max){
                sign[i]=1;
            }
        }

        if (sign[100]==0&&sign[101]==1){
            int num=0;
            for(int i=101;i<sign.length;i++){
                if(sign[i]==1){
                    ++num;
                }else{
                    break;
                }
            }

            num-=50;
            if (num>20&&num<120){
                float speed=(120-num)*0.27f+3;
                callback.BufferDetector(speed);
            }
        }

        callback.Drew(diff,sign);

        count=(count+1)%Accs.length;
    }
}
