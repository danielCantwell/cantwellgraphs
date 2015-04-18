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
 */
public class LineItem {

    private final String LOG = "LineItem";

    private List<Float> mValues;
    private List<Point> mPoints;
    private Path mLinePath;
    private Path mFillPath;

    private int mWidth;
    private int mHeight;
    private float mMaxY;

    private FillType mFillType;

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
     * @param values - list of datapoints
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
    private void init() {

        mIsSmoothed = false;

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        setLineColor(Color.parseColor(DEFAULT_LINE_COLOR));
        setLineWidth(DEFAULT_LINE_WIDTH);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        setSolidFillColor(Color.parseColor(DEFAULT_FILL_END_COLOR));
        setGradientFillColor(Color.parseColor(DEFAULT_FILL_START_COLOR), Color.parseColor(DEFAULT_FILL_END_COLOR));

        mVerticalHighlight = null;
        mPointHighlight = null;
    }

    /**
     * Calculate the coordinates based on the values and graph size
     */
    public void createPoints() {
        float maxX = mValues.size();

        // ratio used for normalizing the coordinates to the graph space
        float yRatio = (mHeight * 9 / 10) / mMaxY;
        float dx = mWidth / (maxX - 1);
        float currentX = 0;

        mPoints = new ArrayList<>();
        // Add the first point at x coordinate 0
        mPoints.add(new Point(0, currentX, mHeight - (mValues.get(0) * yRatio)));
        // Loop through and add the rest of the points
        for (int i =  1; i < maxX; i++) {
            currentX += dx;
            float value = mValues.get(i);
            mPoints.add(new Point(value, currentX, mHeight - (value * yRatio)));
            Log.d(LOG, "X: " + mPoints.get(i).x + " Y: " + mPoints.get(i).y);
        }
    }

    /**
     * Calculate the line path based on the coordinates
     */
    public void createLinePath() {
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

    public void createSmoothLinePath() {
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

    public void createFillPath() {
        Path path = new Path();

        path.moveTo(0, mHeight);
        for (Point p : mPoints) {
            path.lineTo(p.x, p.y);
        }
        path.lineTo(mWidth, mHeight);

        mFillPath = path;
    }

    public void createSmoothFillPath() {
        Path path = new Path();

        Point prevPoint = null;
        path.moveTo(0, mHeight);
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

    public void update(int width, int height, float maxY) {
        mWidth = width;
        mHeight = height;
        mMaxY = maxY;

        if (mFillType == FillType.GRADIENT) {
            mFillPaint.setShader(new LinearGradient(0, 0, 0, mHeight, mGradientEndColor, mGradientStartColor, Shader.TileMode.CLAMP));
        }

        createPoints();

        if (mIsSmoothed) {
            createSmoothLinePath();
            if (hasFill())
                createSmoothFillPath();
        }
        else {
            createLinePath();
            if (hasFill())
                createFillPath();
        }
    }

    /****************************************
                Setter Functions
     ****************************************/

    public void setSmoothed(boolean isSmoothed) {
        mIsSmoothed = isSmoothed;
    }

    public void setLineColor(int color) {
        mLineColor = color;
        mLinePaint.setColor(mLineColor);
    }

    public void setLineWidth(float width) {
        mLineWidth = width;
        mLinePaint.setStrokeWidth(mLineWidth);
    }

    public void setSolidFillColor(int color) {
        mFillColor = color;
        mFillPaint.setColor(mFillColor);
    }

    public void setGradientFillColor(int startColor, int endColor) {
        mGradientStartColor = startColor;
        mGradientEndColor = endColor;
    }

    public void attachVerticalHighlight(VerticalHighlight v) {
        mVerticalHighlight = v;
    }

    public void attachPointHighlight(PointHighlight p) {
        mPointHighlight = p;
    }

    /****************************************
                Getter Functions
     ****************************************/

    public void draw(Canvas canvas) {
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

    public boolean hasFill() {
        return mFillType != FillType.NONE;
    }

    public float getMaxValue() {
        return Collections.max(mValues);
    }

    public boolean containsPoint(Point p) {
        return mPoints.contains(p);
    }

    public Point findDataPoint(float x, float y) {
        float shortestDistance = Float.NaN;
        Point closest = null;

        for (Point p : mPoints) {

            float x1 = p.x;
            float y1 = p.y;
            float x2 = x;
            float y2 = y;

            float distance = (float) Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));

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

    public Point findDataPointX(float x) {
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

    public void onTap(Point p) {
        if (mVerticalHighlight != null) {
            mVerticalHighlight.update(mHeight, p);
        }

        if (mPointHighlight != null) {
            mPointHighlight.update(p);
        }
    }
}
