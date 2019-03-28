package com.loslink.myview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.loslink.myview.R;

import static com.loslink.myview.widget.BatteryCleanView.BatteryState.BestState;
import static com.loslink.myview.widget.BatteryCleanView.BatteryState.CheckFinish;

public class MainBatteryCleanView extends FrameLayout {

    private BatteryCleanView batteryCleanView;
    private TextView tv_text,tv_clean;
    private CleanClickListener cleanClickListener;

    public CleanClickListener getCleanClickListener() {
        return cleanClickListener;
    }

    public void setCleanClickListener(CleanClickListener cleanClickListener) {
        this.cleanClickListener = cleanClickListener;
    }

    public MainBatteryCleanView(Context context) {
        this(context, null);
    }

    public MainBatteryCleanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainBatteryCleanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        findViews(context);
        setListenr();
    }

    private void findViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_battery_clean_view, this);
        batteryCleanView = view.findViewById(R.id.batteryCleanView);
        tv_text = view.findViewById(R.id.tv_text);
        tv_clean = view.findViewById(R.id.tv_clean);
    }

    private void setListenr(){
        tv_clean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cleanClickListener!=null){
                    switch (batteryCleanView.getCleanState()){
                        case Checking:
                            break;
                        case CheckFinish:
                            cleanClickListener.onCheck();
                            break;
                        case BestState:
                            cleanClickListener.onCheck();
                            break;
                    }
                }

            }
        });
    }

    public void setCleanState(BatteryCleanView.BatteryState cleanState){
        batteryCleanView.setCleanState(cleanState);
        switch (cleanState){
            case Checking:
                tv_text.setVisibility(View.VISIBLE);
                tv_clean.setVisibility(View.GONE);
                break;
            case CheckFinish:
                tv_text.setVisibility(View.VISIBLE);
                tv_clean.setVisibility(View.VISIBLE);
                tv_clean.setText("CLEAN");
                break;
            case BestState:
                tv_text.setVisibility(View.VISIBLE);
                tv_clean.setVisibility(View.VISIBLE);
                tv_clean.setText("CHECK");
                tv_text.setText("Best state");
                break;
        }
    }

    public void startAnimation(){
        batteryCleanView.startAnimation();
    }

    public void setJunkFileSize(float junkFileSize){
        batteryCleanView.setJunkFileSize(junkFileSize);
        tv_text.setText((int)junkFileSize+" MB");
    }


    public void destroy(){
        if(batteryCleanView!=null){
            batteryCleanView.destroy();
        }
    }

    public interface CleanClickListener {
        void onCheck();
    }
}
