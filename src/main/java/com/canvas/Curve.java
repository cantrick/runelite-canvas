package com.canvas;

import net.runelite.api.Point;

import java.util.ArrayList;
import java.util.List;

public class Curve {
    private final Point from;
    private final Point to;
    private final List<Point> curve = new ArrayList<>();

    public Curve(Point from, Point to) {
        this.from = from;
        this.to = to;

        // TODO hook size of Curve into config
        for(int i = 1; i <= 5; i++) {
            curve.add(interpolate(from, to, (double) (i * 2) / 10));
        }
    }

    /**
     * Linearly interpolates between two points.
     *
     * @param from The starting point.
     * @param to The ending point.
     * @param t The interpolation progress starting at 0 and going to 1 (percent of distance between points).
     * @return The interpolated point.
     */
    private Point interpolate(Point from, Point to, double t) {
        double x = from.getX() * (1 - t) + to.getX() * t;
        double y = from.getY() * (1 - t) + to.getY() * t;
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    public List<Point> getCurve() {
        return curve;
    }
}
