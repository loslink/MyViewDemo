package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loslink.myview.utils.DipToPx;
import com.loslink.myview.utils.GbLog;
import com.loslink.myview.widget.bean.Station;
import com.loslink.myview.widget.photoview.Util;

import java.util.ArrayList;
import java.util.List;

public class MapStationLayout extends FrameLayout {

    private Context mContext;
    private RectF curRectF;
    private Matrix transformMatrix=new Matrix();
    private List<Station> stations = new ArrayList<>();
    private GestureDetector gestureDetector;
    private Paint paint,linePaint;
    private PointF downPoint = new PointF();
    private boolean isLongPress = false;
    private Path cubicPath=new Path();

    public MapStationLayout(Context context) {
        this(context, null);
    }

    public MapStationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapStationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
//        setClickable(true);//代表onTouchEvent return true
        mContext = context;
        paint =new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(3);

        linePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        gestureDetector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if(isLongPress){
                    addStation(e.getX(),e.getY());
                    invalidate();
                }

            }

        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downPoint.x = event.getX();
                downPoint.y = event.getY();
                isLongPress = true;
                gestureDetector.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                isLongPress = false;
                break;
            case MotionEvent.ACTION_MOVE:
                gestureDetector.onTouchEvent(event);
                float disX = event.getX()-downPoint.x;
                float disY = event.getY()-downPoint.y;
                //小区域长按有效
                if(disX > DipToPx.dipToPx(getContext(),10) || disY > DipToPx.dipToPx(getContext(),10)){
                    isLongPress = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setMatrix(Matrix matrix){
        transformMatrix = matrix;
        for(int i=0;i<stations.size();i++){
            Station station=stations.get(i);
            float[] points =new float[]{station.x,station.y};
            transformMatrix.mapPoints(points);
            station.displayX=points[0];
            station.displayY=points[1];
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        cubicPath.reset();
        for(int i=0;i<stations.size();i++){
            Station station=stations.get(i);
            canvas.drawCircle(station.displayX,station.displayY,20,paint);

            if(i == 0){
                cubicPath.moveTo(station.displayX,station.displayY);
            }else{
                Station preStation = stations.get(i-1);
                cubicPath.cubicTo(preStation.displayX,preStation.displayY,(station.displayX-preStation.displayX)/2 +20,(station.displayY-preStation.displayY)/2,station.displayX,station.displayY);
            }

        }
        canvas.drawPath(cubicPath, linePaint);
    }

    /**
     * 添加站点
     * @param x
     * @param y
     */
    private void addStation(float x,float y){
        Station station=new Station();
        PointF pointF=getOriginPoint(x,y);//得到相对地图复原状态时的坐标
        station.x = pointF.x;
        station.y = pointF.y;
        station.displayX = x;
        station.displayY = y;
        stations.add(station);
    }

    /**
     * 得到相对地图复原状态时的坐标
     * @param x
     * @param y
     * @return
     */
    private PointF getOriginPoint(float x, float y) {
        float[] eventXY = new float[]{x, y};
        Matrix matrix = new Matrix();
        transformMatrix.invert(matrix);
        matrix.mapPoints(eventXY);
        return new PointF(eventXY[0], eventXY[1]);
    }
}
