package com.cantwellcode.cantwellgraphs;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * Created by danielCantwell on 4/18/15.
 */
public class VerticalHighlight {

    private Paint mFillPaint;
    private Paint mStrokePaint;

    private RectF mRect;

    private float mWidth;

    private int mStrokeColor;
    private float mStrokeWidth;

    private int mSolidFillColor;
    private int mGradientFillStartColor;
    private int mGradientFillEndColor;

    private FillType mFillType;
    private boolean mHasStroke;

    private int mGraphHeight;

    private Point mPoint;

    /****************************************
            Initialization
     ****************************************/

    public VerticalHighlight(FillType fillType, boolean hasStroke) {
        setWidth(50);


        mFillType = fillType;
        mHasStroke = hasStroke;

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        setStrokeColor(Color.BLACK);
        setStrokeWidth(5.0f);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

        mRect = new RectF();
        mGraphHeight = 0;
    }

    public void update(int height, Point p) {
        mGraphHeight = height;
        mPoint = p;
        float x = mPoint.x;

        mRect.set(x - (mWidth / 2), 0, x + (mWidth / 2), mGraphHeight);

        if (mFillType == FillType.GRADIENT) {
            mFillPaint.setShader(new LinearGradient(0, 0, 0, height, mGradientFillEndColor, mGradientFillStartColor, Shader.TileMode.CLAMP));
        }
    }

    /****************************************
                Setters
     ****************************************/

    public void setWidth(float width) {
        mWidth = width;
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        mStrokePaint.setColor(mStrokeColor);
    }

    public void setStrokeWidth(float width) {
        mStrokeWidth = width;
        mStrokePaint.setStrokeWidth(mStrokeWidth);
    }

    public void setSolidFillColor(int solidFillColor) {
        mSolidFillColor = solidFillColor;
        mFillPaint.setColor(mSolidFillColor);
    }

    public void setGradientFillColor(int startColor, int endColor) {
        mGradientFillStartColor = startColor;
        mGradientFillEndColor = endColor;
    }

    /****************************************
                Getters
     ****************************************/

    public void draw(Canvas canvas) {
        if (mPoint != null) {
            if (mFillType != FillType.NONE)
                canvas.drawRect(mRect, mFillPaint);
            if (hasStroke())
                canvas.drawRect(mRect, mStrokePaint);
        }
    }

    public boolean hasStroke() {
        return mHasStroke;
    }
}
