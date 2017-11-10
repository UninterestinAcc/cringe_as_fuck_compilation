package io.github.loldatsec.mcplugs.halocore;

import java.util.List;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

	public static void send(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, stay, fadeOut));
		if (subTitle != null) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + subTitle + "\"}")));
		}
	}

	public static void broadcast(List<Player> plist, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		for (Player player : plist) {
			send(player, title, subTitle, fadeIn, stay, fadeOut);
		}
	}
}
