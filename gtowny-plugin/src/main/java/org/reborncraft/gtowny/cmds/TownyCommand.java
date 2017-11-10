package org.reborncraft.gtowny.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.GTowny;
import org.reborncraft.gtowny.chat.ChatOutput;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.cmds.utils.ClaimUtils;
import org.reborncraft.gtowny.data.TownyDataHandler;
import org.reborncraft.gtowny.data.Chunk;
import org.reborncraft.gtowny.data.User;

public class TownyCommand implements TownyCommandExecutor {
	@GTownySubcommand
	public void claim(CommandSender sender, User senderUser, String[] args) {
		if (sender.hasPermission("gtowny.admin")) {
			Chunk c = senderUser.getCurrentChunk();
			if (args.length >= 1) {
				if (args[0].toLowerCase().startsWith("s")) {
					TownyDataHandler.TOWN_SAFEZONE.claimChunk(c);
					sender.sendMessage(MessageFormatter.success("Claimed chunk into SAFEZONE."));
				} else if (args[0].toLowerCase().startsWith("w")) {
					TownyDataHandler.TOWN_WARZONE.claimChunk(c);
					sender.sendMessage(MessageFormatter.success("Claimed chunk into WARZONE."));
				} else {
					sender.sendMessage(MessageFormatter.error("Must specify either [S]AFEZONE or [W]ARZONE."));
				}
			} else {
				sender.sendMessage(MessageFormatter.error("Must specify either [S]AFEZONE or [W]ARZONE."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("No permission."));
		}
	}

	@GTownySubcommand
	public void massclaim(CommandSender sender, User senderUser, String[] args) {
		if (sender.hasPermission("gtowny.admin")) {
			if (args.length >= 1) {
				if (args.length >= 2) {
					if (args.length >= 3) {
						int radii = Integer.parseInt(args[1]);
						Chunk c = senderUser.getCurrentChunk();
						if (args[0].toLowerCase().startsWith("s")) {
							if (args[2].toLowerCase().startsWith("r")) {
								sender.sendMessage(MessageFormatter.success("Claimed " + ClaimUtils.claimRect(c.getChunkLocation(), radii, ((Player) sender).getWorld().getName(), -2, true, true) + " chunks into SAFEZONE."));
							} else if (args[2].toLowerCase().startsWith("c")) {
								sender.sendMessage(MessageFormatter.success("Claimed " + ClaimUtils.claimCirc(c.getChunkLocation(), radii, ((Player) sender).getWorld().getName(), -2, true, true) + " chunks into SAFEZONE."));
							} else {
								sender.sendMessage(MessageFormatter.error("Must specify either [R]ectangular or [C]ircular."));
							}
						} else if (args[0].toLowerCase().startsWith("w")) {
							if (args[2].toLowerCase().startsWith("r")) {
								sender.sendMessage(MessageFormatter.success("Claimed " + ClaimUtils.claimRect(c.getChunkLocation(), radii, ((Player) sender).getWorld().getName(), -3, true, true) + " chunks into WARZONE."));
							} else if (args[2].toLowerCase().startsWith("c")) {
								sender.sendMessage(MessageFormatter.success("Claimed " + ClaimUtils.claimCirc(c.getChunkLocation(), radii, ((Player) sender).getWorld().getName(), -3, true, true) + " chunks into WARZONE."));
							} else {
								sender.sendMessage(MessageFormatter.error("Must specify either [R]ectangular or [C]ircular."));
							}
						} else {
							sender.sendMessage(MessageFormatter.error("Must specify either [S]AFEZONE or [W]ARZONE."));
						}
					} else {
						sender.sendMessage(MessageFormatter.error("Must specify either [R]ectangular or [C]ircular."));
					}
				} else {
					sender.sendMessage(MessageFormatter.error("Must specify the radii."));
				}
			} else {
				sender.sendMessage(MessageFormatter.error("Must specify either [S]AFEZONE or [W]ARZONE."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("No permission."));
		}
	}

	@GTownySubcommand
	public void declaim(CommandSender sender, User senderUser, String[] args) {
		if (sender.hasPermission("gtowny.admin")) {
			Chunk c = senderUser.getCurrentChunk();
			TownyDataHandler.TOWN_WILDERNESS.claimChunk(c);
			sender.sendMessage(MessageFormatter.success("Chunk is now owned by WILDERNESS."));
		} else {
			sender.sendMessage(MessageFormatter.error("No permission."));
		}
	}

	@GTownySubcommand (requirePlayer = false)
	public void clearChat(CommandSender sender, User senderUser, String[] args) {
		if (sender.hasPermission("gtowny.admin")) {
			Bukkit.getOnlinePlayers().forEach(p -> {
				ChatOutput.clearChat(p);
				GTowny.getMessageRecords().clear();
			});
		}
	}

	@Override
	public void defaultCommand(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_AQUA + "[GTowny] " + ChatColor.YELLOW + "Towny made for Reboncraft.org. Created by GrumpyCowOG. https://github.com/LolDatSec");
	}
}
