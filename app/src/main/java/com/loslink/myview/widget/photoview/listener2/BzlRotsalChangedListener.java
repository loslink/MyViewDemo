package com.loslink.myview.widget.photoview.listener2;

import android.graphics.PointF;

public interface BzlRotsalChangedListener {

    /**
     *
     * @param pointF1
     * @param pointF2
     * @param originDis
     * @param currentDis
     * @param originRadian
     * @param currentRadian
     */
    void onRotsalChange(PointF pointF1, PointF pointF2, float originDis, float currentDis, float originRadian, float currentRadian);
}
