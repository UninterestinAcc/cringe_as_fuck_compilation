package io.github.loldatsec.mcplugs.prisonplus.vote;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VoteShop implements Listener, CommandExecutor {

	public static final String invTitle = "\u00a72\u00a7lVote Shop";
	protected List<PItemStack> sold = new ArrayList<PItemStack>();

	public VoteShop() {
		sold.add(new PItemStack(Material.GOLDEN_APPLE, 2, 1, "\u00a7a[\u00a72Vote Gapple\u00a7a]"));
		sold.add(new PItemStack(Material.REDSTONE_TORCH_ON, 4, 0, "\u00a7aMining Explosive", new String[] { "\u00a7aPower: \u00a7c4" }));
		sold.add(new PItemStack(Material.STEP, 4, 4, "\u00a7aMining Explosive", new String[] { "\u00a7aPower: \u00a7c12" }));
		sold.add(new PItemStack(Material.STEP, 2, 6, "\u00a7aMining Explosive", new String[] { "\u00a7aPower: \u00a7c17" }));
		sold.add(new PItemStack(Material.DIAMOND_HELMET, 1, "\u00a7a[\u00a72Vote Armor\u00a7a]", Enchantment.PROTECTION_ENVIRONMENTAL, 5));
		sold.add(new PItemStack(Material.DIAMOND_CHESTPLATE, 1, "\u00a7a[\u00a72Vote Armor\u00a7a]", Enchantment.PROTECTION_ENVIRONMENTAL, 5));
		sold.add(new PItemStack(Material.DIAMOND_LEGGINGS, 1, "\u00a7a[\u00a72Vote Armor\u00a7a]", Enchantment.PROTECTION_ENVIRONMENTAL, 5));
		sold.add(new PItemStack(Material.DIAMOND_BOOTS, 1, "\u00a7a[\u00a72Vote Armor\u00a7a]", Enchantment.PROTECTION_ENVIRONMENTAL, 5));
		sold.add(new PItemStack(Material.BOOK, 64));
	}

	@EventHandler
	public void click(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (isShop(e) && deductHandler(e)) {
			ItemStack c = e.getInventory().getItem(e.getSlot());
			p.getInventory().addItem(c);
			p.updateInventory();
		}
	}

	public boolean isShop(InventoryClickEvent e) {
		if (e.getInventory().getTitle() == null) {
			return false;
		} else if (e.getInventory().getTitle().equalsIgnoreCase(invTitle)) {
			e.setCancelled(true);
			try {
				ItemStack c = e.getInventory().getItem(e.getSlot());
				if (c != null) {
					if (c.getType() != Material.NETHER_STAR && c.getType() != Material.BARRIER) {
						return true;
					}
				}
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public boolean deductHandler(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		int reqStars = e.getInventory().getItem(e.getSlot() - 9).getAmount();
		if (deductStars(p, reqStars)) {
			return true;
		}
		return false;
	}

	public Inventory showShop(Player p) {
		Inventory inv = Bukkit.createInventory(p, 18, invTitle);
		int slot = 0;
		for (PItemStack pis : sold) {
			if (slot >= 9) {
				break;
			}
			inv.setItem(slot, new ItemStack(Material.NETHER_STAR, pis.getReqStars()));
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
