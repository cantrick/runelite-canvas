package com.canvas;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.*;

@ConfigGroup("Canvas")
public interface CanvasConfig extends Config
{
	@ConfigItem(
			keyName = "showOverlay",
			name = "Show Overlay",
			description = "Determines whether or not painting overlay is on",
			position = 2
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "paintColor",
			name = "Set Paint Color",
			description = "Sets the color that you paint with",
			position = 0
	)
	default Color getColor()
	{
		return new Color(2,2,2);
	}

	@ConfigItem(
			keyName = "brushSize",
			name = "Set Brush Size",
			description = "Sets size of the brush",
			position = 1
	)
	default int getBrushSize()
	{
		return 2;
	}

	@ConfigItem(
			keyName = "drawKey",
			name = "Bind draw key",
			description = "Binds the draw key",
			position = 3
	)
	default Keybind drawKey()
	{
		return Keybind.CTRL;
	}

	@ConfigItem(
			keyName = "undoKey",
			name = "Bind undo key",
			description = "Binds the undo key",
			position = 4
	)
	default Keybind undoKey()
	{
        return Keybind.NOT_SET;
	}
}
