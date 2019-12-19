package com.loslink.myview.widget.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class StationDrawable extends Drawable {

    //region variants
    private Paint mPaint;
    private float radius;
    private int color;
    private String text;
    private int textSize;
    //endregion

    //region constructor
    public StationDrawable(@Nullable Float radius, @Nullable Integer color, @Nullable String text, @Nullable Integer textSize) {
        mPaint = new Paint();
        if (radius == null)
            this.radius = 18f;
        else
            this.radius = radius;
        if (color == null)
            this.color = Color.parseColor("#FF0077");
        else
            this.color = color;
        if (text == null)
            this.text = "1";
        else
            this.text = text;
        if (textSize == null)
            this.textSize = 28;
        else
            this.textSize = textSize;
    }
    //endregion

    //region override methods
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        canvas.drawCircle( radius, radius, radius, mPaint);

        Path path = new Path();
        path.moveTo((float) (radius - radius / 2 * Math.sqrt(3)), radius + radius / 2);
        path.lineTo((float) (radius + radius / 2 * Math.sqrt(3)), radius + radius / 2);
        path.lineTo(radius, radius * 3);
        path.close();
        canvas.drawPath(path, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(textSize);
        if (Float.parseFloat(text) < 10)
            canvas.drawText(text, radius / 2, radius * 2 - radius / 5, mPaint);
        else
            canvas.drawText(text, radius / 2 - radius / 3, radius * 2 - radius / 5, mPaint);

        mPaint.setColor(Color.parseColor("#3E4F86"));
        canvas.drawOval(radius - radius / 2, radius * 3 + radius / 4, radius + radius / 2, radius * 3 + radius / 4 * 3, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
    //endregion

    //region getter&setter
    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
        invalidateSelf();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidateSelf();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidateSelf();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidateSelf();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        invalidateSelf();
    }

    public float getWidth() {
        return radius * 2;
    }

    public float getHeight() {
        return radius * 3 + radius / 4 * 3;
    }
    //endregion
}