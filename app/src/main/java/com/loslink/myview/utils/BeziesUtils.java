package com.loslink.myview.utils;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class BeziesUtils {
    private int FAME = 1000;

    public BeziesUtils() {
    }

    public static BeziesUtils getInstance() {
        return Instance.beziesUtils;
    }

    static class Instance {
        static BeziesUtils beziesUtils = new BeziesUtils();
    }

    /**
     * deCasteljau算法
     * 所有高阶的贝塞尔曲线点都可以看成2阶贝塞尔曲线的点。
     * controller 控制点集合
     * startPf 起始点
     * stopPf终止点
     *
     * @param t 时间 0f-1f
     * @return
     */
    public float deCasteljauX(List<PointF> controller, PointF startPf, PointF stopPf, float t) {
        List<PointF> mControlPoints = combinationPfList(controller, startPf, stopPf);
        return deCasteljauX(mControlPoints, controller.size() + 1, 0, t);
    }

    /**
     * deCasteljau算法
     * 所有高阶的贝塞尔曲线点都可以看成2阶贝塞尔曲线的点。
     * controller 控制点集合
     * startPf 起始点
     * stopPf终止点
     *
     * @param t 时间 0f-1f
     * @return
     */
    public float deCasteljauY(List<PointF> controller, PointF startPf, PointF stopPf, float t) {
        List<PointF> mControlPoints = combinationPfList(controller, startPf, stopPf);
        return deCasteljauY(mControlPoints, controller.size() + 1, 0, t);
    }

    private float deCasteljauX(List<PointF> mControlPoints, int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * mControlPoints.get(j).x + t * mControlPoints.get(j + 1).x;
        }
        return (1 - t) * deCasteljauX(mControlPoints, i - 1, j, t) + t * deCasteljauX(mControlPoints, i - 1, j + 1, t);
    }

    private List<PointF> combinationPfList(List<PointF> controller, PointF startPf, PointF stopPf) {
        List<PointF> pointFS = new ArrayList<>();
        pointFS.add(startPf);
        pointFS.addAll(controller);
        pointFS.add(stopPf);
        return pointFS;
    }

    private float deCasteljauY(List<PointF> mControlPoints, int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * mControlPoints.get(j).y + t * mControlPoints.get(j + 1).y;
        }
        return (1 - t) * deCasteljauY(mControlPoints, i - 1, j, t) + t * deCasteljauY(mControlPoints, i - 1, j + 1, t);
    }

}
