package org.reborncraft.gtowny.chat;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.GTowny;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;

public class ChatOutput {
	public static void title(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(fadeIn, stay, fadeOut));
		if (title != null && title.length() > 0) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title.replaceAll("\"", "\\\"") + "\"}")));
		}
		if (subTitle != null && subTitle.length() > 0) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitle.replaceAll("\"", "\\\"") + "\"}")));
		}
	}

	public static void title(Player player, String title) {
		title(player, title, "");
	}

	public static void title(Player player, String title, String subTitle) {
		title(player, title, subTitle, 0, 20, 8);
	}

	public static void actionBar(Player player, String text) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replaceAll("\"", "\\\"") + "\"}"), (byte) 2));
	}

	public static void chat(Player player, ChatComponent chatComponent) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(chatComponent.asJson())));
	}

	public static void clearChat(Player player) {
		chat(player, new ChatComponent().append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n").append("\n"));
	}

	public static void sendRecentMessages(Player p) {
		p.sendMessage(String.join("\n", GTowny.getRecentMessages(p)) + "\n");
	}

	public static void confirmAction(Player p, String action, String warning, String command) {
		String rule = MessageFormatter.createSeparator(ChatColor.GREEN + action, ChatColor.GOLD, ChatColor.STRIKETHROUGH);
		ChatOutput.clearChat(p);
		ChatOutput.sendRecentMessages(p);
		ChatOutput.chat(p, new ChatComponent()
				.append(rule + "\n")
				.append(warning + "\n").setColour(ChatColor.RED)
				.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GREEN + "Click yes to confirm.\n" + ChatColor.YELLOW + "Nothing will happen if you don't confirm.")
				.append("\n" + ChatColor.BOLD + "     ")
				.append(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Yes]")
				.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.DARK_RED + "Click me to confirm.")
				.setClickable(ChatComponent.ClickAction.RUN_COMMAND, command)
				.append(ChatColor.BOLD + "                                      ")
				.append(ChatColor.GREEN + "" + ChatColor.BOLD + "[No]\n")
				.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GREEN + "Nothing will happen if you click me.")
				.append(rule)
		);
	}
}
