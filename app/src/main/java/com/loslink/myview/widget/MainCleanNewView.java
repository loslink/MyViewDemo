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
    private CleanState currentCleanState;
    private CleanClickListener cleanClickListener;

    public enum CleanState {
        None,//错误状态
        Analysing,//扫描阶段
        AnalyseFinish,//扫描完成，发现垃圾
        Checking,//检查垃圾
        CheckFinish,//垃圾检查完成
        BestState//最佳状态
    }

    public CleanClickListener getCleanClickListener() {
        return cleanClickListener;
    }

    public void setCleanClickListener(CleanClickListener cleanClickListener) {
        this.cleanClickListener = cleanClickListener;
    }


    public MainCleanNewView(Context context) {
        this(context, null);
    }

    public MainCleanNewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainCleanNewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        findViews(context);
        setListenr();
    }

    private void findViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_clean_new_view, this);
        cleanNewView = view.findViewById(R.id.cleanNewView);
        tv_text = view.findViewById(R.id.tv_text);
        tv_clean = view.findViewById(R.id.tv_clean);
    }

    private void setListenr(){
        tv_clean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cleanClickListener!=null){
                    switch (currentCleanState){
                        case AnalyseFinish:
                            cleanClickListener.onCheck();
                            break;
                        case CheckFinish:
                            cleanClickListener.onClean();
                            break;
                    }
                }

            }
        });
    }

    public void setCleanState(CleanState cleanState){
        cleanNewView.setCleanState(cleanState);
        currentCleanState=cleanState;
        switch (cleanState){
            case Analysing:
                tv_text.setVisibility(View.GONE);
                tv_clean.setVisibility(View.GONE);
                break;
            case AnalyseFinish:
                tv_text.setVisibility(View.VISIBLE);
                tv_clean.setVisibility(View.VISIBLE);
                tv_text.setText("Junk Files Found");
                tv_clean.setText("CHECK");
                break;
            case Checking:
                tv_text.setVisibility(View.VISIBLE);
                tv_clean.setVisibility(View.GONE);
                break;
            case CheckFinish:
                tv_text.setVisibility(View.VISIBLE);
                tv_clean.setVisibility(View.VISIBLE);
                tv_clean.setText("CLEAN");
                break;
        }
    }

    public void startAnimation(){
        cleanNewView.startAnimation();
    }

    public void setJunkFileSize(float junkFileSize){
        cleanNewView.setJunkFileSize(junkFileSize);
        tv_text.setText((int)junkFileSize+" MB");
    }


    public void destroy(){
        if(cleanNewView!=null){
            cleanNewView.destroy();
        }
    }

    public interface CleanClickListener {
        void onCheck();
        void onClean();
    }
}
