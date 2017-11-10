package io.github.loldatsec.mcplugs.halocore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Inv {
	public static void addSkull(Player player, String deadName) {
		Inventory inv = player.getInventory();
		for (int slot = 26; slot >= 18; slot--) {
			inv.setItem(slot, inv.getItem(slot - 1));
		}
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
		skull.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(deadName);
		List<String> lore = new ArrayList<String>();
		lore.add("§eKilled by §6" + player.getName());
		lore.add("§2Time: §a"
				+ new SimpleDateFormat("YYYY-MMM-dd HH:mm:ss z")
						.format(new Date()));
		meta.setLore(lore);
		meta.setDisplayName("§bVictim: §a" + deadName);
		skull.setItemMeta(meta);
		inv.setItem(18, skull);
		player.updateInventory();
	}
}
