package io.github.loldatsec.mcplugs.prisonplus.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Stack {

	public Stack(Player p, boolean silent) {
		Map<Material, Integer> stackable = new HashMap<Material, Integer>();
		for (int n = 0; n <= 35; n++) {
			try {
				ItemStack i = p.getInventory().getItem(n);
				if (i.getType() == Material.COAL || i.getType() == Material.IRON_INGOT || i.getType() == Material.GOLD_INGOT || i.getType() == Material.DIAMOND || i.getType() == Material.EMERALD || i.getType() == Material.REDSTONE || i.getType() == Material.QUARTZ || (i.getType() == Material.INK_SACK && i.getDurability() == 4)) {
					if (stackable.containsKey(i.getType())) {
						stackable.put(i.getType(), stackable.get(i.getType()) + i.getAmount());
					} else {
						stackable.put(i.getType(), i.getAmount());
					}
					p.getInventory().setItem(n, null);
				}
			} catch (NullPointerException e) {
			}
		}
		List<String> in = new ArrayList<String>();
		List<String> out = new ArrayList<String>();
		for (Material mat : stackable.keySet()) {
			int amt = stackable.get(mat);
			in.add(amt + " " + mat.toString().toLowerCase());
			int raw = amt % 9;
			int ret = (amt - raw) / 9;
			Material rm = Material.COAL_BLOCK;
			if (mat == Material.QUARTZ) {
				raw = amt % 4;
				ret = (amt - raw) / 4;
			}
			if (mat == Material.IRON_INGOT) {
				rm = Material.IRON_BLOCK;
			} else if (mat == Material.GOLD_INGOT) {
				rm = Material.GOLD_BLOCK;
			} else if (mat == Material.DIAMOND) {
				rm = Material.DIAMOND_BLOCK;
			} else if (mat == Material.EMERALD) {
				rm = Material.EMERALD_BLOCK;
			} else if (mat == Material.REDSTONE) {
				rm = Material.REDSTONE_BLOCK;
			} else if (mat == Material.QUARTZ) {
				rm = Material.QUARTZ_BLOCK;
			} else if (mat == Material.INK_SACK) {
				rm = Material.LAPIS_BLOCK;
			}
			if (ret >= 1) {
				p.getInventory().addItem(new ItemStack(rm, ret));
				out.add(ret + " " + rm.toString().toLowerCase());
			}
			if (raw >= 1) {
				ItemStack r = new ItemStack(mat, raw);
				if (mat == Material.INK_SACK) {
					r.setDurability((short) 4);
				}
				p.getInventory().addItem(r);
			}
		}
		if (!silent) {
			if (out.size() >= 1) {
				p.sendMessage("\u00a7aTurned \u00a7c" + String.join(", ", in) + " \u00a7ain to \u00a7b" + String.join(", ", out) + "\u00a7a.");
				p.updateInventory();
			} else {
				p.sendMessage("\u00a7cNothing to stack!");
			}
		}
	}
}
