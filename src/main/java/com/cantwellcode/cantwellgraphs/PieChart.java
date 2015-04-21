package com.cantwellcode.cantwellgraphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielCantwell on 4/20/15.
 */
public class PieChart extends View {

    /**
     * *************************************
     * Variables
     * **************************************
     */

    private final String LOG = "PieChart";

    private int mWidth;
    private int mHeight;
    private int mPadTop;
    private int mPadBottom;
    private int mPadLeft;
    private int mPadRight;

    private float mRadius;

    private List<PieSection> mPieSections;
    private float mSum;

    private int mBackgroundColor;
    private int mEmptyColor;

    /**
     * *************************************
     * Initialization
     * **************************************
     */

    public PieChart(Context context) {
        super(context);
        init();
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPieSections = new ArrayList<>();
        mBackgroundColor = Color.WHITE;
        mEmptyColor = Color.DKGRAY;
        mSum = 0;
    }

    /**
     * *************************************
     * Methods
     * **************************************
     */

    private float centerX() {
        return (mWidth / 2) + mPadLeft;
    }

    private float centerY() {
        return (mHeight / 2) + mPadTop;
    }

    public void addPieItem(PieSection item) {
        mPieSections.add(item);
        mSum += item.getValue();
    }

    public void setPieItems(List<PieSection> items) {
        mPieSections = items;
        for (PieSection item : items) {
            mSum += item.getValue();
        }
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setEmptyColor(int color) {
        mEmptyColor = color;
    }

    private void drawArc(Canvas canvas, Paint paint, float startDegree, float angle) {
        RectF arc = new RectF(centerX() - mRadius, centerY() - mRadius, centerX() + mRadius, centerY() + mRadius);
        canvas.drawArc(arc, startDegree, angle, true, paint);
    }

    public void drawChart() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(LOG, "onDraw");

        canvas.drawColor(mBackgroundColor);

        if (mSum == 0) {
            Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            emptyPaint.setColor(mEmptyColor);
            drawArc(canvas, emptyPaint, 0, 360);
        } else {

            float startDegree = 0;
            for (PieSection item : mPieSections) {

                float angle = item.getValue() * 360 / mSum;
                drawArc(canvas, item.getFillPaint(), startDegree, angle);
                drawArc(canvas, item.getStrokePaint(), startDegree, angle);

                if (item.hasLabel()) {
                    drawLabel(canvas, item, startDegree, angle);
                }

                startDegree += angle;
            }
        }
    }

    private void drawLabel(Canvas canvas, PieSection item, float startDegree, float angle) {

        /* Find the Label Position */

        float deg = (startDegree + startDegree + angle) / 2;
        float r = mRadius * 3 / 5;
        double radians = deg * Math.PI / 180;
        float x = (float) (r * Math.cos(radians)) + centerX();
        float y = (float) (r * Math.sin(radians)) + centerY() - ((item.getLabelPaint().descent() + item.getLabelPaint().ascent()) / 2);



        /* Draw the correct label at the position */

        String labelString = "";
        switch (item.getLabelType()) {
            case NAME:
                labelString = item.getName();
                break;
            case NAME_then_VALUE:
                labelString = String.format("%s %.1f", item.getName(), item.getValue());
                break;
            case NAME_then_PERCENTAGE:
                float p1 = item.getValue() * 100 / mSum;
                labelString = String.format("%s %.1f%s", item.getName(), p1, "%");
                break;
            case VALUE:
                labelString = String.format("%.1f", item.getValue());
                break;
            case VALUE_then_NAME:
                labelString = String.format("%.1f %s", item.getValue(), item.getName());
                break;
            case VALUE_then_PERCENTAGE:
                float p2 = item.getValue() * 100 / mSum;
                labelString = String.format("%.1f %.1f%s", item.getValue(), p2, "%");
                break;
            case PERCENTAGE:
                float p3 = item.getValue() * 100 / mSum;
                labelString = String.format("%.1f%s", p3, "%");
                break;
            case PERCENTAGE_then_NAME:
                float p4 = item.getValue() * 100 / mSum;
                labelString = String.format("%.1f%s %s", p4, "%", item.getName());
                break;
            case PERCENTAGE_then_VALUE:
                float p5 = item.getValue() * 100 / mSum;
                labelString = String.format("%.1f%s %.1f", p5, "%", item.getValue());
                break;
        }
        canvas.drawText(labelString, x, y, item.getLabelPaint());

        Log.d(LOG, "Drawing Label at " + x + ", " + y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        mPadLeft = getPaddingLeft();
        mPadRight = getPaddingRight();
        mPadTop = getPaddingTop();
        mPadBottom = getPaddingBottom();

        mWidth = w - (mPadLeft + mPadRight);
        mHeight = h - (mPadBottom + mPadTop);

        mRadius = mWidth < mHeight ? mWidth / 2 : mHeight / 2;

        Log.d(LOG, "Width: " + mWidth + " Height: " + mHeight);
    }
}
