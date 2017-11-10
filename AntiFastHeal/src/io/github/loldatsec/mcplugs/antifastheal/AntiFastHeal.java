package io.github.loldatsec.mcplugs.antifastheal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class AntiFastHeal extends JavaPlugin implements Listener {

	public static final String prefix = "\u00a7cAFH: \u00a77";
	private Map<String, Integer> track = new HashMap<String, Integer>();
	private Map<String, List<Integer>> trackR = new HashMap<String, List<Integer>>();
	private Map<String, Integer> strack = new HashMap<String, Integer>();
	private Map<String, List<Integer>> strackR = new HashMap<String, List<Integer>>();
	private Map<String, List<Location>> mtrack = new HashMap<String, List<Location>>();
	private Map<String, Location> ground = new HashMap<String, Location>();
	private Map<String, Boolean> lowjump = new HashMap<String, Boolean>();
	private Map<String, Integer> tolerence = new HashMap<String, Integer>();

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (String n : track.keySet()) {
					if (track.get(n) > 15) {
						if (!trackR.containsKey(n)) {
							trackR.put(n, new ArrayList<Integer>());
						}
						trackR.get(n).add(track.get(n));
						String msg = prefix + n + " has a unusual " + track.get(n) + "healspeed. DATA:" + trackR.get(n) + "/80ticks.";
						Bukkit.getConsoleSender().sendMessage(msg);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.isOp() || p.hasPermission("afh.viewlogger")) {
								p.sendMessage(msg);
							}
						}
						if (trackR.get(n).size() % 3 == 0) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + n + " hacks detected: scheduler$0.AFH[0$] 100% Accurate. DATA:" + trackR.get(n).toString());
						}
					}
				}
				track = new HashMap<String, Integer>();
			}
		}, 0L, 60L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (String n : strack.keySet()) {
					if (strack.get(n) > 15) {
						if (!strackR.containsKey(n)) {
							strackR.put(n, new ArrayList<Integer>());
						}
						strackR.get(n).add(strack.get(n));
						String msg = prefix + n + " has a unusual " + strack.get(n) + "atk/s. DATA:" + strackR.get(n);
						Bukkit.getConsoleSender().sendMessage(msg);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.isOp() || p.hasPermission("afh.viewlogger")) {
								p.sendMessage(msg);
							}
						}
						if (strackR.get(n).size() % 3 == 0) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + n + " hacks detected: scheduler$0.AFH[1$] 100% Accurate. DATA:" + strackR.get(n).toString());
						}
					}
				}
				strack = new HashMap<String, Integer>();
			}
		}, 0L, 20L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (String n : mtrack.keySet()) {
					if (mtrack.get(n).size() > 1) {
						Location from = mtrack.get(n).get(0);
						Location to = mtrack.get(n).get(mtrack.get(n).size() - 1);
						double dist = Math.sqrt(Math.pow(Math.abs(to.getX() - from.getX()), 2) + Math.pow(Math.abs(to.getZ() - from.getZ()), 2));
						Player speed = Bukkit.getPlayer(n);
						if (speed instanceof Player) {
							if (!speed.isFlying()) {
								if (dist >= (tolerence.containsKey(n) ? tolerence.get(n) + 6 : 6)) {
									from.add(0, 2, 0);
									speed.teleport(from);
									if (dist >= (tolerence.containsKey(n) ? tolerence.get(n) + 8 : 8)) {
										String msg = "\u00a7cAFH: \u00a77" + n + " moved " + ((float) (dist * 1000000000) / 1000000000) + " metres in 0.5 second.";
										Bukkit.getConsoleSender().sendMessage(msg);
										for (Player p : Bukkit.getOnlinePlayers()) {
											if (p.isOp() || p.hasPermission("afh.viewlogger")) {
												p.sendMessage(msg);
											}
										}
									}
								}
							}
						}
					}
				}
				mtrack.clear();
				tolerence.clear();
			}
		}, 0L, 10L);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onHeal(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player) {
			String n = ((Player) e.getEntity()).getName();
			if (!((Player) e.getEntity()).hasPotionEffect(PotionEffectType.REGENERATION)) {
				if (track.containsKey(n)) {
					track.put(n, track.get(n) + 1);
					e.setCancelled(true);
				} else {
					track.put(n, 0);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			String n = ((Player) e.getDamager()).getName();
			if (strack.containsKey(n)) {
				strack.put(n, strack.get(n) + 1);
				if (strack.get(n) >= 15) {
					e.setCancelled(true);
				}
			} else {
				strack.put(n, 0);
			}
			if (e.getEntity() instanceof Player) {
				if (((Player) e.getDamager()).getInventory().getItemInHand() instanceof ItemStack) {
					tolerence.put(((Player) e.getEntity()).getName(), ((Player) e.getDamager()).getInventory().getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onMove(PlayerMoveEvent e) {
		String n = e.getPlayer().getName();
		if (!mtrack.containsKey(n)) {
			mtrack.put(n, new ArrayList<Location>());
		}
		if (e.getPlayer().isOnGround() || !ground.containsKey(n)) {
			ground.put(n, e.getTo());
			if (lowjump.containsKey(n) && lowjump.get(n)) {
				// Woops
			} else {
				lowjump.remove(n);
			}
		} else if (!e.getPlayer().isFlying()) {
			if (e.getTo().getY() - 3 > ground.get(n).getY()) {
				e.getPlayer().teleport(ground.get(n));
				String msg = "\u00a7cAFH: \u00a77" + n + " moved vertical 3 blocks upwards.";
				Bukkit.getConsoleSender().sendMessage(msg);
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.isOp() || p.hasPermission("afh.viewlogger")) {
						p.sendMessage(msg);
					}
				}
			} else if (e.getTo().getY() - 0.2 < ground.get(n).getY() && lowjump.containsKey(n) ? lowjump.get(n) : true) {
				lowjump.put(n, true);
			} else if (e.getTo().getY() - 0.2 > ground.get(n).getY()) {
				lowjump.put(n, false);
			}
		} else {
			ground.put(e.getPlayer().getName(), e.getTo());
		}
		mtrack.get(n).add(e.getTo());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onTeleport(PlayerTeleportEvent e) {
		mtrack.put(e.getPlayer().getName(), new ArrayList<Location>());
		ground.put(e.getPlayer().getName(), e.getTo());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onTeleport(PlayerVelocityEvent e) {
		mtrack.put(e.getPlayer().getName(), new ArrayList<Location>());
	}
}
