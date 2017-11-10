package me.stefan15ist.PrisonRankup;

import io.github.loldatsec.mcplugs.prisonrankup.PrisonRankup.Rankup;

import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		Economy econ = rsp.getProvider();
		return econ != null;
	}

	public void onEnable() {
		Rankup r = new Rankup();
		getCommand("rankup").setExecutor(r);
		getConfig().options().copyDefaults();
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		econ = rsp.getProvider();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				Rankup.sortPrefixes();
			}
		}, 0L, 1200L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				try {
					for (Player p : Bukkit.getOnlinePlayers()) {
						Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
						Objective o = s.registerNewObjective(p.getName(), "dummy");
						o.setDisplaySlot(DisplaySlot.SIDEBAR);
						o.setDisplayName(p.getDisplayName());
						double bal = econ.getBalance(p.getName());
						int r = 99;
						o.getScore("\u00a7a\u00a7lBalance:").setScore(r--);
						o.getScore("\u00a7c$" + numformat(bal)).setScore(r--);
						o.getScore("\u00a7a\u00a7lRanks: ").setScore(r--);
						for (PermissionGroup rank : PermissionsEx.getUser(p).getGroups()) {
							o.getScore(ChatColor.translateAlternateColorCodes('&', rank.getPrefix())).setScore(r--);
						}
						String nextrank = Rankup.getNextRank(PermissionsEx.getUser(p).getGroupsNames());
						if (!(nextrank.equalsIgnoreCase("Voter") || nextrank == "")) {
							o.getScore("\u00a7a\u00a7lNext Rank: ").setScore(r--);
							o.getScore(ChatColor.translateAlternateColorCodes('&', PermissionsEx.getPermissionManager().getGroup(nextrank).getPrefix())).setScore(r--);
							o.getScore("\u00a7a\u00a7lCost: ").setScore(r--);
							o.getScore("\u00a7c$" + numformat(Rankup.getNextRankPrice(PermissionsEx.getUser(p).getGroupsNames()))).setScore(r--);
							o.getScore("\u00a7a\u00a7lProgress: ").setScore(r--);
							double progress = ((float) (bal / Rankup.getNextRankPrice(PermissionsEx.getUser(p).getGroupsNames())));
							o.getScore("\u00a7b" + ((int) (progress * 100)) + "%").setScore(r--);
							if (progress >= 1) {
								p.setExp(1);
								o.getScore("\u00a7e[\u00a74\u00a7l»»»»»»»»»»»»»»»»»»\u00a7e]").setScore(r--);
								if (!nextrank.equalsIgnoreCase("PrestigeA")) {
									Bukkit.dispatchCommand(p, "rankup next");
								}
							} else {
								p.setExp((float) progress);
								int pg = (int) (progress * 18);
								String pgb = "\u00a7e[\u00a7a\u00a7l";
								for (int c = 0; c < 18; c++) {
									if (c == pg) {
										pgb += "\u00a78\u00a7l";
									}
									pgb += "»";
								}
								pgb += "\u00a7e]";
								o.getScore(pgb).setScore(r--);
							}
						}
						p.setScoreboard(s);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0L, 20L);
	}

	private String numformat(double number) {
		String baldisp = "";
		if (number >= 1000000000000000000L) {
			baldisp = ((float) (number / 1000000000000000000L)) + "P";
		} else if (number >= 1000000000000000L) {
			baldisp = ((float) (number / 1000000000000000L)) + "Q";
		} else if (number >= 1000000000000L) {
			baldisp = ((float) (number / 1000000000000L)) + "T";
		} else if (number >= 1000000000L) {
			baldisp = ((float) (number / 1000000000L)) + "B";
		} else if (number >= 1000000) {
			baldisp = ((float) (number / 1000000L)) + "M";
		} else if (number >= 1000) {
			baldisp = ((float) (number / 1000L)) + "k";
		} else {
			baldisp = "" + number;
		}
		return baldisp;
	}
}
