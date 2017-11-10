package io.github.loldatsec.mcplugs.prisonplus.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Smelt {

	public Smelt(Player p, boolean silent) {
		Map<Material, Integer> smeltme = new HashMap<Material, Integer>();
		List<Integer> clearSlots = new ArrayList<Integer>();
		List<Integer> coalSlots = new ArrayList<Integer>();
		List<Integer> coalBlockSlots = new ArrayList<Integer>();
		Map<Material, ItemStack> smeltable = smeltable();
		int smeltAmt = 0;
		for (int n = 0; n <= 35; n++) {
			try {
				ItemStack i = p.getInventory().getItem(n);
				if (smeltable.keySet().contains(i.getType())) {
					if (smeltme.containsKey(i.getType())) {
						smeltme.put(i.getType(), smeltme.get(i.getType()) + i.getAmount());
					} else {
						smeltme.put(i.getType(), i.getAmount());
					}
					smeltAmt += i.getAmount();
					clearSlots.add(n);
				} else if (i.getType() == Material.COAL) {
					coalSlots.add(n);
				} else if (i.getType() == Material.COAL_BLOCK) {
					coalBlockSlots.add(n);
				}
			} catch (NullPointerException e) {
			}
		}
		int coalReq = (int) (smeltAmt / 8 + 1);
		int coalNeeded = coalReq;
		for (int cs : coalSlots) {
			if (coalNeeded > 0) {
				ItemStack coal = p.getInventory().getItem(cs);
				int removedCoal = coal.getAmount();
				if (removedCoal > coalNeeded) {
					removedCoal = coalNeeded;
				}
				coal.setAmount(coal.getAmount() - removedCoal);
				coalNeeded = coalNeeded - removedCoal;
				if (coal.getAmount() <= 0) {
					p.getInventory().setItem(cs, null);
				} else {
					p.getInventory().setItem(cs, coal);
				}
			}
		}
		for (int cs : coalBlockSlots) {
			if (coalNeeded > 0) {
				ItemStack coal = p.getInventory().getItem(cs);
				int removedCoal = coal.getAmount();
				if (removedCoal > coalNeeded) {
					removedCoal = coalNeeded;
				}
				coal.setAmount(coal.getAmount() - removedCoal);
				coalNeeded = coalNeeded - (removedCoal * 9);
				if (coal.getAmount() <= 0) {
					p.getInventory().setItem(cs, null);
				} else {
					p.getInventory().setItem(cs, coal);
				}
			}
		}
		if (smeltAmt <= 0) {
			if (!silent) {
				p.sendMessage("\u00a7cNothing to smelt!");
			}
			int cn = 0 - coalNeeded;
			if (cn >= 1) {
				p.getInventory().addItem(new ItemStack(Material.COAL, cn));
			}
		} else if (coalNeeded > 0) {
			if (!silent) {
				p.sendMessage("\u00a7cYou need \u00a77" + coalNeeded + " \u00a7cmore coal to smelt.");
			}
			int coalRetAmt = coalReq - coalNeeded;
			if (coalRetAmt >= 1) {
				p.getInventory().addItem(new ItemStack(Material.COAL, coalRetAmt));
			}
		} else {
			for (int cs : clearSlots) {
				p.getInventory().setItem(cs, null);
			}
			List<String> in = new ArrayList<String>();
			List<String> out = new ArrayList<String>();
			for (Material mat : smeltme.keySet()) {
				int amt = smeltme.get(mat);
				in.add(amt + " " + mat.toString().toLowerCase());
				ItemStack ret = smeltable.get(mat);
				ret.setAmount(amt);
				out.add(amt + " " + ret.getType().toString().toLowerCase());
				p.getInventory().addItem(ret);
			}
			if (!silent) {
				p.sendMessage("\u00a7aSmelted \u00a7c" + String.join(", ", in) + "\u00a7a into \u00a7b" + String.join(",  ", out) + "\u00a7a, using \u00a77" + coalReq + "\u00a7a coal.");
			}
		}
		p.updateInventory();
	}

	public Map<Material, ItemStack> smeltable() {
		Map<Material, ItemStack> smeltable = new HashMap<Material, ItemStack>();
		smeltable.put(Material.LOG, new ItemStack(Material.COAL, 1));
		smeltable.put(Material.LOG_2, new ItemStack(Material.COAL, 1));
		smeltable.put(Material.CLAY_BALL, new ItemStack(Material.CLAY_BRICK, 1));
		smeltable.put(Material.CLAY, new ItemStack(Material.HARD_CLAY, 1));
		smeltable.put(Material.COBBLESTONE, new ItemStack(Material.STONE, 1));
		smeltable.put(Material.CACTUS, new ItemStack(Material.INK_SACK, 1, (short) 2));
		smeltable.put(Material.LAPIS_ORE, new ItemStack(Material.INK_SACK, 1, (short) 4));
		smeltable.put(Material.SAND, new ItemStack(Material.GLASS, 1));
		smeltable.put(Material.QUARTZ_ORE, new ItemStack(Material.QUARTZ, 1));
		smeltable.put(Material.COAL_ORE, new ItemStack(Material.COAL, 1));
		smeltable.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT, 1));
		smeltable.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT, 1));
		smeltable.put(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND, 1));
		smeltable.put(Material.EMERALD_ORE, new ItemStack(Material.EMERALD, 1));
		smeltable.put(Material.REDSTONE_ORE, new ItemStack(Material.REDSTONE, 1));
		smeltable.put(Material.NETHERRACK, new ItemStack(Material.NETHER_BRICK_ITEM, 1));
		smeltable.put(Material.PORK, new ItemStack(Material.GRILLED_PORK, 1));
		smeltable.put(Material.RAW_BEEF, new ItemStack(Material.COOKED_BEEF, 1));
		smeltable.put(Material.RAW_CHICKEN, new ItemStack(Material.COOKED_CHICKEN, 1));
		smeltable.put(Material.MUTTON, new ItemStack(Material.COOKED_MUTTON, 1));
		return smeltable;
	}
}
