package com.loslink.myview.model;

import android.graphics.RectF;
import android.view.View;

import java.util.List;

public class StitchHistoryAction {

    private RectF action;
    private int actionIndex=-1;//动作下标
    private int cuterIndex=-1;//剪刀位置

    public StitchHistoryAction(RectF action,int actionIndex,int cuterIndex){
        this.action=action;
        this.actionIndex=actionIndex;
        this.cuterIndex=cuterIndex;
    }

    public RectF getAction() {
        return action;
    }

    public void setAction(RectF action) {
        this.action = action;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(int actionIndex) {
        this.actionIndex = actionIndex;
    }

    public int getCuterIndex() {
        return cuterIndex;
    }

    public void setCuterIndex(int cuterIndex) {
        this.cuterIndex = cuterIndex;
    }


}
