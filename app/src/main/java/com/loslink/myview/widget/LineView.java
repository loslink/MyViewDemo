package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.SumPathEffect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static java.lang.Math.PI;

public class LineView extends View {

    private Paint paint,textPaint;
    private Path path;
    private Point pointA,pointB,pointC;
    private int canvasWidth,canvasHeight;
    private Point p0=new Point(0,0),p1=new Point(100,0),p2=new Point(200,200);

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    public void init() {
        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setColor(Color.BLUE);

        textPaint=new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(100);
        textPaint.setStrokeWidth(8);
        textPaint.setColor(Color.BLUE);

        pointA=new Point();
        pointB=new Point();
        pointC=new Point();

        path=new Path();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth=w;
        canvasHeight=h;

        pointA.x = 20;
        pointA.y = 20;

        pointB.x = 320;
        pointB.y = 320;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                pointC.x = (int)event.getX()-(canvasWidth/2);
                pointC.y = (int)event.getY()-(canvasHeight/2);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)   {
        canvas.save();
        //将背景填充成白色
        canvas.drawColor(Color.BLACK);
        canvas.translate(canvasWidth/2,canvasHeight/2);

        path.moveTo(pointA.x,pointA.y);
        path.lineTo(pointB.x,pointB.y);
        canvas.drawPath(path, paint);

        canvas.drawCircle(pointC.x,pointC.y,5f,paint);

        Point vectorAB = new Point();
        Point vectorAC = new Point();

        vectorAB.x = pointB.x - pointA.x;
        vectorAB.y = pointB.y - pointA.y;

        vectorAC.x = pointC.x - pointA.x;
        vectorAC.y = pointC.y - pointA.y;

        double result = vectorAB.x * vectorAC.x + vectorAB.y * vectorAC.y;
        double abMol = Math.sqrt(vectorAB.x  * vectorAB.x  + vectorAB.y * vectorAB.y );
        double acMol = Math.sqrt(vectorAC.x  * vectorAC.x  + vectorAC.y * vectorAC.y );
        double cos = result / (abMol * acMol);
        double jiaodu=Math.acos(cos);
        double v2 = jiaodu/2/PI*360;

        canvas.drawText("cos:"+cos+"\n夹角:"+v2,-canvasWidth/2,-canvasHeight/2+200,textPaint);

        Path bPath = new Path();
        bPath.moveTo(100,100);
        bPath.cubicTo(100,100,200f,100f,300,300);
        canvas.drawPath(bPath, paint);

        drawBesaier(canvas);
    }


    private void drawBesaier(Canvas canvas){

        canvas.restore();

        p0.x = 0;
        p0.y = 0;

        p1.x = canvasWidth;
        p1.y = canvasHeight/2;

        p2.x = 0;
        p2.y = canvasHeight;
        int count = 500;
        double step = 1d/count;
        for(int i=0;i<count;i++){
            double t = step * (i+1);
            double x = (1 - t) * (1 - t) * p0.x + 2 * t *(1-t)*p1.x + t * t *p2.x;
            double y = (1 - t) * (1 - t) * p0.y + 2 * t *(1-t)*p1.y + t * t *p2.y;
            canvas.drawPoint((float) x,(float)y,paint);
        }
    }
}

