package com.canvas;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import java.util.Deque;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.overlay.OverlayManager;
import java.awt.event.MouseEvent;
import java.util.*;


import java.awt.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Canvas"
)
public class CanvasPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CanvasConfig config;
	private CanvasOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MouseManager mouseManager;

	private Deque<Curve> curve;
	private Point temp = null;
	public boolean isDrawing = false;
	public List<Deque<Curve>> curveList = new ArrayList<>();
	public List<Color> colorList = new ArrayList<>();
	public List<Integer> sizeList = new ArrayList<>();
	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public MouseEvent mousePressed(MouseEvent e) {
			colorList.add(config.getColor());
			sizeList.add(config.getBrushSize());
			curve = new ArrayDeque<>();
			isDrawing = true;
			return e;
		}

		@Override
		public MouseEvent mouseReleased(MouseEvent e) {
			isDrawing = false;
			temp = null;
			curveList.add(curve);
			return e;
		}

		@Override
		public MouseEvent mouseMoved(MouseEvent e) {
			return e;
		}

		@Override
		public MouseEvent mouseDragged(MouseEvent e) {
			if(isDrawing) {
				updateMousePositions(new Point(e.getX(), e.getY()));
			}
			return e;
		}
	};

	@Override
	protected void startUp() throws Exception
	{
		if (overlay == null) {
			overlay = new CanvasOverlay(this, config);
		}
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			setMouseListenerEnabled(true);
		}
	}

	public void setMouseListenerEnabled(boolean enabled)
	{
		if (enabled)
		{
			mouseManager.registerMouseListener(mouseAdapter);
		}
		else
		{
			mouseManager.unregisterMouseListener(mouseAdapter);
		}
	}

	public void updateMousePositions(Point point) {
		if (temp != null) {
			Curve current = new Curve(temp, point);
			curve.add(current);
		}
		temp = point;
	}

	public Deque<Curve> getTrail() {
		return curve;
	}

	@Provides
	CanvasConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CanvasConfig.class);
	}
}
