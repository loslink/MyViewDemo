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
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.loslink.myview.R;
import com.loslink.myview.utils.DipToPx;

public class CleanNewView extends View {

    private Paint itemPaint,circleWhitePaint,circleInnerPaint;
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

        itemPaint = new Paint();
        itemPaint.setAntiAlias(true);
        itemPaint.setStyle(Paint.Style.FILL);

        circleWhitePaint = new Paint();
        circleWhitePaint.setAntiAlias(true);
        circleWhitePaint.setColor(Color.WHITE);
        circleWhitePaint.setStyle(Paint.Style.FILL);
        circleWhitePaint.setShadowLayer(10,6f,6f,Color.parseColor("#11000000"));

        circleInnerPaint = new Paint();
        circleInnerPaint.setAntiAlias(true);
        circleInnerPaint.setColor(Color.RED);
        circleInnerPaint.setStyle(Paint.Style.FILL);

        itemPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth = w;
        canvasHeight = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);//对单独的View在运行时阶段禁用硬件加速,阴影才有效
        canvas.drawColor(getColor(R.color.cleanViewBgColor));
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);

        drawOuterItems(canvas);

        canvas.drawCircle(0,0,INNER_OUT_RADIUS,circleWhitePaint);

        canvas.drawCircle(0,0,INNER_IN_RADIUS,circleInnerPaint);
    }

    /**
     * 绘制外圈小块
     * @param canvas
     */
    private void drawOuterItems(Canvas canvas){
        canvas.save();
        canvas.rotate(startDegree);
        float lastDegree=startDegree;
        itemPath.addRoundRect(itemRectF, BAR_HEIGHT / 2, BAR_HEIGHT / 2, Path.Direction.CCW);//耗性能
//        itemPaint.setShadowLayer(3f,20f,2f,Color.GRAY);
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



    public void startAnimation(float junkFileSize){
        float level = junkFileSize/junkFileSizeMax;
        if(level >= 1f){
            levelProgress = 1f;
        }else{
            levelProgress = junkFileSize/junkFileSizeMax;
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
            }
        });
        animator.start();
        isStart=true;
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
            Shader leftShader = new LinearGradient(0, bitmapHeight / 2, bitmapWidth, bitmapHeight / 2, colors, null, Shader.TileMode.REPEAT);
            ComposeShader shader = new ComposeShader(leftShader, leftShader, PorterDuff.Mode.SCREEN);

            leftPaint.setShader(shader);
            canvas.drawRect(0, 0, width, height, leftPaint);
        }
        return mColorAreaBmp;
    }

    public void destroy(){
        if(animator!=null && animator.isRunning()){
            animator.cancel();
        }
        if(colorBitmap!=null && !colorBitmap.isRecycled()){
            colorBitmap.recycle();
        }
    }

    private int getColor(@ColorRes int color){
        return context.getResources().getColor(color);
    }
}

