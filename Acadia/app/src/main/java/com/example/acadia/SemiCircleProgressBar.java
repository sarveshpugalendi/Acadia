package com.example.acadia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SemiCircleProgressBar extends View {

    private Paint mPaint;
    private RectF mOval;
    private float mProgress;

    public SemiCircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mOval = new RectF();
        mProgress = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        mPaint.setAntiAlias(true);
        mPaint.setColor(0xFFCCCCCC);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);

        mOval.set(0, 0, width, height * 2);

        canvas.drawArc(mOval, 180, 180, false, mPaint);

        mPaint.setColor(0xFF007ACC);

        canvas.drawArc(mOval, 180, 180 * (mProgress / 100), false, mPaint);
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }
}