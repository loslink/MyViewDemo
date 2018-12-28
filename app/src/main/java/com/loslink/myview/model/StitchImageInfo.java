package com.loslink.myview.model;

import android.graphics.RectF;
import android.view.View;

import java.util.List;

public class StitchImageInfo {

    private String path;
    private View controllerView;
    private List<StitchHistoryAction> historyActions;
    private boolean isEditing=false;
    private RectF currentCropRectF;//当前裁剪框状态
    private boolean toCut=false;//是否执行剪裁
    private boolean goBack=false;//是否返回上一步
    private float scaleValue;//缩放后图相对于原图的缩放比，即：原图/缩放后

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public View getControllerView() {
        return controllerView;
    }

    public void setControllerView(View controllerView) {
        this.controllerView = controllerView;
    }

    public List<StitchHistoryAction> getHistoryActions() {
        return historyActions;
    }

    public void setHistoryActions(List<StitchHistoryAction> historyActions) {
        this.historyActions = historyActions;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public RectF getCurrentCropRectF() {
        return currentCropRectF;
    }

    public void setCurrentCropRectF(RectF currentCropRectF) {
        this.currentCropRectF = currentCropRectF;
    }

    public boolean isToCut() {
        return toCut;
    }

    public void setToCut(boolean toCut) {
        this.toCut = toCut;
    }

    public boolean isGoBack() {
        return goBack;
    }

    public void setGoBack(boolean goBack) {
        this.goBack = goBack;
    }

    public float getScaleValue() {
        return scaleValue;
    }

    public void setScaleValue(float scaleValue) {
        this.scaleValue = scaleValue;
    }


}
