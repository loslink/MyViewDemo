package com.loslink.myview.widget.photoview.runnable;

import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.loslink.myview.widget.photoview.Compat;
import com.loslink.myview.widget.photoview.listener.OnGestureListener;


/**
 * 缩放动画的执行
 */
public class AnimatedZoomRunnable implements Runnable {

    private final float mFocalX, mFocalY;//焦点
    private final long mStartTime;//开始时间
    private final float mZoomStart, mZoomEnd;

    private Interpolator mInterpolator;
    private float getScale;
    private OnGestureListener onGestureListener;
    private ImageView mImageView;
    private int mZoomDuration;

    public AnimatedZoomRunnable(final float currentZoom, final float targetZoom, final float focalX, final float focalY, Interpolator mInterpolator, float getScale, OnGestureListener onGestureListener, ImageView mImageView, int mZoomDuration) {
        mFocalX = focalX;
        mFocalY = focalY;
        mStartTime = System.currentTimeMillis();
        mZoomStart = currentZoom;
        mZoomEnd = targetZoom;
        this.mInterpolator = mInterpolator;
        this.getScale = getScale;
        this.onGestureListener = onGestureListener;
        this.mImageView = mImageView;
        this.mZoomDuration = mZoomDuration;
    }

    @Override
    public void run() {
        float t = interpolate();
        float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
        float deltaScale = scale / getScale;
        onGestureListener.onScale(deltaScale, mFocalX, mFocalY);
        // We haven't hit our target scale yet, so post ourselves again
        if (t < 1f) {
            Compat.postOnAnimation(mImageView, this);
        }
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
        t = Math.min(1f, t);
        t = mInterpolator.getInterpolation(t);
        return t;
    }
}