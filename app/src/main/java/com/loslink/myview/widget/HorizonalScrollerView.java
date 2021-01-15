package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.core.content.ContextCompat;

import com.loslink.myview.R;
import com.loslink.myview.utils.GbLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 横向移动列表
 * @time: 2021/1/13
 * @author: Loslink
 */
public class HorizonalScrollerView extends View {

    private final int BLOCK_LENGTH = 200;
    private final int BLOCK_COUNT = 3000;
    private final int BLOCK_GAP = 0;
    private final int BLOCK_SHOW_COUNT_CAPACITY = 6;
    private Scroller mScroller;
    private Context context;
    private Paint paint;
    private float mXDown;
    private float mXLastMove;
    private float mXMove;
    //速度检测器，根据触摸来计算该滑行速度
    private VelocityTracker velocityTracker;
    private int mMaxFlintVelocity, mMinFlintVelocity;
    private int viewWidth,viewHeight,contentWidth;
    private int hideCount;
    private int showCountPerPage;
    private List<DataBean> originDataList = new ArrayList<>();
    private List<DataBean> showDataList = new ArrayList<>();

    public HorizonalScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init(){
        mScroller = new Scroller(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mMaxFlintVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinFlintVelocity = viewConfiguration.getScaledMinimumFlingVelocity();

        for(int i=0;i < BLOCK_COUNT;i++){
            originDataList.add(new DataBean());
        }

        contentWidth = BLOCK_COUNT * (BLOCK_LENGTH + BLOCK_GAP);

        for(int i=0;i<BLOCK_SHOW_COUNT_CAPACITY;i++){
            DataBean bean = new DataBean();
            bean.bitmap = loadBitmap();
            bean.index = i;
            showDataList.add(bean);
        }
    }


    private void handleQueue(){
        int scrollX = getScrollX();
        //前面隐藏的个数
        hideCount = Math.abs(scrollX) / BLOCK_LENGTH;
        showCountPerPage = (int)(getWidth() / (float)BLOCK_LENGTH + 0.5f);
//        GbLog.d("HorizonalScrollerView hideCount:"+hideCount + ",showCountPerPage:"+showCountPerPage);

        int showStartIndex = hideCount - (int)((BLOCK_SHOW_COUNT_CAPACITY - showCountPerPage) / 2 + 0.5f);
        if(showStartIndex < 0){
            showStartIndex = 0;
        }
        int showEndIndex = BLOCK_SHOW_COUNT_CAPACITY + showStartIndex - 1;
        if(showEndIndex > showDataList.size() - 1){
            showEndIndex = showDataList.size() - 1;
        }
//        GbLog.d("HorizonalScrollerView showStartIndex:"+showStartIndex + ",showEndIndex:"+showEndIndex);

        //队列中数据的下标范围
        int queueStartIndex = showDataList.get(0).index;
        int queueEndIndex = showDataList.get(showDataList.size()-1).index;

        int recycleCount = 0;
        //是否回收前面的，否则回收后面的
        boolean isRecyclePre = false;
        //手指向后滑动
        if(queueStartIndex > showStartIndex){
            //回收后面个数
            recycleCount = queueStartIndex - showStartIndex;
            isRecyclePre = false;
        }else{//手指向前滑动（内容向左滑行）
            //回收前面个数
            recycleCount = showStartIndex - queueStartIndex;
            isRecyclePre = true;
        }

        if(recycleCount > 0){

            List<DataBean> tempList = new ArrayList<>();
            //内容向左滑行,回收左边
            if(isRecyclePre){
                for(int i = 0;i<showDataList.size();i++){
                    //处理左边回收的数据
                    if(i < recycleCount){
                        DataBean dataBean = showDataList.get(i);
                        tempList.add(dataBean);

                        //需要填充到回收位置的下标数据
                        int leftIndex = i + recycleCount;
                        //只需要填充满回收坑位
                        if(leftIndex < showDataList.size()){
                            showDataList.set(i,showDataList.get(leftIndex));
                        }else{
                            DataBean dataBeanTemp = tempList.get(leftIndex - showDataList.size());
                            dataBeanTemp.bitmap.recycle();
                            dataBeanTemp.bitmap = loadBitmap();
                            dataBeanTemp.index = showDataList.get(i-1).index + 1;
                            showDataList.set(i,dataBeanTemp);
                        }
                    }else{
                        int index = i + recycleCount;
                        if(index < showDataList.size()){
                            showDataList.set(i,showDataList.get(index));
                        }else {//回收的放后面
                            DataBean dataBean = tempList.get(index - showDataList.size());
                            dataBean.bitmap.recycle();
                            dataBean.bitmap = loadBitmap();
                            dataBean.index = showDataList.get(i-1).index + 1;
                            showDataList.set(i,dataBean);
                        }
                    }
                }
            }else{//内容向右滑行，回收右边
                for(int i = 0;i<showDataList.size();i++){
                    DataBean dataBean = showDataList.get(i);
                    tempList.add(dataBean);

                    //把后面回收的插在前面
                    if(i<recycleCount){
                        int index = i + (showDataList.size() - recycleCount);
                        DataBean recycleData = showDataList.get(index);
                        recycleData.bitmap.recycle();
                        recycleData.bitmap = loadBitmap();
                        recycleData.index = dataBean.index - recycleCount;
                        showDataList.set(i,recycleData);
                    }else{//剩余部分右移
                        DataBean dataBeanTemp = tempList.get(i - recycleCount);
                        showDataList.set(i,dataBeanTemp);
                    }
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        handleQueue();

        for(int i = 0;i < BLOCK_SHOW_COUNT_CAPACITY; i++){
            DataBean dataBean = showDataList.get(i);
            int realIndex = dataBean.index;
            int left = realIndex * BLOCK_LENGTH + (realIndex + 1) * BLOCK_GAP;
            int right = (realIndex + 1) * (BLOCK_LENGTH + BLOCK_GAP);
            Rect rect = new Rect(left,0,right,BLOCK_LENGTH);
            canvas.drawRect(rect,paint);

            if(dataBean != null && !dataBean.bitmap.isRecycled()){
                Bitmap bitmap = dataBean.bitmap;
                Rect src=new Rect(0,0,bitmap.getHeight(),bitmap.getHeight());
                canvas.drawBitmap(bitmap,src,rect,paint);
            }
            GbLog.d("HorizonalScrollerView index:"+dataBean.index);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = event.getRawX();
                mXLastMove = mXDown;
                //滑动过程中，点击滑动停止
                mScroller.forceFinished(true);
                return true;
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);
                scrollBy(scrolledX, 0);
                mXLastMove = mXMove;
                GbLog.d("scrolledX:"+scrolledX);
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起，计算当前速率
                float mXUp = event.getX();
                //单位为1000毫秒
                velocityTracker.computeCurrentVelocity(1000, mMaxFlintVelocity);
                //1000毫秒走得总路程像素
                int xVelocity = (int) velocityTracker.getXVelocity();
                //scrollTo/scrollBy导致滑动后的坐标
                int scrollX = getScrollX();
                GbLog.d("mMinFlintVelocity=" + mMinFlintVelocity + ",xVelocity=" + xVelocity);
                if (Math.abs(xVelocity) > mMinFlintVelocity) {
                    int flingMaxX = contentWidth - getWidth();
                    //从scrollX点开始以每秒xVelocity像素的速度滑行，允许最远的滑行位置为flingMaxX像素（非单次滑行）
                    mScroller.fling(scrollX, 0, -xVelocity, 0, 0,flingMaxX , 0, 0);
                    GbLog.d("mXUp=" + mXUp + "scrollX=" + scrollX + ", startX=" + mScroller.getStartX() + ", flingMaxX=" + flingMaxX);

                    awakenScrollBars(mScroller.getDuration());
                    invalidate();
                }
                if(scrollX < 0 ){
                    //向左移动回原点
                    mScroller.startScroll(scrollX,0,-scrollX,0);
                    invalidate();
                }else if(scrollX >  contentWidth - getWidth()){
                    int scrollStep = contentWidth - getWidth() - scrollX;
                    //向右移动到末尾
                    mScroller.startScroll(scrollX,0,scrollStep,0);
                    invalidate();
                }
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            //滚动至mScroller计算出来该到的位置
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private Bitmap loadBitmap(){
        final BitmapFactory.Options optionsOut = new BitmapFactory.Options();
        optionsOut.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.girl,optionsOut);
        int scale = 1;
        if (optionsOut.outWidth > BLOCK_LENGTH) {
            scale = (int)((float)optionsOut.outWidth/BLOCK_LENGTH);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.test,options);

        return bmp;
    }

    class DataBean implements Comparable<DataBean>{
        public Bitmap bitmap;
        public int index;

        @Override
        public int compareTo(DataBean o) {
            if (this.index > o.index) {
                return 1;
            }
            return -1;
        }
    }

}

