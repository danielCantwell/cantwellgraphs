package com.cantwellcode.cantwellgraphs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by danielCantwell on 4/18/15.
 */
public class PointHighlight {

    private RectF mRect;

    private Paint mFillPaint;
    private Paint mStrokePaint;

    private float mRadius;

    private boolean mHasFill;
    private boolean mHasStroke;

    private Point mPoint;

    /****************************************
            Initialization
     ****************************************/

    public PointHighlight(boolean hasFill, boolean hasStroke) {
        mHasFill = hasFill;
        mHasStroke = hasStroke;

        setRadius(15);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        setStrokeColor(Color.BLACK);
        setStrokeWidth(5);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        setFillColor(Color.parseColor("#ddFFFFFF"));
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

    public void setStrokeWidth(float width) {
        mStrokePaint.setStrokeWidth(width);
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public void draw(Canvas canvas) {
        if (mPoint != null) {
            if (mHasFill)
                canvas.drawOval(mRect, mFillPaint);
            if (mHasStroke)
                canvas.drawCircle(mPoint.x, mPoint.y, mRadius, mStrokePaint);
        }
    }
}
