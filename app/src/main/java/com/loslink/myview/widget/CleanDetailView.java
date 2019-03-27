package com.loslink.myview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.loslink.myview.R;
import com.loslink.myview.utils.DipToPx;

public class CleanDetailView extends View {

    private Paint itemPaint;
    private long duration = 1000;
    private Context context;
    private float canvasWidth, canvasHeight;
    private float OUTER_RADIUS = 70;
    private float BAR_WIDTH = 9;
    private float BAR_HEIGHT = 2;
    private float startDegree = 0, endDegree = 360f;
    private float currentDegree = startDegree;
    private float currentDegree2 = startDegree;
    private float itemDegree = 5f;
    private ValueAnimator animator, repeatAnimator;
    private RectF itemRectF;
    private Path itemPath;
    private float levelProgress = 0f;//垃圾级别
    private boolean isStart = false;
    private float centerWidth = 100, centerHeight = 100;
    private CleanDetailState currentCleanState;
    private float junkFileSize;
    private int blueItemCount = 6;

    public CleanDetailView(Context context) {
        this(context, null);
    }

    public CleanDetailView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CleanDetailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void init() {

        OUTER_RADIUS = DipToPx.dipToPx(context, OUTER_RADIUS);
        BAR_WIDTH = DipToPx.dipToPx(context, BAR_WIDTH);
        BAR_HEIGHT = DipToPx.dipToPx(context, BAR_HEIGHT);
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
            if (lastDegree > endDegree ) {
                break;
            }
            switch (currentCleanState) {
                case Checking:
                    int currentItem = (int) ((currentDegree - startDegree) / itemDegree);
                    int startCount = (int) ((currentDegree - endDegree) / itemDegree);
                    if((currentDegree > endDegree && itemIndex < startCount)
                            || (itemIndex >= (currentItem - blueItemCount) && itemIndex <= currentItem )){
                        itemPaint.setColor(Color.parseColor("#6482fc"));
                    } else {
                        itemPaint.setColor(getColor(R.color.cleanItemDetailLightColor));
                    }
                    break;
                case CheckFinish:
                    int currentItem2 = (int) ((currentDegree - startDegree) / itemDegree);//停止的item
                    int startCount2 = (int) ((currentDegree - endDegree) / itemDegree);
                    float current=currentDegree%endDegree;//停止的位置角度
                    if((currentDegree > endDegree && itemIndex < startCount2)
                            || (itemIndex >= (currentItem2 - blueItemCount) && itemIndex <= currentItem2)
                            || ((lastDegree <= current + currentDegree2) && lastDegree >= current)
                            || (currentDegree2 > (endDegree-current) && lastDegree<(currentDegree2-(endDegree-current)))){
                        itemPaint.setColor(Color.parseColor("#6482fc"));
                    }else {
                        itemPaint.setColor(getColor(R.color.cleanItemDetailLightColor));
                    }
                    break;
            }
            canvas.drawPath(itemPath, itemPaint);

            canvas.rotate(itemDegree);
            lastDegree = lastDegree + itemDegree;
            itemIndex++;
        }
        canvas.restore();
    }

    public void setCleanState(CleanDetailState cleanState) {
        currentCleanState = cleanState;
        switch (cleanState) {
            case Checking:
                levelProgress = 1f;
                break;
            case CheckFinish:
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

        if (currentCleanState == CleanDetailState.Checking) {
            startRepeatAnimation();
            return;
        }

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(0, endDegree-(itemDegree * blueItemCount));

        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(0);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentDegree2 = (float) animation.getAnimatedValue();
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
        isStart = true;
    }

    public void startRepeatAnimation() {

        if (repeatAnimator != null && repeatAnimator.isRunning()) {
            repeatAnimator.cancel();
        }

        float length=itemDegree * blueItemCount;
        repeatAnimator = ValueAnimator.ofFloat(startDegree+length, endDegree+ length);

        final long duration = 1000;
        repeatAnimator.setDuration(duration);
        repeatAnimator.setInterpolator(new LinearInterpolator());
        repeatAnimator.setRepeatCount(ValueAnimator.INFINITE);
        repeatAnimator.setRepeatMode(ValueAnimator.RESTART);
        repeatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentDegree = (float) animation.getAnimatedValue();
                postInvalidate();
                if (currentCleanState == CleanDetailState.CheckFinish) {
                    postInvalidate();
                    repeatAnimator.cancel();
                    startAnimation();
                }
            }
        });
        repeatAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }
        });
        repeatAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        repeatAnimator.start();
        isStart = true;
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
    }

    public enum CleanDetailState {
        None,//错误状态
        Checking,//检查垃圾
        CheckFinish//垃圾检查完成
    }

}

