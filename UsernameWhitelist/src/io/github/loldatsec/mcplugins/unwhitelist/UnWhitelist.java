package io.github.loldatsec.mcplugins.unwhitelist;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class UnWhitelist extends JavaPlugin implements Listener {

	List<String> whitelist = new ArrayList<String>();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		loadConfig();
	}

	@EventHandler
	public void asyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		if (!whitelist.contains(e.getName().toLowerCase())) {
			e.disallow(Result.KICK_WHITELIST, "\u00a76[Whitelist] \u00a7eYou are not allowed.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("whitelist.admin") || sender instanceof ConsoleCommandSender) {
			if (args.length >= 1) {
				String action = args[0];
				if (action.equalsIgnoreCase("LIST")) {
					sender.sendMessage("\u00a73Username Whitelist> \u00a7b" + whitelist);
					return true;
				} else if (action.equalsIgnoreCase("RELOAD")) {
					loadConfig();
					sender.sendMessage("\u00a73Username Whitelist> \u00a7bReloaded configuration.");
					return true;
				} else if (args.length >= 2) {
					String name = args[1].toLowerCase();
					if (action.equalsIgnoreCase("ADD")) {
						if (!whitelist.contains(name)) {
							whitelist.add(name);
							saveConfig();
							sender.sendMessage("\u00a73Username Whitelist> \u00a7bAdded " + name + " to whitelist.");
						} else {
							sender.sendMessage("\u00a74Error> \u00a7c" + name + " is already allowed!");
						}
						return true;
					} else if (action.equalsIgnoreCase("REMOVE")) {
						if (whitelist.contains(name)) {
							whitelist.remove(name);
							saveConfig();
							sender.sendMessage("\u00a73Username Whitelist> \u00a7bRemoved " + name + " from whitelist.");
						} else {
							sender.sendMessage("\u00a74Error> \u00a7c" + name + "is not allowed!");
						}
						return true;
					}
				}
			}
		} else {
			sender.sendMessage("\u00a74Error> \u00a7cNo permissions!");
			return true;
		}
		sender.sendMessage("\u00a74Error> \u00a7cUnrecognized parameter(s): \u00a77" + String.join(" ", args));
		return true;
	}

	@SuppressWarnings("unchecked")
	public void loadConfig() {
		try {
			List<String> preWriteList = new ArrayList<String>();
			for (String name : (List<String>) getConfig().getList("allowed")) {
				preWriteList.add(name.toLowerCase());
			}
			if (preWriteList.size() >= 1) {
				whitelist = preWriteList;
				Bukkit.getConsoleSender().sendMessage("\u00a76[Whitelist] \u00a7eConfiguration loaded.");
			} else {
				Bukkit.getConsoleSender().sendMessage("\u00a76[Whitelist] \u00a7eConfiguration section 'allowed' is either null, improperly formatted or empty.");
			}
		} catch (ClassCastException ex) {
			Bukkit.getConsoleSender().sendMessage("\u00a76[Whitelist] \u00a7eConfiguration section 'allowed' is either null or improperly formatted.");
		}
	}

	public void saveConfig() {
		getConfig().set("allowed", whitelist);
		super.saveConfig();
		Bukkit.getConsoleSender().sendMessage("\u00a76[Whitelist] \u00a7eConfiguration saved.");
	}
}
