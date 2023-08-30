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
//		@Override
//		public MouseEvent mousePressed(MouseEvent e) {
//			colorList.add(config.getColor());
//			sizeList.add(config.getBrushSize());
//			curve = new ArrayDeque<>();
//			isDrawing = true;
//			return e;
//		}
//
//		@Override
//		public MouseEvent mouseReleased(MouseEvent e) {
//			isDrawing = false;
//			temp = null;
//			curveList.add(curve);
//			return e;
//		}

		@Override
		public MouseEvent mouseMoved(MouseEvent e) {
			if(isDrawing) {
				updateMousePositions(new Point(e.getX(), e.getY()));
			}
			return e;
		}

		@Override
		public MouseEvent mouseDragged(MouseEvent e) {
//			if(isDrawing) {
//				updateMousePositions(new Point(e.getX(), e.getY()));
//			}
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
		initHotkeys();
		toggleHotkeys();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
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

	public Deque<Curve> getTrail() {
		return curve;
	}

	private void initHotkeys() {
		hotkeys[0] = new HotkeyListener(() -> config.drawKey()) {
			@Override
			public void hotkeyPressed() {
				log.info("DRAW PRESS");
				colorList.add(config.getColor());
				sizeList.add(config.getBrushSize());
				curve = new ArrayDeque<>();
				isDrawing = true;
			}

			@Override
			public void hotkeyReleased() {
				log.info("DRAW RELEASE");
				isDrawing = false;
				temp = null;
				curveList.add(curve);
			}
		};
		hotkeys[1] = new HotkeyListener(() -> config.undoKey()) {
			@Override
			public void hotkeyPressed() {

			}
		};
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

//	@Subscribe
//	public void onConfigChanged(ConfigChanged event) {
//		if (OPTION_KEYS.contains(event.getKey())) {
//			panel.update(state);
//		}
//
//		if (event.getKey().equals(AUTO_HIDE_KEY)) {
//			boolean atZul = REGION_IDS.contains(getRegionId());
//			togglePanel(atZul || !config.autoHide(), false);
//		}
//	}

	@Provides
	CanvasConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CanvasConfig.class);
	}
}
