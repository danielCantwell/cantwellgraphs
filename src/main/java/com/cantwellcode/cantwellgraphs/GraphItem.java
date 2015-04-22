package com.cantwellcode.cantwellgraphs;

import android.graphics.Canvas;

/**
 * Created by danielCantwell on 4/21/15.
 */
public abstract class GraphItem {

    // Width and Height of the graph
    protected int mWidth;
    protected int mHeight;

    protected float mMinY;  // The minimum y value of all items in the graph
    protected float mMaxY;  // The maximum y value of all items in the graph

    protected float mLabelWidth;    // Width of the largest y value

    protected FillType mFillType;   // None, Solid, Gradient

    //  If top padding is enabled, the maximum y coordinate is 9/10 the height of the graph
    protected boolean mTopPaddingEnabled;
    //  If bottom padding is enabled, the minimum y coordinate is 1/10 the height of the graph,
    //  but if there is "fill" for any of the lines, it goes all the way to the bottom
    protected boolean mBottomPaddingEnabled;

    abstract void init(); // Initialization function

    /*
        Before an item is drawn via drawItem, the graph calls updateItem
        It passes these values so the item knows how to draw itself
    */
    protected void updateItem(int width, int height, float minY, float maxY, int labelWidth) {
        mWidth = width;
        mHeight = height;
        mMinY = minY;
        mMaxY = maxY;
        mLabelWidth = labelWidth;
    }
    protected abstract void drawItem(Canvas canvas);

    /**
     * Calculate the y coordinate for a given value, based on the min and max values and coordinates
     * @param value
     * @param minYCoord
     * @param maxYCoord
     * @return
     */
    float getYCoordinate(float value, float minYCoord, float maxYCoord) {
        if (value == mMinY) {
            return mHeight - minYCoord;
        } else if (value == mMaxY) {
            return mHeight - maxYCoord;
        } else {
            return mHeight - (minYCoord + ((value - mMinY) * (maxYCoord - minYCoord) / (mMaxY - mMinY)));
        }
    }

    protected abstract float getMinValue();
    protected abstract float getMaxValue();

    protected void setBottomPaddingEnabled(boolean enabled) {
        mBottomPaddingEnabled = enabled;
    }
    protected void setTopPaddingEnabled(boolean enabled) {
        mTopPaddingEnabled = enabled;
    }
}
