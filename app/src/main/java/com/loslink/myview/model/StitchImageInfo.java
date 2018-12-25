package com.loslink.myview.model;

import android.graphics.RectF;
import android.view.View;

import java.util.List;

public class StitchImageInfo {

    private String path;
    private View controllerView;
    private List<RectF> historyActions;
    private boolean isEditing=false;

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

    public List<RectF> getHistoryActions() {
        return historyActions;
    }

    public void setHistoryActions(List<RectF> historyActions) {
        this.historyActions = historyActions;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

}
