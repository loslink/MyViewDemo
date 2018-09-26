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
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
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


public class AirWingView extends View {

    private Paint circlePaint,baselinePaint;
    private float canvasWidth,canvasHeight;
    private int MAX_COUNT=20,MAX_COUNT_LOGO=10;
    double startRadius=20,endRadius=30;
    double leftStartX,leftEndX,leftStartY,leftEndY;
    double rightStartX,rightEndX,rightStartY,rightEndY;
    int pointAreaW,pointAreaH;
    List<BunblePoint> mPointList=new ArrayList<>();
    List<BunblePoint> mLogoList=new ArrayList<>();
    private Bitmap circle1,circle2;
    private Matrix matrix1,matrix2;
    private float degree1,degree2;
    private Paint mBitPaint,logoBitPaint,backgroudPaint,numTextPaint,unitTextPaint,bottomTextPaint;
    private float bgHeight=0;
    private float circleAlpha=0;
    private int number=255,numbering;
    private String unit="MB";
    private String bottomText="Freeable";
    List<Bitmap> logoList=new ArrayList<>();
    private int duration =3000;
    private int bgStartColor= Color.BLUE;
    private int bgEndColor= Color.BLUE;
    Rect bgRect=new Rect();
    Rect rectNum=new Rect();
    Rect bottomNum=new Rect();
    private StateListenr stateListenr;
    private boolean isAnimateEnd=false;
    private float step;
    private float lastAnimTime;

    public AirWingView(Context context) {
        this(context, null);
    }

    public AirWingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AirWingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        leftStartX=-(pointAreaW+canvasWidth/2);
        leftEndX=-(canvasWidth/2+100);
        leftStartY=-(pointAreaH/2);
        leftEndY=(pointAreaH/2);

        rightStartX=(canvasWidth/2+100);
        rightEndX=(pointAreaW+canvasWidth/2);
        rightStartY=-(pointAreaH/2);
        rightEndY=(pointAreaH/2);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AirWingView);
        number = array.getInt(R.styleable.AirWingView_number,0);
        unit = array.getString(R.styleable.AirWingView_unit);
        bottomText = array.getString(R.styleable.AirWingView_bottomText);
        duration = array.getInt(R.styleable.AirWingView_duration,4000);
        bgStartColor= array.getColor(R.styleable.AirWingView_bgStartColor,Color.BLUE);
        bgEndColor= array.getColor(R.styleable.AirWingView_bgEndColor,Color.BLUE);

        array.recycle();

        baselinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baselinePaint.setColor(Color.RED);
        baselinePaint.setStyle(Paint.Style.STROKE);
        baselinePaint.setStrokeWidth(2);

        backgroudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroudPaint.setColor(bgStartColor);
        backgroudPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroudPaint.setAntiAlias(true);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setAntiAlias(true);

        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);

        logoBitPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        logoBitPaint.setFilterBitmap(true);
        logoBitPaint.setDither(true);

        numTextPaint=new Paint();
        numTextPaint.setAntiAlias(true);
        numTextPaint.setTextSize(180);
        numTextPaint.setColor(Color.WHITE);
        numTextPaint.setStyle(Paint.Style.FILL);

        unitTextPaint=new Paint();
        unitTextPaint.setAntiAlias(true);
        unitTextPaint.setTextSize(40);
        unitTextPaint.setColor(Color.WHITE);
        unitTextPaint.setStyle(Paint.Style.FILL);

        bottomTextPaint=new Paint();
        bottomTextPaint.setAntiAlias(true);
        bottomTextPaint.setTextSize(50);
        bottomTextPaint.setColor(Color.WHITE);
        bottomTextPaint.setStyle(Paint.Style.FILL);

        circle1 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_wing_outer)).getBitmap();
        matrix1 = new Matrix();
        circle2 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_wing_circle)).getBitmap();
        matrix2 = new Matrix();

    }


    public void start(){

        isAnimateEnd=false;
        lastAnimTime=0;
        this.post(new Runnable() {
            @Override
            public void run() {
                MAX_COUNT_LOGO=logoList.size();
                initCirclePoints();
                initLogoPoints();
                for(int i=0;i<logoList.size();i++){
                    BunblePoint bunblePoint=mLogoList.get(i);
                    bunblePoint.logo=logoList.get(i);
                    Matrix matrix = new Matrix();
                    bunblePoint.matrix=matrix;
                    bunblePoint.alpha=255;
                }
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

        final ValueAnimator animatorPoints=ValueAnimator.ofInt(number,0);
        animatorPoints.setDuration(duration);
        animatorPoints.setInterpolator(new AlerConstantDelerInterploator());
        animatorPoints.setRepeatCount(0);
        animatorPoints.setRepeatMode(ValueAnimator.RESTART);
        animatorPoints.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                numbering= (int) animation.getAnimatedValue();
                calcuPoints(animation);
                postInvalidate();
                Log.v("airwingview","test");
            }
        });

        final ValueAnimator animatorCircleAlphaScaleDismiss=alphaScaleAnimate(1,0);

        animatorPoints.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorCircleAlphaScaleDismiss.start();
                if(stateListenr!=null){
                    stateListenr.animateEnd();
                }
                isAnimateEnd=true;
            }
        });

        final ValueAnimator animatorCircleAlphaScale=alphaScaleAnimate(0,1);

        animatorBg.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorCircleAlphaScale.start();
            }
        });

        animatorCircleAlphaScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorPoints.start();
                refreshDegree(duration, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        degree1=(float)animation.getAnimatedValue();
                    }
                });

                refreshDegree(duration, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        degree2=(float)animation.getAnimatedValue();
                    }
                });
            }
        });

        animatorBg.start();

    }

    private ValueAnimator alphaScaleAnimate(float start, float end){
        final ValueAnimator animatorCircleAlphaScale=ValueAnimator.ofFloat(start,end);
        animatorCircleAlphaScale.setDuration(200);
        animatorCircleAlphaScale.setInterpolator(new LinearInterpolator());
        animatorCircleAlphaScale.setRepeatCount(0);
        animatorCircleAlphaScale.setRepeatMode(ValueAnimator.RESTART);
        animatorCircleAlphaScale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleAlpha= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        return animatorCircleAlphaScale;
    }

    @Override
    protected void onDraw(Canvas canvas)   {
        super.onDraw(canvas);

        if(isAnimateEnd){
            canvas.drawColor(bgStartColor);
            return;
        }
        canvas.translate(canvasWidth/2,canvasHeight/3);

        bgRect.set(-(int)canvasWidth/2,-(int)canvasHeight/2,(int)canvasWidth/2,(int)bgHeight);

        LinearGradient linearGradient=new LinearGradient(-canvas.getWidth()/2,-canvas.getHeight()/2,
                canvas.getWidth()/2,canvas.getHeight()/2,
                bgStartColor,bgEndColor,
                Shader.TileMode.CLAMP);
        backgroudPaint.setShader(linearGradient);
        canvas.drawRect(bgRect,backgroudPaint);

//        canvas.drawLine(-canvasWidth/2,0,canvasWidth/2,0,baselinePaint);
//        canvas.drawLine(0,-canvasHeight/2,0,canvasHeight/2,baselinePaint);

        drawCircles(canvas);
        drawCenterCircles(canvas);
        drawLogos(canvas);

//        Path mPath=new Path();
//        mPath.addCircle(50, 50, 50, Path.Direction.CCW);
//        canvas.clipPath(mPath, Region.Op.REPLACE);

        if(numbering!=0){

            numTextPaint.getTextBounds(numbering+"",0,(numbering+"").length(),rectNum);
            numTextPaint.setAlpha((int)(circleAlpha*255));
            canvas.drawText(numbering+"",-rectNum.width()/2,(canvas.getHeight()*1.2f/3)*circleAlpha,numTextPaint);

            canvas.drawText(unit,rectNum.width()/2+30,(canvas.getHeight()*1.2f/3)*circleAlpha,unitTextPaint);

            bottomTextPaint.getTextBounds(bottomText,0,bottomText.length(),bottomNum);
            bottomTextPaint.setAlpha((int)(circleAlpha*255));
            canvas.drawText(bottomText,-bottomNum.width()/2,(canvas.getHeight()*1.2f/3+rectNum.height())*circleAlpha,bottomTextPaint);
        }


    }



    private void calcuPoints(ValueAnimator animation){

        step = (((float)animation.getCurrentPlayTime()-lastAnimTime)/(float) animation.getDuration())
                *(canvasWidth/2+pointAreaW);
        lastAnimTime=(float) animation.getCurrentPlayTime();
        Log.v("calcuPoints",step+"");
        float step = this.step +6;
        for(int i=0;i<mPointList.size();i++){
            BunblePoint point=mPointList.get(i);
            if(point.x>=0 && point.y<=0){//第一象限
                point=getNextPoint(point,-step,point.aParam,1);
            }else if(point.x<0 && point.y<0){//第二象限
                point=getNextPoint(point,step,point.aParam,1);
            }else if(point.x<=0 && point.y>=0){//第三象限
                point=getNextPoint(point,step,point.aParam,-1);
            }else{//第四象限
                point=getNextPoint(point,-step,point.aParam,-1);
            }

        }

        float logoStep = this.step;
        for(int i=0;i<mLogoList.size();i++){
            BunblePoint point=mLogoList.get(i);
            if(point.x>=0 && point.y<=0){//第一象限
                point=getNextPoint(point,-logoStep,point.aParam,1);
            }else if(point.x<0 && point.y<0){//第二象限
                point=getNextPoint(point,logoStep,point.aParam,1);
            }else if(point.x<=0 && point.y>=0){//第三象限
                point=getNextPoint(point,logoStep,point.aParam,-1);
            }else{//第四象限
                point=getNextPoint(point,-logoStep,point.aParam,-1);
            }
        }

//        if(checkPointAllCenter()){
//            initCirclePoints();
//        }
//
//        if(checkLogoAllCenter()){
//            initLogoPoints();
//        }

    }


    private ValueAnimator refreshDegree(long duration,ValueAnimator.AnimatorUpdateListener listener){
        ValueAnimator animator1=ValueAnimator.ofFloat(0,360*((float)duration/1000f));
        animator1.setDuration(duration);
        animator1.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator1.setInterpolator(new AlerConstantDelerInterploator());
//        animator1.setInterpolator(new LinearInterpolator());
        animator1.setRepeatCount(0);
        animator1.setRepeatMode(ValueAnimator.RESTART);
        animator1.addUpdateListener(listener);
        animator1.start();
        return animator1;
    }

    private boolean checkPointAllCenter(){
        for(BunblePoint point:mPointList){
            if(point.x!=0 || point.y!=0){
                return false;
            }
        }
        return true;
    }

    private boolean checkLogoAllCenter(){
        for(BunblePoint point:mLogoList){
            if(point.x!=0 || point.y!=0){
                return false;
            }
        }
        return true;
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

    private void drawCenterCircles(Canvas canvas){
        matrix1.reset();
        float sx=((float) canvas.getWidth()/circle1.getWidth())*0.6f;
        matrix1.setScale(sx*circleAlpha,sx*circleAlpha);
        matrix1.postTranslate(-circle1.getWidth()*sx*circleAlpha/2,-circle1.getWidth()*sx*circleAlpha/2);
        matrix1.postRotate(degree1,0,0);
        canvas.drawBitmap(circle1,matrix1,mBitPaint);

        matrix2.reset();
        matrix2.setScale(sx*circleAlpha,sx*circleAlpha);
        matrix2.postTranslate(-circle2.getWidth()*sx*circleAlpha/2,-circle2.getWidth()*sx*circleAlpha/2);
        matrix2.postRotate(degree2,0,0);
        canvas.drawBitmap(circle2,matrix2,mBitPaint);
    }

    private void initCirclePoints(){

        if(mPointList.size()<=0){
            for(int i=0;i<MAX_COUNT;i++){
                BunblePoint point=new BunblePoint();
                mPointList.add(point);
            }
        }

        for(int i=0;i<MAX_COUNT;i++){
            BunblePoint point=mPointList.get(i);

            if(i<MAX_COUNT/2){
                point=getLeftRandomPoint(point);
            }else{
                point=getRightRandomPoint(point);
            }

            int rad=(int)(Math.random()*(endRadius-startRadius)+startRadius);
            point.radius=rad;
            point.alpha=153;
            float paramA=(point.y*point.y)/point.x;
            point.aParam=paramA;
        }


    }

    private void initLogoPoints(){

        if(mLogoList.size()<=0){
            for(int i=0;i<MAX_COUNT_LOGO;i++){
                BunblePoint point=new BunblePoint();
                mLogoList.add(point);
            }
        }

        for(int i=0;i<MAX_COUNT_LOGO;i++){
            BunblePoint point=mLogoList.get(i);
            if(i<MAX_COUNT_LOGO/2){//left
                point=getLeftRandomPoint(point);
            }else{//right
                point=getRightRandomPoint(point);
            }
            Float paramA=(point.y*point.y)/point.x;
            point.aParam=paramA;
        }

    }

    private void drawCircles(Canvas canvas){

        for(int i=0;i<mPointList.size();i++){
            BunblePoint point=mPointList.get(i);
            circlePaint.setAlpha((int)(( Math.abs(point.x)/(canvasWidth/2f))*155));
            canvas.drawCircle(point.x,point.y,point.radius,circlePaint);
        }

    }

    private void drawLogos(Canvas canvas){

        for(int i=0;i<mLogoList.size();i++){
            BunblePoint point=mLogoList.get(i);
            float par=((float) Math.abs(point.x)/(canvasWidth/2));
            logoBitPaint.setAlpha((int)(par*255));
            Matrix matrix=point.matrix;
            matrix.reset();
            matrix.setScale(par,par);
            matrix.postTranslate(point.x,point.y);

            Bitmap logo=point.logo;
            matrix.postRotate(point.degree,point.x+matrix.mapRadius(logo.getWidth())/2,point.y+matrix.mapRadius(logo.getHeight())/2);
            canvas.drawBitmap(logo,matrix,logoBitPaint);
        }

    }


    private BunblePoint getLeftRandomPoint(BunblePoint point){
        int x=(int)(Math.random()*(leftEndX-leftStartX)+leftStartX);
        int y=(int)(Math.random()*(leftEndY-leftStartY)+leftStartY);
        point.x=x;
        point.y=y;
        return point;
    }

    private BunblePoint getRightRandomPoint(BunblePoint point){
        int x=(int)(Math.random()*(rightEndX-rightStartX)+rightStartX);
        int y=(int)(Math.random()*(rightEndY-rightStartY)+rightStartY);
        point.x=x;
        point.y=y;
        return point;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public List<Bitmap> getLogoList() {
        return logoList;
    }

    public void setLogoList(List<Bitmap> logoList) {
        this.logoList = logoList;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

