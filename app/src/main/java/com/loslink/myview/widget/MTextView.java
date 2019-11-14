package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public class MTextView extends View {

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


//        setLayerType(LAYER_TYPE_SOFTWARE, null);//对单独的View在运行时阶段禁用硬件加速
//        float x, y;
//        x = 0;
//        y = 0;
//        Paint shadowPaint = new Paint();
//        shadowPaint.setTextSize(200);
//        shadowPaint.setStyle(Paint.Style.STROKE);
//        shadowPaint.setAntiAlias(true);
//        shadowPaint.setShadowLayer(3f, 2f, 2f, Color.parseColor("#55000000"));
////            canvas.drawText(text,x,y,shadowPaint);
//
////            srcPaint.setMaskFilter(new BlurMaskFilter(BAR_HEIGHT / 1.5f, BlurMaskFilter.Blur.INNER));//内发光
//        Paint paint2 = new Paint();
//        paint2.setColor(Color.RED);
//        paint2.setStyle(Paint.Style.FILL);
//        float[] direction = new float[] { 1, 1, 1 };
//        // 设置环境光亮度
//        float light = 0.1f;
//        // 选择要应用的反射等级
//        float specular = 6;
//        // 向mask应用一定级别的模糊
//        float blur = 5f;
//        EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
//        paint2.setMaskFilter(emboss);
//        canvas.drawCircle(300, 250, 50, paint2);



    }
}

