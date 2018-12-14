package com.loslink.myview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.loslink.myview.R;

class SwitchButton extends View {

    private Paint basePaint,circlePaint,textPaint;
    private Context context;
    private float canvasWidth,canvasHeight;
    private float rectStartX;
    private float radius;
    private boolean isShowText=false;
    private float oringinalRectStartX;
    private float buttonWidth,buttonHeight;
    private float originalWidth;

    public SwitchButton(Context context) {
        this(context,null);
        init();
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
        init();
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        basePaint =new Paint();
        basePaint.setAntiAlias(true);
        basePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        basePaint.setStrokeWidth(10);
        basePaint.setColor(context.getResources().getColor(R.color.buttonBaseColor));

        circlePaint =new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setStrokeWidth(10);
        circlePaint.setColor(context.getResources().getColor(R.color.buttonCircleColor));

        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth=w;
        canvasHeight=h;

        resetData();
    }

    private void resetData(){
        originalWidth=dip2px(80);
        buttonHeight=dip2px(30);
        buttonWidth=buttonHeight;
        radius=buttonHeight/2;
        rectStartX=canvasWidth/2-originalWidth/2+radius;
    }

    @Override
    protected void onDraw(Canvas canvas)   {

        drawButton(canvas);

        if(isShowText){
            Rect rect=new Rect();
            String text="YES";
            textPaint.getTextBounds(text,0,text.length(),rect);
            textPaint.setAlpha(255);
            canvas.drawText(text,canvasWidth/2-rect.width()/2,canvasHeight/2+rect.height()/2,textPaint);
        }

    }



    private void drawButton(Canvas canvas){

        Path path1=new Path();
        RectF rectF=new RectF(canvasWidth/2-originalWidth/2,
                canvasHeight/2-buttonHeight/2,
                canvasWidth/2+originalWidth/2,
                canvasHeight/2+buttonHeight/2);
        path1.addRoundRect(rectF,radius,radius,Path.Direction.CCW);
        canvas.drawPath(path1, basePaint);


        Path path=new Path();
        RectF rectF2=new RectF(rectStartX,
                canvasHeight/2-buttonHeight/2,
                rectStartX+buttonWidth-2*radius,
                canvasHeight/2+buttonHeight/2);
        path.addCircle(rectStartX,canvasHeight/2,radius,Path.Direction.CCW);
        path.addRect(rectF2,Path.Direction.CCW);
        path.addCircle(rectStartX+buttonWidth-2*radius,canvasHeight/2,radius,Path.Direction.CCW);

        canvas.drawPath(path, circlePaint);

    }

    private void startAnimation(){

        isShowText=false;
        ValueAnimator scrollAnimator=ValueAnimator.ofFloat(canvasWidth/2-originalWidth/2+radius,canvasWidth/2+originalWidth/2-radius);
        scrollAnimator.setRepeatCount(0);
        scrollAnimator.setInterpolator(new AccelerateInterpolator());
        scrollAnimator.start();
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rectStartX =(float)animation.getAnimatedValue();
                oringinalRectStartX=rectStartX;
                invalidate();
            }
        });


        final ValueAnimator valueAnimator=ValueAnimator.ofFloat(radius*2,originalWidth);
        scrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                valueAnimator.start();
            }
        });


        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                buttonWidth =(float)animation.getAnimatedValue();
                rectStartX=oringinalRectStartX-(buttonWidth-radius*2);
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShowText=true;
                invalidate();
            }
        });

    }

    public int dip2px(int dipValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        resetData();
        startAnimation();
        return true;
    }

}

