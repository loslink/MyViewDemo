package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频涂鸦
 * @author loslink
 * @time 2018/12/13 18:27
 */
public class VideoPaintView extends View { 
    private Paint mPaint;
    private Bitmap mDrawBit;
    private Paint mEraserPaint;
    private Canvas mPaintCanvas = null;
    private boolean eraser=false;

    private boolean isTouch = true;
    private int picWidth, picHeight;
    private int canvasWidth, canvasHeight;
    private boolean drawed=false;//是否绘制过
    List<MyPath> pathList=new ArrayList<>();
    // 默认画笔为黑色
    private int currentColor = Color.BLACK;
    // 画笔的粗细
    private int currentSize = 5,eraserSize = 5;
    // 当前所选画笔的形状
    private MyPath curAction = null;

    public VideoPaintView(Context context) {
        super(context);
        init(context);
    }

    public VideoPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoPaintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth = w;
        canvasHeight = h;

        if(picWidth == 0 && picHeight == 0){
            picWidth=canvasWidth;
            picHeight=canvasHeight;
            generatorBit();
        }
    }

    public boolean isDrawed() {
        return drawed;
    }

    public void setDrawed(boolean drawed) {
        this.drawed = drawed;
    }


    private void generatorBit() {
        mDrawBit = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);
        mPaintCanvas = new Canvas(mDrawBit);
    }

    private void init(Context context) {


        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setColor(currentColor);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEraserPaint.setStrokeWidth(40);

    }

    public void setWidthHeight(final int w, final int h) {

        this.post(new Runnable() {
            @Override
            public void run() {
                float canvas = (float) canvasWidth / (float) canvasHeight;
                float pic = (float) w / (float) h;
                if (canvas >= pic) {
                    picWidth = (int) (pic * canvasHeight);
                    picHeight = canvasHeight;
                } else {
                    picWidth = canvasWidth;
                    picHeight = (int) (canvasWidth / pic);
                }
                if (mDrawBit == null) {
                    generatorBit();
                }
            }
        });

    }

    public void setColor(int color) {
        this.currentColor = color;
        this.mPaint.setColor(currentColor);
    }

    public boolean isTouch() {
        return isTouch;
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }


    public void setWidth(int width) {
        currentSize=width;
        this.mPaint.setStrokeWidth(currentSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawBit != null) {
            float cf = (float) canvasWidth / (float) canvasHeight;
            float pic = (float) picWidth / (float) picHeight;
            float left;
            float top;
            if (cf >= pic) {
                left = (canvasWidth - picWidth) / 2;
                top = 0;
            } else {
                left = 0;
                top = (canvasHeight - picHeight) / 2;
            }
            canvas.drawBitmap(mDrawBit, left, top, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        if (!isTouch) {
            return ret;
        }
        float x = event.getX() - (canvasWidth - picWidth) / 2;
        float y = event.getY() - (canvasHeight - picHeight) / 2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ret = true;
                setCurAction(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if(mPaintCanvas==null){
                    return false;
                }
//                mDrawBit.eraseColor(Color.TRANSPARENT);//清空画布
//                for (BaseAction baseAction : pathList) {
//                    baseAction.draw(mPaintCanvas);
//                }
                curAction.move(x, y);
                curAction.draw(mPaintCanvas);
                this.postInvalidate();
                drawed=true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                pathList.add(curAction);
                ret = false;
                break;
        }
        return ret;
    }

    /**
     * 重新绘制
     */
    private void reDraw(){
        mDrawBit.eraseColor(Color.TRANSPARENT);//清空画布
        for (BaseAction baseAction : pathList) {
            baseAction.draw(mPaintCanvas);
        }
        this.postInvalidate();
    }

    /**
     * 返回上一步
     */
    public void backLastStep(){
        if(pathList!=null && pathList.size()>0){
            pathList.remove(pathList.size()-1);
        }
        reDraw();
    }

    /**
     * 得到当前画笔的类型，并进行实例化
     *
     * @param x
     * @param y
     */
    private void setCurAction(float x, float y) {
        if(eraser){
            curAction = new MyPath(x, y, eraserSize, currentColor,true);
        }else{
            curAction = new MyPath(x, y, currentSize, currentColor,false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
        }
    }

    public void setEraser(boolean eraser) {
        this.eraser = eraser;
    }

    public Bitmap getPaintBit() {
        return mDrawBit;
    }

    public void reset() {
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
        }
        generatorBit();
        invalidate();
        drawed=false;
    }
}
