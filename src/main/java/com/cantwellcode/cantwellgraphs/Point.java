package com.cantwellcode.cantwellgraphs;

/**
 * Created by danielCantwell on 4/17/15.
 */
public class Point {

    public int dataIndex;

    public float x;
    public float y;

    public float dx;
    public float dy;

    public Point() {}

    public Point(int dataIndex, float x, float y) {
        this.dataIndex = dataIndex;
        this.x = x;
        this.y = y;
    }
}
