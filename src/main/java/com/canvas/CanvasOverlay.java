package com.canvas;

import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import java.awt.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CanvasOverlay extends Overlay {

    private final CanvasPlugin plugin;
    private final CanvasConfig config;

    public List<Deque<Curve>> curveList;
    public List<Color> colorList;
    public List<Integer> sizeList;


    public CanvasOverlay(CanvasPlugin plugin, CanvasConfig config) {
        this.plugin = plugin;
        this.config = config;
        curveList = plugin.curveList;
        colorList = plugin.colorList;
        sizeList = plugin.sizeList;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_WIDGETS);

    }

    @Override
    public Dimension render(Graphics2D graphics){
        // Disable overlay if canvas set to off
        if (config.showOverlay() == false) {
            return null;
        }

        drawCurvesFromList(curveList, colorList, graphics);

        List<Curve> trail = new ArrayList<>(plugin.getTrail());
        Point midBefore = null;
        Point midAfter = null;
        // Set trail size, stroke, and antialiasing
        graphics.setStroke(new BasicStroke(sizeList.get(sizeList.size()-1), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Fallback default color
        graphics.setColor(colorList.get(colorList.size()-1));

        for(int i = 0; i < trail.size(); i++) {
            // Get Points from Curve
            List<Point> points = trail.get(i).getCurve();
            // Loop through points
            for(int j = 0; j < points.size(); j++ ) {

                // Initialize points used to calculate parts of trail with null
                Point before = null;
                Point after = null;
                Point previous = null;
                Point current = null;
                // Logic for setting points depending on where in the loops we are
                if(i != 0 && j == 0) {
                    // Get previous list of points from the previous Curve
                    List<Point> previousPoints = trail.get(i - 1).getCurve();
                    before = previousPoints.get(previousPoints.size() - 2);
                    after = points.get(j + 1);
                    previous = previousPoints.get(previousPoints.size() - 1);
                    current = points.get(j);
                } else if(j > 1) {
                    // Set points from current Curve
                    before = points.get(j - 2);
                    previous = points.get(j - 1);
                    current = points.get(j);
                    // If the last point in the Curve, and not the last curve in the trail, get second point from next Curve
                    if(i < trail.size() - 1 && j == points.size() - 1) {
                        after = trail.get(i + 1).getCurve().get(1);
                    }
                }
                // Get second to last in previous Curve and third point in current Curve
                // Will be used to draw another line due to tiny gaps left in between Curves
                if(j == 4) {
                    if (midBefore == null) {
                        midBefore = points.get(j);
                    }
                } else if (j == 2 && midBefore != null) {
                    midAfter = points.get(j);
                }
                // Set position and size of trail
                // Multiply by five due to preset size of Curve

                // Draw lines of the trail
                // We are drawing three to fill in gaps and slight inconsistencies
                // Still a small bug when drawing large circles very quickly
                // Causes lines to occasionally overlap at the edges
                if(previous != null && current != null) {
                    graphics.drawLine(previous.getX(), previous.getY(), current.getX(), current.getY());
                }
                if(before != null && after != null) {
                    graphics.drawLine(before.getX(), before.getY(), after.getX(), after.getY());
                }
                if(midBefore != null && midAfter != null) {
                    graphics.drawLine(midBefore.getX(), midBefore.getY(), midAfter.getX(), midAfter.getY());
                    // Progress mid points
                    midBefore = midAfter;
                    midAfter = null;
                }
            }
        }

        return null;
    }

    public void drawCurvesFromList(List<Deque<Curve>> curveList, List<Color> colorList, Graphics2D graphics) {
        for(int k = 0; k < curveList.size(); k++) {
            Deque<Curve> currentCurve = curveList.get(k);
            List<Curve> trail = new ArrayList<>(currentCurve);
            Point midBefore = null;
            Point midAfter = null;
            // Set trail size, stroke, and antialiasing
            graphics.setStroke(new BasicStroke(sizeList.get(k), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Fallback default color
            graphics.setColor(colorList.get(k));
            for(int i = 0; i < trail.size(); i++) {
                // Get Points from Curve
                List<Point> points = trail.get(i).getCurve();
                // Loop through points
                for(int j = 0; j < points.size(); j++ ) {

                    // Initialize points used to calculate parts of trail with null
                    Point before = null;
                    Point after = null;
                    Point previous = null;
                    Point current = null;
                    // Logic for setting points depending on where in the loops we are
                    if(i != 0 && j == 0) {
                        // Get previous list of points from the previous Curve
                        List<Point> previousPoints = trail.get(i - 1).getCurve();
                        before = previousPoints.get(previousPoints.size() - 2);
                        after = points.get(j + 1);
                        previous = previousPoints.get(previousPoints.size() - 1);
                        current = points.get(j);
                    } else if(j > 1) {
                        // Set points from current Curve
                        before = points.get(j - 2);
                        previous = points.get(j - 1);
                        current = points.get(j);
                        // If the last point in the Curve, and not the last curve in the trail, get second point from next Curve
                        if(i < trail.size() - 1 && j == points.size() - 1) {
                            after = trail.get(i + 1).getCurve().get(1);
                        }
                    }
                    // Get second to last in previous Curve and third point in current Curve
                    // Will be used to draw another line due to tiny gaps left in between Curves
                    if(j == 4) {
                        if (midBefore == null) {
                            midBefore = points.get(j);
                        }
                    } else if (j == 2 && midBefore != null) {
                        midAfter = points.get(j);
                    }
                    // Set position and size of trail
                    // Multiply by five due to preset size of Curve
                    // TODO hook curve size multiplier into config
                    int position = i * 5 + j;
                    int size = trail.size() * 5;


                    // Draw lines of the trail
                    // We are drawing three to fill in gaps and slight inconsistencies
                    // Still a small bug when drawing large circles very quickly
                    // Causes lines to occasionally overlap at the edges
                    if(previous != null && current != null) {
                        graphics.drawLine(previous.getX(), previous.getY(), current.getX(), current.getY());
                    }
                    if(before != null && after != null) {
                        graphics.drawLine(before.getX(), before.getY(), after.getX(), after.getY());
                    }
                    if(midBefore != null && midAfter != null) {
                        graphics.drawLine(midBefore.getX(), midBefore.getY(), midAfter.getX(), midAfter.getY());
                        // Progress mid points
                        midBefore = midAfter;
                        midAfter = null;
                    }
                }
            }
        }
    }
}
