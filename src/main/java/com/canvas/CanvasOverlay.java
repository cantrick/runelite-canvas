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
        if (!config.showOverlay()) {
            return null;
        }

        drawCurvesFromList(curveList, colorList, graphics);

        List<Curve> trail = new ArrayList<>(plugin.getTrail());
        Point midBefore = null;
        Point midAfter = null;
        graphics.setStroke(new BasicStroke(sizeList.get(sizeList.size()-1), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(colorList.get(colorList.size()-1));

        for(int i = 0; i < trail.size(); i++) {
            List<Point> points = trail.get(i).getCurve();
            for(int j = 0; j < points.size(); j++ ) {

                Point before = null;
                Point after = null;
                Point previous = null;
                Point current = null;
                if(i != 0 && j == 0) {
                    List<Point> previousPoints = trail.get(i - 1).getCurve();
                    before = previousPoints.get(previousPoints.size() - 2);
                    after = points.get(j + 1);
                    previous = previousPoints.get(previousPoints.size() - 1);
                    current = points.get(j);
                } else if(j > 1) {
                    before = points.get(j - 2);
                    previous = points.get(j - 1);
                    current = points.get(j);
                    if(i < trail.size() - 1 && j == points.size() - 1) {
                        after = trail.get(i + 1).getCurve().get(1);
                    }
                }

                if(j == 4) {
                    if (midBefore == null) {
                        midBefore = points.get(j);
                    }
                } else if (j == 2 && midBefore != null) {
                    midAfter = points.get(j);
                }

                if(previous != null && current != null) {
                    graphics.drawLine(previous.getX(), previous.getY(), current.getX(), current.getY());
                }
                if(before != null && after != null) {
                    graphics.drawLine(before.getX(), before.getY(), after.getX(), after.getY());
                }
                if(midBefore != null && midAfter != null) {
                    graphics.drawLine(midBefore.getX(), midBefore.getY(), midAfter.getX(), midAfter.getY());
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

            graphics.setStroke(new BasicStroke(sizeList.get(k), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(colorList.get(k));
            for(int i = 0; i < trail.size(); i++) {

                List<Point> points = trail.get(i).getCurve();

                for(int j = 0; j < points.size(); j++ ) {

                    Point before = null;
                    Point after = null;
                    Point previous = null;
                    Point current = null;

                    if(i != 0 && j == 0) {

                        List<Point> previousPoints = trail.get(i - 1).getCurve();
                        before = previousPoints.get(previousPoints.size() - 2);
                        after = points.get(j + 1);
                        previous = previousPoints.get(previousPoints.size() - 1);
                        current = points.get(j);
                    } else if(j > 1) {

                        before = points.get(j - 2);
                        previous = points.get(j - 1);
                        current = points.get(j);

                        if(i < trail.size() - 1 && j == points.size() - 1) {
                            after = trail.get(i + 1).getCurve().get(1);
                        }
                    }

                    if(j == 4) {
                        if (midBefore == null) {
                            midBefore = points.get(j);
                        }
                    } else if (j == 2 && midBefore != null) {
                        midAfter = points.get(j);
                    }

                    if(previous != null && current != null) {
                        graphics.drawLine(previous.getX(), previous.getY(), current.getX(), current.getY());
                    }
                    if(before != null && after != null) {
                        graphics.drawLine(before.getX(), before.getY(), after.getX(), after.getY());
                    }
                    if(midBefore != null && midAfter != null) {
                        graphics.drawLine(midBefore.getX(), midBefore.getY(), midAfter.getX(), midAfter.getY());
                        midBefore = midAfter;
                        midAfter = null;
                    }
                }
            }
        }
    }
}
