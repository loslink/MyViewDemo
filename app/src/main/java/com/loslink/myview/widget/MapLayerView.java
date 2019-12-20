package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loslink.myview.utils.DipToPx;
import com.loslink.myview.widget.photoview.PhotoView;
import com.loslink.myview.widget.photoview.listener.OnMatrixChangedListener;

public class MapLayerView extends FrameLayout {

    private Context mContext;
    private static final String TAG = MapLayerView.class.getSimpleName();

    private PhotoView photoView;            // 地图层
    private RectF tempRectF;
    private Matrix photoViewMatrix;
    Mode curMode = Mode.TOUCH;
    private Bitmap mBitmap;
    private MapStationLayout mapStationLayout;

    public enum Mode {
        TOUCH,
        INIT_POS,
        GO,
        ADD_STATION
    }

    public MapLayerView(@NonNull Context context) {
        this(context, null);
    }

    public MapLayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapLayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    void initView(final Context context) {
        mContext = context;
        photoView = new PhotoView(context);
        photoView.setMaximumScale(8);
        LayoutParams layoutParams =new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(photoView, layoutParams);
        photoView.setOnMatrixChangeListener(matrixChangedListener);

        mapStationLayout = new MapStationLayout(context);
        LayoutParams lpStation =new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mapStationLayout, lpStation);

        handleGesture();
    }

    private PointF downPoint = new PointF();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                photoView.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                photoView.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if(!mapStationLayout.isEventOnlyDo()){//非站点layout使用
                    photoView.dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                photoView.dispatchTouchEvent(event);
                break;
            default:
                photoView.dispatchTouchEvent(event);
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private void handleGesture() {
        photoView.setAllowDispatchTouchEvent(true);
        photoView.setBzlDispatchTouchEventListener(event -> {
            int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_UP:

                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
            return true;
        });

    }

    OnMatrixChangedListener matrixChangedListener = new OnMatrixChangedListener() {
        @Override
        public void onMatrixChanged(RectF rectF) {
//            Matrix mImageMatrix = photoView.getImageMatrix();
            Matrix mImageMatrix = new Matrix();
            photoView.getAttacher().getSuppMatrix(mImageMatrix);//得到图片缩放旋转矩阵，去掉初始化矩阵干扰
            mapStationLayout.setMatrix(mImageMatrix);
        }
    };

    private PointF getOriginPoint(float x, float y) {
        float[] eventXY = new float[]{x, y};
        Matrix matrix = new Matrix();
        photoView.getImageMatrix().invert(matrix);
        matrix.mapPoints(eventXY);
        return new PointF(eventXY[0], eventXY[1]);
    }

    public void setMode(Mode mode) {
        curMode = mode;
    }

    public Mode getMode() {
        return curMode;
    }

    public PhotoView.Gesture getGesture() {
        return photoView.getGesture();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private Matrix tmpMatrix = new Matrix();

    public void setImage(Bitmap bmp) {
        photoView.getSuppMatrix(tmpMatrix);
        photoView.setImageBitmap(bmp);
        photoView.setDisplayMatrix(tmpMatrix);
        tempRectF = photoView.getDisplayRect();
        mBitmap = bmp;
    }

    public void setDrawable(Drawable drawable) {
        photoView.setImageDrawable(drawable);
    }

    public Drawable getDrawable() {
        return photoView.getDrawable();
    }

    public void invalidatePhotoview() {
        photoView.invalidate();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Matrix getImageMatrix() {
        return photoView.getImageMatrix();
    }

    public void setImage(int id) {
        photoView.setImageResource(id);
    }

}