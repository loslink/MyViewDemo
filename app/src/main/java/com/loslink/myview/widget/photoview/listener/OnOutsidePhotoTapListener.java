package com.loslink.myview.widget.photoview.listener;

import android.widget.ImageView;

/**
 * 当用户点击照片外部时回调(跟onPhotoTap相反)
 */
public interface OnOutsidePhotoTapListener {

    void onOutsidePhotoTap(ImageView imageView);
}
