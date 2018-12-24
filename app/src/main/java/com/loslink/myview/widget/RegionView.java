package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.loslink.myview.R;
import com.loslink.myview.utils.DipToPx;

import java.io.FileInputStream;

/**
 * 显示图片区域
 */
public class RegionView extends View {
    private Bitmap showPic,srcPic;
    private int startX = 0;
    private int startY = 0;
    private int width,height,canvasW,cavasH;
    private Context context;
    private Paint strokePaint;
    private int strokeWidth;

    public RegionView(Context context) {
        this(context,null);
    }

    public RegionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RegionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init(){
        strokeWidth=DipToPx.dipToPx(context,2);
        strokePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(Color.WHITE);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//第一次为0？
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//触发测量父控件canvasW
        canvasW=MeasureSpec.getSize(widthMeasureSpec);//得到父控件最大宽度
        if(canvasW>0){
            setPath(null);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImage(canvas);
        float picW=showPic.getWidth();
        float picH=showPic.getHeight();
        canvas.drawLine(0,strokeWidth/2,picW,strokeWidth/2,strokePaint);
        canvas.drawLine(strokeWidth/2,0,strokeWidth/2,picH,strokePaint);
        canvas.drawLine(picW-strokeWidth/2,0,picW-strokeWidth/2,picH,strokePaint);
    }

    private void drawImage(Canvas canvas){
        if(startX>srcPic.getWidth()){
            startX=srcPic.getWidth();
        }
        if(startY>srcPic.getHeight()){
            startY=srcPic.getHeight();
        }
        if(width+startX > srcPic.getWidth()){
            width=srcPic.getWidth()-startX;
        }
        if(height+startY > srcPic.getHeight()){
            height=srcPic.getHeight()-startY;
        }
        if(width==0 || height==0){
            return;
        }
        showPic = Bitmap.createBitmap(srcPic, startX, startY, width, height);
        if(showPic!=null){
            canvas.drawBitmap(showPic, 0, 0, null);
        }
    }

    public void setPath(String path){

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),R.mipmap.girl,o);
        int scale = 1;
        int IMAGE_MAX_SIZE=canvasW;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        cavasH=(int)(canvasW*((float)o.outHeight/o.outWidth));
        setMeasuredDimension(canvasW, cavasH);//传递给父控件,父控件大小

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.mipmap.girl,options);

        Matrix matrix = new Matrix();
        matrix.postScale( (float) canvasW / bmp.getWidth(), (float) cavasH / bmp.getHeight() );
        Bitmap result = Bitmap.createBitmap( bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true );
        this.width=result.getWidth();
        this.height=result.getHeight();
        setBitmap(result);
        bmp.recycle();
    }

    public void setBitmap(Bitmap b) {
        srcPic = b;
    }

    public void setBitmapRegion(int startX, int startY,int width,int height) {
        this.startX = startX;
        this.startY = startY;
        this.width=width;
        this.height=height;
        postInvalidate();
    }
}

