package com.cantwellcode.cantwellgraphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by danielCantwell on 4/17/15.
 */
public class LineGraph extends View {

    private final String LOG = "LineGraph";

    private List<LineItem> mLineItems;

    private int mWidth;
    private int mHeight;

    private int mBackgroundColor;

    private boolean mTouchEnabled;

    private boolean mTopPadding;
    private boolean mBottomPadding;

    private boolean mCustomBaseValue;
    private float mBaseValue;

    private boolean mCustomTopValue;
    private float mTopValue;

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mLineItems = new ArrayList<>();
        mBackgroundColor = Color.parseColor("#FFFFFF");

        Log.d(LOG, "Constructor");

        mTouchEnabled = false;

        mTopPadding = true;
        mBottomPadding = true;

        mCustomBaseValue = false;
        mCustomTopValue = false;
    }

    public void addLineItem(LineItem lineItem) {
        mLineItems.add(lineItem);
    }

    public void setLineItems(List<LineItem> lineItems) {
        mLineItems = lineItems;
    }

    public void setGraphBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void enableTouch(boolean isEnabled) { mTouchEnabled = isEnabled; }

    public void removeGraphPaddingTop() {
        mTopPadding = false;
    }

    public void removeGraphPaddingBottom() {
        mBottomPadding = false;
    }

    public void setYBaseValue(float base) {
        mCustomBaseValue = true;
        mBaseValue = base;
    }

    public void setYTopValue(float top) {
        mCustomTopValue = true;
        mTopValue = top;
    }

    public void drawGraph() {
        invalidate();
        Log.d(LOG, "drawGraph");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(LOG, "onDraw");

        canvas.drawColor(mBackgroundColor);

        float minY = Float.MAX_VALUE;
        float maxY = 0;


        // If there is neither a customer base nor top value
        // Then only need to loop through once, finding both min and max values
        if (!mCustomBaseValue && !mCustomTopValue) {
            for (LineItem lineItem : mLineItems) {
                float min = lineItem.getMinValue();
                float max = lineItem.getMaxValue();

                Log.d(LOG, "min is " + min);
                Log.d(LOG, "max is " + max);

                if (min < minY) {
                    minY = min;
                    Log.d(LOG, "minY set to " + minY);
                }
                if (max > maxY) {
                    maxY = max;
                    Log.d(LOG, "maxY set to " + maxY);
                }
            }
        } else {

            // Check for base value
            if (mCustomBaseValue) {
                minY = mBaseValue;
            } else {
                for (LineItem lineItem : mLineItems) {
                    float min = lineItem.getMinValue();

                    Log.d(LOG, "min is " + min);

                    if (min < minY) {
                        minY = min;
                        Log.d(LOG, "minY set to " + minY);
                    }
                }
            }

            // Check for top value
            if (mCustomTopValue) {
                maxY = mTopValue;
            } else {
                for (LineItem lineItem : mLineItems) {
                    float max = lineItem.getMaxValue();

                    Log.d(LOG, "max is " + max);

                    if (max > maxY) {
                        maxY = max;
                        Log.d(LOG, "maxY set to " + maxY);
                    }
                }
            }
        }

        for (LineItem lineItem : mLineItems) {

            lineItem.setTopPaddingEnabled(mTopPadding);
            lineItem.setBottomPaddingEnabled(mBottomPadding);
            lineItem.update(mWidth, mHeight, minY, maxY);
            lineItem.draw(canvas);

            Log.d(LOG, "minY: " + minY + " maxY: " + maxY);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // padding
        int xPad = getPaddingLeft() + getPaddingRight();
        int yPad = getPaddingTop() + getPaddingBottom();

        mWidth = w - xPad;
        mHeight = h - yPad;

        Log.d(LOG, "Width: " + mWidth + " Height: " + mHeight);
    }


    private Point findDataPoint(float x, float y) {

        if (mLineItems.size() == 1) {
            // If there is only 1 line item
            // Find the closest point based only on the x coordinate
            if (mLineItems.get(0).findDataPointX(x) != null)
                return mLineItems.get(0).findDataPointX(x);
            else
                return null;

        } else {
            // If there is more that 1 line item
            // Find the closest point based on the x and y coordinate

            List<Point> closePoints = new ArrayList<>();
            for (LineItem lineItem : mLineItems) {
                closePoints.add(lineItem.findDataPoint(x, y));
            }

            float shortestDistance = Float.NaN;
            Point closest = null;

            for (Point p : closePoints) {

                float x1 = p.x;
                float y1 = p.y;
                float x2 = x;
                float y2 = y;

                float distance = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

                if (closest == null || distance < shortestDistance) {
                    shortestDistance = distance;
                    closest = p;
                }
            }

            if (closest != null) {
                return closest;
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchEnabled) {
            Point p = findDataPoint(event.getX(), event.getY());
            Log.d(LOG, "onTouch");

            if (p != null) {
                Log.d(LOG, "Found Nearby Datapoint");
                for (LineItem lineItem : mLineItems) {
                    if (lineItem.containsPoint(p)) {
                        Log.d(LOG, "Found line that contains the point");
                        lineItem.onTap(p);
                        invalidate();
                        break;
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
