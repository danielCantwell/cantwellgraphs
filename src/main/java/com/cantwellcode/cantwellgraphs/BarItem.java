package com.cantwellcode.cantwellgraphs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by danielCantwell on 4/21/15.
 */
public class BarItem extends GraphItem {
    private final String LOG = "BarItem";

    private float mValue;
    private int mItemIndex;
    private int mItemCount;

    private RectF mRect;

    private Paint mStrokePaint;
    private final String DEFAULT_STROKE_COLOR = "#000000";
    private final float DEFAULT_STROKE_WIDTH = 10;

    private Paint mFillPaint;
    private int mGradientStartColor;
    private int mGradientEndColor;
    private final String DEFAULT_FILL_START_COLOR = "#FFFFFF";
    private final String DEFAULT_FILL_END_COLOR = "#000000";

    public BarItem(float value, FillType fillType) {
        mValue = value;
        mFillType = fillType;
        init();
    }

    /**
     * Sets default values
     */
    @Override
    protected void init() {
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        setStrokeColor(Color.parseColor(DEFAULT_STROKE_COLOR));
        setStrokeWidth(DEFAULT_STROKE_WIDTH);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        setSolidFillColor(Color.parseColor(DEFAULT_FILL_END_COLOR));
        setGradientFillColor(Color.parseColor(DEFAULT_FILL_START_COLOR), Color.parseColor(DEFAULT_FILL_END_COLOR));

        mTopPaddingEnabled = true;
        mBottomPaddingEnabled = true;

        mItemIndex = 0;
        mItemCount = 1;
    }

    @Override
    protected void updateItem(int width, int height, float minY, float maxY, int labelWidth) {
        super.updateItem(width, height, minY, maxY, labelWidth);

        if (mFillType == FillType.GRADIENT) {
            mFillPaint.setShader(new LinearGradient(0, 0, 0, mHeight, mGradientEndColor, mGradientStartColor, Shader.TileMode.CLAMP));
        }

        createRect();
    }

    @Override
    protected void drawItem(Canvas canvas) {
        Log.d(LOG, "drawing Item");
        if (hasFill()) {
            // If the rect has a fill, draw the fill
            canvas.drawRect(mRect, mFillPaint);
        }
        // Draw Line
        canvas.drawRect(mRect, mStrokePaint);
    }

    @Override
    protected float getMinValue() {
        return mValue;
    }

    @Override
    protected float getMaxValue() {
        return mValue;
    }

    protected void setItemIndex(int itemIndex) {
        mItemIndex = itemIndex;
    }

    protected void setItemCount(int itemCount) {
        mItemCount = itemCount;
    }

    private void createRect() {
        float maxX = mItemCount;

        // ratio used for normalizing the coordinates to the graph space
        float maxYCoordinate = mTopPaddingEnabled ? mHeight * 9 / 10 : mHeight;
        float minYCoordinate = mBottomPaddingEnabled ? mHeight / 10 : 0;
        float dX = mWidth / maxX;
        float leftX = mItemIndex == 0 ? 0 : mItemIndex * dX;
        float rightX = leftX + dX;
        float topY = getYCoordinate(mValue, minYCoordinate, maxYCoordinate);

        Log.d(LOG, String.format("%.1f  %.1f  %.1f  %.1f", leftX, topY, rightX, 0.0));

        mRect = new RectF(leftX, topY, rightX, mHeight);
    }

    private float getYCoordinate(float value, float minYCoord, float maxYCoord) {
        if (value == mMinY) {
            return mHeight - minYCoord;
        } else if (value == mMaxY) {
            return mHeight - maxYCoord;
        } else {
            return mHeight - (minYCoord + ((value - mMinY) * (maxYCoord - minYCoord) / (mMaxY - mMinY)));
        }
    }


    public void setStrokeColor(int color) {
        mStrokePaint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        mStrokePaint.setStrokeWidth(width);
    }

    public void setSolidFillColor(int color) {
        mFillPaint.setColor(color);
    }

    public void setGradientFillColor(int startColor, int endColor) {
        mGradientStartColor = startColor;
        mGradientEndColor = endColor;
    }

    protected boolean hasFill() {
        return mFillType != FillType.NONE;
    }
}
