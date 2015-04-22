package com.cantwellcode.cantwellgraphs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by danielCantwell on 4/20/15.
 */
public class PieSection {

    private String mName;
    private float mValue;

    private Paint mStrokePaint;
    private Paint mFillPaint;
    private Paint mLabelPaint;

    private PieLabelType mLabelType;
    private boolean mShowLabel;

    /**
     * Constructor
     * @param name or title of this section of the pie
     * @param value of this section of the pie
     */
    public PieSection(String name, float value) {
        mName = name;
        mValue = value;

        mShowLabel = true;

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        setStrokeWidth(6);
        setStrokeColor(Color.BLACK);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
        setFillColor(Color.WHITE);

        mLabelType = PieLabelType.NAME;

        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setTextSize(30);
        mLabelPaint.setColor(Color.BLACK);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);
    }

    /*
            Setters
     */

    public void setStrokeWidth(float width) {
        mStrokePaint.setStrokeWidth(width);
    }

    public void setStrokeColor(int color) {
        mStrokePaint.setColor(color);
    }

    public void setFillColor(int color) {
        mFillPaint.setColor(color);
    }

    public void setLabelType(PieLabelType type) {
        mLabelType = type;
    }

    public void setLabelColor(int color) {
        mLabelPaint.setColor(color);
    }

    public void setLabelSize(float size) {
        mLabelPaint.setTextSize(size);
    }

    public void showLabel(boolean show) {
        mShowLabel = show;
    }

    /*
            Getters
     */

    public float getValue() {
        return mValue;
    }

    public String getName() {
        return mName;
    }

    protected PieLabelType getLabelType() { return mLabelType; }

    protected Paint getStrokePaint() {
        return mStrokePaint;
    }

    protected Paint getFillPaint() {
        return mFillPaint;
    }

    protected Paint getLabelPaint() { return mLabelPaint; }

    protected boolean hasLabel() {
        return mShowLabel;
    }
}
