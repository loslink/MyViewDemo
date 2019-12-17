package com.loslink.myview.widget.photoview.runnable;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.OverScroller;

import com.loslink.myview.widget.photoview.Compat;


public class FlingRunnable implements Runnable {

    private final OverScroller mScroller;
    private int mCurrentX, mCurrentY;

    private RectF getDisplayRect;
    private Matrix mSuppMatrix;
    private ImageView mImageView;
    private OnRunListener onRunListener;

    public FlingRunnable(Context context, RectF getDisplayRect, Matrix mSuppMatrix, ImageView mImageView, OnRunListener onRunListener) {
        mScroller = new OverScroller(context);
        this.getDisplayRect = getDisplayRect;
        this.mSuppMatrix = mSuppMatrix;
        this.mImageView = mImageView;
        this.onRunListener = onRunListener;
    }

    public void cancelFling() {
        mScroller.forceFinished(true);
    }

    public void fling(int viewWidth, int viewHeight, int velocityX,
                      int velocityY) {
        final RectF rect = getDisplayRect;
        if (rect == null) {
            return;
        }
        final int startX = Math.round(-rect.left);
        final int minX, maxX, minY, maxY;
        if (viewWidth < rect.width()) {
            minX = 0;
            maxX = Math.round(rect.width() - viewWidth);
        } else {
            minX = maxX = startX;
        }
        final int startY = Math.round(-rect.top);
        if (viewHeight < rect.height()) {
            minY = 0;
            maxY = Math.round(rect.height() - viewHeight);
        } else {
            minY = maxY = startY;
        }
        mCurrentX = startX;
        mCurrentY = startY;
        // If we actually can move, fling the scroller
        if (startX != maxX || startY != maxY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
        }
    }

    @Override
    public void run() {
        if (mScroller.isFinished()) {
            return; // remaining post that should not be handled
        }
        if (mScroller.computeScrollOffset()) {
            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();
            mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
            if (onRunListener != null){
                onRunListener.checkAndDisplayMatrix();
            }
            mCurrentX = newX;
            mCurrentY = newY;
            // Post On animation
            Compat.postOnAnimation(mImageView, this);
        }
    }

    public interface OnRunListener{
        void checkAndDisplayMatrix();
    }
}
