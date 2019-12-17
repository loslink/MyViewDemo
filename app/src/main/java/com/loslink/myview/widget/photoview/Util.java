package com.loslink.myview.widget.photoview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.ImageView;


public class Util {

    public static void checkZoomLevels(float minZoom, float midZoom,
                                       float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    public static boolean hasDrawable(ImageView imageView) {
        return imageView.getDrawable() != null;
    }

    public static boolean isSupportedScaleType(final ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        switch (scaleType) {
            case MATRIX:
                throw new IllegalStateException("Matrix scale type is not supported");
        }
        return true;
    }

    public static int getPointerIndex(int action) {
        return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    public static float getPointsDis(PointF p1, PointF p2) {
        float disX = p1.x - p2.x;
        float disY = p1.y - p2.y;
        float dis = (float) Math.sqrt(disX * disX + disY * disY);
        return dis;
    }

    public static PointF getOriginPoint(Matrix currentMatrix, float x, float y) {
        float[] eventXY = new float[]{x, y};
        Matrix matrix = new Matrix();
        currentMatrix.invert(matrix);
        matrix.mapPoints(eventXY);
        return new PointF(eventXY[0], eventXY[1]);
    }


    public static float getAngleRadian(PointF p1, PointF p2) {
        float angle;
        float centerX = (p1.x + p2.x) / 2;
        float centerY = (p1.y + p2.y) / 2;
        angle = (float) Math.atan2(p1.x - centerX, p1.y - centerY);
        return angle;
    }
}
