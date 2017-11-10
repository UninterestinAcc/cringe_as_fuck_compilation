package io.github.loldatsec.mcplugs.prisonplus.vote;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PItemStack extends ItemStack {

	public int getReqStars() {
		if (this.getType().toString().startsWith("DIAMOND_")) {
			return 8;
		} else if (this.getType().toString().equalsIgnoreCase("STEP")) {
			return 12;
		} else if (this.getType() == Material.BOOK) {
			return 4;
		} else {
			return 3;
		}
	}

	public PItemStack(Material mat, int amt) {
		super(mat, amt);
	}

	public PItemStack(Material mat, int amt, int d, String name) {
		super(mat, amt, (short) d);
		this.setDisplayName(name);
	}

	public PItemStack(Material mat, int amt, String name, String[] lore) {
		super(mat, amt);
		this.setDisplayName(name);
		this.setLore(lore);
	}

	public PItemStack(Material mat, int amt, String name, Enchantment ench, int level) {
		super(mat, amt);
		this.setDisplayName(name);
		this.addUnsafeEnchantment(ench, level);
	}

	public PItemStack(Material mat, int amt, String name, Enchantment ench, int level, String[] lore) {
		super(mat, amt);
		this.setDisplayName(name);
		this.addUnsafeEnchantment(ench, level);
		this.setLore(lore);
	}

	public PItemStack(Material mat, int amt, int d, String name, String[] lore) {
		super(mat, amt, (short) d);
		this.setDisplayName(name);
		this.setLore(lore);
	}

	public PItemStack setDisplayName(String name) {
		ItemMeta m = getItemMeta();
		m.setDisplayName(name);
		setItemMeta(m);
		return this;
	}

	public PItemStack setLore(String[] lore) {
		ItemMeta m = getItemMeta();
		m.setLore(Arrays.asList(lore));
		setItemMeta(m);
		return this;
	}

	private int reqStars = 0;

	public PItemStack setWorth(int reqStars) {
		this.reqStars = reqStars;
		return this;
	}

	public int getWorth() {
		return reqStars;
	}
}
