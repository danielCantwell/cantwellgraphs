package com.cantwellcode.cantwellgraphs;

/**
 * Created by danielCantwell on 4/17/15.
 */
public class Point {

    public LineItem line;
    public float value;
    public int dataIndex;

    public float x;
    public float y;

    public Point(LineItem line, int dataIndex, float value, float x, float y) {
        this.line = line;
        this.dataIndex = dataIndex;
        this.value = value;
        this.x = x;
        this.y = y;
    }
}
