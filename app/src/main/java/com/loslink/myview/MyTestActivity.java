package com.loslink.myview;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;

import com.loslink.myview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/16.
 */

public class MyTestActivity extends Activity {

    private MTextView MTextView;
    private ImageView iv_test;
    private JunkCleanView jcv_test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MTextView=findViewById(R.id.mt_test);
        iv_test=findViewById(R.id.iv_test);
        jcv_test=findViewById(R.id.jcv_test);

        List<Bitmap> list=new ArrayList<>();
        for(int i=0;i<3;i++){
            Bitmap logo = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher_round)).getBitmap();
            list.add(logo);
        }
        jcv_test.setLogoList(list);
        jcv_test.setStateListenr(new JunkCleanView.StateListenr() {
            @Override
            public void animateEnd() {

            }
        });

        Drawable drawableVoice3 = DrawableCompat.wrap(getResources().getDrawable(R.mipmap.circle3));
        DrawableCompat.setTint(drawableVoice3 , Color.RED);
        iv_test.setImageBitmap(drawableToBitmap(drawableVoice3));
//        AnalyticsSdk.init(App.get(), new AnalyticsBuilder.Builder()
//                .setAnalyticsUrl(Consts.Analytics.REPORT_URL) // 设置 上报SDK url ，由接入方运营负责人统一申请
//                .setAppsFlyerKey(Consts.APPSFLYER_KEY) // 设置 AppsFlyerKey ，如果需要则由接入方运营负责人统一申请
//                .setTrafficId(tid) // 设置 应用的tid，由接入方运营负责人统一申请
//                .setChannel(channel) // 设置 本次安装的包渠道
//                .setInstallChannel(installChannel) // 设置 首次安装的包渠道
//                //.setSource("1") // 设置 安装来源信息，可不设置
////                .setBuglyAppId(BuildConfig.BUGLY_ID) // 设置 BuglyAppId ，由接入方运营负责人统一申请
////                .setBuglyDebugMode(false) // 设置是否开启 BuglyDebugMode
//                .build());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;

    }


}
