package io.github.loldatsec.mcplugins.haloplus.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class SpecialChat {

	public static void sendProgress(Player p, int progress, int max, String pre) {
		sendActionBarChat(p, progress(progress, max, pre + " \u00a78[", "\u00a78] \u00a7f%i Seconds", "\u00a72\u00a7l", "\u00a74\u00a7l", "»"));
	}

	public static String progress(int progress, int max, String pre, String post, String prog, String incomp, String progelem) {
		max = max / 4;
		progress = progress / 4;
		String text = prog;
		for (int u = max; u >= 1; u--) {
			if (u == progress) {
				text += incomp;
			}
			text += progelem;
		}
		float p = (float) (progress / 4F);
		return (pre + " " + text + " " + post).replaceAll("%i", (p <= 1 ? p <= 0 ? 0 : (int) p + 1 : (int) p + 1) + "");
	}

	public static String twoWayGameprogress(int blue, int red) {
		// XXX »«
		String b = "\u00a7b\u00a7l";
		String m = "" + (blue + red + 1);
		String r = "\u00a77\u00a7l";
		for (int a = 0; a < 4; a++) {
			if (a == blue) {
				b += "\u00a77\u00a7l";
			}
			b += "»";
		}
		if (red == 5) {
			m = "\u00a7c\u00a7l" + m;
			r = "««««";
		} else {
			if (!b.contains("\u00a77")) {
				b += "\u00a77\u00a7l";
			}
			for (int a = 0; a < 4; a++) {
				if (a == (4 - red)) {
					r += "\u00a7c\u00a7l";
				}
				r += "«";
			}
		}
		return "\u00a78[" + b + m + r + "\u00a78]";
	}

	public static void sendActionBarChat(Player player, String text) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2));
	}

	public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		if (fadeIn + stay + fadeOut > 0) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, ChatSerializer.a("{\"text\":\" \"}"), fadeIn, stay, fadeOut));
		}
		if (title != null) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, stay, fadeOut));
		}
		if (subTitle != null && subTitle != "") {
			sendSubtitle(player, subTitle);
		}
	}

	public static void sendSubtitle(Player player, String subTitle) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + subTitle + "\"}")));
	}

	public static void tell(CommandSender p, String[] text, ChatColor color) {
		p.sendMessage(color + "\u00a7l\u00a7m=============================================");
		for (String t : text) {
			p.sendMessage("    \u00a7e\u00a7l" + t);
		}
		p.sendMessage(color + "\u00a7l\u00a7m=============================================");
	}
}
