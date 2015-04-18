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
            lineItem.draw(canvas);

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


    private Point findDataPoint(float x, float y) {

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
    }
}
