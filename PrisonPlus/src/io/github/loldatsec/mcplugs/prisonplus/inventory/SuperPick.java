package io.github.loldatsec.mcplugs.prisonplus.inventory;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class SuperPick {
	public static void give(String pn) {
		Player p = Bukkit.getPlayerExact(pn);
		if (p != null) {
			ItemStack i = new ItemStack(Material.DIAMOND_PICKAXE, 1);
			i.addUnsafeEnchantment(Enchantment.DIG_SPEED, 150);
			i.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 150);
			i.addUnsafeEnchantment(Enchantment.DURABILITY, 32767);
			ItemMeta m = i.getItemMeta();
			m.setDisplayName("\u00a76[\u00a7e\u00a7l" + p.getName() + "'s Super Pick\u00a76] - [\u00a7e" + p.getStatistic(Statistic.MINE_BLOCK, Material.STONE) + "\u00a76]");
			i.setItemMeta(m);
			if (p.getInventory().getContents().length > 45) {
				p.getWorld().dropItem(p.getLocation(), i).setVelocity(new Vector(0, 0, 0));
			} else {
				p.getInventory().addItem(i);
			}
			p.sendMessage("\u00a76You received a super pickaxe!");
		} else {
			Bukkit.getLogger().log(Level.SEVERE, pn + " donated for a pickaxe but cannot receive it!");
		}
	}
}
