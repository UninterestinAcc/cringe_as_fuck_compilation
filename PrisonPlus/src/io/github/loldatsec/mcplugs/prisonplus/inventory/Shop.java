package io.github.loldatsec.mcplugs.prisonplus.inventory;

import io.github.loldatsec.mcplugs.prisonplus.rankup.Rankup;
import io.github.loldatsec.mcplugs.prisonplus.text.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Shop {

	public Economy econ = null;
	private Map<String, Integer> boosted = new HashMap<String, Integer>();
	private int voteParty = 0;
	private boolean isParty = false;
	private Plugin plugin;
	public static final String votePartyPrefix = "\u00a7d[\u00a75VoteParty\u00a7d] ";
	private int timeleft = 0;
	private BukkitScheduler scheduler;

	public boolean isParty() {
		return isParty;
	}

	public int getVoteParty() {
		return voteParty;
	}

	public double getVotePartyProgress() {
		return (double) ((double) voteParty / 240);
	}

	public void setVoteParty(int voteParty) {
		this.voteParty = voteParty;
	}

	public void incrementVoteParty() {
		++voteParty;
	}

	public void votePartyEvent(String opt) {
		if (!isParty) {
			if (opt.equalsIgnoreCase("schedule")) {
				voteParty = 235;
				return;
			}
			if (opt.startsWith("drop")) {
				ItemStack votePartyPick = new ItemStack(Material.DIAMOND_PICKAXE, 1);
				votePartyPick.addUnsafeEnchantment(Enchantment.DIG_SPEED, 20);
				votePartyPick.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 10);
				ItemMeta vPPm = votePartyPick.getItemMeta();
				vPPm.setDisplayName("\u00a7d[\u00a75Vote Party Pick\u00a7d] - \u00a75[\u00a7d1\u00a75]");
				votePartyPick.setItemMeta(vPPm);
				ItemStack votePartyAxe = new ItemStack(Material.DIAMOND_AXE, 1);
				votePartyAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 15);
				votePartyAxe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
				votePartyAxe.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5);
				ItemMeta vPAm = votePartyAxe.getItemMeta();
				vPAm.setDisplayName("\u00a7d[\u00a75Vote Party Axe\u00a7d] - \u00a75[\u00a7d1\u00a75]");
				votePartyAxe.setItemMeta(vPAm);
				for (Player p : Bukkit.getOnlinePlayers()) {
					econ.depositPlayer(p, 100000);
					p.getWorld().dropItemNaturally(p.getEyeLocation(), votePartyPick);
					p.getWorld().dropItemNaturally(p.getEyeLocation(), votePartyAxe);
					p.sendMessage(votePartyPrefix + "\u00a7dYou earned \u00a7c$100k \u00a7dfrom voteparty!");
					incrementBoost(p.getName(), 1);
				}
			}
			if (opt.endsWith("mine")) {
				isParty = true;
				Chat.bc(votePartyPrefix + "\u00a7e/warp voteparty\u00a7d is open for mining!");
				timeleft = 30;
				for (long delay = 11400; delay < 12000; delay += 40) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

						@Override
						public void run() {
							if (isParty) {
								Chat.bc(votePartyPrefix + "\u00a7e/warp voteparty\u00a7d is closing in \u00a7e" + timeleft + " \u00a7dseconds.");
								timeleft -= 2;
							}
						}
					}, delay);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run() {
						if (isParty) {
							isParty = false;
							Chat.bc(votePartyPrefix + "\u00a7e/warp voteparty\u00a7d is closed.");
						}
					}
				}, 12000L);
			}
		}
	}

	public Map<String, Integer> getBoosted() {
		return boosted;
	}

	public void setBoost(Map<String, Integer> list) {
		boosted = list;
	}

	public Integer getBoost(String pn) {
		Player p = Bukkit.getPlayerExact(pn);
		int boost = 1;
		if (p instanceof Player) {
			if (boosted.containsKey(p.getName())) {
				boost += boosted.get(p.getName());
			}
			int rb = 0;
			for (int b = 2; b <= 4; b++) {
				if (p.hasPermission("prisonplus.sellboost." + b)) {
					rb = b - 1;
				}
			}
			boost += rb;
		}
		return boost;
	}

	public Integer getExclusiveBoost(String pn) {
		int boost = 0;
		if (boosted.containsKey(pn)) {
			boost += boosted.get(pn);
		}
		return boost;
	}

	public Integer setBoost(String pn, int amt) {
		return boosted.put(pn, amt);
	}

	public Integer incrementBoost(String pn, int amt) {
		if (boosted.containsKey(pn)) {
			return boosted.put(pn, boosted.get(pn) + amt);
		} else {
			return setBoost(pn, amt);
		}
	}

	public Integer decrementBoost(String pn, int amt) {
		int a = boosted.get(pn) - amt;
		if (boosted.containsKey(pn)) {
			if (a >= 1) {
				return boosted.put(pn, a);
			} else {
				return boosted.put(pn, 0);
			}
		} else {
			return 0;
		}
	}

	public Shop() {
		this.econ = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		this.plugin = Bukkit.getPluginManager().getPlugin("PrisonPlus");
		this.scheduler = Bukkit.getScheduler();
		scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				if (voteParty >= 240) {
					voteParty = 0;
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "voteparty dropmine");
				}
			}
		}, 0L, 20L);
	}

	public long rankSell(Player p, boolean silent) {
		Map<Material, Long> sellable = sellable(p);
		Map<Material, Integer> sold = new HashMap<Material, Integer>();
		long moneyIncrement = 0;
		for (int n = 0; n <= 35; n++) {
			ItemStack i = p.getInventory().getItem(n);
			try {
				if (sellable.containsKey(i.getType())) {
					moneyIncrement += (i.getAmount() * sellable.get(i.getType()));
					if (sold.containsKey(i.getType())) {
						sold.put(i.getType(), sold.get(i.getType()) + i.getAmount());
					} else {
						sold.put(i.getType(), i.getAmount());
					}
					p.getInventory().setItem(n, null);
				}
			} catch (NullPointerException ex) {
			}
		}
		List<String> items = new ArrayList<String>();
		for (Material m : sold.keySet()) {
			items.add(sold.get(m) + " " + m.toString().toLowerCase());
		}
		if (items.size() == 0) {
			items.add("\u00a7cnothing");
		}
		int boost = getBoost(p.getName());
		moneyIncrement = moneyIncrement * boost;
		econ.depositPlayer(p, moneyIncrement);
		if (!silent) {
			p.sendMessage("\u00a7aYou earned \u00a7c$" + Rankup.numformat(moneyIncrement) + " \u00a7afrom selling \u00a7b" + String.join(", ", items) + "\u00a7a. (Boost x" + boost + ")");
		}
		p.updateInventory();
		return moneyIncrement;
	}

	public static int occupiedSlots(Player p) {
		int r = 0;
		for (int s = 0; s <= 35; s++) {
			if (p.getInventory().getItem(s) instanceof ItemStack) {
				r++;
			}
		}
		return r;
	}

	public Map<Material, Long> sellable(Player p) {
		Map<Material, Long> s = new HashMap<Material, Long>();
		if (isParty) {
			s.put(Material.RED_SANDSTONE, 1500 + Rankup.getNextRankPrice(PermissionsEx.getUser(p).getGroupsNames()) / 1000000);
		}
		if (p.hasPermission("prisonplus.rank.murderer")) {
			// Murderer mine
			s.put(Material.BRICK, 3000L);
			s.put(Material.SPONGE, 4000L);
		}
		if (p.hasPermission("prisonplus.rank.gangster")) {
			// Gangster mine
			s.put(Material.SANDSTONE, 5000L);
		}
		if (p.hasPermission("prisonplus.rank.wanted")) {
			// Wanted mine
			s.put(Material.PRISMARINE, 6000L);
			s.put(Material.PRISMARINE_CRYSTALS, 1000L);
			s.put(Material.PRISMARINE_SHARD, 2000L);
			s.put(Material.SEA_LANTERN, 10000L);
		}
		s.put(Material.SKULL_ITEM, 1000L);
		s.put(Material.LEATHER, 20L);
		s.put(Material.RAW_BEEF, 5L);
		s.put(Material.COOKED_BEEF, 10L);
		s.put(Material.PORK, 5L);
		s.put(Material.GRILLED_PORK, 10L);
		s.put(Material.MUTTON, 5L);
		s.put(Material.COOKED_MUTTON, 10L);
		s.put(Material.WOOL, 20L);
		s.put(Material.LOG, 1L);
		s.put(Material.LOG_2, 1L);
		// A Mine
		s.put(Material.COBBLESTONE, 1L);
		s.put(Material.MOSSY_COBBLESTONE, 2L);
		if (!p.hasPermission("prisonplus.rank.b")) {
			return s;
		}
		// B Mine
		s.put(Material.STONE, 4L);
		if (!p.hasPermission("prisonplus.rank.c")) {
			return s;
		}
		// C to E Mine
		s.put(Material.COAL_ORE, 2L);
		s.put(Material.COAL, 5L);
		s.put(Material.COAL_BLOCK, 45L);
		if (!p.hasPermission("prisonplus.rank.f")) {
			return s;
		}
		// F to H Mine
		s.put(Material.IRON_ORE, 10L);
		s.put(Material.IRON_INGOT, 12L);
		s.put(Material.IRON_BLOCK, 108L);
		if (!p.hasPermission("prisonplus.rank.i")) {
			return s;
		}
		// I to K Mine
		s.put(Material.GOLD_ORE, 20L);
		s.put(Material.GOLD_INGOT, 24L);
		s.put(Material.GOLD_BLOCK, 216L);
		if (!p.hasPermission("prisonplus.rank.l")) {
			return s;
		}
		// L to N Mine
		s.put(Material.LAPIS_ORE, 90L);
		s.put(Material.INK_SACK, 15L);
		s.put(Material.LAPIS_BLOCK, 135L);
		if (!p.hasPermission("prisonplus.rank.o")) {
			return s;
		}
		// O to P Mine
		s.put(Material.REDSTONE_ORE, 120L);
		s.put(Material.GLOWING_REDSTONE_ORE, 120L);
		s.put(Material.REDSTONE, 30L);
		s.put(Material.REDSTONE_BLOCK, 270L);
		if (!p.hasPermission("prisonplus.rank.q")) {
			return s;
		}
		// Q to S Mine
		s.put(Material.NETHERRACK, 50L);
		s.put(Material.NETHER_BRICK_ITEM, 100L);
		s.put(Material.NETHER_BRICK, 400L);
		s.put(Material.QUARTZ_ORE, 240L);
		s.put(Material.QUARTZ, 60L);
		s.put(Material.QUARTZ_BLOCK, 540L);
		if (!p.hasPermission("prisonplus.rank.t")) {
			return s;
		}
		// T Mine
		s.put(Material.ENDER_STONE, 500L);
		s.put(Material.OBSIDIAN, 700L);
		if (!p.hasPermission("prisonplus.rank.u")) {
			return s;
		}
		// U to V Mine
		s.put(Material.EMERALD_ORE, 700L);
		s.put(Material.EMERALD, 500L);
		s.put(Material.EMERALD_BLOCK, 4500L);
		if (!p.hasPermission("prisonplus.rank.w")) {
			return s;
		}
		// W Mine
		s.put(Material.STAINED_CLAY, 800L);
		if (!p.hasPermission("prisonplus.rank.x")) {
			return s;
		}
		// X to Z Mine
		s.put(Material.DIAMOND_ORE, 900L);
		s.put(Material.DIAMOND, 700L);
		s.put(Material.DIAMOND_BLOCK, 6300L);
		s.put(Material.OBSIDIAN, 1600L);
		return s;
	}
}
