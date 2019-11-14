package com.loslink.myview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.bt_recycler)
    public void recycler(View view) {
        Intent intent=new Intent(this,StitchImagesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.bt_1)
    public void bt_1(){
        startPage(1);
    }

    @OnClick(R.id.bt_2)
    public void bt_2(){
        startPage(2);
    }

    @OnClick(R.id.bt_3)
    public void bt_3(){
        startPage(3);
    }

    @OnClick(R.id.bt_4)
    public void bt_4(){
        startPage(4);
    }

    @OnClick(R.id.bt_5)
    public void bt_5(){
        startPage(5);
    }

    @OnClick(R.id.bt_6)
    public void bt_6(){
        startPage(6);
    }

    @OnClick(R.id.bt_7)
    public void bt_7(){
        startPage(7);
    }

    @OnClick(R.id.bt_8)
    public void bt_8(){
        startPage(8);
    }

    @OnClick(R.id.bt_9)
    public void bt_9(){
        startPage(9);
    }

    @OnClick(R.id.bt_10)
    public void bt_10(){
        startPage(10);
    }

    @OnClick(R.id.bt_11)
    public void bt_11(){
        startPage(11);
    }

    @OnClick(R.id.bt_12)
    public void bt_12(){
        startPage(12);
    }

    private void startPage(int value){
        Intent intent=new Intent(this,TestViewActivity.class);
        intent.putExtra(TestViewActivity.KEY_INTENT,value);
        startActivity(intent);
    }
}
