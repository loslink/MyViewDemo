package com.loslink.myview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.loslink.myview.widget.AirWingView;
import com.loslink.myview.widget.CpuBoostView;
import com.loslink.myview.widget.FloatBallView;
import com.loslink.myview.widget.JunkCleanView;
import com.loslink.myview.widget.MTextView;
import com.loslink.myview.widget.MainCircleView;
import com.loslink.myview.widget.MyView;
import com.loslink.myview.widget.RotateView;
import com.loslink.myview.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestViewActivity extends Activity {

    public static String KEY_INTENT;
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
    private int axtra;


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
                break;
            case 3:
                mainCircleView.setVisibility(View.VISIBLE);
                break;
            case 4:
                mTextView.setVisibility(View.VISIBLE);
                break;
            case 5:
                airWingView.setVisibility(View.VISIBLE);
                break;
            case 6:
                junkCleanView.setVisibility(View.VISIBLE);
                break;
            case 7:
                cpuBoostView.setVisibility(View.VISIBLE);
                break;
            case 8:
                switchButton.setVisibility(View.VISIBLE);
                break;
            case 9:
                floatBallView.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
