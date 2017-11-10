package biz.reborncraft.minigames.utilscore;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger {
	public HashMap<String, String> reply = new HashMap<String, String>();

	public void clearOffline() {
		HashMap<String, String> newHashMap = new HashMap<String, String>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			String pn = player.getName();
			if (reply.containsKey(pn)) {
				newHashMap.put(pn, reply.get(pn));
			}
		}
		reply = newHashMap;
	}

	public void pm(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			CommandSender recv = Bukkit.getPlayer(args[0]);
			if (recv == null && args[0].equalsIgnoreCase("Console")) {
				recv = Bukkit.getConsoleSender();
			}
			if (recv instanceof CommandSender) {
				reply.put(sender.getName(), recv.getName());
				reply.put(recv.getName(), sender.getName());
				if (args.length >= 2) {
					String message = "";
					for (String argv : Arrays.copyOfRange(args, 1, args.length)) {
						message += argv + " ";
					}
					sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED
							+ "me » " + recv.getName() + ChatColor.GOLD + "] "
							+ ChatColor.WHITE + message);
					recv.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED
							+ sender.getName() + " » me" + ChatColor.GOLD
							+ "] " + ChatColor.WHITE + message);
					if (recv instanceof Player && sender instanceof Player) {
						((Player) recv).playSound(
								((Player) recv).getEyeLocation(),
								Sound.ITEM_PICKUP, 1, 1);
						Bukkit.getConsoleSender().sendMessage(
								ChatColor.GOLD + "[" + ChatColor.RED
										+ sender.getName() + " » "
										+ recv.getName() + ChatColor.GOLD
										+ "] " + ChatColor.WHITE + message);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Error: "
							+ ChatColor.DARK_RED
							+ "Cant send message because there isn't one!.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Error: "
						+ ChatColor.DARK_RED + "Player not found.");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED
					+ "No player named " + ChatColor.RED + "null"
					+ ChatColor.DARK_RED + ".");
		}
	}

	public void reply(CommandSender sender, String[] args) {
		if (reply.get(sender.getName()) != null) {
			CommandSender recv = Bukkit.getPlayer(reply.get(sender.getName()));
			if (recv == null
					&& reply.get(sender.getName()).equalsIgnoreCase("Console")) {
				recv = Bukkit.getConsoleSender();
			}
			if (recv instanceof CommandSender) {
				reply.put(recv.getName(), sender.getName());
				reply.put(sender.getName(), recv.getName());
				if (args.length >= 1) {
					String message = "";
					for (String argv : args) {
						message += argv + " ";
					}
					sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED
							+ "me » " + recv.getName() + ChatColor.GOLD + "] "
							+ ChatColor.WHITE + message);
					recv.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED
							+ sender.getName() + " » me" + ChatColor.GOLD
							+ "] " + ChatColor.WHITE + message);
					if (recv instanceof Player && sender instanceof Player) {
						((Player) recv).playSound(
								((Player) recv).getEyeLocation(),
								Sound.ITEM_PICKUP, 1, 1);
						Bukkit.getConsoleSender().sendMessage(
								ChatColor.GOLD + "[" + ChatColor.RED
										+ sender.getName() + " » "
										+ recv.getName() + ChatColor.GOLD
										+ "] " + ChatColor.WHITE + message);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Error: "
							+ ChatColor.DARK_RED
							+ "Cant send message because there isn't one!.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Error: "
						+ ChatColor.DARK_RED + "That player is not online.");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED
					+ "You don't have anyone to reply to!");
		}
	}

}
