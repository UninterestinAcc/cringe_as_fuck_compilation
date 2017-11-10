package io.github.loldatsec.mcplugs.clans;

import java.util.List;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBarChat {

	public static void send(Player player, String text) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2));
	}

	public static void broadcast(List<Player> plist, String text) {
		for (Player player : plist) {
			send(player, text);
		}
	}
}
