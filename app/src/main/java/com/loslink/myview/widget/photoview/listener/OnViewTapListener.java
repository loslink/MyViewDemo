package com.loslink.myview.widget.photoview.listener;

import android.view.View;

public interface OnViewTapListener {

    /**
     * 在用户点击imageview的地方接收的回调。如果用户点击视图上的任何位置，您将收到一个回调，点击“空白”将不会被忽略。
     *
     * @param view - View the user tapped.
     * @param x    - where the user tapped from the left of the View.
     * @param y    - where the user tapped from the top of the View.
     */
    void onViewTap(View view, float x, float y);
}
