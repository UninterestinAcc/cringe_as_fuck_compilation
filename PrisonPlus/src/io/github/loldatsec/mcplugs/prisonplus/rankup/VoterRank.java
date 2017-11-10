package io.github.loldatsec.mcplugs.prisonplus.rankup;

import io.github.loldatsec.mcplugs.prisonplus.text.Chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class VoterRank implements CommandExecutor {

	public static String prefix = "\u00a7e[\u00a76RC\u00a7e] \u00a7a";

	private void grantVoter(String pname) {
		Player p = Bukkit.getPlayerExact(pname);
		PermissionUser pu = PermissionsEx.getUser(p);
		if (Arrays.asList(pu.getGroupsNames()).contains("Voter")) {
			p.sendMessage("\u00a7cYou already have the voter rank.");
			return;
		}
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
		int starNeeded = 192;
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
			p.sendMessage("\u00a7cYou need \u00a7b" + starNeeded + " \u00a7cmore star to rankup.");
			int starRetAmt = 192 - starNeeded;
			if (starRetAmt >= 1) {
				p.getInventory().addItem(new ItemStack(Material.NETHER_STAR, starRetAmt));
			}
		} else {
			pu.addGroup("Voter");
			Chat.bc(prefix + "\u00a7b" + pname + " \u00a7ahas ranked up to \u00a72Voter\u00a7a!");
			String uprefix = "";
			for (String pg : pu.getGroupsNames()) {
				uprefix += PermissionsEx.getPermissionManager().getGroup(pg).getPrefix();
			}
			if (!uprefix.equalsIgnoreCase(pu.getPrefix())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pname + " prefix \"" + uprefix + "\"");
			}
			Bukkit.getPlayerExact(pname).sendMessage(prefix + "\u00a7eYou ranked up to \u00a72Voter");
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage("You ain't no player.");
			return true;
		}
		grantVoter(arg0.getName());
		return true;
	}
}
