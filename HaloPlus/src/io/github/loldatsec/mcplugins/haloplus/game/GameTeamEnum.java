package io.github.loldatsec.mcplugins.haloplus.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum GameTeamEnum {
	BLUE, RED, YELLOW, GREEN, SPECTATOR;

	public static ChatColor getColour(GameTeamEnum team) {
		switch (team) {
			case BLUE:
				return ChatColor.BLUE;
			case RED:
				return ChatColor.RED;
			case YELLOW:
				return ChatColor.YELLOW;
			case GREEN:
				return ChatColor.GREEN;
			case SPECTATOR:
				return ChatColor.DARK_GRAY;
			default:
				return ChatColor.GRAY;
		}
	}

	public static Color toRGB(GameTeamEnum team) {
		switch (team) {
			case BLUE:
				return Color.BLUE;
			case RED:
				return Color.RED;
			case YELLOW:
				return Color.YELLOW;
			case GREEN:
				return Color.GREEN;
			case SPECTATOR:
				return Color.GRAY;
			default:
				return Color.GRAY;
		}
	}
}
