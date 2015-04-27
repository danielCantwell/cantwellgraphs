package com.cantwellcode.cantwellgraphs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by danielCantwell on 4/18/15.
 *
 * Copyright (c) Cantwell Code 2015, All Rights Reserved
 */
public class PointHighlight {

    private RectF mRect;

    private Paint mFillPaint;
    private Paint mStrokePaint;
    private Paint mTextPaint;

    private float mRadius;

    private boolean mHasFill;
    private boolean mHasStroke;
    private boolean mShowValue;

    private Point mPoint;

    private ValueDisplay mValueDisplay;

    /****************************************
            Initialization
     ****************************************/

    public PointHighlight(boolean hasFill, boolean hasStroke, boolean showValue) {
        mHasFill = hasFill;
        mHasStroke = hasStroke;
        mShowValue = showValue;

        setRadius(30);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        setStrokeColor(Color.BLACK);
        setStrokeWidth(5);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        setFillColor(Color.parseColor("#ddFFFFFF"));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        setTextColor(Color.BLACK);
        setTextSize(20);

        mValueDisplay = null;
    }

    public void update(Point point) {
        mPoint = point;
        float x = mPoint.x;
        float y = mPoint.y;

        mRect = new RectF(x - mRadius, y - mRadius, x + mRadius, y + mRadius);
    }

    /****************************************
                Setters
     ****************************************/

    public void setFillColor(int color) {
        mFillPaint.setColor(color);
    }

    public void setStrokeColor(int color) {
        mStrokePaint.setColor(color);
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    public void setTextSize(float size) {
        mTextPaint.setTextSize(size);
    }

    public void setStrokeWidth(float width) {
        mStrokePaint.setStrokeWidth(width);
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public void addCustomValueDisplay(ValueDisplay vs) { mValueDisplay = vs; }

    public void draw(Canvas canvas) {
        if (mPoint != null) {
            if (mHasFill)
                canvas.drawOval(mRect, mFillPaint);
            if (mHasStroke)
                canvas.drawCircle(mPoint.x, mPoint.y, mRadius, mStrokePaint);
            if (mShowValue) {
                float x = mPoint.x;
                float y = mPoint.y - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
                if (mValueDisplay == null) {
                    canvas.drawText(getValue(), x, y, mTextPaint);
                } else {
                    canvas.drawText(mValueDisplay.setHighlightValue(mPoint), x, y, mTextPaint);
                }
            }
        }
    }

    private String getValue() {
        return String.valueOf(mPoint.value);
    }

    public interface ValueDisplay {
        String setHighlightValue(Point p);
    }
}
