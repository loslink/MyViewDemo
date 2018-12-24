package com.loslink.myview.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * DipToPx
 * @author loslink
 * @time 2018/12/24 14:34
 */
public class DipToPx {

    private static DisplayMetrics dm;
    public static int dipToPx(Context mContext, int dip) {
        try {
            dm = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        } catch (Exception e) {
            dm = mContext.getResources().getDisplayMetrics();
        }
        return (int) (dip * dm.density + 0.5f);
    }

    public static int dipToPx(Context mContext, float dip) {
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) (dip * dm.density + 0.5f);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
