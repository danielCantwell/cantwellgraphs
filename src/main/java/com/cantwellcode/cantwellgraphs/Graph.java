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
public class Graph extends View {

    private final String LOG = "Graph";

    private List<GraphItem> mGraphItems;

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

    private int mBarItemCount = 0;

    public Graph(Context context) {
        super(context);
        init();
    }

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mGraphItems = new ArrayList<>();
        mBackgroundColor = Color.parseColor("#FFFFFF");

        Log.d(LOG, "Constructor");

        mTouchEnabled = false;

        mTopPadding = true;
        mBottomPadding = true;

        mCustomBaseValue = false;
        mCustomTopValue = false;
    }

    public void addGraphItem(GraphItem graphItem) {
        if (graphItem instanceof BarItem) {
            ((BarItem) graphItem).setItemIndex(mBarItemCount);
            mBarItemCount++;
        }
        mGraphItems.add(graphItem);
    }

    public void addAllGraphItems(List<GraphItem> graphItems) {
        mGraphItems.addAll(graphItems);
    }

    public void removeGraphItem(GraphItem graphItem) { mGraphItems.remove(graphItem); }

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
            for (GraphItem item : mGraphItems) {
                float min = item.getMinValue();
                float max = item.getMaxValue();

                if (min < minY) {
                    minY = min;
                }
                if (max > maxY) {
                    maxY = max;
                }
            }
        } else {

            // Check for base value
            if (mCustomBaseValue) {
                minY = mBaseValue;
            } else {
                for (GraphItem item : mGraphItems) {
                    float min = item.getMinValue();

                    if (min < minY) {
                        minY = min;
                    }
                }
            }

            // Check for top value
            if (mCustomTopValue) {
                maxY = mTopValue;
            } else {
                for (GraphItem item : mGraphItems) {
                    float max = item.getMaxValue();

                    if (max > maxY) {
                        maxY = max;
                    }
                }
            }
        }

        for (GraphItem item : mGraphItems) {

            if (item instanceof BarItem) {
                ((BarItem) item).setItemCount(mBarItemCount);
            }

            item.setTopPaddingEnabled(mTopPadding);
            item.setBottomPaddingEnabled(mBottomPadding);
            item.updateItem(mWidth, mHeight, minY, maxY);
            item.drawItem(canvas);
        }

        Log.d(LOG, "minY: " + minY + " maxY: " + maxY);
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

        int countLineItems = 0;
        LineItem singleLineItem = null;
        for (GraphItem item : mGraphItems) {
            if (item instanceof LineItem) {
                singleLineItem = (LineItem) item;
                countLineItems++;
            }
        }

        if (countLineItems == 1) {
            // If there is only 1 line item
            // Find the closest point based only on the x coordinate
            if (singleLineItem.findDataPointX(x) != null)
                return singleLineItem.findDataPointX(x);
            else
                return null;

        } else {
            // If there is more that 1 line item
            // Find the closest point based on the x and y coordinate

            List<Point> closePoints = new ArrayList<>();
            for (GraphItem item : mGraphItems) {
                if (item instanceof LineItem) {
                    closePoints.add(((LineItem) item).findDataPoint(x, y));
                }
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

            if (p != null) {
                for (GraphItem item : mGraphItems) {
                    if (item instanceof LineItem) {
                        if (((LineItem) item).containsPoint(p)) {
                            ((LineItem) item).onTap(p);
                            invalidate();
                            break;
                        }
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
