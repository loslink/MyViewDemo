package com.loslink.myview.model;

import android.graphics.PointF;

import com.loslink.myview.utils.BeziesUtils;

import java.util.ArrayList;
import java.util.List;

public class Bubble {
    public int noChangeAlph;
    public float value = 0f;
    public int alph;
    public PointF pointF;
    public PointF startPoint, controllPoint, controllPoint2, centerPoint, endPoint;
    public float radio;
    public float noChangeRadio;
    public boolean isFinish = false;

    public void notifyData(boolean isSpeed) {
        List<PointF> controlls = new ArrayList<>();
        if (isSpeed)
            isFinish = true;
        if (!isSpeed) {
            controlls.add(controllPoint);

            pointF.x = BeziesUtils.getInstance().deCasteljauX(controlls, startPoint, centerPoint, value);
            pointF.y = BeziesUtils.getInstance().deCasteljauY(controlls, startPoint, centerPoint, value);
            radio = noChangeRadio + noChangeRadio * value / 2;
            alph = (int) (noChangeAlph - noChangeAlph * (value * 0.7));
        } else {
            radio = noChangeRadio + noChangeRadio / 2 - noChangeRadio * value;
            controlls.add(controllPoint2);
            pointF.x = BeziesUtils.getInstance().deCasteljauX(controlls, centerPoint, endPoint, value);
            pointF.y = BeziesUtils.getInstance().deCasteljauY(controlls, centerPoint, endPoint, value);
            alph = (int) (noChangeAlph - (noChangeAlph * 0.7 + noChangeAlph * (value * 0.3)));

        }


    }

    public void notifyData(float value, boolean isSpeed) {
        List<PointF> controlls = new ArrayList<>();
        if (isSpeed)
            isFinish = true;
        if (!isSpeed) {
            controlls.add(controllPoint);

            pointF.x = BeziesUtils.getInstance().deCasteljauX(controlls, startPoint, centerPoint, value);
            pointF.y = BeziesUtils.getInstance().deCasteljauY(controlls, startPoint, centerPoint, value);
            radio = noChangeRadio + noChangeRadio * value / 2;
            alph = (int) (noChangeAlph - noChangeAlph * (value * 0.7));
        } else {
            radio = noChangeRadio + noChangeRadio / 2 - noChangeRadio * value;
            controlls.add(controllPoint2);
            pointF.x = BeziesUtils.getInstance().deCasteljauX(controlls, centerPoint, endPoint, value);
            pointF.y = BeziesUtils.getInstance().deCasteljauY(controlls, centerPoint, endPoint, value);
            alph = (int) (noChangeAlph - (noChangeAlph * 0.7 + noChangeAlph * (value * 0.3)));

        }


    }


    public Bubble(PointF startPoint, PointF centerPoint, PointF endPoint, float radio, float windth) {
        noChangeRadio = radio;
        this.radio = radio;
        this.startPoint = startPoint;
        this.centerPoint = centerPoint;
        this.endPoint = endPoint;
        float controll1Dx = (float) (Math.random() * windth);
        float controll1Dy = startPoint.y - (float) (Math.random() * (startPoint.y - centerPoint.y));
        float controll2Dx = (float) (Math.random() * windth);
        float controll2Dy = centerPoint.y - (float) (Math.random() * (centerPoint.y - endPoint.y));
        controllPoint = new PointF(controll1Dx, controll1Dy);
        controllPoint2 = new PointF(controll2Dx, controll2Dy);
        pointF = new PointF();
        noChangeAlph = alph = 77 + (int) (Math.random() * 27);
    }


    public Bubble(PointF pointF, PointF startPoint, PointF controllPoint, PointF controllPoint2, PointF centerPoint, PointF endPoint, float radio) {
        this.pointF = pointF;
        this.startPoint = startPoint;
        this.controllPoint = controllPoint;
        this.controllPoint2 = controllPoint2;
        this.centerPoint = centerPoint;
        this.endPoint = endPoint;
        this.radio = radio;
    }
}
