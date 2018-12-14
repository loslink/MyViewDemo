package com.loslink.myview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

class BombView extends View {

    private Paint circlePaint,baselinePaint;
    private float canvasWidth,canvasHeight;
    private int baseRadus=300;
    private int MAX_COUNT=8;
    private int[] radius=new int[MAX_COUNT];
    double startRadius=10,endRadius=30;
    double startValue=-baseRadus,endValue=baseRadus;
    private List<Point> pointList=new ArrayList<>();

    public BombView(Context context) {
        super(context);
        init();
    }

    public BombView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BombView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BombView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initData();
        startAnimation();
        return super.onTouchEvent(event);
    }

    public void init() {

        BlurMaskFilter maskFilter = new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL);
        baselinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baselinePaint.setColor(Color.RED);
        baselinePaint.setStyle(Paint.Style.STROKE);
        baselinePaint.setStrokeWidth(2);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#99ffffff"));
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setMaskFilter(maskFilter);

        initData();

    }

    public void startAnimation(){

        final ValueAnimator animator1=ValueAnimator.ofFloat(0,1);
        animator1.setDuration(3000);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.setRepeatCount(0);
        animator1.setRepeatMode(ValueAnimator.RESTART);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int step = 20;
                for(Point point:pointList){
                    if(point.x>0 && point.y<0){//第一象限
                        point=getNextPoint(point,step);
                    }else if(point.x<0 && point.y<0){//第二象限
                        point=getNextPoint(point,-step);
                    }else if(point.x<0 && point.y>0){//第三象限
                        point=getNextPoint(point,-step);
                    }else{//第四象限
                        point=getNextPoint(point,step);
                    }

                }
                if(checkPointAllOut()){
                    animator1.cancel();
                }
                postInvalidate();
            }
        });
        animator1.start();
    }

    private boolean checkPointAllOut(){
        for(Point point:pointList){
            if(Math.abs(point.x) < canvasWidth/2+endRadius && Math.abs(point.y) < canvasHeight/2+endRadius){
                return false;
            }
        }

        return true;
    }

    private Point getNextPoint(Point point,int step){

        int x=point.x;
        int y=point.y;
        point.x=x+step;
        point.y=(int)(((float)y/x)*(x+step));
        return point;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth=w;
        canvasHeight=h;
    }

    @Override
    protected void onDraw(Canvas canvas)   {

        canvas.translate(canvasWidth/2,canvasHeight/2);
        canvas.drawLine(-canvasWidth/2,0,canvasWidth/2,0,baselinePaint);
        canvas.drawLine(0,-canvasHeight/2,0,canvasHeight/2,baselinePaint);

        drawCircles(canvas);

    }


    private void initData(){

        pointList.clear();
        for(int i=0;i<MAX_COUNT;i++){
            Point point=getRandomPoint();
            pointList.add(point);
            int rad=(int)(Math.random()*(endRadius-startRadius)+startRadius);
            radius[i]=rad;
        }

    }

    private void drawCircles(Canvas canvas){

        for(int i=0;i<pointList.size();i++){
            Point point=pointList.get(i);
            canvas.drawCircle(point.x,point.y,radius[i],circlePaint);
        }

    }


    private Point getRandomPoint(){
        Point point=new Point();
        int x=(int)(Math.random()*(endValue-startValue)+startValue);
        int y=(int)(Math.random()*(endValue-startValue)+startValue);
        point.set(x,y);
        return point;
    }


}

