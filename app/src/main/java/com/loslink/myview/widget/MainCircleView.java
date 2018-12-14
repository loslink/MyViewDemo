package com.loslink.myview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loslink.myview.R;

public class MainCircleView extends FrameLayout {

    private ImageView iv_animation_first;
    private RotateView rotateView;

    public MainCircleView(Context context) {
        this(context, null);
    }

    public MainCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        findViews(context);
    }

    private void findViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_circle_view, this);
        iv_animation_first = view.findViewById(R.id.iv_animation_first);
        rotateView=view.findViewById(R.id.rv_second);
    }


    public void startAnimation(){
        MyAnimationDrawable.animateRawManuallyFromXML(R.drawable.animation_list, iv_animation_first,
                new Runnable() {
                    @Override
                    public void run() {
                        //动画开始

                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        //动画结束
                        iv_animation_first.setVisibility(View.GONE);
                        rotateView.setVisibility(View.VISIBLE);
                        rotateView.startAnimation();
                    }
                },false);
    }

}
