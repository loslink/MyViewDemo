package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.loslink.myview.R;
import com.loslink.myview.utils.DipToPx;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示图片区域
 */
public class RegionView extends View {

    public static final int MODE_NOMAL=0;
    public static final int MODE_TOP=1;
    public static final int MODE_BOTTOM=2;
    private Bitmap showPic,srcPic;
    private int startX = 0;
    private int startY = 0;
    private int width,height,canvasW,cavasH;
    private Context context;
    private Paint strokePaint,controllerPaint;
    private int strokeWidth;
    private boolean isEdit=false;
    private int mode=0;
    private float controllerBarWidth;
    private String imgPath;
    private RectF currentRectF;
    private List<RectF> historyList=new ArrayList<>();

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
        controllerBarWidth=(float) DipToPx.dipToPx(context,30);

        strokePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(Color.WHITE);

        controllerPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        controllerPaint.setFilterBitmap(true);
        controllerPaint.setDither(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//第一次为0？
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//触发测量父控件canvasW
        canvasW=MeasureSpec.getSize(widthMeasureSpec);//得到父控件最大宽度
        if(canvasW>0){
            loadImage();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImage(canvas);
        drawController(canvas);
    }

    /**
     * 外框控制杆绘制
     * @param canvas
     */
    private void drawController(Canvas canvas){
        float picW=showPic.getWidth();
        float picH=showPic.getHeight();
        if(isEdit){
            strokePaint.setColor(Color.RED);
            canvas.drawLine(0,strokeWidth/2,picW,strokeWidth/2,strokePaint);//top
            canvas.drawLine(0,picH-strokeWidth/2,picW,picH-strokeWidth/2,strokePaint);//bottom

            //控制杆
            Bitmap controllerDown = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_controller_down);
            Bitmap controllerUp = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_controller_up);
            Matrix matrix = new Matrix();
            float barHeight=controllerUp.getHeight()*(controllerBarWidth/controllerDown.getWidth());
            matrix.setScale(controllerBarWidth/controllerDown.getWidth(),controllerBarWidth/controllerDown.getWidth());
            matrix.postTranslate((canvasW-controllerBarWidth)/2,0);
            canvas.drawBitmap(controllerDown,matrix,controllerPaint);
            matrix.postTranslate(0,cavasH-barHeight);
            canvas.drawBitmap(controllerUp,matrix,controllerPaint);
        }else{
            strokePaint.setColor(Color.WHITE);
            if(mode==MODE_TOP){
                canvas.drawLine(0,strokeWidth/2,picW,strokeWidth/2,strokePaint);
            }
            if(mode==MODE_BOTTOM){
                canvas.drawLine(0,picH-strokeWidth/2,picW,picH-strokeWidth/2,strokePaint);
            }
        }

        canvas.drawLine(strokeWidth/2,0,strokeWidth/2,picH,strokePaint);//左线
        canvas.drawLine(picW-strokeWidth/2,0,picW-strokeWidth/2,picH,strokePaint);//右线

    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        postInvalidate();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        postInvalidate();
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
        imgPath=path;
    }

    private void loadImage(){
//        if(srcPic!=null){
//            return;
//        }
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
        width=result.getWidth();
        height=result.getHeight();
        setBitmap(result);
        bmp.recycle();
    }

    public void setBitmap(Bitmap b) {
        srcPic = b;
        postInvalidate();
    }

    public void setBitmapRegion(int startX, int startY,int width,int height) {
        this.startX = startX;
        this.startY = startY;
        this.width=width;
        this.height=height;
        postInvalidate();
    }
}

