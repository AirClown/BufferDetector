package com.example.yushichao.bufferdetector;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushi on 2018/10/25.
 */

public class AccControl {

    int r=100;
    private int dataNum=3*r+200;

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
        if (file==null){
            file=new MyFile(path,"Acc.txt");
            file.CreateFile();
        }
    }

    public void refreshAcc(float[] values) {
        float acc = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);

        Accs[count] = acc;

        if (file!=null){
            file.WriteIntoFile(acc+"");
        }

        float[] data = new float[Accs.length];
        for (int i = 0, j = count; i < Accs.length; i++, j--) {
            if (j < 0) {
                j = Accs.length - 1;
            }
            data[i] = Accs[j];
        }

        data=Utils.smoothFilter(data,5);

        float[] ave=new float[data.length];
        float[] var=new float[data.length];

        float sum=0;
        for (int i=0;i<data.length;i++){
            if (i<2*r){
                 sum+=data[i];
            }else{
                sum+=data[i];
                sum-=data[i-2*r];

                ave[i-r]=sum/(2*r);
                float v=0;
                for (int j=i-2*r+1;j<=i;j++){
                    v+=(data[j]-ave[i-r])*(data[j]-ave[i-r]);
                }
                var[i-r]=v/(2*r);
            }
        }

        float[] sign=new float[data.length];
        for (int i=r;i<data.length-r;i++){
            if (data[i]>1.5&&data[i]>2*ave[i]&&var[i]*3>ave[i]){
                sign[i]=5;
            }
        }

        if (sign[r*2]>0&&sign[r]==0&&sign[data.length-r-1]==0) {
            List<Point> points = new ArrayList<>();
            for (int i = r; i < data.length - r; i++) {
                if (sign[i] > 0 && data[i] > data[i - 1] && data[i] > data[i + 1]) {
                    points.add(new Point(i, 0));
                }
            }

            if (points.size() > 2) {
                Point[] re = Kmeans(2, points, 20);
                if (re != null) {
                    sign[re[0].x] = 10;
                    sign[re[1].x] = 10;

                    float speed = (float) (3 / (Math.abs(re[0].x - re[1].x) * 0.02) * 3.6);

                    if (speed < 30 && speed > 3) {
                        callback.BufferDetector(speed);
                    }
                }
            }
        }

        callback.Drew(data,sign);

        count=(count+1)%Accs.length;
    }

    //Kmeans算法
    //k:聚类数量
    //points:坐标数据
    //iteration:迭代计算次数
    //返回：聚类中心
    public static Point[] Kmeans(int k, List<Point> points, int iteration){
        if (points.size()<k) return null;

        //聚类中心
        Point[] centre=new Point[k];
        for(int i=0;i<centre.length;i++){
            int index=(int)(Math.random()*points.size());
            centre[i]=new Point(points.get(index).x,points.get(index).y);
        }

        while (--iteration>0){
            //分组下标，每个数据对应聚类中心数组的下标
            int[] index=new int[points.size()];

            //统计每组的数据数量
            int[] count=new int[centre.length];

            //分组
            for (int i=0;i<points.size();i++){
                //最小距离，-1代表没有初值
                float min=-1;

                //寻找最近的聚类中心
                for (int j=0;j<centre.length;j++){
                    float d=(float) Math.sqrt((centre[j].x-points.get(i).x)*(centre[j].x-points.get(i).x)+
                            (centre[j].y-points.get(i).y)*(centre[j].y-points.get(i).y));
                    if(min<0||d<min){
                        min=d;
                        index[i]=j;
                    }
                }

                ++count[index[i]];
            }

            //更新聚类中心
            for (int i=0;i<index.length;i++){
                centre[index[i]].x+=points.get(i).x;
                centre[index[i]].y+=points.get(i).y;
            }

            for(int i=0;i<centre.length;i++){
                if (count[i]==0) continue;
                centre[i].x/=(count[i]+1);
                centre[i].y/=(count[i]+1);
            }
        }

        return centre;
    }
}
