package com.loslink.myview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.loslink.myview.R;

public class MainCleanNewView extends FrameLayout {

    private CleanNewView cleanNewView;
    private TextView tv_text,tv_clean;

    public MainCleanNewView(Context context) {
        this(context, null);
    }

    public MainCleanNewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainCleanNewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        findViews(context);
    }

    private void findViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_clean_new_view, this);
        cleanNewView = view.findViewById(R.id.cleanNewView);
        tv_text = view.findViewById(R.id.tv_text);
        tv_clean = view.findViewById(R.id.tv_clean);
    }


    public void startAnimation(float junkFileSize){
        cleanNewView.startAnimation(junkFileSize);
    }

    public void destroy(){
        if(cleanNewView!=null){
            cleanNewView.destroy();
        }
    }
}
