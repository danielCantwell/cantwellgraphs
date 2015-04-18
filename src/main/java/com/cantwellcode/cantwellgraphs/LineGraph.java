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

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mLineItems = new ArrayList<>();
        mBackgroundColor = Color.parseColor("#FFFFFF");

        Log.d(LOG, "Constructor");
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

    public void drawGraph() {
        invalidate();
        Log.d(LOG, "drawGraph");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(LOG, "onDraw");

        canvas.drawColor(mBackgroundColor);

        float maxY = 0;
        for (LineItem lineItem : mLineItems) {
            if (lineItem.getMaxValue() > maxY) maxY = lineItem.getMaxValue();
        }

        for (LineItem lineItem : mLineItems) {

            lineItem.update(mWidth, mHeight, maxY);
            if (lineItem.hasFill()) {
                // If the line has a fill, draw the fill
                canvas.drawPath(lineItem.getFillPath(), lineItem.getFillPaint());
            }
            // Draw Line
            canvas.drawPath(lineItem.getLinePath(), lineItem.getLinePaint());

            Log.d(LOG, "draw lineItem");
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

    public int getGraphWidth() {
        return mWidth;
    }

    public int getGraphHeight() {
        return mHeight;
    }

}
