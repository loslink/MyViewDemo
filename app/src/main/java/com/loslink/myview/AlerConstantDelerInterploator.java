package com.loslink.myview;

import android.animation.TimeInterpolator;
import android.util.Log;

/**
 * @author loslink
 * @time 2018/7/2 15:24
 */

public class AlerConstantDelerInterploator implements TimeInterpolator {

    double fra=1.6;
    @Override
    public float getInterpolation(float input) {

        float result = 0;
        float re1 = (float) Math.pow(Double.valueOf(input+""),fra);
        float re2 = 1.0f-(float) Math.pow(Double.valueOf((1.0f - input)+""),fra);
        if(re1<0.3){
            result = re1;
        }else if(input>0.7){
            result = re2;
        }else{
            result = input;
        }
        Log.v("AlerConstaInterploator",result+"");

        return result;
    }
}
