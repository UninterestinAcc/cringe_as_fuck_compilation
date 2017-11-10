package io.github.loldatsec.mcplugs.prisonplus.rankup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Rankup implements CommandExecutor, Listener {

	public static String prefix = "\u00a7e[\u00a76RC\u00a7e] \u00a7a";
	public static final Map<String, Long> price = price();
	public static final String invTitle = "\u00a76\u00a7lPrison Rankup";
	public static final List<String> ranks = Arrays.asList(new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "FreeMan", "PrestigeA", "PrestigeB", "PrestigeC", "PrestigeD", "PrestigeE", "PrestigeF", "PrestigeG", "PrestigeH", "PrestigeI", "PrestigeJ", "PrestigeK", "PrestigeL", "PrestigeM", "PrestigeN", "PrestigeO", "PrestigeP", "PrestigeQ", "PrestigeR", "PrestigeS", "PrestigeT", "PrestigeU", "PrestigeV", "PrestigeW", "PrestigeX", "PrestigeY", "PrestigeZ", "Baron" });
	public Economy econ = null;
	public PermissionManager pm = null;

	public Rankup() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		econ = rsp.getProvider();
		pm = PermissionsEx.getPermissionManager();
	}

	@EventHandler
	public void click(InventoryClickEvent e) {
		if (e.getInventory().getTitle().equalsIgnoreCase(Rankup.invTitle)) {
			e.setCancelled(true);
			int sl = 0;
			for (String rank : ranks) {
				if (rank == getNextRank(pm.getUser((Player) e.getWhoClicked()).getGroupsNames())) {
					if (sl == e.getSlot()) {
						Bukkit.dispatchCommand(e.getWhoClicked(), "rankup");
						e.getWhoClicked().closeInventory();
					}
				}
				sl++;
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You ain't no player.");
		} else if (cmd.getName().equalsIgnoreCase("rankup")) {
			PermissionUser user = PermissionsEx.getUser(sender.getName());
			String[] pg = user.getGroupsNames();
			String next = getNextRank(pg);
			if (next == null) {
				sender.sendMessage("\u00a7cYou rank is not part of a rankup system");
			} else {
				promote(sender.getName(), getPrice(next), next);
			}
		} else {
			Inventory inv = Bukkit.createInventory((InventoryHolder) sender, 54, invTitle);
			int sl = 0;
			boolean achieved = true;
			boolean nr = false;
			double bal = econ.getBalance((Player) sender);
			for (String rank : ranks) {
				boolean cr = rank == getCurrentRank(pm.getUser(sender.getName()).getGroupsNames());
				if (cr) {
					achieved = false;
				}
				short color = 4;
				if (achieved) {
					color = 0;
				} else {
					if (cr) {
						color = 7;
					} else if (rank.equalsIgnoreCase("FreeMan")) {
						color = 1;
					} else if (rank.startsWith("Prestige")) {
						color = 9;
					} else if (rank.equalsIgnoreCase("Baron")) {
						color = 3;
					}
				}
				ItemStack r = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
				ItemMeta rm = r.getItemMeta();
				rm.setDisplayName(ChatColor.translateAlternateColorCodes('&', pm.getGroup(rank).getPrefix()));
				String prog = "\u00a76Progress: \u00a78[";
				long rankprice = getPrice(rank);
				double progress = bal / rankprice;
				if (progress < 1 && nr) {
					int pg = (int) (progress * 18);
					prog += "\u00a72\u00a7l";
					for (int c = 0; c < 18; c++) {
						if (c == pg) {
							prog += "\u00a77\u00a7l";
						}
						prog += "»";
					}
					prog += "\u00a78]";
				} else if (nr) {
					prog += "\u00a72\u00a7l»»»»»»»»»»»»»»»»»»\u00a78]";
				} else if (achieved || cr) {
					prog += "\u00a72\u00a7l»»»»»»»»»»»»»»»»»»\u00a78]";
				} else {
					prog += "\u00a77\u00a7l»»»»»»»»»»»»»»»»»»\u00a78]";
				}
				rm.setLore(Arrays.asList(new String[] { "\u00a7aCost:", "\u00a7c$" + numformat(rankprice), prog }));
				r.setItemMeta(rm);
				inv.setItem(sl++, r);
				if (nr) {
					nr = false;
				}
				if (cr) {
					nr = true;
				}
			}
			((Player) sender).openInventory(inv);
		}
		return true;
	}

	public static String numformat(double number) {
		String suffix = "";
		double bal = number;
		if (number >= 1000000000000000000L) {
			bal = ((double) (number / 1000000000000000000L));
			suffix = "p";
		} else if (number >= 1000000000000000L) {
			bal = ((double) (number / 1000000000000000L));
			suffix = "q";
		} else if (number >= 1000000000000L) {
			bal = ((double) (number / 1000000000000L));
			suffix = "t";
		} else if (number >= 1000000000L) {
			bal = ((double) (number / 1000000000L));
			suffix = "b";
		} else if (number >= 1000000) {
			bal = ((double) (number / 1000000L));
			suffix = "m";
		} else if (number >= 1000) {
			bal = ((double) (number / 1000L));
			suffix = "k";
		}
		bal = ((double)((long) (bal * 100)) / 100);
		return bal + suffix;
	}

	@SuppressWarnings("deprecation")
	private void promote(String name, long amount, String nextrank) {
		EconomyResponse r = econ.withdrawPlayer(name, amount);
		if (r.transactionSuccess()) {
			PermissionUser pu = PermissionsEx.getUser(name);
			String uprefix = "";
			for (String pg : pu.getGroupsNames()) {
				if (pg.matches("^(([a-zA-Z0-9]+||)[A-Z])$") || pg.equalsIgnoreCase("FreeMan")) {
					pu.removeGroup(pg);
				} else {
					uprefix += pm.getGroup(pg).getPrefix();
				}
			}
			pu.addGroup(nextrank);
			uprefix += pm.getGroup(nextrank).getPrefix();
			if (!uprefix.equalsIgnoreCase(pm.getGroup(nextrank).getPrefix())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + name + " prefix \"" + uprefix + "\"");
			}
			Bukkit.broadcastMessage(prefix + "\u00a76Congradulations! " + name + " \u00a7ahas ranked up to \u00a7b" + nextrank);
			Bukkit.getPlayerExact(name).sendMessage(prefix + "\u00a7eYou ranked up to \u00a7b" + nextrank);
			if (nextrank.equalsIgnoreCase("PrestigeA")) {
				econ.withdrawPlayer(name, econ.getBalance(name));
			}
		} else {
			Bukkit.getPlayerExact(name).sendMessage("\u00a7aYou don't have enough money to rankup right now! (Need \u00a7c$" + (amount - econ.getBalance(name)) + "\u00a7a more.)");
		}
	}

	public static void sortPrefixes() {
		PermissionManager pm = PermissionsEx.getPermissionManager();
		for (PermissionUser pu : pm.getUsers()) {
			String uprefix = "";
			String usuffix = "";
			for (String pg : pu.getGroupsNames()) {
				uprefix = pm.getGroup(pg).getPrefix() + uprefix;
				usuffix = pm.getGroup(pg).getSuffix();
			}
			if (!uprefix.equalsIgnoreCase(pu.getPrefix())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pu.getName() + " prefix \"" + uprefix + "\"");
			}
			if (!usuffix.equalsIgnoreCase(pu.getSuffix())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pu.getName() + " suffix \"" + usuffix + "\"");
			}
		}
	}

	public static String getNextRank(String[] currentranks) {
		List<String> groupList = Arrays.asList(currentranks);
		String next = "";
		if (groupList.contains("PrestigeZ")) {
			next = "Baron";
		} else if (groupList.contains("PrestigeY")) {
			next = "PrestigeZ";
		} else if (groupList.contains("PrestigeX")) {
			next = "PrestigeY";
		} else if (groupList.contains("PrestigeW")) {
			next = "PrestigeX";
		} else if (groupList.contains("PrestigeV")) {
			next = "PrestigeW";
		} else if (groupList.contains("PrestigeU")) {
			next = "PrestigeV";
		} else if (groupList.contains("PrestigeT")) {
			next = "PrestigeU";
		} else if (groupList.contains("PrestigeS")) {
			next = "PrestigeT";
		} else if (groupList.contains("PrestigeR")) {
			next = "PrestigeS";
		} else if (groupList.contains("PrestigeQ")) {
			next = "PrestigeR";
		} else if (groupList.contains("PrestigeP")) {
			next = "PrestigeQ";
		} else if (groupList.contains("PrestigeO")) {
			next = "PrestigeP";
		} else if (groupList.contains("PrestigeN")) {
			next = "PrestigeO";
		} else if (groupList.contains("PrestigeM")) {
			next = "PrestigeN";
		} else if (groupList.contains("PrestigeL")) {
			next = "PrestigeM";
		} else if (groupList.contains("PrestigeK")) {
			next = "PrestigeL";
		} else if (groupList.contains("PrestigeJ")) {
			next = "PrestigeK";
		} else if (groupList.contains("PrestigeI")) {
			next = "PrestigeJ";
		} else if (groupList.contains("PrestigeH")) {
			next = "PrestigeI";
		} else if (groupList.contains("PrestigeG")) {
			next = "PrestigeH";
		} else if (groupList.contains("PrestigeF")) {
			next = "PrestigeG";
		} else if (groupList.contains("PrestigeE")) {
			next = "PrestigeF";
		} else if (groupList.contains("PrestigeD")) {
			next = "PrestigeE";
		} else if (groupList.contains("PrestigeC")) {
			next = "PrestigeD";
		} else if (groupList.contains("PrestigeB")) {
			next = "PrestigeC";
		} else if (groupList.contains("PrestigeA")) {
			next = "PrestigeB";
		} else if (groupList.contains("FreeMan")) {
			next = "PrestigeA";
		} else if (groupList.contains("Z")) {
			next = "FreeMan";
		} else if (groupList.contains("Y")) {
			next = "Z";
		} else if (groupList.contains("X")) {
			next = "Y";
		} else if (groupList.contains("W")) {
			next = "X";
		} else if (groupList.contains("V")) {
			next = "W";
		} else if (groupList.contains("U")) {
			next = "V";
		} else if (groupList.contains("T")) {
			next = "U";
		} else if (groupList.contains("S")) {
			next = "T";
		} else if (groupList.contains("R")) {
			next = "S";
		} else if (groupList.contains("Q")) {
			next = "R";
		} else if (groupList.contains("P")) {
			next = "Q";
		} else if (groupList.contains("O")) {
			next = "P";
		} else if (groupList.contains("N")) {
			next = "O";
		} else if (groupList.contains("M")) {
			next = "N";
		} else if (groupList.contains("L")) {
			next = "M";
		} else if (groupList.contains("K")) {
			next = "L";
		} else if (groupList.contains("J")) {
			next = "K";
		} else if (groupList.contains("I")) {
			next = "J";
		} else if (groupList.contains("H")) {
			next = "I";
		} else if (groupList.contains("G")) {
			next = "H";
		} else if (groupList.contains("F")) {
			next = "G";
		} else if (groupList.contains("E")) {
			next = "F";
		} else if (groupList.contains("D")) {
			next = "E";
		} else if (groupList.contains("C")) {
			next = "D";
		} else if (groupList.contains("B")) {
			next = "C";
		} else if (groupList.contains("A")) {
			next = "B";
		}
		return next;
	}

	public static String getDisplayPrefix(Player p) {
		PermissionManager pm = PermissionsEx.getPermissionManager();
		String uprefix = "";
		for (String pg : PermissionsEx.getUser(p).getGroupsNames()) {
			uprefix = pm.getGroup(pg).getPrefix();
		}
		if (uprefix == "") {
			uprefix = pm.getGroup(getCurrentRank(PermissionsEx.getUser(p).getGroupsNames())).getPrefix();
		}
		return uprefix;
	}

	public static String getCurrentRank(String[] currentranks) {
		List<String> groupList = Arrays.asList(currentranks);
		String next = "";
		if (groupList.contains("Baron")) {
			next = "Baron";
		} else if (groupList.contains("PrestigeZ")) {
			next = "PrestigeZ";
		} else if (groupList.contains("PrestigeY")) {
			next = "PrestigeY";
		} else if (groupList.contains("PrestigeX")) {
			next = "PrestigeX";
		} else if (groupList.contains("PrestigeW")) {
			next = "PrestigeW";
		} else if (groupList.contains("PrestigeV")) {
			next = "PrestigeV";
		} else if (groupList.contains("PrestigeU")) {
			next = "PrestigeU";
		} else if (groupList.contains("PrestigeT")) {
			next = "PrestigeT";
		} else if (groupList.contains("PrestigeS")) {
			next = "PrestigeS";
		} else if (groupList.contains("PrestigeR")) {
			next = "PrestigeR";
		} else if (groupList.contains("PrestigeQ")) {
			next = "PrestigeQ";
		} else if (groupList.contains("PrestigeP")) {
			next = "PrestigeP";
		} else if (groupList.contains("PrestigeO")) {
			next = "PrestigeO";
		} else if (groupList.contains("PrestigeN")) {
			next = "PrestigeN";
		} else if (groupList.contains("PrestigeM")) {
			next = "PrestigeM";
		} else if (groupList.contains("PrestigeL")) {
			next = "PrestigeL";
		} else if (groupList.contains("PrestigeK")) {
			next = "PrestigeK";
		} else if (groupList.contains("PrestigeJ")) {
			next = "PrestigeJ";
		} else if (groupList.contains("PrestigeI")) {
			next = "PrestigeI";
		} else if (groupList.contains("PrestigeH")) {
			next = "PrestigeH";
		} else if (groupList.contains("PrestigeG")) {
			next = "PrestigeG";
		} else if (groupList.contains("PrestigeF")) {
			next = "PrestigeF";
		} else if (groupList.contains("PrestigeE")) {
			next = "PrestigeE";
		} else if (groupList.contains("PrestigeD")) {
			next = "PrestigeD";
		} else if (groupList.contains("PrestigeC")) {
			next = "PrestigeC";
		} else if (groupList.contains("PrestigeB")) {
			next = "PrestigeB";
		} else if (groupList.contains("PrestigeA")) {
			next = "PrestigeA";
		} else if (groupList.contains("FreeMan")) {
			next = "FreeMan";
		} else if (groupList.contains("Z")) {
			next = "Z";
		} else if (groupList.contains("Y")) {
			next = "Y";
		} else if (groupList.contains("X")) {
			next = "X";
		} else if (groupList.contains("W")) {
			next = "W";
		} else if (groupList.contains("V")) {
			next = "V";
		} else if (groupList.contains("U")) {
			next = "U";
		} else if (groupList.contains("T")) {
			next = "T";
		} else if (groupList.contains("S")) {
			next = "S";
		} else if (groupList.contains("R")) {
			next = "R";
		} else if (groupList.contains("Q")) {
			next = "Q";
		} else if (groupList.contains("P")) {
			next = "P";
		} else if (groupList.contains("O")) {
			next = "O";
		} else if (groupList.contains("N")) {
			next = "N";
		} else if (groupList.contains("M")) {
			next = "M";
		} else if (groupList.contains("L")) {
			next = "L";
		} else if (groupList.contains("K")) {
			next = "K";
		} else if (groupList.contains("J")) {
			next = "J";
		} else if (groupList.contains("I")) {
			next = "I";
		} else if (groupList.contains("H")) {
			next = "H";
		} else if (groupList.contains("G")) {
			next = "G";
		} else if (groupList.contains("F")) {
			next = "F";
		} else if (groupList.contains("E")) {
			next = "E";
		} else if (groupList.contains("D")) {
			next = "D";
		} else if (groupList.contains("C")) {
			next = "C";
		} else if (groupList.contains("B")) {
			next = "B";
		} else if (groupList.contains("A")) {
			next = "A";
		} else {
			next = groupList.get(groupList.size() - 1);
		}
		return next;
	}

	public static long getNextRankPrice(String[] currentranks) {
		return getPrice(getNextRank(currentranks));
	}

	public static long getPrice(String rank) {
		if (rank == null) {
			return 1;
		} else if (rank == "") {
			return 1;
		} else if (price.containsKey(rank)) {
			return price.get(rank);
		} else {
			return 1;
		}
	}

	public static final Map<String, Long> price() {
		Map<String, Long> m = new HashMap<String, Long>();
		m.put("A", 0L);
		m.put("B", 8000L);
		m.put("C", 16000L);
		m.put("D", 24000L);
		m.put("E", 48000L);
		m.put("F", 72000L);
		m.put("G", 120000L);
		m.put("H", 192000L);
		m.put("I", 312000L);
		m.put("J", 504000L);
		m.put("K", 816000L);
		m.put("L", 1320000L);
		m.put("M", 3136000L);
		m.put("N", 4456000L);
		m.put("O", 7592000L);
		m.put("P", 12048000L);
		m.put("Q", 19640000L);
		m.put("R", 31688000L);
		m.put("S", 51328000L);
		m.put("T", 83016000L);
		m.put("U", 134344000L);
		m.put("V", 214360000L);
		m.put("W", 348704000L);
		m.put("X", 563064000L);
		m.put("Y", 911768000L);
		m.put("Z", 1474832000L);
		m.put("FreeMan", 2386600000L);
		m.put("PrestigeA", 8000000L);
		m.put("PrestigeB", 8000000L);
		m.put("PrestigeC", 16000000L);
		m.put("PrestigeD", 24000000L);
		m.put("PrestigeE", 48000000L);
		m.put("PrestigeF", 72000000L);
		m.put("PrestigeG", 120000000L);
		m.put("PrestigeH", 192000000L);
		m.put("PrestigeI", 312000000L);
		m.put("PrestigeJ", 504000000L);
		m.put("PrestigeK", 816000000L);
		m.put("PrestigeL", 1320000000L);
		m.put("PrestigeM", 3136000000L);
		m.put("PrestigeN", 4456000000L);
		m.put("PrestigeO", 7592000000L);
		m.put("PrestigeP", 12048000000L);
		m.put("PrestigeQ", 19640000000L);
		m.put("PrestigeR", 31688000000L);
		m.put("PrestigeS", 51328000000L);
		m.put("PrestigeT", 83016000000L);
		m.put("PrestigeU", 134344000000L);
		m.put("PrestigeV", 214360000000L);
		m.put("PrestigeW", 348704000000L);
		m.put("PrestigeX", 563064000000L);
		m.put("PrestigeY", 911768000000L);
		m.put("PrestigeZ", 1474832000000L);
		m.put("Baron", 2386600000000L);
		return m;
	}
}
