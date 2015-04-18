package com.cantwellcode.cantwellgraphs;

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

    public enum FillType {
        NONE, SOLID, GRADIENT
    }

    private List<Float> mValues;
    private List<Point> mPoints;
    private Path mLinePath;
    private Path mFillPath;

    private int mWidth;
    private int mHeight;
    private float mMaxY;

    private FillType mFillType;

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
        mPoints = new ArrayList<>();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        setLineColor(Color.parseColor(DEFAULT_LINE_COLOR));
        setLineWidth(DEFAULT_LINE_WIDTH);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

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

        // Add the first point at x coordinate 0
        mPoints.add(new Point(0, currentX, mHeight - (mValues.get(0) * yRatio)));
        // Loop through and add the rest of the points
        for (int i =  1; i < maxX; i++) {
            currentX += dx;
            mPoints.add(new Point(i, currentX, mHeight - (mValues.get(i) * yRatio)));
            Log.d(LOG, "X: " + mPoints.get(i).x + " Y: " + mPoints.get(i).y);
        }
    }

    /**
     * Calculate the line path based on the coordinates
     */
    public void createLinePath() {
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
        createLinePath();
        if (hasFill()) createFillPath();
    }

    /****************************************
                Setter Functions
     ****************************************/

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

    /****************************************
                Getter Functions
     ****************************************/

    public Path getLinePath() {
        return mLinePath;
    }

    public Path getFillPath() {
        return mFillPath;
    }

    public Paint getLinePaint() {
        return mLinePaint;
    }

    public Paint getFillPaint() {
        return mFillPaint;
    }

    public List<Float> getValues() {
        return mValues;
    }

    public List<Point> getPoints() {
        return mPoints;
    }

    public boolean hasFill() {
        return mFillType != FillType.NONE;
    }

    public float getMaxValue() {
        return Collections.max(mValues);
    }

    public float getMinValue() {
        return Collections.min(mValues);
    }
}
