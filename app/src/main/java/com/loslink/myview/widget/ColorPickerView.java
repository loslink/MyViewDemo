package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 颜色选择器控件
 */
public class ColorPickerView extends View {

    private Context mContext;                   /* 上下文 */
    private Paint mRightPaint;                  /* 右边的画笔 */
    private Paint mBitmapPaint;                 /* 通用画笔 */

    private Rect mLeftRect;                     /* 左边的图像 */
    private int mHeight;                        /* 总体高度 */
    private int mWidth;                         /* 总体宽度 */
    private int mPaddingLeft;                   /* 左内边距 */
    private int mPaddingTop;                    /* 右内边距 */
    private int mPaddingRight;                  /* 上内边距 */
    private int mPaddingBottom;                 /* 下内边距 */
    private int[] mRightColors;                 /* 右边颜色渐变的数组 */
    private int mLeftWidth;                     /* 左边选色区的宽度 */
    private Bitmap mColorAreaBmp;               /* 左边选色区的图像 */

    private PointF mLeftSelectPoint;            /* 左边触屏选中的点 */
    private boolean downInLeft = false;         /* 点击区域在选色区， true -- 是 */
    private boolean downInRight = false;        /* 点击区域在亮度调节区， true -- 是 */
    private boolean mLeftMove = false;          /* 左边的图标移动标记，true -- 可移动 */
    private boolean mRightMove = false;         /* 右边的图标移动标记，true -- 可移动 */
    private int mLastColor;                     /* 上次获取到的颜色 */
    private int padding=0;

    private OnColorLis mChangedListener;        /* 获取的颜色值改变监听 */

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setOnColorLis(OnColorLis listener) {
        mChangedListener = listener;
    }

    /**
     * 数据初始化
     */
    private void init() {
        mPaddingLeft = padding + getPaddingLeft();
        mPaddingTop = padding + getPaddingTop();
        mPaddingRight = padding + getPaddingRight();
        mPaddingBottom = padding + getPaddingBottom();

        mRightPaint = new Paint();
        mRightPaint.setStyle(Paint.Style.FILL);
        mRightPaint.setStrokeWidth(1);

        mRightColors = new int[3];
        mRightColors[0] = Color.WHITE;
        mRightColors[2] = Color.BLACK;

        mBitmapPaint = new Paint();

        mLeftSelectPoint = new PointF(mPaddingLeft, mPaddingTop);

    }

    /**
     * 图像绘制
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 左边
        if (null == mLeftRect) {
            mLeftRect = new Rect(mPaddingLeft, mPaddingTop, mLeftWidth, mHeight - mPaddingBottom);
        }
        canvas.drawBitmap(getGradual(), null, mLeftRect, mBitmapPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = 960;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = 540;
        }

        mLeftWidth = mWidth - mPaddingLeft;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downInLeft = inLeftPanel(x, y);
                downInRight = inRightPanel(x, y);
                getParent().requestDisallowInterceptTouchEvent(true);       /* 不允许父控件阻止touch事件 */
                performClick();
            case MotionEvent.ACTION_MOVE:
                if (downInLeft) {
                    mLeftMove = true;
                    proofLeft(x, y);
                    mRightColors[1] = getLeftColor(mLeftSelectPoint.x, mLeftSelectPoint.y);
                } else if (downInRight) {
                    mRightMove = true;
                }
                invalidate();
                int rightColor = mRightColors[1];
                if (mLastColor == rightColor) {
                    break;
                }
                mLastColor = rightColor;
                break;
            case MotionEvent.ACTION_UP:
                downInLeft = false;
                downInRight = false;
                mLeftMove = false;
                mRightMove = false;
                mRightPaint.setColor(mLastColor);
                if (mChangedListener != null) {
                    int color = mRightPaint.getColor();
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    int colorInt = r << 16 | g << 8 | b;
                    String colorStr = String.format("#%02x%02x%02x", r, g, b);
                    mChangedListener.onColorChanged(color, colorStr);
                }
                getParent().requestDisallowInterceptTouchEvent(false);       /* 允许父控件阻止touch事件 */
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mColorAreaBmp != null && !mColorAreaBmp.isRecycled()) {
            mColorAreaBmp.recycle();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * 绘制左边的颜色选择区
     *
     * @return bitmap图像
     */
    private Bitmap getGradual() {
        if (mColorAreaBmp == null) {
            Paint leftPaint = new Paint();
            leftPaint.setStrokeWidth(1);
            mColorAreaBmp = Bitmap.createBitmap(mLeftWidth, mHeight - mPaddingTop - mPaddingBottom, Config.ARGB_8888);
            Canvas canvas = new Canvas(mColorAreaBmp);
            int bitmapWidth = mColorAreaBmp.getWidth();
            mLeftWidth = bitmapWidth;
            int bitmapHeight = mColorAreaBmp.getHeight();
            int[] leftColors = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA};
            Shader leftShader = new LinearGradient(0, bitmapHeight / 2, bitmapWidth, bitmapHeight / 2, leftColors, null, TileMode.REPEAT);
            LinearGradient shadowShader = new LinearGradient(bitmapWidth / 2, 0, bitmapWidth / 2, bitmapHeight,
                    Color.WHITE, Color.BLACK, TileMode.CLAMP);
            ComposeShader shader = new ComposeShader(leftShader, leftShader, PorterDuff.Mode.SCREEN);
            leftPaint.setShader(shader);
            canvas.drawRect(0, 0, bitmapWidth, bitmapHeight, leftPaint);
        }
        return mColorAreaBmp;
    }

    /**
     * 判断选中的点是否在左边的颜色选择区
     *
     * @param x 选中点的x坐标
     * @param y 选中点的y坐标
     * @return true -- 是 ， false -- 不是
     */
    private boolean inLeftPanel(float x, float y) {
        return mPaddingLeft < x && x < mPaddingLeft + mLeftWidth && mPaddingTop < y && y < mHeight - mPaddingBottom;
    }

    /**
     * 判断选中的点是否在右边的亮度调节区
     *
     * @param x 选中点的x坐标
     * @param y 选中点的y坐标
     * @return true -- 是 ， false -- 不是
     */
    private boolean inRightPanel(float x, float y) {
        return mPaddingLeft + mLeftWidth < x && x < mWidth - mPaddingRight && mPaddingTop < y && y < mHeight - mPaddingBottom;
    }

    /**
     * 矫正左边颜色选择器区域选中的x，y
     *
     * @param x 按下的坐标x
     * @param y 按下的坐标y
     */
    private void proofLeft(float x, float y) {
        if (x < mPaddingLeft) {
            mLeftSelectPoint.x = mPaddingLeft;
        } else if (x > mLeftWidth) {
            mLeftSelectPoint.x = mLeftWidth;
        } else {
            mLeftSelectPoint.x = x;
        }
        if (y < mPaddingTop) {
            mLeftSelectPoint.y = mPaddingTop;
        } else if (y > (mHeight - mPaddingBottom)) {
            mLeftSelectPoint.y = mHeight - mPaddingBottom;
        } else {
            mLeftSelectPoint.y = y;
        }
    }

    /**
     * 获取该坐标在左边选色区的颜色
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return 颜色
     */
    private int getLeftColor(float x, float y) {
        Bitmap temp = getGradual();
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

    /**
     * 颜色值回调监听
     */
    public interface OnColorLis {
        void onColorChanged(int color, String colorStr);
    }
}
