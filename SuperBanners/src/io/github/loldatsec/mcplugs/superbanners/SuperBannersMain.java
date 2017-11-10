package io.github.loldatsec.mcplugs.superbanners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperBannersMain extends JavaPlugin implements Listener {

	private List<SuperBannersGUI> guis = new ArrayList<SuperBannersGUI>();

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("superbanners.use")) {
				if (args.length >= 1) {
					guis.add(new SuperBannersCharsGUI((Player) sender));
				} else {
					guis.add(new SuperBannersCraftGUI((Player) sender));
				}
			} else {
				noPermission(sender);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Dood! You are console!");
		}
		return true;
	}

	public void noPermission(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED
				+ "You do not have the permissions for this action.");
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		for (SuperBannersGUI s : guis) {
			if (e.getWhoClicked().equals(s.getViewer())) {
				s.click(e);
				return;
			}
		}
	}

	@EventHandler
	public void inventoryClose(InventoryCloseEvent e) {
		int r = -1;
		for (SuperBannersGUI s : guis) {
			if (e.getPlayer().equals(s.getViewer())) {
				r = guis.indexOf(s);
				break;
			}
		}
		if (r >= 0) {
			guis.remove(r);
		}
	}
}
