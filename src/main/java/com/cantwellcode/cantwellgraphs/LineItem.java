package com.cantwellcode.cantwellgraphs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by danielCantwell on 4/17/15.
 *
 * Copyright (c) Cantwell Code 2015, All Rights Reserved
 */
public class LineItem extends GraphItem {

    private final String LOG = "LineItem";

    private List<Float> mValues;
    private List<Point> mPoints;
    private Path mLinePath;
    private Path mFillPath;

    private boolean mIsSmoothed;

    private Paint mLinePaint;
    private int mLineColor;
    private float mLineWidth;
    private final String DEFAULT_LINE_COLOR = "#000000";
    private final float DEFAULT_LINE_WIDTH = 10;

    private Paint mFillPaint;
    private int mFillColor;
    private int mGradientStartColor;
    private int mGradientEndColor;
    private final String DEFAULT_FILL_START_COLOR = "#FFFFFF";
    private final String DEFAULT_FILL_END_COLOR = "#000000";

    private VerticalHighlight mVerticalHighlight;
    private PointHighlight mPointHighlight;

    /**
     * Constructor
     *
     * @param values   - list of datapoints
     * @param fillType - fill type for below the line : none, solid, gradient
     */
    public LineItem(List<Float> values, FillType fillType) {
        mValues = values;
        Log.d(LOG, "Width: " + mWidth + " Height: " + mHeight);
        mFillType = fillType;
        init();
    }

    /****************************************
     Initialization
     ****************************************/

    /**
     * Set defaults values
     */
    @Override
    protected void init() {

        // By default, there line is not smooth
        mIsSmoothed = false;

        /* Initialize the line paint */
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        setLineColor(Color.parseColor(DEFAULT_LINE_COLOR));
        setLineWidth(DEFAULT_LINE_WIDTH);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);

        /* Initialize the fill paint */
        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        setSolidFillColor(Color.parseColor(DEFAULT_FILL_END_COLOR));
        setGradientFillColor(Color.parseColor(DEFAULT_FILL_START_COLOR), Color.parseColor(DEFAULT_FILL_END_COLOR));

        // By default, there are not data highlights
        mVerticalHighlight = null;
        mPointHighlight = null;

        // By default, both top and bottom padding are enabled
        mTopPaddingEnabled = true;
        mBottomPaddingEnabled = true;
    }

    @Override
    protected void updateItem(int width, int height, float minY, float maxY, int labelWidth) {
        super.updateItem(width, height, minY, maxY, labelWidth);

        if (mFillType == FillType.GRADIENT) {
            mFillPaint.setShader(new LinearGradient(0, 0, 0, mHeight, mGradientEndColor, mGradientStartColor, Shader.TileMode.CLAMP));
        }

        /* Calculate the list of points */

        createPoints();

        /* Create either a smooth or normal line path based on the points */

        if (mIsSmoothed) {
            createSmoothLinePath();
            if (hasFill())
                createSmoothFillPath();
        } else {
            createLinePath();
            if (hasFill())
                createFillPath();
        }
    }

    @Override
    protected void drawItem(Canvas canvas) {
        if (hasFill()) {
            // If the line has a fill, draw the fill
            canvas.drawPath(mFillPath, mFillPaint);
        }
        // Draw Line
        canvas.drawPath(mLinePath, mLinePaint);

        // Draw Vertical Highlight if exists
        if (mVerticalHighlight != null) {
            mVerticalHighlight.draw(canvas);
        }

        // Draw Point Highlight if exists
        if (mPointHighlight != null) {
            mPointHighlight.draw(canvas);
        }
    }

    @Override
    protected float getMaxValue() {
        return Collections.max(mValues);
    }

    @Override
    protected float getMinValue() {
        return Collections.min(mValues);
    }

    /**
     * Calculate the coordinates based on the values and graph size
     */
    private void createPoints() {
        float maxX = mValues.size();

        // ratio used for normalizing the coordinates to the graph space
        float maxYCoordinate = mTopPaddingEnabled ? mHeight * 9 / 10 : mHeight;
        float minYCoordinate = mBottomPaddingEnabled ? mHeight / 10 : 0;
        float dx = (mWidth - mLabelWidth) / (maxX - 1);
        float currentX = mLabelWidth;

        mPoints = new ArrayList<>();
        // Add the first point at x coordinate 0
        mPoints.add(new Point(this, 0, mValues.get(0), currentX, getYCoordinate(mValues.get(0), minYCoordinate, maxYCoordinate)));
        // Loop through and add the rest of the points
        for (int i = 1; i < maxX; i++) {
            currentX += dx;
            float value = mValues.get(i);
            float y = getYCoordinate(value, minYCoordinate, maxYCoordinate);
            mPoints.add(new Point(this, i, value, currentX, y));
            Log.d(LOG, "X: " + mPoints.get(i).x + " Y: " + mPoints.get(i).y);
        }
    }

    /**
     * Calculate the line path based on the coordinates
     */
    private void createLinePath() {
        Path path = new Path();

        boolean firstItem = true;
        for (Point p : mPoints) {
            if (firstItem) {
                path.moveTo(p.x, p.y);
                firstItem = false;
            } else {
                path.lineTo(p.x, p.y);
            }
        }

        mLinePath = path;
    }

    /**
     * Calculate a smoothed line path based on the coordinates
     */
    private void createSmoothLinePath() {
        Path path = new Path();

        Point prevPoint = null;
        for (int i = 0; i < mPoints.size(); i++) {
            Point point = mPoints.get(i);

            if (i == 0) {
                path.moveTo(point.x, point.y);
            } else {
                float midX = (prevPoint.x + point.x) / 2;
                float midY = (prevPoint.y + point.y) / 2;

                if (i == 1) {
                    path.lineTo(midX, midY);
                } else {
                    path.quadTo(prevPoint.x, prevPoint.y, midX, midY);
                }
            }
            prevPoint = point;
        }
        path.lineTo(prevPoint.x, prevPoint.y);

        mLinePath = path;
    }

    /**
     * Calculate the fill path
     * The difference in the line path, is that the fill's first and last points go to the bottom of the graph
     */
    private void createFillPath() {
        Path path = new Path();

        path.moveTo(mLabelWidth, mHeight);
        for (Point p : mPoints) {
            path.lineTo(p.x, p.y);
        }
        path.lineTo(mWidth, mHeight);

        mFillPath = path;
    }

    /**
     * Calculate a smoothed fill path
     * The difference in the line path, is that the fill's first and alst points go to the bottom of the graph
     */
    private void createSmoothFillPath() {
        Path path = new Path();

        Point prevPoint = null;
        path.moveTo(mLabelWidth, mHeight);
        for (int i = 0; i < mPoints.size(); i++) {
            Point point = mPoints.get(i);

            if (i == 0) {
                path.lineTo(point.x, point.y);
            } else {
                float midX = (prevPoint.x + point.x) / 2;
                float midY = (prevPoint.y + point.y) / 2;

                if (i == 1) {
                    path.lineTo(midX, midY);
                } else {
                    path.quadTo(prevPoint.x, prevPoint.y, midX, midY);
                }
            }
            prevPoint = point;
        }
        path.lineTo(prevPoint.x, prevPoint.y);
        path.lineTo(mWidth, mHeight);

        mFillPath = path;
    }

    /*
     * *************************************
     * Setter Functions
     * **************************************
     */

    /**
     * If enabled, it will create a smooth path from point point
     * Otherwise, the path will be a straight line from point to point
     *
     * @param isSmoothed
     */
    public void setSmoothed(boolean isSmoothed) {
        mIsSmoothed = isSmoothed;
    }

    /**
     * Sets the color of the line item
     *
     * @param color
     */
    public void setLineColor(int color) {
        mLineColor = color;
        mLinePaint.setColor(mLineColor);
    }

    /**
     * Sets the width of the line item
     *
     * @param width
     */
    public void setLineWidth(float width) {
        mLineWidth = width;
        mLinePaint.setStrokeWidth(mLineWidth);
    }

    /**
     * Sets the solid fill color below the line item
     *
     * @param color
     */
    public void setSolidFillColor(int color) {
        mFillColor = color;
        mFillPaint.setColor(mFillColor);
    }

    /**
     * Sets the gradient fill colors below the item
     *
     * @param startColor - the color at the bottom of the fill
     * @param endColor   - the color at the top of the fill
     */
    public void setGradientFillColor(int startColor, int endColor) {
        mGradientStartColor = startColor;
        mGradientEndColor = endColor;
    }

    /**
     * Add a vertical data highlight that is used with touch interaction
     *
     * @param v
     */
    public void attachVerticalHighlight(VerticalHighlight v) {
        mVerticalHighlight = v;
    }

    /**
     * Adds a point data highlight that is used with touch interaction
     *
     * @param p
     */
    public void attachPointHighlight(PointHighlight p) {
        mPointHighlight = p;
    }

    /*
     * *************************************
     * Getter Functions
     * **************************************
     */

    /**
     * Check if the line item has fill, as opposed to just a line
     *
     * @return
     */
    protected boolean hasFill() {
        return mFillType != FillType.NONE;
    }

    /**
     * Returns the list of values for the line
     *
     * @return
     */
    public List<Float> getValues() {
        return mValues;
    }

    /**
     * Check if the line contains the point
     *
     * @param p
     * @return
     */
    protected boolean containsPoint(Point p) {
        return mPoints.contains(p);
    }

    /**
     * Finds the point on the line closest the the x,y coordinate given
     *
     * @param x
     * @param y
     * @return
     */
    protected Point findDataPoint(float x, float y) {
        float shortestDistance = Float.NaN;
        Point closest = null;

        for (Point p : mPoints) {

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

    /**
     * Finds the points on the line that closest corresponds to the given x coordinate
     *
     * @param x
     * @return
     */
    protected Point findDataPointX(float x) {
        float shortestDistance = Float.NaN;
        Point closest = null;

        for (Point p : mPoints) {

            float distance = Math.abs(x - p.x);
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

    /**
     * Handle a touch event on a given point
     *
     * @param p
     */
    protected void onTap(Point p) {
        if (mVerticalHighlight != null) {
            mVerticalHighlight.update(mHeight, p);
        }

        if (mPointHighlight != null) {
            mPointHighlight.update(p);
        }
    }
}
