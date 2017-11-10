package io.github.loldatsec.mcplugins.haloplus.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.grenades.EnumGrenade;
import io.github.loldatsec.mcplugins.haloplus.grenades.Grenade;
import io.github.loldatsec.mcplugins.haloplus.utils.BuyWeaponsInterface;
import io.github.loldatsec.mcplugins.haloplus.utils.ExclusiveList;
import io.github.loldatsec.mcplugins.haloplus.utils.SpecialChat;
import io.github.loldatsec.mcplugins.haloplus.weapons.EnumWeapon;
import io.github.loldatsec.mcplugins.haloplus.weapons.Weapon;
import io.github.loldatsec.mcplugins.haloplus.weapons.WeaponType;

public class Game {

	public World w;
	private boolean running = false;
	private boolean ended = false;
	public Map<GameTeamEnum, GameTeam> gameTeams = new HashMap<GameTeamEnum, GameTeam>();
	public int roundsDone = 0;
	public Scoreboard sb;
	public Objective ob;
	public Objective balOb;
	public ExclusiveList<Player> vote2start = new ExclusiveList<Player>();

	public Game(World w) {
		this.w = w;
		register();
		w.getWorldBorder().setSize(201, 1);
		gameTeams.put(GameTeamEnum.BLUE, new GameTeam(GameTeamEnum.BLUE));
		gameTeams.put(GameTeamEnum.RED, new GameTeam(GameTeamEnum.RED));
		gameTeams.put(GameTeamEnum.SPECTATOR, new GameTeam(GameTeamEnum.SPECTATOR));
	}

	public void register() {
		GameManager.games.put(w, this);
	}

	public void unregister() {
		GameManager.games.put(w, null);
	}

	/**
	 * Weapons Unlocks:
	 * 
	 * P99 L:20
	 * 
	 * D. Eagle L:30
	 * 
	 * Grenades Unlocks:
	 * 
	 * Grenade L:25
	 * 
	 * Molotov L:35
	 * 
	 * Armor Unlocks:
	 * 
	 * Iron Boots L:25
	 * 
	 * Iron Leggings L:30
	 * 
	 * Diamond Boots L:35
	 * 
	 * Diamond Leggings L:40
	 */
	public void getKit(Player p) {
		GameTeamEnum gte = getGameTeam(p);
		PlayerInventory i = p.getInventory();
		i.setChestplate(getArmorPiece(gte, Material.LEATHER_CHESTPLATE));
		int lv = HaloPlus.getLevel(p);
		if (p.hasPermission("haloplus.kit.donator")) {
			i.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
			i.setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
		} else {
			if (lv >= 60) {
				i.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
			} else if (lv >= 30) {
				i.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
			} else {
				i.setLeggings(getArmorPiece(gte, Material.LEATHER_LEGGINGS));
			}
			if (lv >= 60) {
				i.setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
			} else if (lv >= 30) {
				i.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
			} else {
				i.setBoots(getArmorPiece(gte, Material.LEATHER_BOOTS));
			}
		}
		if (!(i.getItem(0) instanceof ItemStack)) {
			if (p.hasPermission("haloplus.kit.donator")) {
				i.setItem(0, HaloPlus.weapons.get(EnumWeapon.Desert_Eagle).getItem());
			} else {
				if (lv >= 30) {
					i.setItem(0, HaloPlus.weapons.get(EnumWeapon.Desert_Eagle).getItem());
				} else if (lv >= 20) {
					i.setItem(0, HaloPlus.weapons.get(EnumWeapon.P99).getItem());
				} else {
					i.setItem(0, HaloPlus.weapons.get(EnumWeapon.Glock17).getItem());
				}
			}
		}
	}

	public static ItemStack getArmorPiece(GameTeamEnum gte, Material m) {
		ItemStack i = new ItemStack(m, 1);
		LeatherArmorMeta im = (LeatherArmorMeta) i.getItemMeta();
		im.setColor(GameTeamEnum.toRGB(gte));
		i.setItemMeta(im);
		return i;
	}

	public void startGame() {
		WorldBorder wb = w.getWorldBorder();
		wb.setSize(201, 1);
		wb.setDamageAmount(0);
		for (Player p : w.getPlayers()) {
			GameTeamEnum team;
			GameTeam red = gameTeams.get(GameTeamEnum.RED);
			GameTeam blue = gameTeams.get(GameTeamEnum.BLUE);
			if (red.members.size() > blue.members.size()) {
				blue.members.put(p, 400);
				team = GameTeamEnum.BLUE;
			} else if (red.members.size() < blue.members.size()) {
				red.members.put(p, 400);
				team = GameTeamEnum.RED;
			} else {
				if (Math.random() >= 0.5) {
					blue.members.put(p, 400);
					team = GameTeamEnum.BLUE;
				} else {
					red.members.put(p, 400);
					team = GameTeamEnum.RED;
				}
			}
			SpecialChat.sendTitle(p, "\u00a7eYou joined", GameTeamEnum.getColour(team).toString() + team.toString() + " team", 1, 12, 3);
			SpecialChat.tell(p, new String[] { "Open your inventory to buy guns.", "", "Have fun. :)" }, ChatColor.YELLOW);
			p.setGameMode(GameMode.ADVENTURE);
			p.setHealth(20);
			p.setAllowFlight(false);
			GameManager.immoveable.put(p, w);
		}
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		loadObjectives();
		for (Player p : w.getPlayers()) {
			if (gameTeams.get(GameTeamEnum.BLUE).members.containsKey(p)) {
				balOb.getScore(p.getName()).setScore(gameTeams.get(GameTeamEnum.BLUE).members.get(p));
			} else if (gameTeams.get(GameTeamEnum.RED).members.containsKey(p)) {
				balOb.getScore(p.getName()).setScore(gameTeams.get(GameTeamEnum.RED).members.get(p));
			}
		}
		for (GameTeam g : gameTeams.values()) {
			g.setScoreboardTeam(sb.registerNewTeam(w.getName() + "|" + g.team.toString().substring(0, 1)));
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("HaloPlus"), new Runnable() {

			@Override
			public void run() {
				startRound();
			}
		}, 100);
		running = true;
	}

	private void loadObjectives() {
		if (sb.getObjective(DisplaySlot.SIDEBAR) != null) {
			ob.unregister();
		}
		if (sb.getObjective(DisplaySlot.PLAYER_LIST) != null) {
			balOb.unregister();
		}
		int bs = gameTeams.get(GameTeamEnum.BLUE).score;
		int rs = gameTeams.get(GameTeamEnum.RED).score;
		ob = sb.registerNewObjective("GameProgress", "dummy");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		ob.setDisplayName("\u00a7a\u00a7lGame Progress");
		ob.getScore("\u00a7d\u00a7lBest of 9").setScore(99);
		ob.getScore("\u00a77Round " + (bs + rs + 1) + "/9").setScore(98);
		ob.getScore("\u00a70").setScore(97);
		ob.getScore("\u00a7bBlue Wins: \u00a76\u00a7n" + bs).setScore(96);
		ob.getScore("\u00a71").setScore(95);
		ob.getScore("\u00a7cRed Wins: \u00a76\u00a7n" + rs).setScore(94);
		ob.getScore("\u00a71").setScore(93);
		ob.getScore(SpecialChat.twoWayGameprogress(bs, rs)).setScore(92);
		balOb = sb.registerNewObjective("PlayerBalance", "dummy");
		balOb.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		for (Player p : w.getPlayers()) {
			if (isInTeam(p)) {
				balOb.getScore(p.getName()).setScore(gameTeams.get(getGameTeam(p)).members.get(p));
			}
		}
		for (Player p : w.getPlayers()) {
			p.setScoreboard(sb);
		}
	}

	public void startRound() {
		w.getWorldBorder().setSize(201, 1);
		BukkitScheduler s = Bukkit.getScheduler();
		Plugin h = Bukkit.getPluginManager().getPlugin("HaloPlus");
		for (Item ie : w.getEntitiesByClass(Item.class)) {
			ie.remove();
		}
		loadObjectives();
		for (Player p : w.getPlayers()) {
			if (!gameTeams.get(GameTeamEnum.RED).members.containsKey(p) && !gameTeams.get(GameTeamEnum.BLUE).members.containsKey(p)) {
				GameTeam red = gameTeams.get(GameTeamEnum.RED);
				GameTeam blue = gameTeams.get(GameTeamEnum.BLUE);
				if (red.members.size() > blue.members.size()) {
					blue.members.put(p, 400);
				} else if (red.members.size() < blue.members.size()) {
					red.members.put(p, 400);
				} else {
					if (Math.random() >= 0.5) {
						blue.members.put(p, 400);
					} else {
						red.members.put(p, 400);
					}
				}
			}
			HaloPlus.deleteShotsData(p);
			p.teleport(getSpawnPoint(getGameTeam(p)));
			p.setGameMode(GameMode.ADVENTURE);
			p.setHealth(20);
			p.setFireTicks(0);
			GameManager.immoveable.put(p, w);
			BuyWeaponsInterface.showInterface(p);
			p.sendMessage("\u00a7aBalance> \u00a7eYou have $" + getTeam(p).members.get(p) + ".\n\u00a7aBalance> \u00a76Open your inventory to buy weapons.");
			getKit(p);
		}
		for (GameTeam g : gameTeams.values()) {
			g.updateScoreboard();
		}
		for (int i = 14; i > 0; i--) {
			sendCountDown(i, h, s);
		}
		s.scheduleSyncDelayedTask(h, new Runnable() {

			@Override
			public void run() {
				w.getWorldBorder().setSize(1, 140);
				for (Player p : w.getPlayers()) {
					if (gameTeams.get(GameTeamEnum.RED).members.containsKey(p) || gameTeams.get(GameTeamEnum.BLUE).members.containsKey(p)) {
						BuyWeaponsInterface.hideInterface(p);
					}
					List<Player> remove = new ArrayList<Player>();
					for (Player ep : GameManager.immoveable.keySet()) {
						if (ep.getWorld().equals(w)) {
							remove.add(ep);
						}
					}
					for (Player rm : remove) {
						GameManager.immoveable.remove(rm);
					}
					SpecialChat.sendTitle(p, "\u00a75Round started", "\u00a7dGood luck and have fun.", 1, 15, 1);
					p.sendMessage("\u00a75\u00a7lGame> \u00a7d\u00a7lRound started. Good luck and have fun!");
					p.playSound(p.getEyeLocation(), Sound.DIG_STONE, 2, 2);
				}
			}
		}, 300);
	}

	private Location getSpawnPoint(GameTeamEnum gameTeam) {
		double x = 0;
		double y = -10;
		double z = 0;
		switch (w.getName().toLowerCase().substring(0, 3)) {
			case "ice":
				if (gameTeam == GameTeamEnum.BLUE) {
					x = 60;
					y = 157;
					z = 30;
				} else if (gameTeam == GameTeamEnum.RED) {
					x = -43;
					y = 157;
					z = -111;
				}
			case "cit":
				if (gameTeam == GameTeamEnum.BLUE) {
					x = -90;
					y = 175;
					z = 50;
				} else if (gameTeam == GameTeamEnum.RED) {
					x = 96;
					y = 175;
					z = 50;
				}
				break;
			case "cov":
				if (gameTeam == GameTeamEnum.BLUE) {
					x = 5;
					y = 158;
					z = -74;
				} else if (gameTeam == GameTeamEnum.RED) {
					x = -38;
					y = 166;
					z = 55;
				}
				break;
			case "des":
				if (gameTeam == GameTeamEnum.BLUE) {
					x = 61;
					y = 154;
					z = 9;
				} else if (gameTeam == GameTeamEnum.RED) {
					x = -27;
					y = 161;
					z = 36;
				}
				break;
			case "der":
				if (gameTeam == GameTeamEnum.BLUE) {
					x = -15;
					y = 166;
					z = 93;
				} else if (gameTeam == GameTeamEnum.RED) {
					x = 15;
					y = 166;
					z = -94;
				}
				break;
			case "exo":
				if (gameTeam == GameTeamEnum.BLUE) {
					x = 9;
					y = 53;
					z = -65;
				} else if (gameTeam == GameTeamEnum.RED) {
					x = -74;
					y = 56;
					z = 79;
				}
				break;
			default:
				break;
		}
		return new Location(w, x, y, z);
	}

	private GameTeamEnum getGameTeam(Player p) {
		for (GameTeam gt : gameTeams.values()) {
			if (gt.members.containsKey(p)) { return gt.team; }
		}
		return GameTeamEnum.SPECTATOR;
	}

	public void scheduler() {
		if (!running && !ended) {
			List<Player> vps = new ArrayList<Player>();
			for (Player vp : vote2start.list) {
				if (!vp.isOnline() || !vp.getWorld().getName().equals(w.getName())) {
					vps.add(vp);
				}
			}
			for (Player vp : vps) {
				vote2start.remove(vp);
			}
		} else if (running && !ended) {
			loadObjectives();
			vote2start.clear();
			for (GameTeam gt : gameTeams.values()) {
				if (gt.team != GameTeamEnum.SPECTATOR) {
					int gtOnline = 0;
					int gtAlive = 0;
					List<Player> remove = new ArrayList<Player>();
					for (Player p : gt.members.keySet()) { // Also add one for endRound
						if (p.isOnline() && w.getPlayers().contains(p)) {
							gtOnline++;
							if (p.getGameMode() == GameMode.ADVENTURE) {
								gtAlive++;
							}
						} else {
							remove.add(p);
						}
					}
					for (Player p : remove) {
						gt.members.remove(p);
					}
					if (gtOnline <= 0) {
						endGame();
						break;
					} else if (gtAlive <= 0) {
						endRound();
						break;
					}
				}
			}
			for (Player p : w.getPlayers()) {
				GameTeamEnum team = getGameTeam(p);
				if (team == null || team == GameTeamEnum.SPECTATOR) {
					p.setGameMode(GameMode.SPECTATOR);
				}
				if (p.getGameMode() == GameMode.ADVENTURE) {
					p.setAllowFlight(false);
					p.setFlying(false);
				}
			}
		}
	}
	public boolean endRoundBlock = false;

	public void endRound() {
		if (!endRoundBlock && !ended && running) {
			if (countAlive(GameTeamEnum.RED) <= 0 || countAlive(GameTeamEnum.BLUE) <= 0) {
				GameTeamEnum roundWinner = GameTeamEnum.BLUE;
				if (countAlive(GameTeamEnum.BLUE) <= 0) {
					roundWinner = GameTeamEnum.RED;
				}
				w.getWorldBorder().setSize(201, 1);
				gameTeams.get(roundWinner).score++;
				endRoundBlock = true;
				roundsDone++;
				int bs = gameTeams.get(GameTeamEnum.BLUE).score;
				int rs = gameTeams.get(GameTeamEnum.RED).score;
				boolean q = bs >= 5 || rs >= 5 ? endGame() : false;
				if (!q) {
					for (Player p : w.getPlayers()) {
						p.sendMessage(GameTeamEnum.getColour(roundWinner) + "\u00a7l>>>>>=====+++++-----+++++=====<<<<<");
						p.sendMessage(GameTeamEnum.getColour(roundWinner) + "\u00a7l      " + roundWinner.toString() + " won the round.");
						p.sendMessage(GameTeamEnum.getColour(roundWinner) + "\u00a7l>>>>>=====+++++-----+++++=====<<<<<");
						if (!gameTeams.get(GameTeamEnum.SPECTATOR).members.containsKey(p)) {
							if (gameTeams.get(roundWinner).members.containsKey(p)) {
								p.incrementStatistic(Statistic.CRAFT_ITEM, Material.DIAMOND_SPADE);
								gameTeams.get(roundWinner).members.put(p, gameTeams.get(roundWinner).members.get(p) + 2000);
								p.sendMessage("\u00a77Received \u00a7a$3500\u00a77 for winning the round.");
							} else {
								p.sendMessage("\u00a77Received \u00a7a$1500\u00a77 for losing the round.");
							}
							if (gameTeams.get(GameTeamEnum.RED).members.containsKey(p)) {
								gameTeams.get(GameTeamEnum.RED).members.put(p, gameTeams.get(GameTeamEnum.RED).members.get(p) + 1500);
								balOb.getScore(p.getName()).setScore(gameTeams.get(GameTeamEnum.RED).members.get(p));
							}
							if (gameTeams.get(GameTeamEnum.BLUE).members.containsKey(p)) {
								gameTeams.get(GameTeamEnum.BLUE).members.put(p, gameTeams.get(GameTeamEnum.BLUE).members.get(p) + 1500);
								balOb.getScore(p.getName()).setScore(gameTeams.get(GameTeamEnum.BLUE).members.get(p));
							}
						}
					}
					loadObjectives();
					Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("HaloPlus"), new Runnable() {

						@Override
						public void run() {
							startRound();
							endRoundBlock = false;
						}
					}, 100);
				}
			}
		}
	}

	public boolean endGame() {
		if (!ended) {
			GameTeamEnum winningTeam;
			if (gameTeams.get(GameTeamEnum.BLUE).score > gameTeams.get(GameTeamEnum.RED).score || gameTeams.get(GameTeamEnum.RED).members.size() <= 0) {
				winningTeam = GameTeamEnum.BLUE;
			} else if (gameTeams.get(GameTeamEnum.RED).score > gameTeams.get(GameTeamEnum.BLUE).score || gameTeams.get(GameTeamEnum.BLUE).members.size() <= 0) {
				winningTeam = GameTeamEnum.RED;
			} else {
				return false;
			}
			for (Player p : w.getPlayers()) {
				SpecialChat.tell(p, new String[] { GameTeamEnum.getColour(winningTeam) + "\u00a7l>>>>>=====+++++-----+++++=====<<<<<", GameTeamEnum.getColour(winningTeam) + "\u00a7l      " + winningTeam.toString() + " won the game.",
						GameTeamEnum.getColour(winningTeam) + "\u00a7l>>>>>=====+++++-----+++++=====<<<<<" },
					GameTeamEnum.getColour(winningTeam));
				if (gameTeams.get(winningTeam).members.containsKey(p)) {
					p.incrementStatistic(Statistic.CRAFT_ITEM, Material.DIAMOND_SWORD);
				}
				HaloPlus.shoot.s.scheduleSyncDelayedTask(HaloPlus.shoot.h, new Runnable() {

					@Override
					public void run() {
						Bukkit.dispatchCommand(p, "stats");
					}
				}, 40);
				p.getInventory().clear();
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
				p.getInventory().setBoots(new ItemStack(Material.AIR));
				p.setGameMode(GameMode.ADVENTURE);
				p.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());
			}
			ended = true;
			ob.unregister();
			balOb.unregister();
			for (GameTeam gt : gameTeams.values()) {
				gt.scoreboardTeam.unregister();
			}
			gameTeams.clear();
			w.getWorldBorder().setSize(201, 1);
			unregister();
		}
		return true;
	}

	public void death(Player p) {
		endRound();
	}

	public void kill(Player k) {
		int prize = 900;
		GameTeam team = getTeam(k);
		if (team != null) {
			team.members.put(k, team.members.get(k) + prize);
			k.sendMessage("\u00a7aBalance> \u00a7eReceived $" + prize + " for a kill.");
		}
	}

	public void killAssist(Player k) {
		int prize = 400;
		GameTeam team = getTeam(k);
		if (team != null) {
			team.members.put(k, team.members.get(k) + prize);
			k.sendMessage("\u00a7aBalance> \u00a7eReceived $" + prize + " for a kill assist.");
		}
	}

	public int countAlive(GameTeamEnum team) {
		int c = 0;
		if (running && !ended) {
			for (Player p : gameTeams.get(team).members.keySet()) {
				if (p.getGameMode() == GameMode.ADVENTURE) {
					c++;
				}
			}
		}
		return c;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public boolean purchaseEvent(InventoryClickEvent e) {
		ItemStack clicked = e.getCurrentItem();
		if (clicked != null && clicked instanceof ItemStack && clicked.getType() != Material.AIR) {
			Player p = (Player) e.getWhoClicked();
			if (EnumWeapon.isWeapon(clicked.getType())) {
				Weapon w = EnumWeapon.toWeapon(EnumWeapon.toWeaponEnum(clicked.getType()));
				if (getTeam(p).members.get(p) - w.cost >= 0) {
					getTeam(p).members.put(p, getTeam(p).members.get(p) - w.cost);
					if (p.getInventory().getItem(WeaponType.toSlot(w.type)) instanceof ItemStack) {
						p.getWorld().dropItem(p.getLocation(), p.getInventory().getItem(WeaponType.toSlot(w.type)));
					}
					p.getInventory().setItem(WeaponType.toSlot(w.type), w.getItem());
					p.sendMessage("\u00a7aBalance> \u00a7eBought " + EnumWeapon.toWeaponEnum(e.getCurrentItem().getType()) + ", balance remaining: \u00a7a$" + getTeam(p).members.get(p) + "\u00a7e.");
					return true;
				}
			} else if (EnumGrenade.isGrenade(clicked.getType())) {
				Grenade g = EnumGrenade.toGrenade(EnumGrenade.toGrenadeEnum(clicked.getType()));
				if (getTeam(p).members.get(p) - g.cost >= 0) {
					getTeam(p).members.put(p, getTeam(p).members.get(p) - g.cost);
					if (p.getInventory().getItem(2) instanceof ItemStack) {
						p.getWorld().dropItem(p.getLocation(), p.getInventory().getItem(2));
					}
					p.getInventory().setItem(2, g.getItem());
					p.sendMessage("\u00a7aBalance> \u00a7eBought " + EnumGrenade.toGrenadeEnum(e.getCurrentItem().getType()) + ", balance remaining: \u00a7a$" + getTeam(p).members.get(p) + "\u00a7e.");
					return true;
				}
			}
		}
		return false;
	}

	public GameTeam getTeam(Player p) {
		return gameTeams.get(getGameTeam(p));
	}

	public boolean isInTeam(Player p) {
		return (getGameTeam(p) != GameTeamEnum.SPECTATOR) && (gameTeams.get(getGameTeam(p)) instanceof GameTeam);
	}

	public void sendCountDown(int seconds, Plugin h, BukkitScheduler s) {
		s.scheduleSyncDelayedTask(h, new Runnable() {

			@Override
			public void run() {
				for (Player p : w.getPlayers()) {
					SpecialChat.sendTitle(p, "\u00a75" + seconds, "\u00a7dcount down", 1, 15, 1);
					p.playSound(p.getEyeLocation(), Sound.ORB_PICKUP, 2, 2);
				}
			}
		}, (15 - seconds) * 20);
	}
}
