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
 *
 * Copyright (c) Cantwell Code 2015, All Rights Reserved
 */
public class Graph extends View {

    private final String LOG = "Graph";

    private List<GraphItem> mGraphItems;    // list of generic graph items

    private int mWidth;
    private int mHeight;

    private int mBackgroundColor;

    private boolean mTouchEnabled;      // can the user interact with the graph

    private boolean mTopPadding;        // will the graph leave space at the top
    private boolean mBottomPadding;     // will the graph leave space (besides fill) at the bottom

    private boolean mCustomBaseValue;   // the user can set a custom value for the base y value
    private float mBaseValue;           // the y value at the bottom of the graph

    private boolean mCustomTopValue;    // the user can set a custom value for the top y value
    private float mTopValue;            // the y value at the top of the graph

    private int mBarItemCount = 0;      // to display a bar graph, we need to know how many bars exist

    private boolean mDisplayYLabels;    // the user can choose to display labels for the y coordinate

    private Paint mLabelPaint;          // paint object used for drawing the labels


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

    /* Initialization function called by all three constructors */
    private void init() {

        mGraphItems = new ArrayList<>();
        mBackgroundColor = Color.parseColor("#FFFFFF"); // set default background color to white

        mTouchEnabled = false;      // disable touch interaction by default

        mTopPadding = true;         // enable top padding by default
        mBottomPadding = false;     // disable bottom padding by default

        mCustomBaseValue = false;   // disable custom base value by default
        mCustomTopValue = false;    // disable custom top value by default

        mDisplayYLabels = false;    // hide y coordinate labels by default

        /* Initialize the label paint object */
        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setTextAlign(Paint.Align.RIGHT);
        setLabelColor(Color.BLACK);
        setLabelSize(30);
    }

    /**
     * Add a graph item
     * @param graphItem - either a LineItem or a BarItem (GraphItem is an abstract class)
     */
    public void addGraphItem(GraphItem graphItem) {
        if (graphItem instanceof BarItem) {
            ((BarItem) graphItem).setItemIndex(mBarItemCount);
            mBarItemCount++;
        }
        mGraphItems.add(graphItem);
    }

    /**
     * Add a list of graph items
     * @param graphItems
     */
    public void addAllGraphItems(List<GraphItem> graphItems) {
        mGraphItems.addAll(graphItems);
    }

    /**
     * Remove an item from the graph
     * @param graphItem
     */
    public void removeGraphItem(GraphItem graphItem) { mGraphItems.remove(graphItem); }

    /**
     * Change the background color
     * @param color
     */
    public void setGraphBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    /**
     * Enable/Disable touch interaction with the graph
     * @param isEnabled
     */
    public void enableTouch(boolean isEnabled) { mTouchEnabled = isEnabled; }

    /**
     * If Top Padding is enabled, the maximum value will be at 9/10 the height of the graph
     * Otherwise, the maximum value will be at the top of the graph
     * @param enabled
     */
    public void enableGraphPaddingTop(boolean enabled) {
        mTopPadding = enabled;
    }

    /**
     * If Bottom Padding is enabled, the minimum value will be at 1/10 the height of the graph,
     * but if a LineItem has "fill", the fill will still go all the way to the bottom.
     * Otherwise, the minimum value will be at the bottom of the graph
     * @param enabled
     */
    public void enableGraphPaddingBottom(boolean enabled) {
        mBottomPadding = enabled;
    }

    /**
     * Choose to display the y coordinate labels
     * @param display
     */
    public void displayYLabels(boolean display) {
        mDisplayYLabels = display;
    }

    /**
     * Set the text color for the y coordinate labels
     * @param color
     */
    public void setLabelColor(int color) {
        mLabelPaint.setColor(color);
    }

    /**
     * Set the text size for the y coordinate labels
     * @param size
     */
    public void setLabelSize(float size) {
        mLabelPaint.setTextSize(size);
    }

    /**
     * You may choose to set a custom y value for the bottom of the graph
     * @param base
     */
    public void setYBaseValue(float base) {
        mCustomBaseValue = true;
        mBaseValue = base;
    }

    /**
     * You may choose to set a custom y value for the top of the graph
     * @param top
     */
    public void setYTopValue(float top) {
        mCustomTopValue = true;
        mTopValue = top;
    }

    /**
     * Call this anytime you want to refresh / draw the graph
     */
    public void drawGraph() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(LOG, "onDraw");

        canvas.drawColor(mBackgroundColor);

        float minY = Float.MAX_VALUE;
        float maxY = 0;

        /* If there is neither a custom base nor top value
           Then only need to loop through once, finding both min and max values */
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

            /* Check for base value */
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

            /* Check for top value */
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

        /*  Draw each graph item  */
        for (GraphItem item : mGraphItems) {

            if (item instanceof BarItem) {
                ((BarItem) item).setItemCount(mBarItemCount);
            }

            /* The user sets top/bottom padding enabled for the entire graph, so we must past that on to each item */
            item.setTopPaddingEnabled(mTopPadding);
            item.setBottomPaddingEnabled(mBottomPadding);
            /* Update the item before drawing, so that it knows how to draw itself */
            item.updateItem(mWidth, mHeight, minY, maxY, mDisplayYLabels ? getLabelWidth(String.valueOf(maxY)) : 0);
            /* Draw the item */
            item.drawItem(canvas);
        }

        /*  Draw y labels if necessary  */
        if (mDisplayYLabels) drawLabels(canvas, minY, maxY, getLabelWidth(String.valueOf(maxY)));
    }

    /**
     * Draws labels for the y-coordinate
     * @param canvas    - canvas used to draw on
     * @param minY  - min label value
     * @param maxY  - max label value
     * @param maxLabelWidth - used to determine where to draw the labels and separator line
     */
    private void drawLabels(Canvas canvas, float minY, float maxY, int maxLabelWidth) {
        /* Displaying 5 values for the y coordinate. We already know the min and max, so we need to find the 3 in between */
        float midValue = (minY + maxY) / 2;
        float lowValue = (minY + maxY) / 4;
        float highValue = (minY + maxY) * 3 / 4;

        /* Find the y coordinate of the min and max labels */
        float minYCoordinate = mBottomPadding ? mHeight * 9 / 10 : mHeight - mLabelPaint.descent();
        float maxYCoordinate = mTopPadding ? mHeight / 10 : 3 * mLabelPaint.descent();
        /* Find the y coordinates for the middle ones, based on the min and max coordinates */
        float midYCoordinate = (minYCoordinate + maxYCoordinate) / 2;
        float lowYCoordinate = (minYCoordinate + midYCoordinate) / 2;
        float highYCoordinate = (maxYCoordinate + midYCoordinate) / 2;

        /* Draw all five labels */
        canvas.drawText(String.format("%.1f  ", minY), maxLabelWidth, minYCoordinate, mLabelPaint);
        canvas.drawText(String.format("%.1f  ", lowValue), maxLabelWidth, lowYCoordinate, mLabelPaint);
        canvas.drawText(String.format("%.1f  ", midValue), maxLabelWidth, midYCoordinate, mLabelPaint);
        canvas.drawText(String.format("%.1f  ", highValue), maxLabelWidth, highYCoordinate, mLabelPaint);
        canvas.drawText(String.format("%.1f  ", maxY), maxLabelWidth, maxYCoordinate, mLabelPaint);

        /* Draw a line separating the labels and the graph itself */

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(maxLabelWidth - 2, 0, maxLabelWidth - 2, mHeight, linePaint);
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

    /**
     * Finds the data point on the line graphs closest to x and y
     * @param x - the x coordinate (e.g. x coord of where the user touched)
     * @param y - the y coordinate (e.g. y coord of where the user touched)
     * @return
     */
    private Point findDataPoint(float x, float y) {

        // Check if there is only one line item
        int countLineItems = 0;
        LineItem singleLineItem = null;
        for (GraphItem item : mGraphItems) {
            if (item instanceof LineItem) {
                singleLineItem = (LineItem) item;
                countLineItems++;
            }
        }

        if (countLineItems == 1) {
            /* If there is only 1 line item, find the closest point based only on the x coordinate */

            if (singleLineItem.findDataPointX(x) != null)
                return singleLineItem.findDataPointX(x);
            else
                return null;

        } else {
            /* If there is more that 1 line item, find the closest point based on the x and y coordinate */

            /* Find the closest point of on each line item */
            List<Point> closePoints = new ArrayList<>();
            for (GraphItem item : mGraphItems) {
                if (item instanceof LineItem) {
                    closePoints.add(((LineItem) item).findDataPoint(x, y));
                }
            }

            float shortestDistance = Float.NaN;
            Point closest = null;

            /* Loop through the closest points from each line item to determine with one is closest */
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
            /* Find the closest data point to the touch event */
            Point p = findDataPoint(event.getX(), event.getY());

            if (p != null) {
                for (GraphItem item : mGraphItems) {
                    if (item instanceof LineItem) {
                        /* Find the line item that contains this point, and notify that item to handle it */
                        if (((LineItem) item).containsPoint(p)) {
                            ((LineItem) item).onTap(p);
                            invalidate();
                            break;
                        }
                    }
                }

                return true;
            }
        }
        return false;
    }

    private int getLabelWidth(String maxLabel) {
        return (int) mLabelPaint.measureText(maxLabel + "    ");
    }
}
