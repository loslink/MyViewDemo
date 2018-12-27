package com.loslink.myview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loslink.myview.model.StitchImageInfo;
import com.loslink.myview.utils.BitmapUtils;
import com.loslink.myview.utils.rx.RxTask;
import com.loslink.myview.widget.StitchImagesView;

import java.util.ArrayList;
import java.util.List;

public class StitchImagesActivity extends Activity {

    private StitchImagesView stitchImagesView;
    private TextView tv_save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        stitchImagesView=findViewById(R.id.stitchImagesView);
        tv_save=findViewById(R.id.tv_save);

        List<String> listPath=new ArrayList<>();
        for(int i=0;i<25;i++){
            listPath.add("");
        }
        stitchImagesView.setListImagePath(listPath);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<StitchImageInfo> stitchImageInfos=stitchImagesView.getImageInfoList();
                stitchTask(stitchImageInfos);
            }
        });
    }

    private void stitchTask(final List<StitchImageInfo> list){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RxTask<Integer, Void, Boolean> rxTask = new RxTask<Integer, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Integer... integers) {
//                int IMAGE_WIDTH_MAX_SIZE=1080;
                int IMAGE_WIDTH_MAX_SIZE=720;
                int count=0;
                while (true){
                    try {
                        count++;
                        Log.e("RegionView","stitchTask count:"+count);
                        stitchAndSaveImagesToLocal(list,IMAGE_WIDTH_MAX_SIZE);
                        return true;
                    } catch (OutOfMemoryError e) {
                        IMAGE_WIDTH_MAX_SIZE=IMAGE_WIDTH_MAX_SIZE-100;
                        if(count>8){
                            break;
                        }
                    }
                }

                return false;
            }

            @Override
            protected void onError(Throwable throwable) {
                super.onError(throwable);
                Log.e("RegionView","onError:"+throwable);
                progressDialog.dismiss();
                Toast.makeText(StitchImagesActivity.this,"拼接失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    progressDialog.dismiss();
                    Toast.makeText(StitchImagesActivity.this,"拼接成功",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(StitchImagesActivity.this,"拼接失败",Toast.LENGTH_SHORT).show();
                }
            }
        };
        rxTask.execute();
    }

    private void stitchAndSaveImagesToLocal(final List<StitchImageInfo> list,int IMAGE_WIDTH_MAX_SIZE) throws OutOfMemoryError{
        List<Bitmap> bitmapList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            final BitmapFactory.Options optionsOut = new BitmapFactory.Options();
            optionsOut.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(),R.mipmap.girl,optionsOut);
            int scale = 1;
//            int IMAGE_WIDTH_MAX_SIZE=540;
            if (optionsOut.outWidth > IMAGE_WIDTH_MAX_SIZE) {
                scale = (int)((float)optionsOut.outWidth/(float)IMAGE_WIDTH_MAX_SIZE);
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.mipmap.girl,options);

            Matrix matrix = new Matrix();
            float sx=(float) IMAGE_WIDTH_MAX_SIZE / bmp.getWidth();
            matrix.postScale( sx, sx);
            Bitmap result = Bitmap.createBitmap( bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true );
//                    bmp.recycle();
            bitmapList.add(result);
        }

        Bitmap resultBitmap = null;
        resultBitmap=BitmapUtils.stitchMultiBitmaps(bitmapList);

        for(Bitmap bitmap:bitmapList){
            bitmap.recycle();
            bitmap = null;
        }
        String path = Environment.getExternalStoragePublicDirectory("") + "/stitchResult.jpg";
        BitmapUtils.saveBitmap(resultBitmap,path);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stitchImagesView.onDestroy();
    }
}
