package io.github.loldatsec.mcplugs.halocore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class DeathBattle {
	public List<String> tracked = new ArrayList<String>();
	public JavaPlugin mainPlugin = null;
	public Weapons Weapons = null;

	public DeathBattle(JavaPlugin p) {
		this.mainPlugin = p;
		this.Weapons = new Weapons();
	}

	public void highDamageEvent(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			if (tracked.contains(((Player) e.getEntity()).getName())) {
				e.setCancelled(false);
			}
		}
	}

	public void highDeathEvent(PlayerDeathEvent e) {
		if (tracked.contains(e.getEntity().getName())) {
			e.getEntity().setGameMode(GameMode.SPECTATOR);
			e.getEntity().sendMessage("§4§l>>>>>>>====----====<<<<<<<");
			e.getEntity()
					.sendMessage(
							"§cYou lost this game of deathbattle, if you wanna leave spectating, do §6/spawn§c.\n§7Your place: §6#"
									+ tracked.size());
			e.getEntity().sendMessage("§4§l>>>>>>>====----====<<<<<<<");
			Title.send(e.getEntity(),
					"§7You died ranking §6#" + tracked.size(),
					"§6Type §b/spawn §6to leave the arena.", 10, 80, 10);
			Weapons.equipArmor(e.getEntity());
			tracked.remove(e.getEntity().getName());
			checkEndGame();
		}
	}

	public void highLogoutEvent(PlayerQuitEvent e) {
		if (tracked.contains(e.getPlayer().getName())) {
			tracked.remove(e.getPlayer().getName());
			Chat.bc("§2"
					+ e.getPlayer().getName()
					+ " §achickened during a deathbattle game and logged out lol!");
			checkEndGame();
		}
	}

	public void sendCommandEvent(PlayerCommandPreprocessEvent e) {
		if (tracked.contains(e.getPlayer().getName())) {
			e.getPlayer().sendMessage("§aYou are in a DeathBattle game!");
			e.setCancelled(true);
		}
	}

	public void startGame(CommandSender sender) {
		if (sender.hasPermission("halocore.startdeathbattle")) {
			if (tracked.size() > 0) {
				sender.sendMessage("§cUh oh, there is a game going on! Remaining players: §d"
						+ tracked.toString());
			} else {
				Chat.bc("§7[§6Halo§7]§6 Deathbattle starting in §e5 §6seconds.");
				delayExec(new Runnable() {
					public void run() {
						Chat.bc("§7[§6Halo§7]§6 Deathbattle starting in §e4 §6seconds.");
					}
				}, 20);
				delayExec(new Runnable() {
					public void run() {
						Chat.bc("§7[§6Halo§7]§6 Deathbattle starting in §e3 §6seconds.");
					}
				}, 40);
				delayExec(new Runnable() {
					public void run() {
						Chat.bc("§7[§6Halo§7]§6 Deathbattle starting in §e2 §6seconds.");
					}
				}, 60);
				delayExec(new Runnable() {
					public void run() {
						Chat.bc("§7[§6Halo§7]§6 Deathbattle starting in §e1 §6second.");
					}
				}, 80);
				delayExec(new Runnable() {
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								"mv load Arena");
						Chat.bc("§7[§6Halo§7]§6 A game of deathbattle has started!");
						tracked = new ArrayList<String>();
						for (Player p : Bukkit.getOnlinePlayers()) {
							double xfactor = Math.random() * 40 - 20;
							double zfactor = Math.random() * 40 - 20;
							if (!p.hasPermission("halocore.exemptdeathbattle")) {
								p.setGameMode(GameMode.SURVIVAL);
								p.teleport(Bukkit.getWorld("Arena")
										.getSpawnLocation()
										.add(xfactor, 4, zfactor));
								p.setVelocity(new Vector(0, 0, 0));
								p.sendMessage("§aYou have been summoned to the deathbattle arena!");
								tracked.add(p.getName());
								Weapons.clearInv(p);
								for (PotionEffect effect : p
										.getActivePotionEffects()) {
									p.removePotionEffect(effect.getType());
								}
								p.setHealth(20);
								p.setFoodLevel(20);
								p.getInventory().setItem(0, Weapons.pistol());
								p.getInventory().setItem(1, Weapons.dagger());
								Weapons.equipArmor(p);
								p.updateInventory();
							}
						}
					}
				}, 100);
			}
		} else {
			sender.sendMessage("§cYou dont have permission to start a deathbattle game!");
		}
	}

	public void checkEndGame() {
		List<String> newtrack = new ArrayList<String>();
		for (Player p : Bukkit.getWorld("Arena").getPlayers()) {
			if (tracked.contains(p.getName())) {
				try {
					if (p.getGameMode() == GameMode.SURVIVAL) {
						newtrack.add(p.getName());
					}
				} catch (NullPointerException npe) {
				}
			}
		}
		tracked = newtrack;
		if (tracked.size() <= 1) {
			String wpn = "";
			for (String pn : tracked) {
				wpn = pn;
			}
			for (Player p : Bukkit.getWorld("Arena").getPlayers()) {
				p.setGameMode(GameMode.SURVIVAL);
			}
			Player winner = Bukkit.getPlayerExact(wpn);
			winner.incrementStatistic(Statistic.PLAYER_KILLS, 150);
			winner.incrementStatistic(Statistic.CRAFT_ITEM,
					Material.DIAMOND_SPADE, 150);
			Chat.bc("§2"
					+ winner.getName()
					+ "§e["
					+ winner.getStatistic(Statistic.CRAFT_ITEM,
							Material.DIAMOND_SPADE)
					+ "] §awon this round of deathbattle and received §e[+150] §agame points!");
			tracked.clear();
			for (Player p : Bukkit.getWorld("Arena").getPlayers()) {
				Weapons.clearInv(p);
				p.setGameMode(GameMode.SURVIVAL);
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv unload Arena");
		}
	}

	public void delayExec(Runnable r, long delay) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, r, delay);
	}
}
