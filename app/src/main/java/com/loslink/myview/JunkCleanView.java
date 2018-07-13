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
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class JunkCleanView extends View {

    private Paint baselinePaint;
    private float canvasWidth,canvasHeight;
    private double startRadius=13,endRadius=18;
    private Bitmap circle1,circle2;
    private Matrix matrix1,matrix2;
    private Paint mBitPaint,backgroudPaint,mBitUFOLightPaint,logoBitPaint,circlePaint;
    List<Bitmap> logoList=new ArrayList<>();
    List<BunblePoint> mLogoList=new CopyOnWriteArrayList<>();
    List<BunblePoint> mPointList=new ArrayList<>();
    private int MAX_COUNT=15;
    private float bgHeight=0;
    private int duration =2000;
    private int bgStartColor= Color.BLUE;
    private int bgEndColor= Color.BLUE;
    private StateListenr stateListenr;
    private Rect bgRect=new Rect();
    private Context mContext;
    private float sxUFO;
    private boolean isStartLogo=false;
    private float lastRadius;
    private float lastCircleX,lastCircleY;
    private LinearGradient linearGradient;
    private float startX,endX;
    private float UFOStartAngle,UFOEndAngle,UFOAngle;
    private float startY,endY;
    private float UFOAlphaScaleAfter=1;
    private float UFOAlpha,UFOLightAlpha,UFOLightAlphaOut=1;
    private float lightY;//相对于中点为原点
    private float UFOCenterX=0,UFOCenterY=0;

    public JunkCleanView(Context context) {
        this(context, null);
    }

    public JunkCleanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JunkCleanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth=w;
        canvasHeight=h;

        bgHeight=canvasHeight*2/3;

        sxUFO=(canvasWidth/circle1.getWidth())*0.53f;

        lightY=-circle1.getWidth()*sxUFO+circle1.getHeight()-dip2px(25);

        endX=canvasHeight/2-lightY;
        startX=endX-canvasHeight/4;

        endY=canvasWidth/2-dip2px(50);
        startY=-canvasWidth/2+dip2px(50);

        lastRadius=canvasWidth/5;
        lastCircleY=-(float) Math.sqrt((double) (lastRadius*lastRadius/5));
        lastCircleX=-2*lastCircleY;
        lastCircleY=lastCircleY+lightY;

        UFOCenterX=-lastCircleX;
        UFOCenterY=-lastCircleY+lightY;

        UFOStartAngle=(float) Math.acos(UFOCenterX/lastRadius);
        UFOEndAngle=(float)-Math.PI/2;

        linearGradient=new LinearGradient(-canvasWidth/2,-canvasHeight/2,
                canvasWidth/2,canvasHeight/2,
                bgStartColor,bgEndColor,
                Shader.TileMode.CLAMP);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        start();
        return super.onTouchEvent(event);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.JunkCleanView);
        bgStartColor= array.getColor(R.styleable.JunkCleanView_jbgStartColor,Color.BLUE);
        bgEndColor= array.getColor(R.styleable.JunkCleanView_jbgEndColor,Color.BLUE);
        duration= array.getInt(R.styleable.JunkCleanView_jduration,2000);
        array.recycle();

        baselinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baselinePaint.setColor(Color.RED);
        baselinePaint.setStyle(Paint.Style.STROKE);
        baselinePaint.setStrokeWidth(2);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setAntiAlias(true);

        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);

        mBitUFOLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitUFOLightPaint.setFilterBitmap(true);
        mBitUFOLightPaint.setDither(true);

        backgroudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroudPaint.setColor(bgStartColor);
        backgroudPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroudPaint.setAntiAlias(true);

        logoBitPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        logoBitPaint.setFilterBitmap(true);
        logoBitPaint.setDither(true);

        circle1 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_junk_ufo)).getBitmap();
        matrix1 = new Matrix();

        circle2 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_junk_ufo_light)).getBitmap();
        matrix2 = new Matrix();
    }


    public void start(){

        UFOCenterX=-lastCircleX;
        UFOCenterY=-lastCircleY+lightY;
        UFOAlphaScaleAfter=1;
        UFOLightAlphaOut=1;
        this.post(new Runnable() {
            @Override
            public void run() {
                startThread();
                startPointsThread();
                startAnimation();
            }
        });

    }

    private void startThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mLogoList.clear();
                    for(int i=0;i<logoList.size();i++){
                        BunblePoint bunblePoint=getRandomPoint();
                        bunblePoint.logo=logoList.get(i);
                        mLogoList.add(bunblePoint);
                        Thread.sleep((long)((duration)/logoList.size())/1);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void startPointsThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mPointList.clear();
                    for(int i=0;i< MAX_COUNT;i++){
                        BunblePoint bunblePoint=getRandomPoint();
                        int radius=(int)(Math.random()*(endRadius-startRadius)+startRadius);
                        bunblePoint.radius=radius;
                        bunblePoint.alpha=150;
                        mPointList.add(bunblePoint);
                        Thread.sleep((long)((duration)/MAX_COUNT));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private void startAnimation(){
        UFOLightAlpha=0;
        final ValueAnimator animatorBg=ValueAnimator.ofFloat(-(canvasHeight/2+lightY),canvasHeight/2-lightY);

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

        final ValueAnimator animatorLogos=ValueAnimator.ofInt(1,0);
        animatorLogos.setDuration(duration);
        animatorLogos.setInterpolator(new AccelerateInterpolator());
        animatorLogos.setRepeatCount(ValueAnimator.INFINITE);
        animatorLogos.setRepeatMode(ValueAnimator.RESTART);
        animatorLogos.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(checkLogoAllCenter()){
                    animatorLogos.cancel();
                    return;
                }
                calcuPoints(animation);
                postInvalidate();
            }
        });

        final ValueAnimator animatorUFOAlphaScaleAfter=alphaScaleAnimate(1,0,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                UFOAlphaScaleAfter= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        final ValueAnimator animatorUFOOut=alphaScaleAnimate(UFOStartAngle,UFOEndAngle,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                UFOAngle= (float) animation.getAnimatedValue();
                UFOCenterX=lastRadius*(float) Math.cos(UFOAngle);
                UFOCenterY=lastRadius*(float) Math.sin(UFOAngle);
                postInvalidate();
            }
        });
        final ValueAnimator animatorUFOLightAlphaOut=alphaScaleAnimate(1,0,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                UFOLightAlphaOut= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        animatorLogos.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorUFOLightAlphaOut.start();
            }
        });
        animatorUFOLightAlphaOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorUFOAlphaScaleAfter.start();
                animatorUFOOut.start();
            }
        });
        animatorUFOAlphaScaleAfter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if(stateListenr!=null){
                    stateListenr.animateEnd();
                }
            }
        });

        final ValueAnimator animatorUFOAlphaScale=alphaScaleAnimate(0,1,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                UFOAlpha= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        final ValueAnimator animatorUFOLightAlpha=alphaScaleAnimate(0,1,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                UFOLightAlpha= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        animatorBg.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorUFOAlphaScale.start();
            }
        });

        animatorUFOAlphaScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorUFOLightAlpha.start();

            }
        });
        isStartLogo=false;
        animatorUFOLightAlpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isStartLogo=true;
                animatorLogos.start();
            }
        });
        animatorBg.start();

    }

    private boolean checkLogoAllCenter(){
        for(BunblePoint point:mLogoList){
            if(point.x!=0 || point.y!=0){
                return false;
            }
        }
        for(BunblePoint point:mPointList){
            if(point.x!=0 || point.y!=0){
                return false;
            }
        }
        return true;
    }


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

    @Override
    protected void onDraw(Canvas canvas)   {
        super.onDraw(canvas);

        canvas.translate(canvasWidth/2,canvasHeight/2);
        bgRect.set(-(int)canvasWidth/2,-(int)canvasHeight/2,(int)canvasWidth/2,(int)bgHeight);

        backgroudPaint.setShader(linearGradient);
        canvas.drawRect(bgRect,backgroudPaint);

        drawUFO(canvas);
        if(isStartLogo){
            drawLogos(canvas);
        }

    }

    private void drawCircles(Canvas canvas){

        for(int i=0;i<mPointList.size();i++){
            BunblePoint point=mPointList.get(i);
            float par=(Math.abs(point.x)/(canvasHeight*2/3));
            circlePaint.setAlpha((int)(point.alpha*par));
            canvas.drawCircle(point.x,point.y,point.radius,circlePaint);
        }
    }

    private void drawLogos(Canvas canvas){
        canvas.translate(0,lightY);
        canvas.rotate(90);

        drawCircles(canvas);
        for(int i=0;i<mLogoList.size();i++){
            BunblePoint point=mLogoList.get(i);
            float sx=((float) canvas.getWidth()/point.logo.getWidth())*0.18f;
            float par=(Math.abs(point.x+dip2px(50))/(canvasHeight*2/3))*sx;

            logoBitPaint.setAlpha((int)(point.alpha*par));
            Matrix matrix=point.matrix;
            matrix.reset();
            matrix.setScale(par,par);
            matrix.postTranslate(point.x,point.y);

            Bitmap logo=point.logo;
            canvas.drawBitmap(logo,matrix,logoBitPaint);
        }


    }



    private void drawUFO(Canvas canvas){
        canvas.save();
        canvas.translate(lastCircleX,lastCircleY);

//        canvas.drawLine(-canvasWidth/2,0,canvasWidth/2,0,baselinePaint);
//        canvas.drawLine(0,-canvasHeight/2,0,canvasHeight/2,baselinePaint);

        matrix1.reset();
        matrix1.setScale(sxUFO*UFOAlpha*UFOAlphaScaleAfter,sxUFO*UFOAlpha*UFOAlphaScaleAfter);
        matrix1.postTranslate(UFOCenterX-circle1.getWidth()*sxUFO*UFOAlpha/2,UFOCenterY-circle1.getHeight()*sxUFO*UFOAlpha/2);
        mBitPaint.setAlpha((int) (255*UFOAlphaScaleAfter));
        canvas.drawBitmap(circle1,matrix1,mBitPaint);

        matrix2.reset();
        float sxUFOLight=((float) canvas.getWidth()/circle2.getWidth())*0.75f;
        matrix2.setScale(sxUFOLight,sxUFOLight);
        matrix2.postTranslate(UFOCenterX-circle2.getWidth()*sxUFOLight/2,UFOCenterY+dip2px(15));
        mBitUFOLightPaint.setAlpha((int) (255*UFOLightAlpha*UFOLightAlphaOut));
        canvas.drawBitmap(circle2,matrix2,mBitUFOLightPaint);

//        canvas.drawCircle(UFOCenterX,UFOCenterY,10,baselinePaint);
//        canvas.drawCircle(lastCircleX,lastCircleY,10,baselinePaint);

        canvas.restore();
    }


    private void drawCurve(Canvas canvas){
        canvas.translate(0,lightY);

        canvas.drawLine(-canvasWidth/2,0,canvasWidth/2,0,baselinePaint);
        canvas.drawLine(0,-canvasHeight/2,0,canvasHeight/2,baselinePaint);

        canvas.rotate(90);

        for(int i=0;i<mLogoList.size();i++){
            BunblePoint bunblePoint=mLogoList.get(i);

            for(float x=bunblePoint.x;x>0;x--){
                float y=(float) (bunblePoint.aParam*x*Math.sin(bunblePoint.bParam*x));
                canvas.drawCircle(x,y,1f,baselinePaint);
            }
        }


    }

    private BunblePoint getRandomPoint(){
        BunblePoint point=new BunblePoint();
        int x=(int)(Math.random()*(endX-startX)+startX);
        int y=(int)(Math.random()*(endY-startY)+startY);
        point.x=x;
        point.y=y;
        point.startX=x;
        point.startY=y;
        float K=point.x/1.25f;
        point.bParam=(float) (2*Math.PI)/K;
        point.aParam=(float)(point.y/(point.x*Math.sin(point.bParam*point.x)));
        Matrix matrix = new Matrix();
        point.matrix=matrix;
        return point;
    }

    public int dip2px(int dipValue) {
        float reSize = mContext.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    private void calcuPoints(ValueAnimator animation){

        for(int i=0;i<mLogoList.size();i++){
            BunblePoint point=mLogoList.get(i);
            float progress=1-point.x/point.startX+0.25f;
            float step=point.startX/mLogoList.size()*progress*progress;
            getNextPoint(point,-step,point.aParam,point.bParam);
        }

        for(int i=0;i<mPointList.size();i++){
            BunblePoint point=mPointList.get(i);
            float progress=1-point.x/point.startX+0.25f;
            float step=point.startX/mPointList.size()*progress*progress;
            getNextPoint(point,-step,point.aParam,point.bParam);
        }
    }



    private BunblePoint getNextPoint(BunblePoint point,float step,float paramA,float paramB){

        float x=point.x;
        float y=point.y;
        if(x <= 0){
            point.x=0;
            point.y=0;
            return point;
        }
        point.x=x+step;
        float y2=(float) (paramA*point.x*Math.sin(paramB*point.x));
        point.y=y2;
        if(point.x>canvasHeight/2){
            point.alpha=0;
        }else if(point.x<=dip2px(10)){
            point.alpha=0;
        }else{
            point.alpha=150;
        }
        return point;
    }

    public List<Bitmap> getLogoList() {
        return logoList;
    }

    public void setLogoList(List<Bitmap> logoList) {
        this.logoList = logoList;
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

