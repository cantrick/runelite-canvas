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
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.input.KeyManager;

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
	private KeyManager keyManager;
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
	private HotkeyListener[] hotkeys = new HotkeyListener[2];
	private boolean hotkeysEnabled = false;


	private final MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public MouseEvent mouseMoved(MouseEvent e) {
			if(isDrawing) {
				updateMousePositions(new Point(e.getX(), e.getY()));
			}
			return e;
		}
	};

	private void initHotkeys() {
		hotkeys[0] = new HotkeyListener(() -> config.drawKey()) {
			@Override
			public void hotkeyPressed() {
				colorList.add(config.getColor());
				sizeList.add(config.getBrushSize());
				curve = new ArrayDeque<>();
				isDrawing = true;
			}

			@Override
			public void hotkeyReleased() {
				isDrawing = false;
				temp = null;
				curveList.add(curve);
				curve = null;
			}
		};
		hotkeys[1] = new HotkeyListener(() -> config.undoKey()) {
			@Override
			public void hotkeyPressed() {
				if(curveList.size() > 0 && colorList.size() > 0 && sizeList.size() > 0) {
					curveList.remove(curveList.size() - 1);
					colorList.remove(colorList.size() - 1);
					sizeList.remove(sizeList.size() - 1);
				}
			}
		};
	}

	@Override
	protected void startUp() throws Exception
	{
		if (overlay == null) {
			overlay = new CanvasOverlay(this, config);
		}
		overlayManager.add(overlay);
		initHotkeys();
		toggleHotkeys();
	}

	@Override
	protected void shutDown() throws Exception
	{
		if (hotkeysEnabled) {
			toggleHotkeys();
		}
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

	private void toggleHotkeys() {
		for (HotkeyListener hotkey : hotkeys) {
			if (hotkeysEnabled) {
				keyManager.unregisterKeyListener(hotkey);
			} else {
				keyManager.registerKeyListener(hotkey);
			}
		}
		hotkeysEnabled = !hotkeysEnabled;
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
