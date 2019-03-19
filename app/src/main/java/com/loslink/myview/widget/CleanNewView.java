package com.loslink.myview.widget;

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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.loslink.myview.utils.DipToPx;

public class CleanNewView extends View {

    private Paint itemPaint;
    private long duration=3000;
    private Context context;
    private float canvasWidth, canvasHeight;
    private float OUTER_RADIUS = 133;
    private float BAR_WIDTH = 18;
    private float BAR_HEIGHT = 5;
    private float startDegree=-215, endDegree=35f;
    private float currentDegree=startDegree;
    private float itemDegree=5f;
    private ValueAnimator animator;
    private RectF itemRectF;
    private Path itemPath;
    private int colorBitWidth=100,colorBitHeigth=10;

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
        itemPaint = new Paint();
        itemPaint.setAntiAlias(true);
        itemPaint.setColor(Color.parseColor("#c8c8c8"));
        itemPaint.setStyle(Paint.Style.FILL);

        OUTER_RADIUS = DipToPx.dipToPx(context, OUTER_RADIUS);
        BAR_WIDTH = DipToPx.dipToPx(context, BAR_WIDTH);
        BAR_HEIGHT = DipToPx.dipToPx(context, BAR_HEIGHT);

        itemRectF = new RectF(OUTER_RADIUS - BAR_WIDTH,
                -BAR_HEIGHT / 2,
                OUTER_RADIUS,
                BAR_HEIGHT / 2);
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

        canvas.save();
        canvas.drawColor(Color.parseColor("#f0f0f0"));
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.rotate(startDegree);
        float lastDegree=startDegree;
        itemPath.addRoundRect(itemRectF, BAR_HEIGHT / 2, BAR_HEIGHT / 2, Path.Direction.CCW);
        while (true) {
            if(lastDegree > endDegree){
                break;
            }
            if(lastDegree<=currentDegree){
                float progress=(lastDegree-startDegree)/(endDegree-startDegree);
                itemPaint.setColor(getBitmapColor(progress));
            }else{
                itemPaint.setColor(Color.parseColor("#c8c8c8"));
            }
            canvas.drawPath(itemPath, itemPaint);

            canvas.rotate(itemDegree);
            lastDegree = lastDegree+itemDegree;
        }
        canvas.restore();

    }


    public void startAnimation(){
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

        animator.start();

    }

    private int getBitmapColor(float progress) {
        Bitmap temp = getColorBitmap();
        float x, y=colorBitHeigth/2;
        if(progress<0){
            progress=0;
        }else if(progress>1){
            progress=1;
        }
        x=colorBitWidth*progress;
        // 为了防止越界
        int intX = (int) x;
        int intY = (int) y;
        if (intX >= temp.getWidth()) {
            intX = temp.getWidth() - 1;
        }
        if (intY >= temp.getHeight()) {
            intY = temp.getHeight() - 1;
        }
        return temp.getPixel(intX, intY);
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
            int[] colors = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA};
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
    }
}

