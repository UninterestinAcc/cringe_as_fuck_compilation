package io.github.loldatsec.mcplugs.halocore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Chat {
	public static void bc(String text) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(text);
		}
		Bukkit.getConsoleSender().sendMessage(text);
	}
}
