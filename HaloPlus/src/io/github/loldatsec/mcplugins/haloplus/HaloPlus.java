package io.github.loldatsec.mcplugins.haloplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.loldatsec.mcplugins.haloplus.game.Game;
import io.github.loldatsec.mcplugins.haloplus.game.GameManager;
import io.github.loldatsec.mcplugins.haloplus.game.GameManagerDisplayInterpreter;
import io.github.loldatsec.mcplugins.haloplus.grenades.EnumGrenade;
import io.github.loldatsec.mcplugins.haloplus.grenades.Frag_Grenade;
import io.github.loldatsec.mcplugins.haloplus.grenades.Grenade;
import io.github.loldatsec.mcplugins.haloplus.grenades.Molotov_Cocktail;
import io.github.loldatsec.mcplugins.haloplus.listeners.GeneralListener;
import io.github.loldatsec.mcplugins.haloplus.listeners.PurchaseListener;
import io.github.loldatsec.mcplugins.haloplus.listeners.ShootingListener;
import io.github.loldatsec.mcplugins.haloplus.utils.BarrierKnockbackVector;
import io.github.loldatsec.mcplugins.haloplus.utils.RankupSequence;
import io.github.loldatsec.mcplugins.haloplus.utils.SpecialChat;
import io.github.loldatsec.mcplugins.haloplus.weapons.AK101;
import io.github.loldatsec.mcplugins.haloplus.weapons.AK47;
import io.github.loldatsec.mcplugins.haloplus.weapons.AWP;
import io.github.loldatsec.mcplugins.haloplus.weapons.Desert_Eagle;
import io.github.loldatsec.mcplugins.haloplus.weapons.EnumWeapon;
import io.github.loldatsec.mcplugins.haloplus.weapons.Glock17;
import io.github.loldatsec.mcplugins.haloplus.weapons.HK416;
import io.github.loldatsec.mcplugins.haloplus.weapons.M4_Carbine;
import io.github.loldatsec.mcplugins.haloplus.weapons.M82;
import io.github.loldatsec.mcplugins.haloplus.weapons.MAUL;
import io.github.loldatsec.mcplugins.haloplus.weapons.P99;
import io.github.loldatsec.mcplugins.haloplus.weapons.Spas12;
import io.github.loldatsec.mcplugins.haloplus.weapons.USP;
import io.github.loldatsec.mcplugins.haloplus.weapons.Weapon;

public class HaloPlus extends JavaPlugin {

	public static int playersToStartGame = 2; // Abs. Minimum
	public static HaloPlus halo;
	public static ShootingListener shoot;
	public static GeneralListener gen;
	public static PurchaseListener wb;
	public static Map<EnumWeapon, Weapon> weapons = new HashMap<EnumWeapon, Weapon>();
	public static HaloCommands cmd;
	public static List<Player> vanish = new ArrayList<Player>();
	public static Map<EnumGrenade, Grenade> grenades = new HashMap<EnumGrenade, Grenade>();

	public HaloPlus getHalo() {
		return halo;
	}

	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.getWorld().getName().equalsIgnoreCase("Lobby")) {
				p.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());
				SpecialChat.tell(p, new String[] { "\u00a7c\u00a7lGame halted due to reload." }, ChatColor.DARK_RED);
			}
		}
	}

	public void onEnable() {
		registerWeapons();
		registerGrenades();
		HaloPlus.shoot = new ShootingListener();
		Bukkit.getPluginManager().registerEvents(shoot, this);
		HaloPlus.gen = new GeneralListener();
		Bukkit.getPluginManager().registerEvents(gen, this);
		HaloPlus.wb = new PurchaseListener();
		Bukkit.getPluginManager().registerEvents(wb, this);
		HaloPlus.cmd = new HaloCommands();
		getCommand("spectate").setExecutor(cmd);
		getCommand("spec").setExecutor(cmd);
		getCommand("view").setExecutor(cmd);
		getCommand("gmc").setExecutor(cmd);
		getCommand("gms").setExecutor(cmd);
		getCommand("vanish").setExecutor(cmd);// XXX
		getCommand("leave").setExecutor(cmd);
		getCommand("start").setExecutor(cmd);
		getCommand("game").setExecutor(cmd);
		getCommand("games").setExecutor(cmd);
		getCommand("stat").setExecutor(cmd);
		getCommand("stats").setExecutor(cmd);
		getCommand("onvoteaction").setExecutor(cmd);
		HaloPlus.halo = this;
		for (World w : Bukkit.getWorlds()) {
			if (!w.getName().equalsIgnoreCase("Lobby")) {
				new Game(w);
			}
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getWorld().getName().equalsIgnoreCase("Lobby")) {
						p.setCanPickupItems(true);
						if (GameManager.immoveable.keySet().contains(p)) {
							GameManager.immoveable.remove(p);
						}
					}
				}
				int x = ((int) (Bukkit.getOnlinePlayers().size() / 10)) * 6;
				playersToStartGame = x > 2 ? x : 2;
				GameManager.scheduler();
			}
		}, 0, 100);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				GameManagerDisplayInterpreter.updateScoreboard();
				for (Player p : Bukkit.getOnlinePlayers()) {
					for (Player ep : Bukkit.getOnlinePlayers()) {
						if (!vanish.contains(ep)) {
							if (p.getWorld().equals(ep.getWorld()) || p.getWorld().getName().equalsIgnoreCase("Lobby")) {
								p.showPlayer(ep);
							} else {
								p.hidePlayer(ep);
							}
						} else {
							p.hidePlayer(ep);
						}
					}
					if (vanish.contains(p)) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 140, 1));
					}
				}
				World lob = Bukkit.getWorld("Lobby");
				GameManager.scheduler();
				GameManagerDisplayInterpreter.updateInventories();
				ItemStack air = new ItemStack(Material.AIR);
				for (Player p : lob.getPlayers()) {
					p.setHealth(20);
					if (p.getGameMode() == GameMode.ADVENTURE) {
						p.setScoreboard(GameManagerDisplayInterpreter.score);
						p.getInventory().clear();
						p.getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
					}
				}
			}
		}, 0, 20);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					Location spawn = w.getWorldBorder().getCenter();
					double sx = spawn.getX();
					double sz = spawn.getZ();
					double wb = w.getWorldBorder().getSize() / 2 - 1;
					double swb = wb - 3;
					for (Player p : w.getPlayers()) {
						boolean doKb = false;
						double px = p.getLocation().getX();
						double pz = p.getLocation().getZ();
						if (px < sx && px + wb < sx) {
							doKb = true;
						} else if (px > sx && px - wb > sx) {
							doKb = true;
						} else if (pz < sz && pz + wb < sz) {
							doKb = true;
						} else if (pz > sz && pz - wb > sz) {
							doKb = true;
						}
						boolean doAlert = false;
						if (px < sx && px + swb < sx) {
							doAlert = true;
						} else if (px > sx && px - swb > sx) {
							doAlert = true;
						} else if (pz < sz && pz + swb < sz) {
							doAlert = true;
						} else if (pz > sz && pz - swb > sz) {
							doAlert = true;
						}
						if (doKb) {
							p.setVelocity(new BarrierKnockbackVector(p.getLocation()).toVector());
							p.playNote(p.getEyeLocation(), Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
							if (!w.getName().equalsIgnoreCase("Lobby")) {
								shoot.damagePlayerWorldBorder(p, 3);
							}
						} else if (doAlert) {
							SpecialChat.sendTitle(p, "\u00a74WorldBorder Alert!", "\u00a7cLess than 3 blocks away.", 2, 3, 2);
						}
					}
				}
			}
		}, 0, 5);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					if (w.getName().equalsIgnoreCase("Lobby")) {
						w.getWorldBorder().setSize(99, 1);
					} else {
						w.getWorldBorder().setSize(201, 1);
					}
					for (Entity e : w.getEntities()) {
						if (!(e instanceof Player)) {
							e.remove();
						}
					}
				}
			}
		}, 1);
	}

	public static int getLevel(Player p) {
		return RankupSequence.getPosByLong(getXP(p));
	}

	public static long getXP(Player p) {
		return p.getStatistic(Statistic.PLAYER_KILLS) + (p.getStatistic(Statistic.CRAFT_ITEM, Material.DIAMOND_SPADE) * 3) + (p.getStatistic(Statistic.CRAFT_ITEM, Material.DIAMOND_SPADE) * 15)
			+ (p.getStatistic(Statistic.CRAFT_ITEM, Material.GOLD_SPADE) * 500);
	}

	public static void deleteShotsData(Player p) {
		deleteShotsData(p.getName());
	}

	public static void deleteShotsData(String p) {
		for (Map<String, Integer> d : shoot.shots.values()) {
			if (d.containsKey(p)) {
				d.remove(p);
			}
		}
	}

	public static void deleteReloadData(Player p) {
		deleteReloadData(p.getName());
	}

	public static void deleteReloadData(String p) {
		if (shoot.reload.containsKey(p)) {
			shoot.reload.remove(p);
		}
	}

	public void registerWeapons() {
		// Remove to disable
		new AK101();
		new AK47();
		new AWP();
		new Desert_Eagle();
		new Glock17();
		new HK416();
		new M4_Carbine();
		new M82();
		new MAUL();
		new P99();
		new Spas12();
		new USP();
	}

	public void registerGrenades() {
		// Remove to disable
		new Molotov_Cocktail();
		new Frag_Grenade();
	}

	public static long getReloadSpeed(Player p, double reload) {
		long t = (long) (reload * 20 - getLevel(p) * (reload / 10));
		return (long) (t > reload * 12 ? t : reload * 12);
	}
}
