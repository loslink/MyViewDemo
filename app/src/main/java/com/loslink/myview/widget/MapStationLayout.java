package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.loslink.myview.utils.DipToPx;
import com.loslink.myview.utils.GbLog;
import com.loslink.myview.widget.bean.Station;

import java.util.ArrayList;
import java.util.List;

public class MapStationLayout extends FrameLayout {

    private Context mContext;
    private RectF curRectF;
    private Matrix transformMatrix=new Matrix();
    private List<Station> stations = new ArrayList<>();
    private GestureDetector gestureDetector;
    private Paint stationPaint,linePaint,ctrlPaint;
    private PointF downPoint = new PointF();
    private boolean isLongPress = false;
    private Path cubicPath=new Path();
    private Path editPath=new Path();
    private int stationColor = Color.BLUE;
    private int lineColor = Color.GREEN;
    private int controllerColor = Color.parseColor("#33ff0000");
    private final int TOUCH_RADIUS = 10;
    private int touchLineIndex = -1;

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
        stationPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        stationPaint.setColor(stationColor);
        stationPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        stationPaint.setStrokeWidth(3);

        linePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);

        ctrlPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        ctrlPaint.setColor(controllerColor);
        ctrlPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        ctrlPaint.setStrokeWidth(3);
        gestureDetector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if(isLongPress){
                    addStation(e.getX(),e.getY());
                    invalidate();
                }

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                int index=checkTouchLineIndex(e.getX(),e.getY());
                if(index != -1){
                    touchLineIndex = index;
                    invalidate();
                }
                GbLog.d("checkTouchLineIndex:"+index);

                return super.onSingleTapUp(e);
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
                gestureDetector.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                gestureDetector.onTouchEvent(event);
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
            if(i>0){
                float[] pointCtrl =new float[]{station.ctrlX,station.ctrlY};
                transformMatrix.mapPoints(pointCtrl);
                station.ctrlDisplayX=pointCtrl[0];
                station.ctrlDisplayY=pointCtrl[1];
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawLines(canvas);
        drawStations(canvas);
    }

    private void drawStations(Canvas canvas){
        for(int i=0;i<stations.size();i++){
            Station station=stations.get(i);
            canvas.drawCircle(station.displayX,station.displayY,20, stationPaint);
        }
    }

    private void drawLines(Canvas canvas){
        cubicPath.reset();
        for(int i=0;i<stations.size();i++){
            Station station=stations.get(i);
            if(i == 0){
                cubicPath.moveTo(station.displayX,station.displayY);
            }else{
                Station preStation = stations.get(i-1);
                cubicPath.cubicTo(preStation.displayX,preStation.displayY,(station.displayX+preStation.displayX)/2 ,(station.displayY+preStation.displayY)/2,station.displayX,station.displayY);

                if(touchLineIndex!=-1 && touchLineIndex == i-1){
                    editPath.reset();
                    editPath.moveTo(preStation.displayX,preStation.displayY);
                    editPath.cubicTo(preStation.displayX,preStation.displayY,(station.displayX+preStation.displayX)/2 ,(station.displayY+preStation.displayY)/2,station.displayX,station.displayY);

                }
            }

        }
        linePaint.setColor(lineColor);
        canvas.drawPath(cubicPath, linePaint);
        linePaint.setColor(Color.RED);
        canvas.drawPath(editPath, linePaint);

        if(touchLineIndex!=-1){
            //绘制控制点
            canvas.drawCircle(stations.get(touchLineIndex+1).ctrlDisplayX,stations.get(touchLineIndex+1).ctrlDisplayY,20, ctrlPaint);
        }

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
        if(stations.size() > 0){//非首站点添加控制点
            Station preStation = stations.get(stations.size()-1);
            PointF preP=getOriginPoint(preStation.x,preStation.y);
            station.ctrlX = (pointF.x + preStation.x)/2;
            station.ctrlY = (pointF.y + preStation.y)/2;
            station.ctrlDisplayX = (x + preStation.displayX)/2;
            station.ctrlDisplayY = (y + preStation.displayY)/2;
        }
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

    /**
     * 检测点中哪条线
     * @param touchX
     * @param touchY
     * @return
     */
    private int checkTouchLineIndex(float touchX,float touchY){
        int count = 500;//贝塞尔曲线取点数
        float step = 1f/count;
        for(int i=1;i<stations.size();i++){
            Station preStation=stations.get(i-1);
            Station station=stations.get(i);
            for(int j=0;j<count;j++){
                float t = step * (j+1);
                float x = (1 - t) * (1 - t) * preStation.displayX + 2 * t * (1-t) * station.ctrlDisplayX + t * t * station.displayX;
                float y = (1 - t) * (1 - t) * preStation.displayY + 2 * t * (1-t) * station.ctrlDisplayY + t * t * station.displayY;
                double distance = Math.sqrt((touchX-x)*(touchX-x)+(touchY-y)*(touchY-y));
                if(distance<=TOUCH_RADIUS){
                    return i-1;
                }
            }
        }

        return -1;
    }
}
