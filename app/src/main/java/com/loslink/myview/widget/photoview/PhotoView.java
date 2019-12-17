/*
 Copyright 2011, 2012 Chris Banes.
 <p>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p>
 http://www.apache.org/licenses/LICENSE-2.0
 <p>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.loslink.myview.widget.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

import com.loslink.myview.widget.photoview.listener.OnMatrixChangedListener;
import com.loslink.myview.widget.photoview.listener2.BzlDispatchTouchEventListener;
import com.loslink.myview.widget.photoview.listener2.BzlDragListener;
import com.loslink.myview.widget.photoview.listener2.BzlPressListener;
import com.loslink.myview.widget.photoview.listener2.BzlRotsalChangedListener;
import com.loslink.myview.widget.photoview.listener2.BzlTapListener;

@SuppressWarnings("unused")
public class PhotoView extends AppCompatImageView {

    private PhotoViewAttacher attacher;
    private ScaleType pendingScaleType;

    private BzlDragListener bzlDragListener;    //拖动，暂未使用
    private BzlPressListener bzlPressListener;  //长按后+旋转
    private BzlRotsalChangedListener bzlRotsalChangedListener;  //缩放+旋转，暂未使用
    private BzlTapListener bzlTapListener;  //单击或者双击，暂未使用
    private BzlDispatchTouchEventListener bzlDispatchTouchEventListener;    //特殊情况处理

    private boolean mAllowDispatchTouchEvent = false;

    public enum Gesture {
        NONE,   //无
        DRAG,   //拖动
        ROTSAL, //缩放+旋转
        PRESS,  //长按后+旋转
        SINGLE_TAP,  //单击
        DOUBLE_TAP, //双击
    }

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    private void init() {
        attacher = new PhotoViewAttacher(this);
        super.setScaleType(ScaleType.MATRIX);
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

//    @Override
//    public void setOnLongClickListener(OnLongClickListener l) {
//        attacher.setOnLongClickListener(l);
//    }
//
//    @Override
//    public void setOnClickListener(OnClickListener l) {
//        attacher.setOnClickListener(l);
//    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType;
        } else {
            attacher.setScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap也是调用此方法。
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    @SuppressWarnings("UnusedReturnValue") public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        attacher.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacher.setOnMatrixChangeListener(listener);
    }

//    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
//        attacher.setOnPhotoTapListener(listener);
//    }

//    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
//        attacher.setOnOutsidePhotoTapListener(listener);
//    }

//    public void setOnViewTapListener(OnViewTapListener listener) {
//        attacher.setOnViewTapListener(listener);
//    }

//    public void setOnViewDragListener(OnViewDragListener listener) {
//        attacher.setOnViewDragListener(listener);
//    }

//    public void setScale(float scale) {
//        attacher.setScale(scale);
//    }
//
//    public void setScale(float scale, boolean animate) {
//        attacher.setScale(scale, animate);
//    }
//
//    public void setScale(float scale, float focalX, float focalY, boolean animate) {
//        attacher.setScale(scale, focalX, focalY, animate);
//    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

//    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
//        attacher.setOnDoubleTapListener(onDoubleTapListener);
//    }

//    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
//        attacher.setOnScaleChangeListener(onScaleChangedListener);
//    }

    float downX, downY;
    PointF downPoint = new PointF();
    PointF initPointer1 = new PointF();
    PointF initPointer2 = new PointF();

    private boolean isToast = false;
    private long downTime = 0;
    private float downMilli = 300;
    private float iniDis = 0;
    long[] vibrateTime = new long[]{300, 100};

    PhotoView.Gesture gesture = PhotoView.Gesture.NONE;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mAllowDispatchTouchEvent){
            return super.dispatchTouchEvent(event);
        }
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                downPoint.set(downX, downY);
                downTime = System.nanoTime();
                gesture = Gesture.NONE;
                isToast = false;
                float sx = (downX - getDisplayRect().left) / getDisplayRect().width();
                float sy = (downY - getDisplayRect().top) / getDisplayRect().height();

                PointF originPoint = Util.getOriginPoint(getImageMatrix(), downX, downY);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (gesture != Gesture.PRESS)
                    gesture = Gesture.ROTSAL;

                initPointer1 = new PointF(event.getX(0), event.getY(0));
                initPointer2 = new PointF(event.getX(1), event.getY(1));
                iniDis = Util.getPointsDis(initPointer1, initPointer2);
                break;
            case MotionEvent.ACTION_MOVE:
                long currTime = System.nanoTime();
                PointF movePoint = new PointF(event.getX(), event.getY());
                PointF currPoint1;
                PointF currPoint2;
                if ((currTime - downTime) / 1e6 > downMilli && gesture == Gesture.NONE)
                    gesture = Gesture.PRESS;
                if ((currTime - downTime) / 1e6 < downMilli && gesture == Gesture.NONE && Util.getPointsDis(downPoint, movePoint) > 0.5)
                    gesture = Gesture.DRAG;

                Matrix matrix = new Matrix();
                getSuppMatrix(matrix);
                switch (gesture){
                    case DRAG:
                        matrix.postTranslate(movePoint.x - downPoint.x, movePoint.y - downPoint.y);
                        downPoint = movePoint;
                        setSuppMatrix(matrix);
                        if (bzlDragListener != null){
                            bzlDragListener.onDrag(downPoint.x, downPoint.y, movePoint.x, movePoint.y);
                        }
                        break;
                    case ROTSAL:
                        currPoint1 = new PointF(event.getX(0), event.getY(0));
                        currPoint2 = new PointF(event.getX(1), event.getY(1));
                        float currDis = Util.getPointsDis(currPoint1, currPoint2);
                        float scale = currDis / iniDis;
                        iniDis = currDis;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        if (Math.abs(values[0]) <= 1 && Math.abs(values[1]) <= 1 && scale < 1)
                            scale = 1;
                        matrix.postScale(scale, scale, (currPoint1.x + currPoint2.x) / 2, (currPoint1.y + currPoint2.y) / 2);

                        float initRadian = Util.getAngleRadian(initPointer1, initPointer2);
                        float currRadian = Util.getAngleRadian(currPoint1, currPoint2);
                        float degreeDiff = (float) Math.toDegrees(currRadian - initRadian);
                        matrix.postRotate(-degreeDiff, (initPointer1.x + initPointer2.x) / 2, (initPointer1.y + initPointer2.y) / 2);
                        initPointer1 = currPoint1;
                        initPointer2 = currPoint2;
                        setSuppMatrix(matrix);
                        if (bzlRotsalChangedListener != null){
                            bzlRotsalChangedListener.onRotsalChange(currPoint1, currPoint2, iniDis, currDis, initRadian, currRadian);
                        }
                        break;
                    case PRESS:
                        float moveX = event.getX();
                        float moveY = event.getY();
                        double angle = Math.toDegrees(Math.atan2((moveY - downY), (moveX - downX))) + 90;
                        if (bzlPressListener != null){
                            return bzlPressListener.onPress(downX, downY, moveX, moveY, (float) angle, isToast);
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (gesture != Gesture.PRESS)
                    gesture = Gesture.DRAG;
                PointF stayPoint = new PointF(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()));
                currPoint1 = new PointF(event.getX(0), event.getY(0));
                currPoint2 = new PointF(event.getX(1), event.getY(1));
                if (Util.getPointsDis(stayPoint, currPoint1) > 0.5)
                    downPoint.set(currPoint1.x, currPoint1.y);
                if (Util.getPointsDis(stayPoint, currPoint2) > 0.5)
                    downPoint.set(currPoint2.x, currPoint2.y);
                break;
            case MotionEvent.ACTION_UP:
                currTime = System.nanoTime();
                if ((currTime - downTime) / 1e6 < 100) {
                    gesture = Gesture.SINGLE_TAP;
//                    float[] values = new float[] {1, 0, 0, 0, 1, 0, 0, 0, 1};
//                    matrix = new Matrix();
//                    matrix.setValues(values);
//                    setSuppMatrix(matrix);
                }
                break;
        }
        if (bzlDispatchTouchEventListener != null){
            return bzlDispatchTouchEventListener.dispatchTouchEvent(event);
        }
        return true;
    }

    public void setToast(boolean isToast) {
        this.isToast = isToast;
    }

    public Gesture getGesture() {
        return gesture;
    }

    public void setBzlDragListener(BzlDragListener bzlDragListener) {
        this.bzlDragListener = bzlDragListener;
    }

    public void setBzlPressListener(BzlPressListener bzlPressListener) {
        this.bzlPressListener = bzlPressListener;
    }

    public void setBzlRotsalChangedListener(BzlRotsalChangedListener bzlRotsalChangedListener) {
        this.bzlRotsalChangedListener = bzlRotsalChangedListener;
    }

    public void setAllowDispatchTouchEvent(boolean mAllowDispatchTouchEvent) {
        this.mAllowDispatchTouchEvent = mAllowDispatchTouchEvent;
    }

    public void setBzlTapListener(BzlTapListener bzlTapListener){
        this.bzlTapListener = bzlTapListener;
    }

    public void setBzlDispatchTouchEventListener(BzlDispatchTouchEventListener bzlDispatchTouchEventListener){
        this.bzlDispatchTouchEventListener = bzlDispatchTouchEventListener;
    }
}
