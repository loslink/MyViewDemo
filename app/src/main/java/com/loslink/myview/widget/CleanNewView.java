package com.loslink.myview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.loslink.myview.R;
import com.loslink.myview.model.Bubble;
import com.loslink.myview.utils.DipToPx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CleanNewView extends View {

    private Paint itemPaint,circleWhitePaint,circleInnerPaint,bunblePaint;
    private long duration=3000;
    private Context context;
    private float canvasWidth, canvasHeight;
    private float OUTER_RADIUS = 133;
    private float BAR_WIDTH = 18;
    private float BAR_HEIGHT = 5;
    private float INNER_OUT_RADIUS = 90;
    private float INNER_IN_RADIUS = 80;
    private float startDegree=-215, endDegree=35f;
    private float currentDegree=startDegree;
    private float itemDegree=5f;
    private ValueAnimator animator;
    private RectF itemRectF;
    private Path itemPath;
    private int colorBitWidth=100,colorBitHeigth=10;
    private Bitmap colorBitmap;
    private float levelProgress=0f;//垃圾级别
    private float junkFileSizeMax=400f;//垃圾分界最高
    private boolean isStart=false;
    private Runnable delayRunnable;
    private List<Bubble> bubbles = new ArrayList<>();
    private float centerWidth = 100, centerHeight = 100;
    private List<ValueAnimator> animatorList;

    public CleanNewView(Context context) {
        this(context, null);
    }

    public CleanNewView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CleanNewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void init() {

        OUTER_RADIUS = DipToPx.dipToPx(context, OUTER_RADIUS);
        BAR_WIDTH = DipToPx.dipToPx(context, BAR_WIDTH);
        BAR_HEIGHT = DipToPx.dipToPx(context, BAR_HEIGHT);
        INNER_OUT_RADIUS = OUTER_RADIUS - BAR_WIDTH - DipToPx.dipToPx(context, 10);
        INNER_IN_RADIUS = INNER_OUT_RADIUS - DipToPx.dipToPx(context, 8);

        itemRectF = new RectF(OUTER_RADIUS - BAR_WIDTH,
                -BAR_HEIGHT / 2,
                OUTER_RADIUS,
                BAR_HEIGHT / 2);
        itemPath = new Path();
        itemPath.addRoundRect(itemRectF, BAR_HEIGHT / 2, BAR_HEIGHT / 2, Path.Direction.CCW);//耗性能

        itemPaint = new Paint();
        itemPaint.setAntiAlias(true);
        itemPaint.setStyle(Paint.Style.FILL);
        itemPaint.setShadowLayer(4f,2f,2f,Color.parseColor("#22000000"));

        circleWhitePaint = new Paint();
        circleWhitePaint.setAntiAlias(true);
        circleWhitePaint.setColor(Color.WHITE);
        circleWhitePaint.setStyle(Paint.Style.FILL);
        circleWhitePaint.setShadowLayer(10,6f,6f,Color.parseColor("#11000000"));

        circleInnerPaint = new Paint();
        circleInnerPaint.setAntiAlias(true);
        circleInnerPaint.setColor(Color.RED);
        circleInnerPaint.setStyle(Paint.Style.FILL);

        bunblePaint = new Paint();
        bunblePaint.setAntiAlias(true);
        bunblePaint.setColor(Color.WHITE);
        bunblePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));


        setLayerType(LAYER_TYPE_SOFTWARE, null);//对单独的View在运行时阶段禁用硬件加速,阴影才有效(不要放onDraw里)
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth = w;
        canvasHeight = h;

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getColor(R.color.cleanViewBgColor));
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        drawOuterItems(canvas);
        drowCircle(canvas);
    }

    /**
     * 画内圈
     * @param canvas
     */
    private void drowCircle(Canvas canvas){
        float progress=(currentDegree-startDegree)/(endDegree-startDegree);

        canvas.drawCircle(0,0,INNER_OUT_RADIUS,circleWhitePaint);
        int pixel = getBitmapColor(progress);
        int a = Color.alpha(pixel);
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);
        int startColor=Color.argb(a*2/3,r,g,b);
        LinearGradient linearGradient=new LinearGradient(-INNER_IN_RADIUS,-INNER_IN_RADIUS,
                INNER_IN_RADIUS-DipToPx.dipToPx(context,80),INNER_IN_RADIUS-DipToPx.dipToPx(context,80),
                startColor,
                pixel,
                Shader.TileMode.CLAMP);
        circleInnerPaint.setShader(linearGradient);
        //调用saveLayer时，会生成了一个全新的bitmap，这个bitmap的大小就是我们指定的保存区域的大小，新生成的bitmap是全透明的，在调用saveLayer后所有的绘图操作都是在这个bitmap上进行的。
        int layerID = canvas.saveLayer(-canvasWidth/2, -canvasHeight/2, canvasWidth/2, canvasHeight/2, circleInnerPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(0,0,INNER_IN_RADIUS,circleInnerPaint);
        linearGradient=null;
        drawBunbles(canvas);
    }

    /**
     * 绘制小气泡
     * @param canvas
     */
    private void drawBunbles(Canvas canvas){
        canvas.save();
        canvas.translate(-INNER_IN_RADIUS,-INNER_IN_RADIUS);
        for (Bubble bubble : bubbles) {
            if (bubble.pointF.x > 30) {
                bunblePaint.setAlpha(bubble.alph);
                canvas.drawCircle(bubble.pointF.x, bubble.pointF.y, bubble.radio, bunblePaint);
            }
        }
        canvas.restore();
    }

    /**
     * 绘制外圈小块
     * @param canvas
     */
    private void drawOuterItems(Canvas canvas){
        canvas.save();
        canvas.rotate(startDegree);
        float lastDegree=startDegree;

        while (true) {
            if(lastDegree > endDegree){
                break;
            }
            if(isStart && lastDegree <= currentDegree){
                float progress=(lastDegree-startDegree)/(endDegree-startDegree);
                itemPaint.setColor(getBitmapColor(progress));
            }else{
                itemPaint.setColor(getColor(R.color.cleanItemGrayColor));
            }
            canvas.drawPath(itemPath, itemPaint);

            canvas.rotate(itemDegree);
            lastDegree = lastDegree+itemDegree;
        }
        canvas.restore();
    }

    public void setCleanState(MainCleanNewView.CleanState cleanState){

    }

    /**
     * 开始动画
     * @param junkFileSize
     */
    public void startAnimation(float junkFileSize){
        float level = junkFileSize/junkFileSizeMax;
        if(level >= 1f){
            levelProgress = 1f;
        }else{
            levelProgress = junkFileSize/junkFileSizeMax;
        }
        levelProgress = 1f;

        if(animator!=null && animator.isRunning()){
            animator.cancel();
        }
        cancelBunblesAnim();
        if(delayRunnable!=null){
            removeCallbacks(delayRunnable);
        }

        animator=ValueAnimator.ofFloat(startDegree,endDegree);

        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(0);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentDegree= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                delayRunnable=new Runnable() {
                    @Override
                    public void run() {
                        cancelBunblesAnim();
                    }
                };
                postDelayed(delayRunnable,3000);
            }
        });
        animator.start();
        isStart=true;
        anima((int)(duration/1000)*2);//启动气泡
    }

    private Bubble getRodmBubble() {
        float startRadius=DipToPx.dipToPx(context,3);
        float endRadius=DipToPx.dipToPx(context,7);
        float radios=(float)(Math.random()*(endRadius-startRadius)+startRadius);

        float centerDX = (float) (Math.random() * centerWidth);
        float centerDY = (float) (Math.random() * centerHeight);
        PointF centerPointf = new PointF(INNER_IN_RADIUS + centerDX, INNER_IN_RADIUS + centerDY);
        float startDX = (float) (Math.random() * INNER_IN_RADIUS * 2);
        float startDY = INNER_IN_RADIUS * 2;
        PointF startPointF = new PointF(startDX, startDY);
        float endDX = (float) (Math.random() * INNER_IN_RADIUS * 2);
        float endDY = (float) (Math.random() * (INNER_IN_RADIUS - centerHeight / 2));
        PointF endPointF = new PointF(endDX, endDY);
        return new Bubble(startPointF, centerPointf, endPointF, radios, INNER_IN_RADIUS * 2);
    }

    private void anima(int count) {
        bubbles.clear();
        if (count == 0)
            count = 1;
        for (int i = 0; i < count; i++)
            startBunblesAnim(i * 500);
    }

    private void startBunblesAnim(long delay) {

        final List<Bubble> bubbles1 = new ArrayList<>();
        final int count = 2 + (int) (Math.random() * 3);
        for (int x = 0; x < count; x++) {
            bubbles1.add(getRodmBubble());
            bubbles.addAll(bubbles1);
        }

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f, 2f);
        animator.setDuration(3000);
        animator.setStartDelay(delay);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                boolean isSpeed = false;
                if (value < 1) {
                    value = (3.8f - 1.8f * value) * value * 0.5f;
                } else {
                    value = value - 1;
                    value = (0.4f + 1.6f * value) * value * 0.5f;
                    isSpeed = true;
                }
                for (int x = 0; x < count; x++) {
                    bubbles1.get(x).notifyData(value, isSpeed);
                }
                postInvalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                for (Bubble bubble : bubbles1)
                    bubbles.remove(bubble);
            }
        });
        animator.start();
        if (null == animatorList) {
            animatorList = new CopyOnWriteArrayList<>();
        }
        animatorList.add(animator);
    }

    private void cancelBunblesAnim() {
        if (null != animatorList && animatorList.size() > 0) {
            for (ValueAnimator animator : animatorList) {
                if(animator != null && animator.isRunning()){
                    animator.cancel();
                }
                if (null != animator) {
                    animator.removeAllUpdateListeners();
                    animator.removeAllListeners();
                }
            }
            animatorList.clear();
        }

        if (null != bubbles) {
            bubbles.clear();
        }
    }

    private int getBitmapColor(float progress) {
        if(colorBitmap==null || colorBitmap.isRecycled()){
            colorBitmap = getColorBitmap();
        }
        float x, y=colorBitHeigth/2;
        if(progress<0){
            progress=0;
        }else if(progress>1){
            progress=1;
        }
        x=colorBitWidth*progress*levelProgress;
        // 为了防止越界
        int intX = (int) x;
        int intY = (int) y;
        if (intX >= colorBitmap.getWidth()) {
            intX = colorBitmap.getWidth() - 1;
        }
        if (intY >= colorBitmap.getHeight()) {
            intY = colorBitmap.getHeight() - 1;
        }
        return colorBitmap.getPixel(intX, intY);
    }

    private Bitmap getColorBitmap() {
        Bitmap mColorAreaBmp = null;
        if (mColorAreaBmp == null) {
            int width=colorBitWidth,height=colorBitHeigth;
            Paint leftPaint = new Paint();
            leftPaint.setStrokeWidth(1);
            mColorAreaBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mColorAreaBmp);
            int bitmapWidth = mColorAreaBmp.getWidth();
            int bitmapHeight = mColorAreaBmp.getHeight();
            int[] colors = new int[]{getColor(R.color.colorList1), getColor(R.color.colorList2), getColor(R.color.colorList3), getColor(R.color.colorList4), getColor(R.color.colorList5)};
            Shader leftShader = new LinearGradient(0, bitmapHeight / 2, bitmapWidth, bitmapHeight / 2, colors, null, Shader.TileMode.CLAMP);
            ComposeShader shader = new ComposeShader(leftShader, leftShader, PorterDuff.Mode.SCREEN);

            leftPaint.setShader(leftShader);
            canvas.drawRect(0, 0, width, height, leftPaint);
        }
        return mColorAreaBmp;
    }

    private int getColor(@ColorRes int color){
        return context.getResources().getColor(color);
    }

    public void destroy(){
        if(animator!=null && animator.isRunning()){
            animator.cancel();
        }
        if(colorBitmap!=null && !colorBitmap.isRecycled()){
            colorBitmap.recycle();
        }
        cancelBunblesAnim();
    }


}

