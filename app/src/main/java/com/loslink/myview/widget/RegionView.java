package com.loslink.myview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * 显示图片区域
 */
public class RegionView extends View {
    private Bitmap showPic,srcPic;
    private int startX = 0;
    private int startY = 0;
    private int width,height;

    public RegionView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImage(canvas);
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

