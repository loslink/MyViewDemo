package com.loslink.myview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.SumPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.loslink.myview.R;

import javax.xml.datatype.Duration;

class RotateView extends View {

    private Paint mBitPaint;
    private Bitmap circle1,circle2,circle3,circle4,circle5;
    private Matrix matrix1,matrix2,matrix3,matrix4,matrix5;
    private float degree1,degree2,degree3,degree4,degree5;

    public RotateView(Context context) {
        super(context);
        init();
    }

    public RotateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    public void init() {
        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);

        circle1 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.circle1)).getBitmap();
        circle2 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.circle2)).getBitmap();
        circle3 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.circle3)).getBitmap();
        circle4 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.circle4)).getBitmap();
        circle5 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.circle5)).getBitmap();

//        circle1= BitmapFactory.decodeResource(getResources(),R.mipmap.circle1);//实例化

        matrix1 = new Matrix();
        matrix2 = new Matrix();
        matrix3 = new Matrix();
        matrix4 = new Matrix();
        matrix5 = new Matrix();

    }

    public void startAnimation(){
        refreshDegree(8000, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree1=(float)animation.getAnimatedValue();
                postInvalidate();
            }
        });
        refreshDegree(8300, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree2=(float)animation.getAnimatedValue();
            }
        });
        refreshDegree(8600, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree3=(float)animation.getAnimatedValue();
            }
        });
        refreshDegree(8900, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree4=(float)animation.getAnimatedValue();
            }
        });
        refreshDegree(9200, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree5=(float)animation.getAnimatedValue();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas)   {

        matrix1.reset();
        matrix1.setScale((float) canvas.getWidth()/circle1.getWidth(),(float)canvas.getHeight()/circle1.getHeight());
        matrix1.postRotate(degree1,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(circle1,matrix1,mBitPaint);

        matrix2.reset();
        matrix2.setScale((float) canvas.getWidth()/circle2.getWidth(),(float)canvas.getHeight()/circle2.getHeight());
        matrix2.postRotate(degree2,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(circle2,matrix2,mBitPaint);

        matrix3.reset();
        matrix3.setScale((float) canvas.getWidth()/circle3.getWidth(),(float)canvas.getHeight()/circle3.getHeight());
        matrix3.postRotate(degree3,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(circle3,matrix3,mBitPaint);

        matrix4.reset();
        matrix4.setScale((float) canvas.getWidth()/circle4.getWidth(),(float)canvas.getHeight()/circle4.getHeight());
        matrix4.postRotate(degree4,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(circle4,matrix4,mBitPaint);

        matrix5.reset();
        matrix5.setScale((float) canvas.getWidth()/circle5.getWidth(),(float)canvas.getHeight()/circle5.getHeight());
        matrix5.postRotate(degree5,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(circle5,matrix5,mBitPaint);


    }

    private void refreshDegree(long duration,ValueAnimator.AnimatorUpdateListener listener){
        ValueAnimator animator1=ValueAnimator.ofFloat(0,360);
        animator1.setDuration(duration);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.RESTART);
        animator1.addUpdateListener(listener);
        animator1.start();
    }


}

