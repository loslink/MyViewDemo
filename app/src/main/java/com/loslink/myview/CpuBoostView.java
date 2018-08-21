package com.loslink.myview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class CpuBoostView extends View {

    private Paint baselinePaint,bottomTextPaint;
    private float canvasWidth,canvasHeight;
    private Bitmap circle1,circle2,bitmapLight;
    private Matrix matrix1,matrix2,matrixLight;
    private Paint mBitPaint,backgroudPaint;
    private float bgHeight=0;
    private int duration =2000;
    private int bgStartColor= Color.BLUE;
    private int bgEndColor= Color.BLUE;
    private StateListenr stateListenr;
    private Rect bgRect=new Rect();
    private Context mContext;
    private LinearGradient linearGradient;
    private float zuobiao=2f/5;
    private String bottomText="Scanning the CPU";
    Rect bottomNum=new Rect();


    public CpuBoostView(Context context) {
        this(context, null);
    }

    public CpuBoostView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CpuBoostView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth=w;
        canvasHeight=h;

        bgHeight=canvasHeight*(1-zuobiao);

        sx=(canvasWidth/circle1.getWidth())*0.55f;
        cpuWhidth=circle1.getWidth()*sx;
        cpuHeight=circle1.getHeight()*sx;

        linearGradient=new LinearGradient(-canvasWidth/2,-canvasHeight/2,
                canvasWidth/2,canvasHeight/2,
                bgStartColor,bgEndColor,
                Shader.TileMode.CLAMP);
    }
    float sx;
    float cpuWhidth,cpuHeight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        start();
        return super.onTouchEvent(event);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CpuBoostView);
        bgStartColor= array.getColor(R.styleable.CpuBoostView_bgStartColor,Color.BLUE);
        bgEndColor= array.getColor(R.styleable.CpuBoostView_bgEndColor,Color.BLUE);
        duration= array.getInt(R.styleable.CpuBoostView_duration,2000);
        array.recycle();

        baselinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baselinePaint.setColor(Color.RED);
        baselinePaint.setStyle(Paint.Style.STROKE);
        baselinePaint.setStrokeWidth(2);

        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);


        backgroudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroudPaint.setColor(bgStartColor);
        backgroudPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroudPaint.setAntiAlias(true);

        bottomTextPaint=new Paint();
        bottomTextPaint.setAntiAlias(true);
        bottomTextPaint.setTextSize(50);
        bottomTextPaint.setColor(Color.WHITE);
        bottomTextPaint.setTypeface(Typeface.DEFAULT);
        bottomTextPaint.setStyle(Paint.Style.FILL);

        circle1 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_cpu_scan_pre)).getBitmap();
        matrix1 = new Matrix();

        circle2 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_cpu_scan_after)).getBitmap();
        matrix2 = new Matrix();

        bitmapLight = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_cpu_scan_light)).getBitmap();
        matrixLight = new Matrix();
    }


    public void start(){

        this.post(new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        });

    }

    private void startAnimation(){
        isCanScan=false;
        cpuAlpha=0;
        progress=0;
        lastDismiss=1;
        lightAlphaScale=1;
        final ValueAnimator animatorBg=ValueAnimator.ofFloat(-canvasHeight*zuobiao,canvasHeight*(1-zuobiao));

        animatorBg.setDuration(200);
        animatorBg.setInterpolator(new LinearInterpolator());
        animatorBg.setRepeatCount(0);
        animatorBg.setRepeatMode(ValueAnimator.RESTART);
        animatorBg.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bgHeight= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });


        animatorBg.start();

        final ValueAnimator animatorCpu=ValueAnimator.ofFloat(0,1);
        animatorCpu.setDuration(1500);
        animatorCpu.setInterpolator(new LinearInterpolator());
        animatorCpu.setRepeatCount(0);
        animatorCpu.setRepeatMode(ValueAnimator.RESTART);
        animatorCpu.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });


        final ValueAnimator animatorCpuAlphaScale=alphaScaleAnimate(0,1,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cpuAlpha= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        final ValueAnimator animatorLightAlphaScale=alphaScaleAnimate(1,0,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lightAlphaScale= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        final ValueAnimator animatorLastDismiss=alphaScaleAnimate(1,0,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lastDismiss= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        animatorLightAlphaScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorLastDismiss.start();
            }
        });

        animatorLastDismiss.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(stateListenr!=null){
                    stateListenr.animateEnd();
                }
            }
        });

        animatorCpu.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorLightAlphaScale.start();
            }
        });

        animatorBg.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorCpuAlphaScale.start();
            }
        });

        animatorCpuAlphaScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorCpu.start();
                isCanScan=true;
            }
        });
    }

    float lightAlphaScale=1,lastDismiss=1;

    private ValueAnimator alphaScaleAnimate(float start, float end, ValueAnimator.AnimatorUpdateListener updateListener){
        final ValueAnimator animatorCircleAlphaScale=ValueAnimator.ofFloat(start,end);
        animatorCircleAlphaScale.setDuration(300);
        animatorCircleAlphaScale.setInterpolator(new LinearInterpolator());
        animatorCircleAlphaScale.setRepeatCount(0);
        animatorCircleAlphaScale.setRepeatMode(ValueAnimator.RESTART);
        if(updateListener!=null){
            animatorCircleAlphaScale.addUpdateListener(updateListener);
        }

        return animatorCircleAlphaScale;
    }

    float cpuAlpha;
    float progress;
    boolean isCanScan=false;


    @Override
    protected void onDraw(Canvas canvas)   {
        super.onDraw(canvas);

        canvas.translate(canvasWidth/2,canvasHeight*zuobiao);
        bgRect.set(-(int)canvasWidth/2,-(int)(canvasHeight*zuobiao),(int)canvasWidth/2,(int)bgHeight);

        backgroudPaint.setShader(linearGradient);
        canvas.drawRect(bgRect,backgroudPaint);

        drawCPU(canvas);

        bottomTextPaint.setTextSize(cpuAlpha*lastDismiss*50);
        bottomTextPaint.getTextBounds(bottomText,0,bottomText.length(),bottomNum);
        bottomTextPaint.setAlpha((int)(cpuAlpha*lastDismiss*255));
        canvas.drawText(bottomText,-bottomNum.width()/2,circle2.getHeight(),bottomTextPaint);
    }

    private void drawCPU(Canvas canvas){

//        canvas.drawLine(-canvasWidth/2,0,canvasWidth/2,0,baselinePaint);
//        canvas.drawLine(0,-canvasHeight*zuobiao,0,canvasHeight*(1-zuobiao),baselinePaint);

        matrix1.reset();
        matrix1.setScale(sx*cpuAlpha*lastDismiss,sx*cpuAlpha*lastDismiss);
        matrix1.postTranslate(-cpuWhidth*cpuAlpha*lastDismiss/2,-cpuHeight*cpuAlpha*lastDismiss/2);
        mBitPaint.setAlpha((int)(255*lastDismiss));
        canvas.drawBitmap(circle1,matrix1,mBitPaint);

        Rect src=new Rect(0,0,circle2.getWidth(),(int)(circle2.getHeight()*progress));
        RectF dst=new RectF(-cpuWhidth/2,-cpuHeight/2,cpuWhidth/2,cpuHeight*progress-cpuHeight/2);
//        dst.left=-cpuWhidth/2+(canvasWidth/2-cpuWhidth/2)*(1-lastDismiss);
//        dst.top=-cpuHeight/2+(canvasHeight*zuobiao-cpuHeight/2)*(1-lastDismiss);
//        dst.right=(cpuWhidth/2)*lastDismiss;
//        dst.bottom=(cpuHeight*progress-cpuHeight/2)*lastDismiss;
        canvas.drawBitmap(circle2,src,dst,mBitPaint);

        if(isCanScan){
            float sx2=(canvasWidth/bitmapLight.getWidth())*0.7f;
            matrixLight.reset();
            matrixLight.setScale(sx2*lightAlphaScale,sx2*lightAlphaScale);
            matrixLight.postTranslate(-bitmapLight.getWidth()*sx2*lightAlphaScale/2,cpuHeight*progress-cpuHeight/2);
            canvas.drawBitmap(bitmapLight,matrixLight,mBitPaint);
        }

    }

    public int dip2px(int dipValue) {
        float reSize = mContext.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }


    public StateListenr getStateListenr() {
        return stateListenr;
    }

    public void setStateListenr(StateListenr stateListenr) {
        this.stateListenr = stateListenr;
    }

    public interface StateListenr{
        void animateEnd();
    }

}

