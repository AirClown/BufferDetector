package com.example.yushichao.bufferdetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushi on 2018/9/19.
 */

public class Utils {
    public static int getPeakNum(float[] data){
        int peak=0;
        data=smoothFilter(data,3);
        int[] PeakAndTrough=new int[data.length];

        //需要三个不同的值进行比较，取lo,mid，hi分别为三值
        for (int lo=0,mid=1,hi=2;hi<data.length;hi++){
            //先令data[lo]不等于data[mid]
            while (mid<data.length&&data[mid]==data[lo]){
                mid++;
            }

            hi=mid+1;

            //令data[hi]不等于data[mid]
            while (hi<data.length&&data[hi]==data[mid]){
                hi++;
            }

            if (hi>=data.length){
                break;
            }

            //检测是否为峰值
            if (data[mid]>data[lo]&&data[mid]>data[hi]){
                PeakAndTrough[mid]=1;       //1代表波峰
            }else if(data[mid]<data[lo]&&data[mid]<data[hi]){
                PeakAndTrough[mid]=-1;      //-1代表波谷
            }

            lo=mid;
            mid=hi;
        }

        //计算均值
        float ave=0;
        for (int i=0;i<data.length;i++){
            ave+=data[i];
        }
        ave/=data.length;

        //排除大于均值的波谷和小于均值的波峰
        for (int i=0;i<PeakAndTrough.length;i++){
            if ((PeakAndTrough[i]>0&&data[i]<ave)||(PeakAndTrough[i]<0&&data[i]>ave)){
                PeakAndTrough[i]=0;
            }
        }

        //统计波峰数量
        for (int i=0;i<PeakAndTrough.length;){
            while (i<PeakAndTrough.length&&PeakAndTrough[i]<=0){
                i++;
            }

            if (i>=PeakAndTrough.length){
                break;
            }

            peak++;

            while (i<PeakAndTrough.length&&PeakAndTrough[i]>=0){
                i++;
            }
        }

        return peak;
    }

    public static int[] getPeakNum2(float[] data){
        data=smoothFilter(data,5
        );
        int[] PeakAndTrough=new int[data.length];

        //需要三个不同的值进行比较，取lo,mid，hi分别为三值
        for (int lo=0,mid=1,hi=2;hi<data.length;hi++){
            //先令data[lo]不等于data[mid]
            while (mid<data.length&&data[mid]==data[lo]){
                mid++;
            }

            hi=mid+1;

            //令data[hi]不等于data[mid]
            while (hi<data.length&&data[hi]==data[mid]){
                hi++;
            }

            if (hi>=data.length){
                break;
            }

            //检测是否为峰值
            if (data[mid]>data[lo]&&data[mid]>data[hi]){
                PeakAndTrough[mid]=1;       //1代表波峰
            }else if(data[mid]<data[lo]&&data[mid]<data[hi]){
                PeakAndTrough[mid]=-1;      //-1代表波谷
            }

            lo=mid;
            mid=hi;
        }

        //计算均值
        float ave=0;
        for (int i=0;i<data.length;i++){
            ave+=data[i];
        }
        ave/=data.length;

        //排除大于均值的波谷和小于均值的波峰
        for (int i=0;i<PeakAndTrough.length;i++){
            if ((PeakAndTrough[i]>0&&data[i]<ave)||(PeakAndTrough[i]<0&&data[i]>ave)){
                PeakAndTrough[i]=0;
            }
        }

        //排除相邻的峰谷值
        for (int i=0;i<PeakAndTrough.length;){
            int index=i++;

            while(i<PeakAndTrough.length&&PeakAndTrough[i]<=0){
                if (PeakAndTrough[i]==-1) {
                    if(data[i]<data[index]) {
                        PeakAndTrough[index]=0;
                        index=i;
                    }else{
                        PeakAndTrough[i]=0;
                    }
                }
                i++;
            }

            if (i>=PeakAndTrough.length){
                break;
            }

            index=i++;

            while(i<PeakAndTrough.length&&PeakAndTrough[i]>=0){
                if (PeakAndTrough[i]==1) {
                    if(data[i]>data[index]) {
                        PeakAndTrough[index]=0;
                        index=i;
                    }else{
                        PeakAndTrough[i]=0;
                    }
                }
                i++;
            }
        }

        return PeakAndTrough;
    }

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


//    public static float[] smoothFilter(float[] data,int strength){
//        if (strength>data.length){
//            strength=data.length;
//        }
//        float[] back=new float[data.length];
//
//        back[0]=data[0];
//        back[back.length-1]=data[back.length-1];
//
//        for (int i=1,j=back.length-2;i<=strength/2;i++,j--){
//            back[i]=back[i-1]+data[i*2]+data[i*2-1];
//            back[j]=back[j+1]+data[back.length-i*2-1]+data[back.length-i*2];
//        }
//
//        for (int i=strength/2+1;i<back.length-strength/2-1;i++){
//            back[i]=back[i-1]-data[i-strength/2-1]+data[i+strength/2];
//        }
//
//        for (int i=1;i<=strength/2;i++){
//            back[i]/=(2*i+1);
//            back[back.length-1-i]/=(2*i+1);
//        }
//
//        for (int i=strength/2+1;i<back.length-strength/2-1;i++){
//            back[i]/=strength;
//        }
//
//        return back;
//    }

    //    public static int getPeakNum0(float[] data){
//        int PEAK=1;
//        int TROUGH=-1;
//
//        float[] data1=smoothFilter(data,3);
//        float[] data2=new float[data.length];
//
//        for (int lo=0,mid=1,hi=2;hi<data1.length;){
//            while(mid<data1.length&&data1[mid]==data1[lo]){
//                mid++;
//            }
//
//            hi=mid+1;
//
//            while(hi<data1.length&&data1[hi]==data1[mid]){
//                hi++;
//            }
//
//            if (hi>=data1.length){
//                break;
//            }
//
//            if (data1[mid]>data1[lo]&&data1[mid]>data1[hi]){
//                data2[mid]=PEAK;
//            }else if (data1[mid]<data1[lo]&&data1[mid]<data1[hi]){
//                data2[mid]=TROUGH;
//            }
//
//            lo=mid;
//            mid=hi;
//            ++hi;
//        }
//
//        return 0;
//    }
}
