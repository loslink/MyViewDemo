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
    double startRadius=20,endRadius=30;
    int pointAreaW,pointAreaH;
    private Bitmap circle1,circle2;
    private Matrix matrix1,matrix2;
    private Paint mBitPaint,backgroudPaint,mBitUFOLightPaint,logoBitPaint;
    List<Bitmap> logoList=new ArrayList<>();
    List<BunblePoint> mLogoList=new CopyOnWriteArrayList<>();
    private float bgHeight=0;
    private int duration =3000;
    private int bgStartColor= Color.BLUE;
    private int bgEndColor= Color.BLUE;
    private StateListenr stateListenr;
    Rect bgRect=new Rect();
    private Context mContext;
    float sxUFO;
    private boolean isStartLogo=false;

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

        pointAreaW=(int)((canvasWidth*5)/3);
        pointAreaH=(int)((canvasHeight*2)/4);

        sxUFO=(canvasWidth/circle1.getWidth())*0.53f;

        lightY=-circle1.getWidth()*sxUFO+circle1.getHeight()-dip2px(25);

        endX=canvasHeight/2-lightY;
        startX=endX-canvasHeight/4;

        endY=canvasWidth/2-dip2px(50);
        startY=-canvasWidth/2+dip2px(50);
    }

    float endX;
    float startX;

    float endY;
    float startY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        start();
        return super.onTouchEvent(event);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.JunkCleanView);
        bgStartColor= array.getColor(R.styleable.JunkCleanView_jbgStartColor,Color.BLUE);
        bgEndColor= array.getColor(R.styleable.JunkCleanView_jbgEndColor,Color.BLUE);
        array.recycle();

        baselinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baselinePaint.setColor(Color.RED);
        baselinePaint.setStyle(Paint.Style.STROKE);
        baselinePaint.setStrokeWidth(2);

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

        lastAnimTime=0;
        this.post(new Runnable() {
            @Override
            public void run() {
                startThread();
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
                        Thread.sleep((long)(duration/logoList.size())/1);
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

        final ValueAnimator animatorPoints=ValueAnimator.ofInt(1,0);
        animatorPoints.setDuration(duration);
        animatorPoints.setInterpolator(new AccelerateInterpolator());
        animatorPoints.setRepeatCount(0);
        animatorPoints.setRepeatMode(ValueAnimator.RESTART);
        animatorPoints.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                calcuPoints(animation);
                postInvalidate();
            }
        });

        animatorPoints.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
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
                animatorPoints.start();
            }
        });
        animatorBg.start();

    }

    float UFOAlpha,UFOLightAlpha;
    private ValueAnimator alphaScaleAnimate(float start, float end, ValueAnimator.AnimatorUpdateListener updateListener){
        final ValueAnimator animatorCircleAlphaScale=ValueAnimator.ofFloat(start,end);
        animatorCircleAlphaScale.setDuration(200);
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

        LinearGradient linearGradient=new LinearGradient(-canvas.getWidth()/2,-canvas.getHeight()/2,
                canvas.getWidth()/2,canvas.getHeight()/2,
                bgStartColor,bgEndColor,
                Shader.TileMode.CLAMP);
        backgroudPaint.setShader(linearGradient);
        canvas.drawRect(bgRect,backgroudPaint);

        drawUFO(canvas);

        if(isStartLogo){
            drawLogos(canvas);
        }

//        drawCurve(canvas);

    }

    private void drawLogos(Canvas canvas){
        canvas.translate(0,lightY);

        canvas.drawLine(-canvasWidth/2,0,canvasWidth/2,0,baselinePaint);
        canvas.drawLine(0,-canvasHeight/2,0,canvasHeight/2,baselinePaint);

        canvas.rotate(90);

        for(int i=0;i<mLogoList.size();i++){
            BunblePoint point=mLogoList.get(i);
            float sx=((float) canvas.getWidth()/point.logo.getWidth())*0.1f;
            float par=((float) Math.abs(point.x)/(canvasWidth*2/3))*sx;
//            logoBitPaint.setAlpha((int)(par*255));
            Matrix matrix=point.matrix;
            matrix.reset();
            matrix.setScale(par,par);
            matrix.postTranslate(point.x,point.y);

            Bitmap logo=point.logo;
//            matrix.postRotate(point.degree,point.x+matrix.mapRadius(logo.getWidth())/2,point.y+matrix.mapRadius(logo.getHeight())/2);
            canvas.drawBitmap(logo,matrix,logoBitPaint);
        }

    }

    private void drawUFO(Canvas canvas){
        matrix1.reset();
        float sxUFO=((float) canvas.getWidth()/circle1.getWidth())*0.53f;
        matrix1.setScale(sxUFO*UFOAlpha,sxUFO*UFOAlpha);
        matrix1.postTranslate(-circle1.getWidth()*sxUFO/2,-circle1.getWidth()*sxUFO);
        canvas.drawBitmap(circle1,matrix1,mBitPaint);

        matrix2.reset();
        float sxUFOLight=((float) canvas.getWidth()/circle2.getWidth())*0.75f;
        matrix2.setScale(sxUFOLight,sxUFOLight);
        matrix2.postTranslate(-circle2.getWidth()*sxUFOLight/2,-circle1.getWidth()*sxUFO+circle1.getHeight()-dip2px(25));
        mBitUFOLightPaint.setAlpha((int) (255*UFOLightAlpha));
        canvas.drawBitmap(circle2,matrix2,mBitUFOLightPaint);

    }

    float lightY;//相对于中点为原点
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

    private float lastAnimTime;
    private void calcuPoints(ValueAnimator animation){

        float step = (((float)animation.getCurrentPlayTime()-lastAnimTime)/(float) animation.getDuration())
                *(canvasHeight*2/3);
        lastAnimTime=(float) animation.getCurrentPlayTime();
        for(int i=0;i<mLogoList.size();i++){
            BunblePoint point=mLogoList.get(i);
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

