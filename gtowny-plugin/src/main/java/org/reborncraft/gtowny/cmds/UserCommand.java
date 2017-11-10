package org.reborncraft.gtowny.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.chat.ChatComponent;
import org.reborncraft.gtowny.chat.ChatOutput;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.data.Town;
import org.reborncraft.gtowny.data.TownyDataHandler;
import org.reborncraft.gtowny.data.User;

public class UserCommand implements TownyCommandExecutor {
	@Override
	public void unknownSubcommand(Command cmd, CommandSender sender, String[] args) {
		display(sender, args);
	}

	@Override
	public void defaultCommand(CommandSender sender) {
		display(sender, new String[]{});
	}

	private void display(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			OfflinePlayer target = null;
			if (args.length >= 1) {
				target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					//noinspection deprecation
					target = Bukkit.getOfflinePlayer(args[0]);
				}
			}
			if (target == null) {
				target = (OfflinePlayer) sender;
			}
			User user = User.forName(target.getName());
			Town town = user.getTown();

			String rule = MessageFormatter.createSeparator(ChatColor.GREEN + "Player Information", ChatColor.GOLD, ChatColor.STRIKETHROUGH);
			ChatComponent comp = new ChatComponent()
					.append(rule + "\n")
					.append(ChatColor.GOLD + "Name: " + ChatColor.GREEN + user.getName() + "\n")
					.append("Town: ").setColour(ChatColor.GOLD)
					.append(town.getName()).setColour(ChatColor.GREEN)
					.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Click for more information.")
					.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/town info " + town.getName())
					.append("\n");
			if (town.getId() > 0) {
				comp.append("Town Rank: ").setColour(ChatColor.GOLD)
						.append(user.getRank().getName()).setColour(ChatColor.GREEN).append("\n");
			}

			comp.append(ChatColor.GOLD + "Shield: " + (user.isShieldActive() ? ChatColor.AQUA + MessageFormatter.secondsToHMS(user.getShieldLastsUntil() - System.currentTimeMillis()) : ChatColor.RED + "Deactivated") + "\n")
					.append(ChatColor.GOLD + "Owns " + ChatColor.GREEN + TownyDataHandler.chunkOwnCount(user) + ChatColor.GOLD + " chunks." + "\n")
					.append(ChatColor.GOLD + "Member of " + ChatColor.GREEN + TownyDataHandler.chunkMemberofCount(user) + ChatColor.GOLD + " chunks." + "\n");
			ChatOutput.clearChat((Player) sender);
			ChatOutput.sendRecentMessages((Player) sender);
			ChatOutput.chat((Player) sender, comp.append(rule));
		} else {
			sender.sendMessage(MessageFormatter.error("Only a player can do this."));
		}
	}
}