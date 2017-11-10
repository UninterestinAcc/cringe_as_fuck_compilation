package io.github.loldatsec.mcplugs.prisonplus.vote;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PerkShop implements CommandExecutor, Listener {

	public static final String invTitle = "\u00a72\u00a7lVote Perks Shop";
	protected List<PItemStack> sold = new ArrayList<PItemStack>();

	public PerkShop() {
		sold.add(new PItemStack(Material.FURNACE, 1, "\u00a7a/autoorganize", new String[] { "\u00a7aprisonplus.autoorganize" }).setWorth(3));
		sold.add(new PItemStack(Material.EMERALD, 1, "\u00a7a/autosell", new String[] { "\u00a7aprisonplus.autosell" }).setWorth(8));
		sold.add(new PItemStack(Material.IRON_INGOT, 1, "\u00a76\u00a7ox\u00a76\u00a7o2 \u00a7asellboost", new String[] { "\u00a7aprisonplus.sellboost.2" }).setWorth(12));
		sold.add(new PItemStack(Material.GOLD_INGOT, 1, "\u00a76\u00a7ox\u00a76\u00a7o3 \u00a7asellboost", new String[] { "\u00a7aprisonplus.sellboost.3" }).setWorth(18));
		sold.add(new PItemStack(Material.DIAMOND, 1, "\u00a76\u00a7ox\u00a76\u00a7o4 \u00a7asellboost", new String[] { "\u00a7aprisonplus.sellboost.4" }).setWorth(24));
		sold.add(new PItemStack(Material.TNT, 1, "\u00a7a/superbreaker | Break 27 blocks at a time!", new String[] { "\u00a7aprisonplus.superbreaker" }).setWorth(64));
	}

	public boolean isShop(InventoryClickEvent e) {
		if (e.getInventory().getTitle().equalsIgnoreCase(invTitle)) {
			e.setCancelled(true);
			ItemStack c = e.getInventory().getItem(e.getSlot());
			if (c != null) {
				if (c.getType() != Material.NETHER_STAR && c.getType() != Material.BARRIER) {
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	public void click(InventoryClickEvent e) {
		if (isShop(e)) {
			Player p = (Player) e.getWhoClicked();
			for (int s = 9; s <= e.getSlot(); s++) {
				PItemStack pis = getByMaterial(e.getInventory().getItem(s).getType());
				String perm = getPermission(pis);
				if (s == e.getSlot()) {
					if (p.hasPermission(perm)) {
						p.sendMessage("\u00a74\u00a7l===================================\n\u00a7cError:\n\u00a74-\n\u00a7cYou already have this perk.\n\u00a74\u00a7l===================================");
						return;
					}
				} else if (!p.hasPermission(perm)) {
					p.sendMessage("\u00a74\u00a7l===================================\n\u00a7cError:\n\u00a74-\n\u00a7cYou need to unlock \u00a7b" + perm + " \u00a7cfirst.\u00a74\u00a7l===================================");
					return;
				}
			}
			PItemStack pis = getByMaterial(e.getInventory().getItem(e.getSlot()).getType());
			String perm = getPermission(pis);
			if (deductStars(p, pis.getWorth())) {
				PermissionsEx.getUser(p).addPermission(perm);
				p.closeInventory();
				p.sendMessage("\u00a7aYou earned the permission \u00a7e" + perm);
			}
		}
	}

	public String getPermission(PItemStack pis) {
		return pis.getItemMeta().getLore().get(0).substring(2);
	}

	public PItemStack getByMaterial(Material mt) {
		for (PItemStack s : sold) {
			if (s.getType() == mt) {
				return s;
			}
		}
		return null;
	}

	public Inventory showShop(Player p) {
		Inventory inv = Bukkit.createInventory(p, 18, invTitle);
		int slot = 0;
		for (PItemStack pis : sold) {
			if (slot >= 9) {
				break;
			}
			inv.setItem(slot, new ItemStack(Material.NETHER_STAR, pis.getWorth()));
			inv.setItem(slot + 9, pis);
			slot++;
		}
		for (int s = 0; s < 18; s++) {
			if (inv.getItem(s) == null) {
				inv.setItem(s, new ItemStack(Material.BARRIER, 0));
			}
		}
		p.openInventory(inv);
		return inv;
	}

	public boolean deductStars(Player p, int starNeeded) {
		List<Integer> starSlots = new ArrayList<Integer>();
		for (int n = 0; n <= 35; n++) {
			try {
				ItemStack i = p.getInventory().getItem(n);
				if (i.getType() == Material.NETHER_STAR) {
					starSlots.add(n);
				}
			} catch (NullPointerException e) {
			}
		}
		final int reqS = starNeeded;
		for (int cs : starSlots) {
			if (starNeeded > 0) {
				ItemStack star = p.getInventory().getItem(cs);
				int removedstar = star.getAmount();
				if (removedstar > starNeeded) {
					removedstar = starNeeded;
				}
				star.setAmount(star.getAmount() - removedstar);
				starNeeded = starNeeded - removedstar;
				if (star.getAmount() <= 0) {
					p.getInventory().setItem(cs, null);
				} else {
					p.getInventory().setItem(cs, star);
				}
			}
		}
		if (starNeeded > 0) {
			int starRetAmt = reqS - starNeeded;
			if (starRetAmt >= 1) {
				p.getInventory().addItem(new ItemStack(Material.NETHER_STAR, starRetAmt));
			}
			p.sendMessage("\u00a74\u00a7l===================================\n\u00a7cYou do not have enough stars!\n\u00a74-\n\u00a7cYou need \u00a7b" + starNeeded + " \u00a7cmore stars.\u00a74\u00a7l===================================");
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (arg0 instanceof Player) {
				showShop((Player) arg0);
		} else {
			arg0.sendMessage("\u00a7cYou aren't a player!");
		}
		return true;
	}
}
