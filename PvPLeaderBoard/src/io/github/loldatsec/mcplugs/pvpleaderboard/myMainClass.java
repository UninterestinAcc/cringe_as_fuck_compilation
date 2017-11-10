package io.github.loldatsec.mcplugs.pvpleaderboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class myMainClass extends JavaPlugin implements Listener {
	public int count = 0;
	private DecimalFormat df = new DecimalFormat("#.##");;

	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		if (!getConfig().contains("locations")) {
			getConfig().set("locations", new ArrayList<String>());
			saveConfig();
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
			public void run() {
				Map<String, Integer> topPlayers = new HashMap<String, Integer>();
				for (Player plist : Bukkit.getOnlinePlayers()) {
					topPlayers.put(plist.getName(),
							(int) plist.getStatistic(Statistic.PLAYER_KILLS)
									- plist.getStatistic(Statistic.DEATHS) / 10);
				}
				Object[] a = topPlayers.entrySet().toArray();
				Arrays.sort(a, new Comparator() {
					public int compare(Object o1, Object o2) {
						return ((Map.Entry<String, Integer>) o2).getValue()
								.compareTo(
										((Map.Entry<String, Integer>) o1)
												.getValue());
					}
				});
				String strPlayerList = ",";
				for (Object e : a) {
					strPlayerList += ((Map.Entry<String, Integer>) e).getKey()
							+ ",";
				}
				for (int i = 0; i < getConfig().getStringList("locations")
						.size(); i++) {
					String location = getConfig().getStringList("locations")
							.get(i);
					String[] key = location.split(",");
					int x = Integer.valueOf(key[0]);
					int y = Integer.valueOf(key[1]);
					int z = Integer.valueOf(key[2]);
					String world = key[3];
					int c = Integer.valueOf(key[4]);
					String[] topPlayerList = strPlayerList.split(",");
					if (Bukkit.getWorld(world) != null) {
						Block block = Bukkit.getWorld(world)
								.getBlockAt(x, y, z);
						if (block.getType() == Material.WALL_SIGN
								|| block.getType() == Material.SIGN_POST) {
							Sign sign = (Sign) block.getState();
							sign.setLine(0, ChatColor.BOLD + "#" + (c)
									+ " Online");
							if (c < topPlayerList.length) {
								sign.setLine(1, ChatColor.DARK_RED + ""
										+ ChatColor.BOLD + topPlayerList[c]);
								if (Bukkit.getPlayer(topPlayerList[c]) != null) {
									sign.setLine(
											2,
											ChatColor.DARK_BLUE
													+ ""
													+ ChatColor.BOLD
													+ Bukkit.getPlayer(
															topPlayerList[c])
															.getStatistic(
																	Statistic.PLAYER_KILLS)
													+ "Kills");
									if (Bukkit.getPlayer(topPlayerList[c])
											.getStatistic(Statistic.DEATHS) > 0) {
										sign.setLine(
												3,
												ChatColor.GOLD
														+ ""
														+ ChatColor.BOLD
														+ df.format((float) Bukkit
																.getPlayer(
																		topPlayerList[c])
																.getStatistic(
																		Statistic.PLAYER_KILLS)
																/ Bukkit.getPlayer(
																		topPlayerList[c])
																		.getStatistic(
																				Statistic.DEATHS))
														+ "KD");
									} else {
										sign.setLine(3, ChatColor.GOLD + ""
												+ ChatColor.BOLD + "Undefeated");
									}
								} else {
									sign.setLine(2, "-");
									sign.setLine(3, "-");
								}
							} else {
								sign.setLine(1, ChatColor.DARK_GREEN + ""
										+ ChatColor.BOLD + "Offline");
								sign.setLine(2, ChatColor.DARK_BLUE + ""
										+ ChatColor.BOLD + "N/A");
								sign.setLine(3, ChatColor.GOLD + ""
										+ ChatColor.BOLD + "N/A");
							}
							sign.update();

						}
					}
				}
			}

		}, 0, 200);
		System.out.println("Plugin Sucessfully Enabled!");
	}

	public void onDisable() {
		System.out.println("Plugin Sucessfully Disabled!");
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("stat")
				|| cmd.getName().equalsIgnoreCase("stats")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} else {
				if (args.length >= 1) {

					if (Bukkit.getPlayer(args[0]) != null) {
						Player target = Bukkit.getPlayer(args[0]);
						sender.sendMessage(ChatColor.YELLOW + target.getName()
								+ ChatColor.GOLD + " have " + ChatColor.YELLOW
								+ target.getStatistic(Statistic.PLAYER_KILLS)
								+ ChatColor.GOLD + " kills, "
								+ ChatColor.YELLOW
								+ target.getStatistic(Statistic.DEATHS)
								+ ChatColor.GOLD + " deaths, and "
								+ ChatColor.YELLOW
								+ target.getStatistic(Statistic.LEAVE_GAME)
								+ ChatColor.GOLD + " playthroughs.");
					} else {
						sender.sendMessage(ChatColor.RED + "Error: "
								+ ChatColor.DARK_RED + "Player not found.");
					}
				} else {
					Player player = (Player) sender;
					sender.sendMessage(ChatColor.GOLD + "You have "
							+ ChatColor.YELLOW
							+ player.getStatistic(Statistic.PLAYER_KILLS)
							+ ChatColor.GOLD + " kills, " + ChatColor.YELLOW
							+ player.getStatistic(Statistic.DEATHS)
							+ ChatColor.GOLD + " deaths, and "
							+ ChatColor.YELLOW
							+ player.getStatistic(Statistic.LEAVE_GAME)
							+ ChatColor.GOLD + " playthroughs.");

				}
				return true;
			}

		}
		if (cmd.getName().equalsIgnoreCase("setlbcounter")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.getPlayer().hasPermission(
						"pvpleaderboard.changecounter")
						|| !player.getPlayer().isOp()) {
					return false;
				}
			}
			if (args.length == 1) {
				this.count = Integer.valueOf(args[0]);
				sender.sendMessage(ChatColor.GREEN + "The count is now "
						+ args[0] + ". This will get incremented.");
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("clearsigns")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.getPlayer().hasPermission(
						"pvpleaderboard.clearsigns")
						|| !player.getPlayer().isOp()) {
					return false;
				}
			}
			getConfig().set("locations", new ArrayList<String>());
			sender.sendMessage(ChatColor.GREEN + "All bound signs removed!");
			return true;
		}

		return false;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("pvpleaderboard.ptsetsign")
				|| e.getPlayer().isOp()) {
			if (e.getLine(0).equalsIgnoreCase("[PVPLB]")) {
				List<String> locations = getConfig().getStringList("locations");
				locations.add(e.getBlock().getX() + "," + e.getBlock().getY()
						+ "," + e.getBlock().getZ() + ","
						+ e.getBlock().getWorld().getName() + ","
						+ ++this.count);
				getConfig().set("locations", locations);
				e.setLine(0, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "#"
						+ this.count + " PvPLB");
				e.setLine(1, ChatColor.DARK_BLUE + "" + ChatColor.BOLD
						+ "Initializing...");
				e.getPlayer().sendMessage(
						ChatColor.GREEN + "Leaderboard sign placed. :) ("
								+ this.count + ")");

			}
		}
	}
}