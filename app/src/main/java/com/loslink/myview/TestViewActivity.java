package com.loslink.myview;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.loslink.myview.widget.AirWingView;
import com.loslink.myview.widget.BatteryCleanView;
import com.loslink.myview.widget.CleanDetailView;
import com.loslink.myview.widget.CpuBoostView;
import com.loslink.myview.widget.FloatBallView;
import com.loslink.myview.widget.JunkCleanView;
import com.loslink.myview.widget.LineView;
import com.loslink.myview.widget.MTextView;
import com.loslink.myview.widget.MainBatteryCleanView;
import com.loslink.myview.widget.MainCircleView;
import com.loslink.myview.widget.MainCleanNewView;
import com.loslink.myview.widget.MyView;
import com.loslink.myview.widget.RotateView;
import com.loslink.myview.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestViewActivity extends Activity {

    public static String KEY_INTENT;
    private int axtra;
    @BindView(R.id.mEffect)
    MyView mEffect;
    @BindView(R.id.rv_second)
    RotateView rotateView;
    @BindView(R.id.mv_test)
    MainCircleView mainCircleView;
    @BindView(R.id.mt_test)
    MTextView mTextView;
    @BindView(R.id.awv_test)
    AirWingView airWingView;
    @BindView(R.id.jcv_test)
    JunkCleanView junkCleanView;
    @BindView(R.id.cbv_test)
    CpuBoostView cpuBoostView;
    @BindView(R.id.sb_my)
    SwitchButton switchButton;
    @BindView(R.id.fv)
    FloatBallView floatBallView;
    @BindView(R.id.mainCleanNewView)
    MainCleanNewView mainCleanNewView;
    @BindView(R.id.cleanDetailView)
    CleanDetailView cleanDetailView;
    @BindView(R.id.mainBatteryCleanView)
    MainBatteryCleanView mainBatteryCleanView;
    @BindView(R.id.lineView)
    LineView lineView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    private void getIntentData(){
        axtra=getIntent().getIntExtra(KEY_INTENT,-1);
    }

    private void initView(){

        switch (axtra){
            case 1:
                mEffect.setVisibility(View.VISIBLE);
                break;
            case 2:
                rotateView.setVisibility(View.VISIBLE);
                rotateView.startAnimation();
                break;
            case 3:
                mainCircleView.setVisibility(View.VISIBLE);
                mainCircleView.startAnimation();
                break;
            case 4:
                mTextView.setVisibility(View.VISIBLE);
                break;
            case 5:
                airWingView.setVisibility(View.VISIBLE);
                airWingView.start();
                break;
            case 6:
                junkCleanView.setVisibility(View.VISIBLE);
                junkCleanView.start();
                break;
            case 7:
                cpuBoostView.setVisibility(View.VISIBLE);
                cpuBoostView.start();
                break;
            case 8:
                switchButton.setVisibility(View.VISIBLE);
                break;
            case 9:
                floatBallView.setVisibility(View.VISIBLE);
                floatBallView.startAnimation();
                break;
            case 10:
                mainCleanNewView.setVisibility(View.VISIBLE);
                analyseJunkFiles();
                mainCleanNewView.setCleanClickListener(new MainCleanNewView.CleanClickListener() {
                    @Override
                    public void onCheck() {
                        checkJunkFiles();
                    }

                    @Override
                    public void onClean() {
                        mainCleanNewView.setCleanState(MainCleanNewView.CleanState.BestState);
                    }

                    @Override
                    public void onBestCheck() {

                    }
                });
                break;
            case 11:
                cleanDetailView.setVisibility(View.VISIBLE);
                cleanDetailView.setCleanState(CleanDetailView.CleanDetailState.Checking);
                cleanDetailView.startAnimation();
                cleanDetailView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cleanDetailView.setCleanState(CleanDetailView.CleanDetailState.CheckFinish);
                    }
                },3500);
                break;
            case 12:
                mainBatteryCleanView.setVisibility(View.VISIBLE);
                mainBatteryCleanView.setCleanState(BatteryCleanView.BatteryState.Checking);
                mainBatteryCleanView.startAnimation();
                mainBatteryCleanView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainBatteryCleanView.setBatteryPower(20);
                        mainBatteryCleanView.setCleanState(BatteryCleanView.BatteryState.CheckFinish);
                        mainBatteryCleanView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainBatteryCleanView.setBatteryPower(10);
                                mainBatteryCleanView.setCleanState(BatteryCleanView.BatteryState.BestState);
                            }
                        },2000);
                    }
                },3500);

                break;
            case 13:
                lineView.setVisibility(View.VISIBLE);

                break;
        }

    }

    private void analyseJunkFiles(){
        mainCleanNewView.setCleanState(MainCleanNewView.CleanState.Analysing);
        mainCleanNewView.startAnimation();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                while(true){
                    size+=20;
                    if(size>6000){
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainCleanNewView.setCleanState(MainCleanNewView.CleanState.AnalyseFinish);
                    }
                });
            }
        }).start();
    }

    private void checkJunkFiles(){
        mainCleanNewView.setCleanState(MainCleanNewView.CleanState.Checking);
        mainCleanNewView.startAnimation();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                while(true){
                    size+=20;
                    final int finalSize = size;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainCleanNewView.setJunkFileSize(finalSize);
                        }
                    });

                    if(size>10000){
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainCleanNewView.setCleanState(MainCleanNewView.CleanState.CheckFinish);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mainCleanNewView!=null){
            mainCleanNewView.destroy();
        }
    }
}
