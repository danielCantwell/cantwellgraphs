package com.cantwellcode.cantwellgraphs;

import android.graphics.Canvas;

/**
 * Created by danielCantwell on 4/21/15.
 */
public abstract class GraphItem {

    protected int mWidth;
    protected int mHeight;

    protected float mMinY;
    protected float mMaxY;

    protected FillType mFillType;

    protected boolean mTopPaddingEnabled;
    protected boolean mBottomPaddingEnabled;

    protected abstract void init();
    protected void updateItem(int width, int height, float minY, float maxY) {
        mWidth = width;
        mHeight = height;
        mMinY = minY;
        mMaxY = maxY;
    }
    protected abstract void drawItem(Canvas canvas);

    protected abstract float getMinValue();
    protected abstract float getMaxValue();

    protected void setBottomPaddingEnabled(boolean enabled) {
        mBottomPaddingEnabled = enabled;
    }
    protected void setTopPaddingEnabled(boolean enabled) {
        mTopPaddingEnabled = enabled;
    }
}
