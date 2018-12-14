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
import android.graphics.Shader;
import android.graphics.SumPathEffect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

class MTextView extends View {

    private Paint srcPaint;
    private String text="754";

    public MTextView(Context context) {
        super(context);
        init();
    }

    public MTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    public void init() {
        srcPaint=new Paint();
        srcPaint.setAntiAlias(true);
        srcPaint.setTextSize(200);
        srcPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas)   {

        canvas.drawColor(Color.parseColor("#f8941c"));

        Paint shadowPaint = new Paint();
        shadowPaint.setTextSize(200);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setShadowLayer(3f,2f,2f,Color.parseColor("#55000000"));
        canvas.drawText(text,canvas.getWidth()/2,canvas.getHeight()/2+200,shadowPaint);

        LinearGradient linearGradient=new LinearGradient(canvas.getWidth()/2,canvas.getHeight()/2,
                canvas.getWidth()/2,canvas.getHeight()/2+300,
                Color.WHITE,Color.parseColor("#f8941c"),
                Shader.TileMode.CLAMP);
        srcPaint.setShader(linearGradient);
        canvas.drawText(text,canvas.getWidth()/2,canvas.getHeight()/2+200,srcPaint);


    }
}

