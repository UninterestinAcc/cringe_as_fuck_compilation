package org.reborncraft.gtowny;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.data.Town;

public class GTownyVaultInterface {
	private final Economy econ;
	private final Permission perm;

	public GTownyVaultInterface() {
		econ = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		perm = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
	}

	public boolean deduct(Player p, int amt) {
		return getMoney(p) >= amt && econ.withdrawPlayer(p, amt).transactionSuccess();
	}

	@Deprecated
	@SuppressWarnings ("deprecation")
	public boolean deduct(String p, int amt) {
		return getMoney(p) >= amt && econ.withdrawPlayer(p, amt).transactionSuccess();
	}

	@Deprecated
	@SuppressWarnings ("deprecation")
	public double getMoney(String p) {
		return econ.getBalance(p);
	}

	public boolean increase(Player p, int amt) {
		return econ.depositPlayer(p, amt).transactionSuccess();
	}

	@Deprecated
	@SuppressWarnings ("deprecation")
	public boolean increase(String p, int amt) {
		return econ.depositPlayer(p, amt).transactionSuccess();
	}

	public double getMoney(Player p) {
		return econ.getBalance(p);
	}

	public boolean deduct(Town t, int amt) {
		if (getMoney(t) >= amt) {
			t.setMoneyBank(t.getMoneyBank() - amt);
			return true;
		}
		return false;
	}

	public void increase(Town t, int amt) {
		t.setMoneyBank(t.getMoneyBank() + amt);
	}

	public double getMoney(Town t) {
		return t.getMoneyBank();
	}

	public String getPermissionsPrefix(Player p) {
		if (perm.hasGroupSupport()) {
			String pg = perm.getPrimaryGroup(p);
			if (!pg.isEmpty()) {
				return ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "<" + ChatColor.DARK_GREEN + pg + ChatColor.BLUE + "" + ChatColor.BOLD + "> ";
			}
		}
		return "";
	}
}
