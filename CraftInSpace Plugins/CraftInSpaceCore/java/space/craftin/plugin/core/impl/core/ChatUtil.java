/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.core;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {
	public static void send(CommandSender sender, ChatMessage message) {
		sender.sendMessage(message.toString());
	}

	public static void send(CommandSender sender, String message) {
		sender.sendMessage(message);
	}

	public enum ChatMessage {
		ERROR_PREFIX(ChatColor.DARK_RED + "Error: " + ChatColor.RED),
		FAIL_PREFIX(ChatColor.GOLD + "Action failed: " + ChatColor.YELLOW),
		OK_PREFIX(ChatColor.DARK_GREEN + "Successfully performed action: " + ChatColor.GREEN),
		HELP_PREFIX(ChatColor.DARK_GRAY + "Available subcommands: " + ChatColor.GRAY),
		NO_PERMISSIONS(ERROR_PREFIX + "No permissions."),
		NOT_PLAYER(ERROR_PREFIX + "Only a player can do that."),
		NO_SUCH_SUBCOMMAND(ERROR_PREFIX + "No subcommand found by that name."),
		FACCREATE_FAIL(FAIL_PREFIX + "Create faction."),
		SHIPCREATE_FAIL(FAIL_PREFIX + "Create ship."),
		FACCREATE_OK(OK_PREFIX + "Create faction."),
		LEAVE_FACTION_OK(OK_PREFIX + "Leave faction.");
		private final String message;

		ChatMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return message;
		}
	}
}
