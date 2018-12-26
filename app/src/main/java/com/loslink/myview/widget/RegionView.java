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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.loslink.myview.R;
import com.loslink.myview.model.StitchImageInfo;
import com.loslink.myview.utils.DipToPx;
import com.loslink.myview.utils.rx.RxTask;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 显示图片区域
 */
public class RegionView extends View {

    public static final int MODE_NOMAL=0;
    public static final int MODE_TOP=1;
    public static final int MODE_BOTTOM=2;
    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_SCALE = 1;
    private Bitmap showPic,srcPic;
    private int startX = 0;
    private int startY = 0;
    private int width,height,canvasW,cavasH;
    private Context context;
    private Paint strokePaint,controllerPaint,mBackgroundPaint;
    private int strokeWidth;
    private boolean isEdit=false;
    private int mode=0;
    private float controllerBarWidth,controllerBarHeight;
    private String imgPath;
    private RectF cropRectF;//相对于原图片的尺寸坐标（要表示当前画布坐标需要转化一下）
    private RectF currentCropRectF=new RectF();//当前画布坐标裁剪框
    private RectF LastCropRectF=new RectF();
    private List<RectF> historyActions=new ArrayList<>();//相对于原图片的尺寸坐标的历史操作
    private int status=STATUS_NORMAL;
    private int selectedController;
    private RectF topControllerRect=new RectF(),bottomControllerRect=new RectF();
    private RectF backUpRect=new RectF(),backDownRect=new RectF();
    private Matrix matrixDown=new Matrix(),matrixUp=new Matrix();
    private Bitmap controllerDown,controllerUp;
    private OnRegionViewListenr onRegionViewListenr;

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

    public OnRegionViewListenr getOnRegionViewListenr() {
        return onRegionViewListenr;
    }

    public void setOnRegionViewListenr(OnRegionViewListenr onRegionViewListenr) {
        this.onRegionViewListenr = onRegionViewListenr;
    }

    public RectF getCropRectF() {
        return cropRectF;
    }

    public void setCropRectF(RectF cropRectF) {
        if(cropRectF!=null){
            this.cropRectF = cropRectF;
        }else {
            this.cropRectF = new RectF();
        }

    }

    /**
     * 把原图片的裁剪框转化为当前画布坐标的裁剪框
     * tips:需要画布宽高确定后方有效
     * @return
     */
    private RectF getCurrentCanvasCrop(){
        if(historyActions==null || historyActions.size()==0){
            currentCropRectF.set(cropRectF);
            return currentCropRectF;
        }
        RectF hisRectF=historyActions.get(historyActions.size()-1);
        currentCropRectF.set(hisRectF.left,cropRectF.top-hisRectF.top,hisRectF.right,cropRectF.bottom-hisRectF.top);
        return currentCropRectF;
    }

    /**
     * 获取上一次裁剪框（相对于原图片画布坐标）
     * @return
     */
    private RectF getLastCropActionWithSrcImage(){
        if(historyActions==null || historyActions.size()==0){
            LastCropRectF.set(0,0,canvasW,cavasH);
            return LastCropRectF;
        }
        RectF hisRectF=historyActions.get(historyActions.size()-1);
        LastCropRectF.set(hisRectF);
        return LastCropRectF;
    }

    public List<RectF> getHistoryActions() {
        return historyActions;
    }

    public void setHistoryActions(List<RectF> historyActions) {
        if(historyActions!=null){
            this.historyActions = historyActions;
        }else{
            this.historyActions=new ArrayList<>();
        }

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

        mBackgroundPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(Color.parseColor("#88000000"));

        controllerDown = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_controller_down);
        controllerUp = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_controller_up);
        controllerBarHeight=controllerDown.getHeight()*(controllerBarWidth/controllerDown.getWidth());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//第一次为0？
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//触发测量父控件canvasW
        canvasW=MeasureSpec.getSize(widthMeasureSpec);//得到父控件最大宽度

        if(canvasW>0){
            loadImage();//由图片来确定宽高
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(srcPic!=null && !srcPic.isRecycled()){
            srcPic.recycle();
            srcPic=null;
        }
        if(showPic!=null && !showPic.isRecycled()){
            showPic.recycle();
            showPic=null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImage(canvas);
        drawController(canvas);
        drawCropOutside(canvas);
    }

    /**
     * 裁剪框外蒙层
     * @param canvas
     */
    private void drawCropOutside(Canvas canvas){
        if(srcPic==null){
            return;
        }
        backUpRect.set(getCurrentCanvasCrop().left, 0, getCurrentCanvasCrop().right, getCurrentCanvasCrop().top);
        backDownRect.set(getCurrentCanvasCrop().left, getCurrentCanvasCrop().bottom, getCurrentCanvasCrop().right, cavasH);

        canvas.drawRect(backUpRect, mBackgroundPaint);
        canvas.drawRect(backDownRect, mBackgroundPaint);
    }

    /**
     * 外框控制杆绘制
     * @param canvas
     */
    private void drawController(Canvas canvas){

        if(srcPic==null){
            return;
        }
        if(isEdit){
            strokePaint.setColor(Color.RED);
            canvas.drawLine(getCurrentCanvasCrop().left,getCurrentCanvasCrop().top+strokeWidth/2,getCurrentCanvasCrop().right,getCurrentCanvasCrop().top+strokeWidth/2,strokePaint);//top
            canvas.drawLine(getCurrentCanvasCrop().left,getCurrentCanvasCrop().bottom-strokeWidth/2,getCurrentCanvasCrop().right,getCurrentCanvasCrop().bottom-strokeWidth/2,strokePaint);//bottom

            //控制杆
            topControllerRect.set((getCurrentCanvasCrop().right-controllerBarWidth)/2,
                    getCurrentCanvasCrop().top,
                    (getCurrentCanvasCrop().right-controllerBarWidth)/2+controllerBarWidth,
                    getCurrentCanvasCrop().top+controllerBarHeight);
            float sx=controllerBarWidth/controllerDown.getWidth();
            float sy=controllerBarWidth/controllerDown.getWidth();
            matrixDown.setScale(sx,sy);
            matrixDown.postTranslate(topControllerRect.left,topControllerRect.top);
            canvas.drawBitmap(controllerDown,matrixDown,controllerPaint);

            bottomControllerRect.set((getCurrentCanvasCrop().right-controllerBarWidth)/2,
                    getCurrentCanvasCrop().bottom-controllerBarHeight,
                    (getCurrentCanvasCrop().right-controllerBarWidth)/2+controllerBarWidth,
                    getCurrentCanvasCrop().bottom);
            matrixUp.setScale(sx,sy);
            matrixUp.postTranslate(bottomControllerRect.left,bottomControllerRect.top);
            canvas.drawBitmap(controllerUp,matrixUp,controllerPaint);
        }else{
            strokePaint.setColor(Color.WHITE);
            if(mode==MODE_TOP){
                canvas.drawLine(getCurrentCanvasCrop().left,getCurrentCanvasCrop().top+strokeWidth/2,getCurrentCanvasCrop().right,getCurrentCanvasCrop().top+strokeWidth/2,strokePaint);//top
            }
            if(mode==MODE_BOTTOM){
                canvas.drawLine(getCurrentCanvasCrop().left,getCurrentCanvasCrop().bottom-strokeWidth/2,getCurrentCanvasCrop().right,getCurrentCanvasCrop().bottom-strokeWidth/2,strokePaint);//bottom
            }
        }

        canvas.drawLine(getCurrentCanvasCrop().left+strokeWidth/2,getCurrentCanvasCrop().top,getCurrentCanvasCrop().left+strokeWidth/2,getCurrentCanvasCrop().bottom,strokePaint);//左线
        canvas.drawLine(getCurrentCanvasCrop().right-strokeWidth/2,getCurrentCanvasCrop().top,getCurrentCanvasCrop().right-strokeWidth/2,getCurrentCanvasCrop().bottom,strokePaint);//右线

    }

    /**
     * 触摸事件处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEdit){
            return false;
        }
        this.getParent().requestDisallowInterceptTouchEvent(true);
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                int selectController = isSeletedControllerCircle(x, y);
                if (selectController > 0) {// 选择控制点
                    ret = true;
                    selectedController = selectController;// 记录选中控制点编号
                    status = STATUS_SCALE;// 进入缩放状态
                }else{
                    status = STATUS_NORMAL;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (status == STATUS_SCALE) {// 缩放控制
                    scaleCropController(x, y);
                    ret = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                status = STATUS_NORMAL;
                break;
        }
        return ret;
    }

    /**
     * 操作控制点 控制缩放
     *
     * @param x
     * @param y
     */
    private void scaleCropController(float x, float y) {
//        tempRect.set(cropRect);// 存贮原有数据，以便还原
        switch (selectedController) {
            case 1:// 选中顶部控制点
                if(y>0){
                    if(y>getCurrentCanvasCrop().bottom-controllerBarHeight*2){
                        cropRectF.top = getLastCropActionWithSrcImage().top+getCurrentCanvasCrop().bottom-controllerBarHeight*2;
                    }else{
                        cropRectF.top = getLastCropActionWithSrcImage().top+y;
                    }
                }else{
                    cropRectF.top = getLastCropActionWithSrcImage().top;
                }
                break;
            case 2:// 选中底部控制点
                if(y<cavasH){
                    if(y<getCurrentCanvasCrop().top+controllerBarHeight*2){
                        cropRectF.bottom = getLastCropActionWithSrcImage().top+getCurrentCanvasCrop().top+controllerBarHeight*2;
                    }else{
                        cropRectF.bottom = getLastCropActionWithSrcImage().top+y;
                    }
                }else{
                    cropRectF.bottom = getLastCropActionWithSrcImage().bottom;
                }
                break;
        }
        if(onRegionViewListenr!=null){
            onRegionViewListenr.onCropRect(cropRectF);
        }
        postInvalidate();
    }

    private int isSeletedControllerCircle(float x, float y) {
        if (topControllerRect.contains(x, y))// 选中顶部控制点
            return 1;
        if (bottomControllerRect.contains(x, y))// 选中底部控制点
            return 2;
        return -1;
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

        if(srcPic==null){
            return;
        }
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

    /**
     * 同步加载图片
     */
//    private void loadImage(){
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(),R.mipmap.girl,o);
//        int scale = 1;
//        int IMAGE_MAX_SIZE=canvasW;
//        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
//            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//        }
//
//        cavasH=(int)(canvasW*((float)o.outHeight/o.outWidth));
//        setMeasuredDimension(canvasW, cavasH);//传递给父控件,父控件大小
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = scale;
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.mipmap.girl,options);
//
//        Matrix matrix = new Matrix();
//        matrix.postScale( (float) canvasW / bmp.getWidth(), (float) cavasH / bmp.getHeight() );
//        Bitmap result = Bitmap.createBitmap( bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true );
//        width=result.getWidth();
//        height=result.getHeight();
//        setBitmap(result);
//        bmp.recycle();
//    }

    /**
     * 异步加载图片
     */
    private void loadImage(){

        final BitmapFactory.Options optionsOut = new BitmapFactory.Options();
        optionsOut.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),R.mipmap.girl,optionsOut);

        if(historyActions!=null && historyActions.size()>0){
            RectF rectF=historyActions.get(historyActions.size()-1);
            startY=(int)rectF.top;
            height=(int)(rectF.bottom-rectF.top);
            cavasH=height;
        }else{
            startY=0;//还原复用值
            height=0;
            cavasH=(int)(canvasW*((float)optionsOut.outHeight/optionsOut.outWidth));
        }
        setMeasuredDimension(canvasW, cavasH);//传递给父控件,父控件大小(注意：在measure中先确定大小)

//        Log.e("RegionView",imgPath+" startY:"+startY+" height:"+height);
        if(srcPic!=null){
            return;
        }

        RxTask<Integer, Void, Bitmap> rxTask = new RxTask<Integer, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Integer... integers) {
                int scale = 1;
                int IMAGE_MAX_SIZE=canvasW;
                if (optionsOut.outHeight > IMAGE_MAX_SIZE || optionsOut.outWidth > IMAGE_MAX_SIZE) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(optionsOut.outHeight, optionsOut.outWidth)) / Math.log(0.5)));
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.mipmap.girl,options);

                Matrix matrix = new Matrix();
                matrix.postScale( (float) canvasW / bmp.getWidth(), (float) canvasW / bmp.getWidth());
                Bitmap result = Bitmap.createBitmap( bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true );

                bmp.recycle();
                return result;
            }

            @Override
            protected void onError(Throwable throwable) {
                super.onError(throwable);
                Log.e("RegionView","onError:");
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    width=result.getWidth();
                    height=result.getHeight();
                    setBitmap(result);
                }
            }
        };
        rxTask.execute();
    }

    public void setBitmap(Bitmap b) {
        srcPic = b;
        if(cropRectF.left==0 && cropRectF.top==0 && cropRectF.right==0 && cropRectF.bottom==0){
            cropRectF = new RectF(0, 0, srcPic.getWidth(), srcPic.getHeight());//裁剪框最外围坐标为裁剪真实区
        }
        postInvalidate();
    }

    /**
     * 裁剪图片
     */
    public void cutImage(){
        if(!isEdit){
            return;
        }
        if(cropRectF==null || (cropRectF.left==0 && cropRectF.top==0 && cropRectF.right==0 && cropRectF.bottom==0)){//另一个不进行裁剪时
            return;
        }
        setBitmapRegion((int)cropRectF.left,
                (int)cropRectF.top,
                (int)(cropRectF.right-cropRectF.left),
                (int)(cropRectF.bottom-cropRectF.top));
        cavasH=(int)(cropRectF.bottom-cropRectF.top);
        setMeasuredDimension(canvasW, cavasH);

        if(onRegionViewListenr!=null){
            RectF rectF=new RectF();
            rectF.set(cropRectF);
            historyActions.add(rectF);//新建一个新对象塞进列表
            onRegionViewListenr.onHistoryAction(historyActions);//保存裁剪记录
        }
    }

    private void setBitmapRegion(int startX, int startY,int width,int height) {
        this.startX = startX;
        this.startY = startY;
        this.width=width;
        this.height=height;
        postInvalidate();
    }

    public interface OnRegionViewListenr{
        void onCropRect(RectF cropRectF);
        void onHistoryAction(List<RectF> historyActions);
    }
}

