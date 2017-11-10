package org.reborncraft.gtowny.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.reborncraft.gtowny.data.User;

public class GTownyCommand implements TownyCommandExecutor {
	@Override
	public void unknownSubcommand(Command cmd, CommandSender sender, String[] args) {
		defaultCommand(sender);
	}

//	@GTownySubcommand
	public void debug(CommandSender sender, User senderUser, String[] args) {
		if (sender.hasPermission("gtowny.admin")) {
		}
	}

	@Override
	public void sendHelp(CommandSender sender) {
		defaultCommand(sender);
	}

	@Override
	public void defaultCommand(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_AQUA + "[GTowny] " + ChatColor.YELLOW + "Towny made for Reboncraft.org. Created by GrumpyCowOG. https://github.com/LolDatSec");
	}
}
