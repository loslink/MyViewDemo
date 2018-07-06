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
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;


public class JunkCleanView extends View {

    private Paint baselinePaint;
    private float canvasWidth,canvasHeight;
    double startRadius=20,endRadius=30;
    int pointAreaW,pointAreaH;
    private Bitmap circle1;
    private Matrix matrix1;
    private Paint mBitPaint,backgroudPaint;
    private float bgHeight=0;
    private int duration =3000;
    private int bgStartColor= Color.BLUE;
    private int bgEndColor= Color.BLUE;
    private StateListenr stateListenr;
    Rect bgRect=new Rect();

    public JunkCleanView(Context context) {
        this(context, null);
    }

    public JunkCleanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JunkCleanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

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

        backgroudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroudPaint.setColor(bgStartColor);
        backgroudPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroudPaint.setAntiAlias(true);


        circle1 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_wing_outer)).getBitmap();
        matrix1 = new Matrix();
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

        final ValueAnimator animatorBg=ValueAnimator.ofFloat(-canvasHeight/3,canvasHeight*2/3);

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

    }

    @Override
    protected void onDraw(Canvas canvas)   {
        super.onDraw(canvas);


        canvas.translate(canvasWidth/2,canvasHeight/2);

        bgRect.set(-(int)canvasWidth/2,-(int)canvasHeight/2,(int)canvasWidth/2,(int)canvasHeight/2);

        LinearGradient linearGradient=new LinearGradient(-canvas.getWidth()/2,-canvas.getHeight()/2,
                canvas.getWidth()/2,canvas.getHeight()/2,
                bgStartColor,bgEndColor,
                Shader.TileMode.CLAMP);
        backgroudPaint.setShader(linearGradient);
        canvas.drawRect(bgRect,backgroudPaint);

        canvas.drawLine(-canvasWidth/2,0,canvasWidth/2,0,baselinePaint);
        canvas.drawLine(0,-canvasHeight/2,0,canvasHeight/2,baselinePaint);


    }



    private void calcuPoints(ValueAnimator animation){

//        step = (((float)animation.getCurrentPlayTime()-lastAnimTime)/(float) animation.getDuration())
//                *(canvasWidth/2+pointAreaW);
//        lastAnimTime=(float) animation.getCurrentPlayTime();
//        Log.v("calcuPoints",step+"");
//        float step = this.step +6;
//        for(int i=0;i<mPointList.size();i++){
//            BunblePoint point=mPointList.get(i);
//            if(point.x>=0 && point.y<=0){//第一象限
//                point=getNextPoint(point,-step,point.aParam,1);
//            }else if(point.x<0 && point.y<0){//第二象限
//                point=getNextPoint(point,step,point.aParam,1);
//            }else if(point.x<=0 && point.y>=0){//第三象限
//                point=getNextPoint(point,step,point.aParam,-1);
//            }else{//第四象限
//                point=getNextPoint(point,-step,point.aParam,-1);
//            }
//
//        }

    }


    private ValueAnimator refreshDegree(long duration,ValueAnimator.AnimatorUpdateListener listener){
        ValueAnimator animator1=ValueAnimator.ofFloat(0,360*((float)duration/1000f));
        animator1.setDuration(duration);
        animator1.setInterpolator(new AccelerateDecelerateInterpolator());
        animator1.setRepeatCount(0);
        animator1.setRepeatMode(ValueAnimator.RESTART);
        animator1.addUpdateListener(listener);
        animator1.start();
        return animator1;
    }



    private BunblePoint getNextPoint(BunblePoint point,float step,float paramA,float paramB){

        float x=point.x;
        float y=point.y;
        if(Math.abs(x) <= Math.abs(step/2)+5){
            point.x=0;
            point.y=0;
            return point;
        }
        point.x=x+step;
        if(y>0){
            point.y=Math.abs((float) (paramB*Math.sqrt(paramA*point.x)));
        }else{
            point.y=-Math.abs((float) (paramB*Math.sqrt(paramA*point.x)));
        }

        if(Math.abs(point.x)<=canvasWidth/2){
            point.degree=point.degree+3;
        }

        return point;
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

