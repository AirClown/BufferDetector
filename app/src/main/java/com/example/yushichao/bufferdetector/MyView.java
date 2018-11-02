package com.example.yushichao.bufferdetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class MyView extends View{

    private int Range;

    private int count;
    private float[] data1;
    private float[] data2;

    private String title="";

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRange(int num){
        if(Range>0){
            return;
        }
        Range=num;

        count=0;
        data1=new float[Range];
        data2=new float[Range];
    }

    public void setData(float[] data1,float[] data2){
        this.data1=data1;
        this.data2=data2;
        invalidate();
    }

    public void setTitle(String title){
        this.title=title;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(Range>0) {
            int Width=canvas.getWidth();
            int Height=canvas.getHeight();

            Paint paint1 = new Paint();
            paint1.setColor(Color.BLUE );
            paint1.setStrokeWidth(10);
            paint1.setTextSize(100);
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setAlpha(100);

            canvas.drawLine(Width/2,0,Width/2,Height,paint1);
            canvas.drawLine(0,0,0,Height,paint1);
            canvas.drawText(title,Width/2,150,paint1);

            paint1.setStrokeWidth(10);

            float ix=(float)(Width-Width/20)/10;
            float iy=(float)Height/Range;

            Path path=new Path();
            path.moveTo(data1[0]*ix,0);
            for(int i=1;i<Range;i++){
                if (data1[i]>=0) {
                    path.lineTo(data1[i] * ix, iy * i);
                }
            }
            paint1.setColor(Color.GRAY);
            canvas.drawPath(path,paint1);

            Path path2=new Path();
            path2.moveTo(data2[0]*ix,0);
            for(int i=1;i<Range;i++){
                if (data2[i]>=0) {
                    path2.lineTo(data2[i] * ix, iy * i);
                }
            }
            paint1.setColor(Color.RED);
            canvas.drawPath(path2,paint1);
        }
    }
}
