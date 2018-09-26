package com.loslink.myview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class FloatBallView extends View {

    private Paint circlePaint, circleContentPaint, textPaint;
    private Context context;
    private float canvasWidth, canvasHeight;
    private float moveX;
    private float circleStrokeWidth=10;
    private float ballWidth=100;
    private float progress=0.5f;
    private ValueAnimator waveAnimator;
    private Path path1 = new Path();
    private Path path2 = new Path();

    public FloatBallView(Context context) {
        super(context);
        this.context = context;
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FloatBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    public void init(AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatBallView);
        ballWidth= array.getDimension(R.styleable.FloatBallView_circleWidth,60);
        circleStrokeWidth= array.getDimension(R.styleable.FloatBallView_strokeWidth,2);
        array.recycle();


        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleStrokeWidth);
        circlePaint.setColor(context.getResources().getColor(R.color.floatBallCircleYellowOutColor));

        circleContentPaint = new Paint();
        circleContentPaint.setAntiAlias(true);
        circleContentPaint.setStyle(Paint.Style.FILL);
        circleContentPaint.setColor(context.getResources().getColor(R.color.floatBallCircleYellowColor));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth = w;
        canvasHeight = h;

    }



    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(canvasWidth / 2, canvasHeight / 2);
        if(progress>=0.7){
            circlePaint.setColor(context.getResources().getColor(R.color.floatBallCircleRedOutColor));
            circleContentPaint.setColor(context.getResources().getColor(R.color.floatBallCircleRedColor));
        }else if(progress>=0.5){
            circlePaint.setColor(context.getResources().getColor(R.color.floatBallCircleYellowOutColor));
            circleContentPaint.setColor(context.getResources().getColor(R.color.floatBallCircleYellowColor));
        }else{
            circlePaint.setColor(context.getResources().getColor(R.color.floatBallCircleGreenOutColor));
            circleContentPaint.setColor(context.getResources().getColor(R.color.floatBallCircleGreenColor));
        }

        path1.reset();
        path1.addCircle(0, 0, ballWidth/2, Path.Direction.CW);
        setPath(path2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            path1.op(path2, Path.Op.INTERSECT);
        }else{
            /**
             * Api 19下采用画布的clip实现
             */
            canvas.clipPath(path2, Region.Op.DIFFERENCE);
        }

        canvas.drawPath(path1, circleContentPaint);

        canvas.drawCircle(0,0,ballWidth/2+2,circlePaint);

    }

    private void setPath(Path path) {
        float x = 0;
        float y = 0;
        //每次进来都把path重置一下
        path.reset();
        for (int i = 0; i < ballWidth; i++) {
            x = i-ballWidth/2;
            y = (float) Math.sin(((i-moveX) * Math.PI)/(ballWidth/2))*10+(0.5f-progress)*ballWidth;
            if (i == 0) {
                // x=0的时候，即左上角的点，移动画笔于此
                path.moveTo(x, y);
            }
            // 用每个x求得每个y，用quadTo方法连接成一条贝塞尔曲线
            path.quadTo(x, y, x + 1, y);
        }
        // 最后连接到右下角底部
        path.lineTo(ballWidth/2, ballWidth/2);
        // 连接到左下角底部
        path.lineTo(-ballWidth/2, ballWidth/2);
        // 起点在左上角，这个时候可以封闭路径了，封闭。
        path.close();
    }

    public void setProgress(float progress){
        if(progress<0){
            progress=0;
        }else if(progress>1){
            progress=1;
        }

        this.progress=progress;
    }


    public void startAnimation() {

        waveAnimator = ValueAnimator.ofFloat(0, ballWidth);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setRepeatMode(ValueAnimator.RESTART);
        waveAnimator.start();
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });


    }

    public void cancelAnimation(){
        if(waveAnimator!=null && waveAnimator.isRunning()){
            waveAnimator.cancel();
        }
    }

    public int dip2px(int dipValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

}

