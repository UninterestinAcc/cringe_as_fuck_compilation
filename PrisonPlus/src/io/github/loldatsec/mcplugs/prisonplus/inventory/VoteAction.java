package io.github.loldatsec.mcplugs.prisonplus.inventory;

import io.github.loldatsec.mcplugs.prisonplus.text.Chat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VoteAction {

	public static void award(String pn, Shop s) {
		s.incrementVoteParty();
		Player p = null;
		try {
			p = Bukkit.getPlayerExact(pn);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + p.getName() + " 5000");
			p.getInventory().addItem(new ItemStack(Material.NETHER_STAR, 1));
			s.incrementBoost(pn, 1);
			p.updateInventory();
			Chat.bc("§e[§6Vote§e] §2" + p.getName() + " §avoted for RebornCraft and got a extra§e 5000 dollars§a and a sell boost of §ex" + s.getBoost(pn) + "§a! §e/vote§a to vote.");
		} catch (NullPointerException npe) {
		}
	}
}
