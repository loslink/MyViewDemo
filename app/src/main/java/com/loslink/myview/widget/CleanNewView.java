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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
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

    private Paint itemPaint, circleWhitePaint, circleInnerPaint, bunblePaint;
    private long duration = 3000;
    private Context context;
    private float canvasWidth, canvasHeight;
    private float OUTER_RADIUS = 133;
    private float BAR_WIDTH = 18;
    private float BAR_HEIGHT = 5;
    private float INNER_OUT_RADIUS = 90;
    private float INNER_IN_RADIUS = 80;
    private float startDegree = -215, endDegree = 35f;
    private float currentDegree = startDegree;
    private float itemDegree = 5f;
    private ValueAnimator animator, repeatAnimator;
    private RectF itemRectF;
    private Path itemPath;
    private int colorBitWidth = 100, colorBitHeigth = 10;
    private Bitmap colorBitmap, startColorBitmap;
    private float levelProgress = 0f;//垃圾级别
    private float junkFileSizeMax = 400f;//垃圾分界最高
    private boolean isStart = false;
    private Runnable delayRunnable;
    private List<Bubble> bubbles = new ArrayList<>();
    private float centerWidth = 100, centerHeight = 100;
    private List<ValueAnimator> animatorList;
    private MainCleanNewView.CleanState currentCleanState;
    private float junkFileSize;
    private int blueItemCount = 6;
    private Matrix shadowMatrix;
    private Bitmap shadowBitmap;

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
        itemPaint.setShadowLayer(4f, 2f, 2f, Color.parseColor("#22000000"));

        circleWhitePaint = new Paint();
        circleWhitePaint.setAntiAlias(true);
        circleWhitePaint.setColor(Color.WHITE);
        circleWhitePaint.setStyle(Paint.Style.FILL);
//        circleWhitePaint.setShadowLayer(10,6f,6f,Color.parseColor("#11000000"));//需要关闭硬件加速
//        setLayerType(LAYER_TYPE_SOFTWARE, null);//对单独的View在运行时阶段禁用硬件加速,阴影才有效(不要放onDraw里)

        circleInnerPaint = new Paint();
        circleInnerPaint.setAntiAlias(true);
        circleInnerPaint.setColor(Color.RED);
        circleInnerPaint.setStyle(Paint.Style.FILL);

        bunblePaint = new Paint();
        bunblePaint.setAntiAlias(true);
        bunblePaint.setColor(Color.WHITE);
        bunblePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        shadowMatrix = new Matrix();
        shadowBitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_main_clean_shadow)).getBitmap();
        float resultW = (INNER_OUT_RADIUS + DipToPx.dipToPx(context, 6)) * 2f;
        float par = resultW / (float) shadowBitmap.getWidth();
        shadowMatrix.setScale(par, par);
        shadowMatrix.postTranslate(-resultW / 2, -resultW / 2);

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
     *
     * @param canvas
     */
    private void drowCircle(Canvas canvas) {
        float progress = (currentDegree - startDegree) / (endDegree - startDegree);
        if (currentCleanState == MainCleanNewView.CleanState.Checking) {
            progress = junkFileSize / junkFileSizeMax;
            if (progress > 1f) {
                progress = 1f;
            }
        }

        canvas.drawBitmap(shadowBitmap, shadowMatrix, circleWhitePaint);
        canvas.drawCircle(0, 0, INNER_OUT_RADIUS, circleWhitePaint);
        int pixel = getStartBitmapColor(progress);
        int a = Color.alpha(pixel);
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);
        int startColor = Color.argb(a * 2 / 3, r, g, b);
        LinearGradient linearGradient = new LinearGradient(-INNER_IN_RADIUS, -INNER_IN_RADIUS,
                INNER_IN_RADIUS - DipToPx.dipToPx(context, 80), INNER_IN_RADIUS - DipToPx.dipToPx(context, 80),
                pixel,
                getBitmapColor(progress),
                Shader.TileMode.CLAMP);
        circleInnerPaint.setShader(linearGradient);
        //调用saveLayer时，会生成了一个全新的bitmap，这个bitmap的大小就是我们指定的保存区域的大小，新生成的bitmap是全透明的，在调用saveLayer后所有的绘图操作都是在这个bitmap上进行的。
        int layerID = canvas.saveLayer(-canvasWidth / 2, -canvasHeight / 2, canvasWidth / 2, canvasHeight / 2, circleInnerPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(0, 0, INNER_IN_RADIUS, circleInnerPaint);
        linearGradient = null;
        drawBunbles(canvas);
    }

    /**
     * 绘制小气泡
     *
     * @param canvas
     */
    private void drawBunbles(Canvas canvas) {
        canvas.save();
        canvas.translate(-INNER_IN_RADIUS, -INNER_IN_RADIUS);
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
     *
     * @param canvas
     */
    private void drawOuterItems(Canvas canvas) {
        canvas.save();
        canvas.rotate(startDegree);
        float lastDegree = startDegree;
        int itemIndex = 0;

        while (true) {
            if (lastDegree > endDegree) {
                break;
            }
            switch (currentCleanState) {
                case Analysing:
                    itemPaint.setColor(getColor(R.color.cleanItemGrayColor));
                    break;
                case AnalyseFinish:
                    break;
                case Checking:
                    int currentItem = (int) ((currentDegree - startDegree) / itemDegree);
                    if (itemIndex >= (currentItem - blueItemCount) && itemIndex <= currentItem) {
                        itemPaint.setColor(Color.parseColor("#3c8ef4"));
                    } else {
                        itemPaint.setColor(getColor(R.color.cleanItemGrayColor));
                    }
                    break;
                case CheckFinish:
                    if (isStart && lastDegree <= currentDegree) {
                        float progress = (lastDegree - startDegree) / (endDegree - startDegree);
                        itemPaint.setColor(getBitmapColor(progress));
                    } else {
                        itemPaint.setColor(getColor(R.color.cleanItemGrayColor));
                    }
                    break;
                case BestState:
                    itemPaint.setColor(getColor(R.color.cleanItemGrayColor));
                    break;
            }
            canvas.drawPath(itemPath, itemPaint);

            canvas.rotate(itemDegree);
            lastDegree = lastDegree + itemDegree;
            itemIndex++;
        }
        canvas.restore();
    }

    public void setCleanState(MainCleanNewView.CleanState cleanState) {
        currentCleanState = cleanState;
        switch (cleanState) {
            case Analysing:
                levelProgress = 0.5f;
                break;
            case AnalyseFinish:
                break;
            case Checking:
                levelProgress = 1f;
                break;
            case CheckFinish:
                break;
            case BestState:
                levelProgress = 0f;
                postInvalidate();
                break;
        }
    }

    public void setJunkFileSize(float junkFileSize) {
        this.junkFileSize = junkFileSize;
    }

    /**
     * 开始动画
     */
    public void startAnimation() {

        if (currentCleanState == MainCleanNewView.CleanState.Checking) {
            startRepeatAnimation();
            return;
        }

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        cancelBunblesAnim();
        if (delayRunnable != null) {
            removeCallbacks(delayRunnable);
        }

        animator = ValueAnimator.ofFloat(startDegree, endDegree);

        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(0);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentDegree = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                delayRunnable = new Runnable() {
                    @Override
                    public void run() {
                        cancelBunblesAnim();
                    }
                };
                postDelayed(delayRunnable, 3000);
            }
        });
        animator.start();
        isStart = true;
        anima((int) (duration / 1000) * 2);//启动气泡
    }

    public void startRepeatAnimation() {

        if (repeatAnimator != null && repeatAnimator.isRunning()) {
            repeatAnimator.cancel();
        }

        repeatAnimator = ValueAnimator.ofFloat(startDegree, endDegree + (itemDegree * blueItemCount));

        final long duration = 1500;
        repeatAnimator.setDuration(duration);
        repeatAnimator.setInterpolator(new LinearInterpolator());
        repeatAnimator.setRepeatCount(ValueAnimator.INFINITE);
        repeatAnimator.setRepeatMode(ValueAnimator.RESTART);
        repeatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentDegree = (float) animation.getAnimatedValue();
                postInvalidate();
                if (currentCleanState == MainCleanNewView.CleanState.CheckFinish) {
                    repeatAnimator.cancel();
                    startAnimation();
                }
            }
        });
        repeatAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                for (int i = 0; i < duration / 500; i++) {
                    startBunblesAnim(i * 500);
                }
            }
        });
        repeatAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        for (int i = 0; i < duration / 500; i++) {
            startBunblesAnim(i * 500);
        }
        repeatAnimator.start();
        isStart = true;
    }

    private Bubble getRodmBubble() {
        float startRadius = DipToPx.dipToPx(context, 3);
        float endRadius = DipToPx.dipToPx(context, 7);
        float radios = (float) (Math.random() * (endRadius - startRadius) + startRadius);

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
                if (animator != null && animator.isRunning()) {
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
        if (colorBitmap == null || colorBitmap.isRecycled()) {
            int[] colors = new int[]{getColor(R.color.colorList1), getColor(R.color.colorList2), getColor(R.color.colorList3), getColor(R.color.colorList4), getColor(R.color.colorList5)};
            colorBitmap = getColorBitmap(colors);
        }
        float x, y = colorBitHeigth / 2;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        x = colorBitWidth * progress * levelProgress;
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

    private int getStartBitmapColor(float progress) {
        if (startColorBitmap == null || startColorBitmap.isRecycled()) {
            int[] colors = new int[]{getColor(R.color.startColorList1), getColor(R.color.startColorList2), getColor(R.color.startColorList3), getColor(R.color.startColorList4), getColor(R.color.startColorList5)};
            startColorBitmap = getColorBitmap(colors);
        }
        float x, y = colorBitHeigth / 2;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        x = colorBitWidth * progress * levelProgress;
        // 为了防止越界
        int intX = (int) x;
        int intY = (int) y;
        if (intX >= startColorBitmap.getWidth()) {
            intX = startColorBitmap.getWidth() - 1;
        }
        if (intY >= startColorBitmap.getHeight()) {
            intY = startColorBitmap.getHeight() - 1;
        }
        return startColorBitmap.getPixel(intX, intY);
    }

    private Bitmap getColorBitmap(int[] colors) {
        Bitmap mColorAreaBmp = null;
        int width = colorBitWidth, height = colorBitHeigth;
        Paint leftPaint = new Paint();
        leftPaint.setStrokeWidth(1);
        mColorAreaBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mColorAreaBmp);
        int bitmapWidth = mColorAreaBmp.getWidth();
        int bitmapHeight = mColorAreaBmp.getHeight();
        Shader leftShader = new LinearGradient(0, bitmapHeight / 2, bitmapWidth, bitmapHeight / 2, colors, null, Shader.TileMode.CLAMP);
        ComposeShader shader = new ComposeShader(leftShader, leftShader, PorterDuff.Mode.SCREEN);

        leftPaint.setShader(leftShader);
        canvas.drawRect(0, 0, width, height, leftPaint);
        return mColorAreaBmp;
    }

    private int getColor(@ColorRes int color) {
        return context.getResources().getColor(color);
    }

    public void destroy() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        if (repeatAnimator != null && repeatAnimator.isRunning()) {
            repeatAnimator.cancel();
        }
        if (colorBitmap != null && !colorBitmap.isRecycled()) {
            colorBitmap.recycle();
        }
        if (startColorBitmap != null && !startColorBitmap.isRecycled()) {
            startColorBitmap.recycle();
        }
        cancelBunblesAnim();
    }


}

