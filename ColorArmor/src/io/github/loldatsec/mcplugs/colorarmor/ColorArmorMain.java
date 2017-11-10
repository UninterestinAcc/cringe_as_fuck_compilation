package io.github.loldatsec.mcplugs.colorarmor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ColorArmorMain extends JavaPlugin {

	private List<String> change = new ArrayList<String>();

	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (String playerName : change) {
					Player p = Bukkit.getPlayerExact(playerName);
					if (p != null) {
						armor(p);
					}
				}
			}
		}, 0L, 1L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("colorarmor.use")) {
				if (change.contains(sender.getName())) {
					change.remove(sender.getName());
				} else {
					change.add(sender.getName());
				}
				sender.sendMessage(ChatColor.GOLD + "Toggled colored armor mode to " + ChatColor.RED + change.contains(sender.getName()));
			} else {
				noPermission(sender);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Dood! You are console!");
		}
		return true;
	}

	public void noPermission(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "You do not have the permissions for this action.");
	}

	protected void armor(Player p) {
		Color c = Color.fromRGB((int) (Math.random() * 16777215));
		int s = 39;
		for (Material armorPiece : new Material[] { Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS }) {
			ItemStack item = new ItemStack(armorPiece, 1);
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setColor(c);
			meta.setDisplayName("§cC§6o§el§ao§3r§9e§5d");
			item.setItemMeta(meta);
			p.getInventory().setItem(s--, item);
		}
	}
}
