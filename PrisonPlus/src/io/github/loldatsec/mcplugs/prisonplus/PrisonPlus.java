package io.github.loldatsec.mcplugs.prisonplus;

import io.github.loldatsec.mcplugs.prisonplus.blocks.BlockBreak;
import io.github.loldatsec.mcplugs.prisonplus.blocks.Mines;
import io.github.loldatsec.mcplugs.prisonplus.inventory.*;
import io.github.loldatsec.mcplugs.prisonplus.inventory.Stack;
import io.github.loldatsec.mcplugs.prisonplus.rankup.Rankup;
import io.github.loldatsec.mcplugs.prisonplus.rankup.VoterRank;
import io.github.loldatsec.mcplugs.prisonplus.text.ActionBarChat;
import io.github.loldatsec.mcplugs.prisonplus.text.Title;
import io.github.loldatsec.mcplugs.prisonplus.vote.PerkShop;
import io.github.loldatsec.mcplugs.prisonplus.vote.VoteShop;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.text.SimpleDateFormat;
import java.util.*;

public class PrisonPlus extends JavaPlugin implements Listener {

	private Economy econ = null;
	public Shop s = null;
	public VoteShop vs = null;
	public PerkShop ps = null;
	private BlockBreak bbl = null;
	private Rankup rum = null;
	private PermissionManager pm = null;
	private Map<String, Scoreboard> ntsb = new HashMap<String, Scoreboard>();
	private Map<String, Integer> commandDelayInTicks = new HashMap<String, Integer>();
	private Map<String, Integer> tpGraceTicks = new HashMap<String, Integer>();
	private Mines minesController = null;

	@SuppressWarnings ("unchecked")
	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			List<String> markRemove = new ArrayList<String>();
			for (String pn : commandDelayInTicks.keySet()) {
				int dec = commandDelayInTicks.get(pn) - 1;
				if (dec >= 1) {
					commandDelayInTicks.put(pn, dec);
				} else {
					markRemove.add(pn);
				}
			}
			for (String pn : markRemove) {
				commandDelayInTicks.remove(pn);
			}
			markRemove.clear();
			for (String pn : tpGraceTicks.keySet()) {
				int dec = tpGraceTicks.get(pn) - 1;
				if (dec >= 1) {
					tpGraceTicks.put(pn, dec);
				} else {
					markRemove.add(pn);
				}
			}
			for (String pn : markRemove) {
				tpGraceTicks.remove(pn);
			}
		}, 0L, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			String df = new SimpleDateFormat("HH:mm").format(new Date());
			if (df.equalsIgnoreCase("00:00") || df.equalsIgnoreCase("08:00") || df.equalsIgnoreCase("16:00")) {
				s.setBoost(new HashMap<String, Integer>());
			}
			Rankup.sortPrefixes();
		}, 0L, 1200);
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		econ = rsp.getProvider();
		s = new Shop();
		vs = new VoteShop();
		ps = new PerkShop();
		rum = new Rankup();
		minesController = new Mines();
		minesController.registerAlias("a", 0);
		minesController.addComposition(0, new ItemStack(Material.COBBLESTONE));
		minesController.addComposition(0, new ItemStack(Material.MOSSY_COBBLESTONE));
		minesController.registerAlias("b", 1);
		minesController.addComposition(1, new ItemStack(Material.STONE));
		minesController.registerAlias("c", 2);
		minesController.addComposition(2, new ItemStack(Material.STONE));
		minesController.addComposition(2, new ItemStack(Material.STONE));
		minesController.addComposition(2, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("d", 3);
		minesController.addComposition(3, new ItemStack(Material.STONE));
		minesController.addComposition(3, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("e", 4);
		minesController.addComposition(4, new ItemStack(Material.STONE));
		minesController.addComposition(4, new ItemStack(Material.COAL_ORE));
		minesController.addComposition(4, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("f", 5);
		minesController.addComposition(5, new ItemStack(Material.COAL_ORE));
		minesController.addComposition(5, new ItemStack(Material.COAL_ORE));
		minesController.addComposition(5, new ItemStack(Material.IRON_ORE));
		minesController.registerAlias("g", 6);
		minesController.addComposition(6, new ItemStack(Material.COAL_ORE));
		minesController.addComposition(6, new ItemStack(Material.IRON_ORE));
		minesController.registerAlias("h", 7);
		minesController.addComposition(7, new ItemStack(Material.COAL_ORE));
		minesController.addComposition(7, new ItemStack(Material.IRON_ORE));
		minesController.addComposition(7, new ItemStack(Material.IRON_ORE));
		minesController.registerAlias("i", 8);
		minesController.addComposition(8, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(8, new ItemStack(Material.IRON_ORE));
		minesController.addComposition(8, new ItemStack(Material.IRON_ORE));
		minesController.addComposition(8, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("j", 9);
		minesController.addComposition(9, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(9, new ItemStack(Material.IRON_ORE));
		minesController.addComposition(9, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(9, new ItemStack(Material.IRON_ORE));
		minesController.addComposition(9, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("k", 10);
		minesController.addComposition(10, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(10, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(10, new ItemStack(Material.IRON_ORE));
		minesController.addComposition(10, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("l", 11);
		minesController.addComposition(11, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(11, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(11, new ItemStack(Material.COAL_ORE));
		minesController.addComposition(11, new ItemStack(Material.LAPIS_ORE));
		minesController.registerAlias("m", 12);
		minesController.addComposition(12, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(12, new ItemStack(Material.LAPIS_ORE));
		minesController.addComposition(12, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(12, new ItemStack(Material.LAPIS_ORE));
		minesController.addComposition(12, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("n", 13);
		minesController.addComposition(13, new ItemStack(Material.GOLD_ORE));
		minesController.addComposition(13, new ItemStack(Material.LAPIS_ORE));
		minesController.addComposition(13, new ItemStack(Material.LAPIS_ORE));
		minesController.addComposition(13, new ItemStack(Material.COAL_ORE));
		minesController.registerAlias("o", 14);
		minesController.addComposition(14, new ItemStack(Material.REDSTONE_ORE));
		minesController.addComposition(14, new ItemStack(Material.LAPIS_ORE));
		minesController.addComposition(14, new ItemStack(Material.LAPIS_ORE));
		minesController.registerAlias("p", 15);
		minesController.addComposition(15, new ItemStack(Material.REDSTONE_ORE));
		minesController.addComposition(15, new ItemStack(Material.REDSTONE_ORE));
		minesController.addComposition(15, new ItemStack(Material.LAPIS_ORE));
		minesController.registerAlias("q", 16);
		minesController.addComposition(16, new ItemStack(Material.NETHERRACK));
		minesController.addComposition(16, new ItemStack(Material.NETHERRACK));
		minesController.addComposition(16, new ItemStack(Material.QUARTZ_ORE));
		minesController.registerAlias("r", 17);
		minesController.addComposition(17, new ItemStack(Material.NETHERRACK));
		minesController.addComposition(17, new ItemStack(Material.QUARTZ_ORE));
		minesController.addComposition(17, new ItemStack(Material.QUARTZ_ORE));
		minesController.registerAlias("s", 18);
		minesController.addComposition(18, new ItemStack(Material.NETHER_BRICK));
		minesController.addComposition(18, new ItemStack(Material.QUARTZ_ORE));
		minesController.addComposition(18, new ItemStack(Material.QUARTZ_ORE));
		minesController.registerAlias("t", 19);
		minesController.addComposition(19, new ItemStack(Material.ENDER_STONE));
		minesController.addComposition(19, new ItemStack(Material.OBSIDIAN));
		minesController.registerAlias("u", 20);
		minesController.addComposition(20, new ItemStack(Material.EMERALD_ORE));
		minesController.addComposition(20, new ItemStack(Material.OBSIDIAN));
		minesController.registerAlias("v", 21);
		minesController.addComposition(21, new ItemStack(Material.EMERALD_ORE));
		minesController.registerAlias("w", 22);
		for (short dmg = 0; dmg <= 15; dmg++) {
			minesController.addComposition(22, new ItemStack(Material.STAINED_CLAY, 1, dmg));
		}
		minesController.registerAlias("x", 23);
		minesController.addComposition(23, new ItemStack(Material.DIAMOND_ORE));
		minesController.addComposition(23, new ItemStack(Material.EMERALD_ORE));
		minesController.registerAlias("y", 24);
		minesController.addComposition(24, new ItemStack(Material.DIAMOND_ORE));
		minesController.registerAlias("z", 25);
		minesController.addComposition(25, new ItemStack(Material.DIAMOND_BLOCK));
		minesController.addComposition(25, new ItemStack(Material.DIAMOND_BLOCK));
		minesController.addComposition(25, new ItemStack(Material.DIAMOND_BLOCK));
		minesController.addComposition(25, new ItemStack(Material.EMERALD_BLOCK));
		minesController.registerAlias("Murderer", 26);
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.BRICK));
		minesController.addComposition(26, new ItemStack(Material.SPONGE));
		minesController.registerAlias("Gangster", 27);
		minesController.addComposition(27, new ItemStack(Material.SANDSTONE));
		minesController.registerAlias("Wanted", 28);
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.PRISMARINE, 1, (short) 2));
		minesController.addComposition(28, new ItemStack(Material.SEA_LANTERN));
		minesController.registerAlias("Vote", 29);
		minesController.registerAlias("Party", 29);
		minesController.registerAlias("VoteParty", 29);
		minesController.registerAlias("VP", 29);
		minesController.addComposition(29, new ItemStack(Material.RED_SANDSTONE, 1, (short) 2));
		minesController.setNukeable(29, false);
		minesController.registerAlias("Log", 30);
		minesController.registerAlias("LogFarm", 30);
		minesController.addComposition(30, new ItemStack(Material.LOG, 1, (short) 12));
		minesController.addComposition(30, new ItemStack(Material.LOG, 1, (short) 13));
		minesController.addComposition(30, new ItemStack(Material.LOG, 1, (short) 14));
		minesController.addComposition(30, new ItemStack(Material.LOG, 1, (short) 15));
		minesController.addComposition(30, new ItemStack(Material.LOG_2, 1, (short) 12));
		minesController.addComposition(30, new ItemStack(Material.LOG_2, 1, (short) 13));
		minesController.registerAlias("Spleeef", 31);
		minesController.registerAlias("SpleeMine", 31);
		minesController.addComposition(31, new ItemStack(Material.COBBLESTONE, 1));
		minesController.setNukeable(31, false);
		bbl = new BlockBreak(s, minesController);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> minesController.trigger(), 2400L, 4800);
		pm = PermissionsEx.getPermissionManager();
		try {
			bbl.addAllAutoSellPlayers((List<String>) getConfig().get("autosell"));
		} catch (Exception e) {
		}
		try {
			bbl.addAllAutoOrganizePlayers((List<String>) getConfig().get("autoorganize"));
		} catch (Exception e) {
		}
		try {
			bbl.addAllSuperBreakPlayers((List<String>) getConfig().get("superbreak"));
		} catch (Exception e) {
		}
		try {
			List<String> list = (List<String>) getConfig().get("boosted");
			for (String l : list) {
				s.setBoost(l.split(":")[0], Integer.parseInt(l.split(":")[1]));
			}
		} catch (Exception e) {
		}
		try {
			for (int t = 0; t < (int) getConfig().get("voteparty"); t++) {
				s.incrementVoteParty();
			}
		} catch (Exception e) {
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(bbl, this);
		getCommand("rankup").setExecutor(rum);
		getCommand("ranks").setExecutor(rum);
		Bukkit.getPluginManager().registerEvents(rum, this);
		getCommand("voteshop").setExecutor(vs);
		Bukkit.getPluginManager().registerEvents(vs, this);
		getCommand("voteperk").setExecutor(ps);
		Bukkit.getPluginManager().registerEvents(ps, this);
		getCommand("voterank").setExecutor(new VoterRank());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			private boolean swt = true;
			private Map<String, String> currentPre = new HashMap<String, String>();

			@SuppressWarnings ("deprecation")
			@Override
			public void run() {
				if (swt) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						currentPre.put(p.getName(), Rankup.getDisplayPrefix(p).replaceAll("&", "§"));
					}
				}
				swt = !swt;
				try {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.setLevel(30);
						Scoreboard sc = null;
						if (!ntsb.containsKey(p.getName())) {
							ntsb.put(p.getName(), Bukkit.getScoreboardManager().getNewScoreboard());
						}
						sc = ntsb.get(p.getName());
						for (Player px : Bukkit.getOnlinePlayers()) {
							String tn = px.getName();
							Team t = sc.getTeam(tn);
							if (t != null) {
								t.unregister();
							}
							t = sc.registerNewTeam(tn);
							if (currentPre.containsKey(px.getName())) {
								String pre = currentPre.get(px.getName());
								pre = pre.replace("Prestige", "Presti").replace("Trial", "T").replace("Head", "H").replace("Murderer", "Murder").replace("Gangster", "Gangst").replace("-", "").replace("§l", "");
								if (pre.length() > 15) {
									pre = pre.substring(0, 15);
								}
								if (pre.endsWith("§")) {
									pre = pre.substring(0, pre.length() - 1);
								}
								t.setPrefix(pre + " ");
								t.addPlayer(px);
							}
						}
						Objective o = sc.getObjective(swt + "_o");
						if (o != null) {
							o.unregister();
						}
						o = sc.registerNewObjective(swt + "_o", "dummy");
						o.setDisplayName(p.getDisplayName());
						double bal = econ.getBalance(p.getName());
						int r = 99;
						o.getScore("§ehttp://reborncraft.info").setScore(r--);
						o.getScore("§a§lBalance: §c$" + Rankup.numformat(bal)).setScore(r--);
						o.getScore("§a§lSell boost: §7§ox§6§o" + s.getBoost(p.getName()) + " §e/voting").setScore(r--);
						o.getScore("§a§lAutoorganize: §c" + bbl.isAutoOrganizePlayer(p.getName()) + " §e/voting").setScore(r--);
						o.getScore("§a§lAutosell: §c" + bbl.isAutoSellPlayer(p.getName()) + " §e/voting").setScore(r--);
						o.getScore("§a§lSuperbreaker: §c" + bbl.isSuperBreakPlayer(p.getName())).setScore(r--);
						o.getScore("§a§lVoteParty: §d" + s.getVoteParty() + " /240").setScore(r--);
						String vps = "§8[§5§l";
						int vp = (int) (s.getVotePartyProgress() * 18);
						for (int c = 0; c < 18; c++) {
							if (c == vp) {
								vps += "§7§l";
							}
							vps += "»";
						}
						vps += "§8]";
						o.getScore(vps).setScore(r--);
						o.getScore("§a§lRank: " + currentPre.get(p.getName())).setScore(r--);
						String nextrank = Rankup.getNextRank(PermissionsEx.getUser(p).getGroupsNames());
						long nextprice = Rankup.getNextRankPrice(PermissionsEx.getUser(p).getGroupsNames());
						if (nextrank != "") {
							o.getScore("§a§lNext Rank: " + ChatColor.translateAlternateColorCodes('&', pm.getGroup(nextrank).getPrefix())).setScore(r--);
							o.getScore("§a§lCost: §c$" + Rankup.numformat(nextprice)).setScore(r--);
							double progress = ((float) (bal / nextprice));
							o.getScore("§a§lProgress: §b" + ((int) (progress * 100)) + "%").setScore(r--);
							if (progress < 1) {
								p.setExp((float) progress);
								int pg = (int) (progress * 18);
								String pgb = "§8[§2§l";
								for (int c = 0; c < 18; c++) {
									if (c == pg) {
										pgb += "§7§l";
									}
									pgb += "»";
								}
								pgb += "§8]";
								o.getScore(pgb).setScore(r--);
							} else {
								p.setExp(1);
								o.getScore("§8[§2§l»»»»»»»»»»»»»»»»»»§8]").setScore(r--);
								o.getScore("§6Do §e/rankup§6 to rank up!").setScore(r--);
							}
						} else {
							p.setExp(1);
						}
						o.setDisplaySlot(DisplaySlot.SIDEBAR);
						p.setScoreboard(sc);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0L, 20L);
		ShapelessRecipe lava = new ShapelessRecipe(new ItemStack(Material.LAVA_BUCKET, 1));
		lava.addIngredient(Material.BEACON);
		lava.addIngredient(Material.BUCKET);
		Bukkit.addRecipe(lava);
		ShapelessRecipe water = new ShapelessRecipe(new ItemStack(Material.WATER_BUCKET, 1));
		water.addIngredient(Material.NETHER_STAR);
		water.addIngredient(Material.BUCKET);
		Bukkit.addRecipe(water);
	}

	public void onDisable() {
		getConfig().set("autosell", bbl.getAutoSellPlayers());
		getConfig().set("autoorganize", bbl.getAutoOrganizePlayers());
		getConfig().set("superbreak", bbl.getSuperBreakPlayers());
		List<String> boost = new ArrayList<String>();
		for (String pn : s.getBoosted().keySet()) {
			boost.add(pn + ":" + s.getExclusiveBoost(pn));
		}
		getConfig().set("boosted", boost);
		getConfig().set("voteparty", s.getVoteParty());
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("voteaction")) {
			if (sender instanceof Player) {
				sender.sendMessage("§cError: §4Only console can do this.");
			} else {
				if (args.length >= 1) {
					VoteAction.award(args[0], s);
				} else {
					sender.sendMessage("You are doing it wrong...");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("donatesuperpick")) {
			if (sender instanceof Player) {
				sender.sendMessage("§cError: §4Only console can do this.");
			} else {
				if (args.length >= 1) {
					SuperPick.give(args[0]);
				} else {
					sender.sendMessage("You are doing it wrong...");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("voteparty")) {
			if (sender instanceof Player) {
				sender.sendMessage("§cError: §4Only console can do this.");
			} else {
				if (args.length >= 1) {
					s.votePartyEvent(args[0]);
				} else {
					s.votePartyEvent("dropmine");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("voting")) {
			Bukkit.dispatchCommand(sender, "vote");
			sender.sendMessage("§aVote Reward Commands:\n§a/voteperk - Vote permissions\n§a/voteshop - Vote Items\n§a/voterank - A shiny voter rank!\n§eClick on one of the above links to get started with voting!");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("forceresetmines")) {
			if (sender.hasPermission("prisonplus.forceresetmines")) {
				if (args.length >= 1) {
					int id = -1;
					try {
						id = Integer.parseInt(args[0]);
					} catch (NumberFormatException npe) {
						id = minesController.getIdByAlias(args[0]);
					}
					if (id >= 0) {
						if (minesController.resetMine(id)) {
							sender.sendMessage(Mines.prefix + "Resetting mine: §e" + minesController.getAliasById(id));
							return true;
						}
					}
					sender.sendMessage(Mines.prefix + "Mine not found: §e" + args[0]);
				} else if (sender.hasPermission("prisonplus.forceresetmines.all")) {
					minesController.trigger();
				} else {
					sender.sendMessage("§cError: §4One argument required! §c/forceresetmines <minename|mineid>");
				}
			} else {
				sender.sendMessage("§cError: §4You do not have the permission §cprisonplus.forceresetmines §4You need to rank up to a higher rank to use this.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("sell") || cmd.getName().equalsIgnoreCase("sellall") || cmd.getName().equalsIgnoreCase("s")) {
			if (sender instanceof Player) {
				s.rankSell(((Player) sender), false);
			} else {
				sender.sendMessage("§cError: §4Only players can do this.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("stack") || cmd.getName().equalsIgnoreCase("st")) {
			if (sender instanceof Player) {
				new Stack((Player) sender, false);
			} else {
				sender.sendMessage("§cError: §4Only players can do this.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("smelt") || cmd.getName().equalsIgnoreCase("sm")) {
			if (sender instanceof Player) {
				new Smelt((Player) sender, false);
			} else {
				sender.sendMessage("§cError: §4Only players can do this.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setboost")) {
			if (sender.hasPermission("prisonplus.sellboost.set")) {
				if (args.length >= 2) {
					try {
						String pn = Bukkit.getPlayer(args[0]).getName();
						int amt = Integer.parseInt(args[1]) - 1;
						if (amt < 0) {
							amt = 0;
						}
						s.setBoost(pn, amt);
						sender.sendMessage("§6" + pn + "§a's boost factor is now §e" + s.getBoost(pn));
					} catch (NumberFormatException nfex) {
						sender.sendMessage("§cError: §4Command failed. Reason: §ccannot parse " + args[1] + " to a integer.");
					}
				} else {
					sender.sendMessage("§cError: §4Two arguments required! §c/setboost <playername> <amount>");
				}
			} else {
				sender.sendMessage("§cError: §4You do not have the permission §cprisonplus.sellboost.set §4You need to rank up to a higher rank to use this.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("nv")) {
			if (sender instanceof Player) {
				if (((Player) sender).hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
					((Player) sender).removePotionEffect(PotionEffectType.NIGHT_VISION);
					sender.sendMessage("§aEffect removed.");
				} else {
					((Player) sender).addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
					sender.sendMessage("§aNow you can see!");
				}
			} else {
				sender.sendMessage("§cError: §4Only players can do this.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("pay")) {
			try {
				if (sender instanceof Player) {
					if (args.length >= 2) {
						Player p = Bukkit.getPlayer(args[0]);
						long amt = Long.parseLong(args[1]);
						if (p == null) {
							sender.sendMessage("§cError: §4Player not found.");
							return true;
						}
						if (!p.hasPermission("prisonplus.receivemoney")) {
							sender.sendMessage("§cError: §4You cannot pay that player!");
							return true;
						}
						RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
						Economy econ = rsp.getProvider();
						EconomyResponse r = econ.withdrawPlayer((Player) sender, amt);
						if (r.transactionSuccess()) {
							econ.depositPlayer(p, amt);
							sender.sendMessage("§aYou paid §b" + p.getName() + " §c$" + amt);
							p.sendMessage("§b" + sender.getName() + " §apaid you §c$" + amt);
						} else {
							sender.sendMessage("§cError: §4Transaction failed. Reason: §cError: §c" + r.errorMessage);
						}
					} else {
						sender.sendMessage("§cError: §4Two arguments required! §c/pay <playername> <amount>");
					}
				} else {
					sender.sendMessage("§cError: §4Only players can do this.");
				}
			} catch (NumberFormatException ex) {
				sender.sendMessage("§cError: §4Transaction failed. Reason: §ccannot parse " + args[1] + " to a long.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("autosell")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("prisonplus.autosell")) {
					if (bbl.isAutoSellPlayer(sender.getName())) {
						bbl.remAutoSellPlayers(sender.getName());
					} else {
						bbl.addAutoSellPlayers(sender.getName());
					}
					sender.sendMessage("§aToggled auto selling to §c" + bbl.isAutoSellPlayer(sender.getName()) + ".");
				} else {
					sender.sendMessage("§cError: §4You do not have the permission §cprisonplus.autosell §4You need to rank up to a higher rank to use this.");
				}
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("autoorganize")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("prisonplus.autoorganize")) {
					if (bbl.isAutoOrganizePlayer(sender.getName())) {
						bbl.remAutoOrganizePlayers(sender.getName());
					} else {
						bbl.addAutoOrganizePlayers(sender.getName());
					}
					sender.sendMessage("§aToggled auto organize to §c" + bbl.isAutoOrganizePlayer(sender.getName()) + ".");
				} else {
					sender.sendMessage("§cError: §4You do not have the permission §cprisonplus.autoorganize §4You need to rank up to a higher rank to use this.");
				}
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("superbreaker")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("prisonplus.superbreaker")) {
					if (bbl.isSuperBreakPlayer(sender.getName())) {
						bbl.remSuperBreakPlayers(sender.getName());
					} else {
						bbl.addSuperBreakPlayers(sender.getName());
					}
					sender.sendMessage("§aToggled super breaking to §c" + bbl.isSuperBreakPlayer(sender.getName()) + ".");
				} else {
					sender.sendMessage("§cError: §4You do not have the permission §cprisonplus.superbreaker §4You need to rank up to a higher rank to use this.");
				}
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void teleport(PlayerTeleportEvent e) {
		tpGraceTicks.put(e.getPlayer().getName(), 120);
	}

	public Map<String, Location> lastValid = new HashMap<String, Location>();

	@SuppressWarnings ("deprecation")
	@EventHandler (priority = EventPriority.LOW)
	public void move(PlayerMoveEvent e) {
		GameMode gm = e.getPlayer().getGameMode();
		if (e.getPlayer().getWorld().getName().equalsIgnoreCase("prison") && gm == GameMode.SURVIVAL) {
			if (!e.getPlayer().hasPermission("prisonplus.allowfly") && e.getPlayer().isFlying()) {
				if (!minesController.isInMine(e.getTo())) {
					e.getPlayer().teleport((lastValid.containsKey(e.getPlayer().getName())) ? lastValid.get(e.getPlayer().getName()) : e.getTo().getWorld().getSpawnLocation());
					e.getPlayer().setFlying(false);
					e.getPlayer().setAllowFlight(false);
					e.getPlayer().sendMessage("§6Set flying §cdisabled §6for " + e.getPlayer().getDisplayName() + "§6.");
				}
			}
			if (!(gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR || tpGraceTicks.containsKey(e.getPlayer().getName()))) {
				Player p = e.getPlayer();
				if (commandDelayInTicks.containsKey(p.getName())) {
					commandDelayInTicks.put(p.getName(), (int) (commandDelayInTicks.get(p.getName()) + 1));
				}
				if (e.getTo().getY() <= 2) {
					p.teleport(p.getWorld().getSpawnLocation());
				} else {
					Block b = e.getTo().getBlock();
					Block pb = b.getRelative(BlockFace.UP);
					Map<Material, Long> s = this.s.sellable(p);
					boolean tpback = false;
					if (s.containsKey(pb.getType())) {
						Material o = pb.getType();
						byte d = pb.getData();
						pb.setType(Material.AIR);
						pb.setType(o);
						pb.setData(d);
						tpback = true;
					}
					if (s.containsKey(b.getType())) {
						Material o = b.getType();
						byte d = b.getData();
						b.setType(Material.AIR);
						b.setType(o);
						b.setData(d);
						tpback = true;
					}
					if (tpback && !p.isFlying()) {
						int x = b.getX();
						int z = b.getZ();
						int vy = 0;
						boolean f = false;
						for (int y = 10; y < 250; y++) {
							Block yb = b.getWorld().getBlockAt(x, y, z);
							Material o = yb.getType();
							byte d = yb.getData();
							yb.setType(Material.AIR);
							yb.setType(o);
							yb.setData(d);
							if (yb.getType() != Material.AIR) {
								f = true;
							}
							if (f == true && vy == 0 && y <= 100) {
								if (yb.getType() == Material.AIR && yb.getRelative(BlockFace.UP).getType() == Material.AIR) {
									vy = y;
								}
							}
						}
						if (lastValid.containsKey(p.getName()) && lastValid.get(p.getName()).getY() >= e.getFrom().getY()) {
							Block t = lastValid.get(p.getName()).getBlock();
							Block pt = t.getRelative(BlockFace.UP);
							if (t.getType() == Material.AIR && pt.getType() == Material.AIR) {
								p.teleport(lastValid.get(p.getName()));
								return;
							}
						}
						Location l = p.getLocation();
						l.setY(vy);
						p.teleport(l);
					} else {
						lastValid.put(p.getName(), e.getTo());
					}
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getPlayer().getWorld().getName().equalsIgnoreCase("prison")) {
				if (e.getPlayer().getItemInHand().getType() == Material.WATER_BUCKET || e.getPlayer().getItemInHand().getType() == Material.LAVA_BUCKET) {
					e.setCancelled(true);
					e.getPlayer().sendMessage("§cNo liquids allowed!");
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void blockPlace(BlockPlaceEvent e) {
		if (e.getPlayer().getWorld().getName().equalsIgnoreCase("prison") && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void hungry(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@SuppressWarnings ("deprecation")
	@EventHandler (priority = EventPriority.LOWEST)
	public void playerDeath(PlayerDeathEvent e) {
		Player v = e.getEntity();
		if (commandDelayInTicks.containsKey(v.getName())) {
			commandDelayInTicks.remove(v.getName());
		}
		Player k = e.getEntity().getKiller();
		e.setDroppedExp(0);
		String deadName = v.getName();
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
		skull.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		Date d = new Date();
		if (d.getMonth() == 4 && d.getDay() == 1) {
			meta.setOwner("MHF_Cow");
			meta.setDisplayName("§bSkull of §aa innocent cow");
		} else {
			meta.setOwner(deadName);
			meta.setDisplayName("§bSkull of §a" + deadName);
		}
		meta.setDisplayName("§bSkull of §a" + deadName);
		List<String> lore = new ArrayList<String>();
		boolean p = k instanceof Player;
		String dm = "§6" + v.getDisplayName() + "§6";
		if (p) {
			lore.add("§eMurdered by: §6" + k.getName());
			ItemStack weapon = k.getItemInHand();
			dm += " was slain by " + k.getDisplayName() + "§6[§c" + ((int) (k.getHealth())) + "§6/§420§6]";
			try {
				String wp = weapon.getItemMeta().getDisplayName();
				if (wp == null) {
					wp = weapon.getType().toString().replace("_", " ");
				}
				if (wp != null) {
					dm += " using §b[" + wp + "§b]§6";
				}
			} catch (NullPointerException npe) {
			}
		} else {
			try {
				DamageCause c = v.getLastDamageCause().getCause();
				if (c == DamageCause.BLOCK_EXPLOSION || c == DamageCause.ENTITY_EXPLOSION) {
					dm += " blew up.";
				} else if (c == DamageCause.ENTITY_ATTACK) {
					dm += " was slain";
				} else if (c == DamageCause.DROWNING) {
					dm += " drowned";
				} else if (c == DamageCause.FALLING_BLOCK) {
					dm += " was squashed to death";
				} else if (c == DamageCause.FIRE || c == DamageCause.FIRE_TICK || c == DamageCause.LAVA) {
					dm += " was burnt to a crisp";
				} else if (c == DamageCause.LIGHTNING) {
					dm += " was smitten to death";
				} else if (c == DamageCause.MAGIC) {
					dm += " was splashed with instant damage";
				} else if (c == DamageCause.POISON) {
					dm += " was poisoned";
				} else if (c == DamageCause.PROJECTILE) {
					dm += " shot right in teh pussy.";
				} else if (c == DamageCause.STARVATION) {
					dm += " starved to death";
				} else if (c == DamageCause.SUFFOCATION) {
					dm += " suffocated";
				} else if (c == DamageCause.SUICIDE) {
					dm += " fapped too hard and fell off his bed";
				} else if (c == DamageCause.CONTACT || c == DamageCause.THORNS) {
					dm += " was pricked to death";
				} else if (c == DamageCause.VOID) {
					dm += " fapped too hard and fell off the world";
				} else if (c == DamageCause.WITHER) {
					dm += " was sent to hell by the wither";
				}
				lore.add("§eKilled with: §6" + c.toString());
			} catch (Exception ex) {
				dm += " fapped too hard and his dick fell off";
			}
		}
		lore.add("§2Death time: §a" + new SimpleDateFormat("YYYY-MMM-dd HH:mm:ss z").format(new Date()));
		meta.setLore(lore);
		skull.setItemMeta(meta);
		if (p) {
			e.getEntity().getKiller().getInventory().addItem(skull);
		} else {
			e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), skull);
		}
		dm += ".";
		e.setDeathMessage(dm);
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Title.send(e.getPlayer(), "§6Welcome to §aRcPrison.", null, 50, 120, 30);
		e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
	}

	@EventHandler
	public void furnaceGetOutput(FurnaceExtractEvent e) {
		e.setExpToDrop(0);
	}

	@EventHandler
	public void xpChange(PlayerExpChangeEvent e) {
		e.setAmount(0);
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void lowChat(AsyncPlayerChatEvent e) {
		PermissionUser pu = PermissionsEx.getUser(e.getPlayer());
		e.setFormat(pu.getPrefix().replaceAll("&", "\u00a7") + e.getPlayer().getDisplayName() + pu.getSuffix().replaceAll("&", "\u00a7") + ": " + e.getMessage().replaceAll("%", ""));
		int dc = 0;
		boolean utb = false;
		boolean con = false;
		for (char c : e.getMessage().toCharArray()) {
			if ((c == '.' || c == ',' || c == '_' || c == ':' || c == ';') && !con) {
				con = true;
				dc++;
			} else if (c == '{' || c == '(' || c == '[') {
				utb = true;
			} else if (c == '}' || c == ')' || c == ']') {
				if (utb) {
					dc++;
					utb = false;
				}
			} else {
				con = false;
			}
		}
		if (dc >= 3) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§4§lPrisonAntiAds: §cDetected likely advertisement; message cancelled.");
		}
	}

	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void chat(AsyncPlayerChatEvent e) {
		String msg = e.getMessage();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (msg.contains(p.getName())) {
				p.playSound(p.getEyeLocation(), Sound.ORB_PICKUP, 1F, 1F);
				p.sendMessage("§e" + e.getPlayer().getName() + " §6mentioned you.");
			}
		}
		PermissionUser pu = PermissionsEx.getUser(e.getPlayer());
		String uprefix = "";
		for (String pg : pu.getGroupsNames()) {
			uprefix = PermissionsEx.getPermissionManager().getGroup(pg).getPrefix() + uprefix;
		}
		if (!uprefix.equalsIgnoreCase(pu.getPrefix())) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pu.getName() + " prefix \"" + uprefix + "\"");
		}
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void command(PlayerCommandPreprocessEvent e) {
		if (commandDelayInTicks.containsKey(e.getPlayer().getName())) {
			float delay = ((float) commandDelayInTicks.get(e.getPlayer().getName()) / 20);
			e.setCancelled(true);
			e.getPlayer().sendMessage("§9[§7Combat§9] §7You need to wait (stand still without combatting) §c" + delay + " §7more seconds before you can run commands.");
		}
	}

	@SuppressWarnings ("deprecation")
	@EventHandler (priority = EventPriority.HIGH)
	public void entDamageEnt(EntityDamageByEntityEvent e) {
		if (e.getDamager().getLocation().distance(e.getEntity().getLocation()) > 5) {
			e.setCancelled(true);
		}
		Player p = null;
		if (e.getDamager() instanceof Player) {
			p = (Player) e.getDamager();
		} else if (e.getDamager() instanceof Projectile) {
			if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
				p = (Player) ((Projectile) e.getDamager()).getShooter();
			}
		}
		if (p != null) {
			if (e.getEntity() instanceof Damageable) {
				int he = (int) ((Damageable) e.getEntity()).getHealth() / 2;
				if (!e.isCancelled()) {
					if (e.getEntity() instanceof Player) {
						commandDelayInTicks.put(e.getEntity().getName(), 300);
						commandDelayInTicks.put(p.getName(), 300);
					}
					he -= e.getDamage() / 2;
				}
				String health = "";
				for (int h = 1; h <= ((Damageable) e.getEntity()).getMaxHealth() / 2; h++) {
					health += "❤";
					if (h == he) {
						health += "§8§l";
					}
				}
				String ename = e.getEntity().getType().toString();
				if (e.getEntity() instanceof Player) {
					ename = ((Player) e.getEntity()).getName();
				}
				ActionBarChat.send(p, "§6" + ename + "'s§e health: §0§l[§4§l" + health + "§0§l]");
			}
			ItemStack h = p.getItemInHand();
			if (h.getType().toString().endsWith("AXE")) {
				h.setDurability((short) 0);
				p.setItemInHand(h);
			}
		}
		if (!e.isCancelled()) {
			Date d = new Date();
			if (d.getMonth() == 4 && d.getDay() == 1) {
				ItemStack leather = new ItemStack(Material.LEATHER, 1);
				ItemMeta leatherMeta = leather.getItemMeta();
				leatherMeta.setDisplayName("\u00a76Cow Skin");
				leatherMeta.setLore(Arrays.asList(new String[]{"\u00a7eA cow inside died.", "\u00a7eStop killing cows. D:"}));
				leather.setItemMeta(leatherMeta);
				e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), leather);
			}
		}
	}

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		try {
			ItemStack i = e.getItem().getItemStack();
			if (i.getType() == Material.TNT || i.getType() == Material.APPLE) {
				e.setCancelled(true);
				e.getItem().remove();
			} else if (i.getType() == Material.SKULL_ITEM) {
				SkullMeta meta = (SkullMeta) i.getItemMeta();
				if (meta.getOwner().startsWith("MHF_")) {
					meta.setDisplayName("§bSkull of §a" + meta.getOwner());
				} else {
					meta.setDisplayName("§bSkull of §a" + meta.getOwner().substring(4));
				}
				e.getItem().getItemStack().setItemMeta(meta);
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void click(InventoryClickEvent e) {
		try {
			ItemStack i = e.getInventory().getItem(e.getSlot());
			if (i.getType() == Material.TNT || i.getType() == Material.APPLE) {
				e.setCancelled(true);
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void death(EntityDeathEvent e) {
		if (!(e.getEntity() instanceof Player) && e.getEntity() instanceof Creature && e.getEntity().getKiller() instanceof Player) {
			Player killer = e.getEntity().getKiller();
			int occupiedSlots = 0;
			for (int is = 0; is <= 35; is++) {
				try {
					if (killer.getInventory().getItem(is).getType() != Material.AIR) {
						occupiedSlots++;
					}
				} catch (NullPointerException npe) {
				}
			}
			if (occupiedSlots + e.getDrops().size() > 35) {
				if (bbl.isAutoSellPlayer(killer.getName())) {
					s.rankSell(killer, true);
				} else {
					Title.send(killer, "§4Full Inventory!", "§e/sellall§c to sell all your items.", 0, 100, 0);
					return;
				}
			}
			for (ItemStack d : e.getDrops()) {
				killer.getInventory().addItem(d);
			}
			e.getDrops().clear();
			e.setDroppedExp(0);
			if (Math.random() < 0.007) {
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
				skull.setDurability((short) 3);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				String mhf = "MHF_";
				mhf += e.getEntityType().toString().substring(0, 1).toUpperCase() + e.getEntityType().toString().substring(1).toLowerCase();
				meta.setOwner(mhf);
				meta.setDisplayName("§eSkull of a §6" + e.getEntityType().toString());
				skull.setItemMeta(meta);
				killer.getInventory().addItem(skull);
			}
		}
	}

	/**
	 * GetPing
	 * <p>
	 * Returns ping of player. (ms in int)
	 *
	 * @param p
	 */
	@Deprecated
	public int getPing(Player p) {
		org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer cp = (org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) p;
		net.minecraft.server.v1_8_R3.EntityPlayer ep = cp.getHandle();
		return ep.ping;
	}
}
