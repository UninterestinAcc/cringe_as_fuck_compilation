package io.github.loldatsec.mcplugins.haloplus.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.game.GameManager;
import io.github.loldatsec.mcplugins.haloplus.game.GameTeamEnum;
import io.github.loldatsec.mcplugins.haloplus.grenades.EnumGrenade;
import io.github.loldatsec.mcplugins.haloplus.grenades.Grenade;
import io.github.loldatsec.mcplugins.haloplus.utils.ExclusiveList;
import io.github.loldatsec.mcplugins.haloplus.utils.Reload;
import io.github.loldatsec.mcplugins.haloplus.utils.SpecialChat;
import io.github.loldatsec.mcplugins.haloplus.weapons.EnumWeapon;
import io.github.loldatsec.mcplugins.haloplus.weapons.Weapon;

public class ShootingListener implements Listener {

	// TODO KillAssist
	public Map<Player, Location> valid = new HashMap<Player, Location>();
	public BukkitScheduler s;
	public Plugin h;
	public Map<String, Long> lastShotMillis = new HashMap<String, Long>();
	public Map<String, Long> lastRightClickMillis = new HashMap<String, Long>();
	public Map<EnumWeapon, Map<String, Integer>> shots = new HashMap<EnumWeapon, Map<String, Integer>>();
	public Map<String, Reload> reload = new HashMap<String, Reload>();
	public ExclusiveList<UUID> markedBullets = new ExclusiveList<UUID>();

	public ShootingListener() {
		s = Bukkit.getScheduler();
		h = Bukkit.getPluginManager().getPlugin("HaloPlus");
		/**
		 * Reload & shoot
		 */
		s.scheduleSyncRepeatingTask(h, new Runnable() {

			@Override
			public void run() {
				List<String> isReloading = new ArrayList<String>();
				List<String> removeReloading = new ArrayList<String>();
				for (String s : reload.keySet()) {
					isReloading.add(s);
					reload.get(s).timeleftDecrement();
					Player p = Bukkit.getPlayer(s);
					if (p instanceof Player && p.getGameMode() == GameMode.ADVENTURE && !(GameManager.immoveable.keySet().contains(p) && GameManager.immoveable.get(p).equals(p.getWorld()))) {
						if (p.getItemInHand() instanceof ItemStack) {
							if (p.getItemInHand().getType() instanceof Material) {
								Weapon w = HaloPlus.weapons.get(EnumWeapon.toWeaponEnum(p.getItemInHand().getType()));
								if (w instanceof Weapon && w.equals(reload.get(s).weapon)) {
									long r = reload.get(s).timeleft;
									if (r <= 0) {
										if (!shots.containsKey(w.weaponEnum)) {
											shots.put(w.weaponEnum, new HashMap<String, Integer>());
										}
										shots.get(w.weaponEnum).put(s, w.clipSize);
										removeReloading.add(s);
									} else {
										SpecialChat.sendProgress(p, (int) r, (int) w.reload * 20, "\u00a76Reload: ");
									}
									continue;
								}
							}
						}
						SpecialChat.sendActionBarChat(p, "\u00a74\u00a7lReload cancelled.");
						removeReloading.add(s);
					} else {
						removeReloading.add(s);
					}
				}
				for (String s : removeReloading) {
					reload.remove(s);
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getGameMode() == GameMode.ADVENTURE && !(GameManager.immoveable.keySet().contains(p) && GameManager.immoveable.get(p).equals(p.getWorld()))) {
						if (p.getItemInHand() instanceof ItemStack) {
							if (p.getItemInHand().getType() instanceof Material) {
								Weapon w = HaloPlus.weapons.get(EnumWeapon.toWeaponEnum(p.getItemInHand().getType()));
								if (w instanceof Weapon) {
									int sl = shots.containsKey(w.weaponEnum) && shots.get(w.weaponEnum).containsKey(p.getName()) ? shots.get(w.weaponEnum).get(p.getName()) : w.clipSize;
									if (!isReloading.contains(p.getName())) {
										SpecialChat.sendActionBarChat(p, "\u00a7e" + sl + "\u00a76 / \u00a7a" + w.clipSize);
									}
									if (!lastShotMillis.containsKey(p.getName()) || lastShotMillis.get(p.getName()) <= System.currentTimeMillis() - (w.speed * 1000)) {
										if (lastRightClickMillis.containsKey(p.getName()) && lastRightClickMillis.get(p.getName()) >= System.currentTimeMillis() - 200) {
											if (!shots.containsKey(w.weaponEnum)) {
												shots.put(w.weaponEnum, new HashMap<String, Integer>());
											}
											if (!isReloading.contains(p.getName()) && sl > 0 && w.onShoot(p)) {
												if (shots.get(w.weaponEnum).containsKey(p.getName())) {
													shots.get(w.weaponEnum).put(p.getName(), shots.get(w.weaponEnum).get(p.getName()) - 1);
												} else {
													shots.get(w.weaponEnum).put(p.getName(), w.clipSize - 1);
												}
												lastShotMillis.put(p.getName(), System.currentTimeMillis());
											}
											if (sl <= 1) {
												doReload(p, w);
											}
										}
									}
								}
								// TODO Hook grenade
							}
						}
					}
				}
				List<String> remove = new ArrayList<String>();
				for (String pn : lastRightClickMillis.keySet()) {
					if (lastRightClickMillis.get(pn) <= System.currentTimeMillis() - 200) {
						remove.add(pn);
					}
				}
				for (String r : remove) {
					lastRightClickMillis.remove(r);
				}
				for (World w : Bukkit.getWorlds()) {
					if (!w.getName().equalsIgnoreCase("Lobby")) {
						for (Item i : w.getEntitiesByClass(Item.class)) {
							if (EnumGrenade.isGrenade(i.getItemStack().getType())) {
								EnumGrenade gren = EnumGrenade.toGrenadeEnum(i.getItemStack().getType());
								if (i.isOnGround()) {
									if (i.getCustomName() != null && i.getCustomName() != "") {
										Grenade.onExplode(i.getLocation(), gren, Bukkit.getPlayer(i.getCustomName()));
										i.remove();
									}
								}
							}
						}
					}
				}
			}
		}, 0, 1);
		/**
		 * Garbage Cleaner
		 */
		s.scheduleSyncRepeatingTask(h, new Runnable() {

			@Override
			public void run() {
				for (String pn : reload.keySet()) {
					if (!(Bukkit.getPlayer(pn) instanceof Player)) {
						reload.remove(pn);
					}
				}
				for (EnumWeapon ew : shots.keySet()) {
					for (String pn : shots.get(ew).keySet()) {
						if (!(Bukkit.getPlayer(pn) instanceof Player)) {
							reload.remove(pn);
						}
					}
				}
				ExclusiveList<Player> removeme = new ExclusiveList<Player>();
				for (Player p : valid.keySet()) {
					if (!p.isOnline()) {
						removeme.add(p);
					}
				}
				for (Player p : removeme.list) {
					valid.remove(p);
				}
				markedBullets.clear();
			}
		}, 0, 300);
		s.scheduleSyncRepeatingTask(h, new Runnable() {

			@Override
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					if (!w.getName().equalsIgnoreCase("Lobby")) {
						for (Projectile bullet : w.getEntitiesByClass(Projectile.class)) {
							for (Player p : w.getPlayers()) {
								if (p.getWorld().equals(bullet.getWorld())) {
									if (p.getLocation().add(0, 1, 0).distance(bullet.getLocation()) <= 1.4) {
										if (!markedBullets.contains(bullet.getUniqueId())) {
											if (bullet.getShooter() instanceof Player && !p.getName().equalsIgnoreCase(((Player) bullet.getShooter()).getName())) {
												double health = p.getHealth();
												damagePlayerShoot(p, bullet);
												if (health > p.getHealth()) {
													markedBullets.add(bullet.getUniqueId());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}, 0, 1);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			lastRightClickMillis.put(p.getName(), System.currentTimeMillis());
			Grenade.onThrow(p);
		} else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (p.getItemInHand() instanceof ItemStack) {
				if (p.getItemInHand().getType() instanceof Material) {
					Weapon w = HaloPlus.weapons.get(EnumWeapon.toWeaponEnum(p.getItemInHand().getType()));
					if (w instanceof Weapon) {
						doReload(p, w);
					}
				}
			}
		}
	}

	@EventHandler
	public void damageE2E(EntityDamageByEntityEvent e) {
		e.setCancelled(true);
		if (!e.getEntity().getWorld().getName().equalsIgnoreCase("Lobby")) {
			if (e.getDamager() instanceof LivingEntity) {
				// No Melee
			} else if (e.getEntity() instanceof Player) {
				if (e.getDamager() instanceof Projectile) {
					Projectile bullet = (Projectile) e.getDamager();
					if (!markedBullets.contains(bullet.getUniqueId())) {
						double health = ((Player) e.getEntity()).getHealth();
						damagePlayerShoot((Player) e.getEntity(), bullet);
						if (health > ((Player) e.getEntity()).getHealth()) {
							markedBullets.add(bullet.getUniqueId());
						}
					}
				}
			}
		}
	}

	public static boolean isValidDamage(Player p, Player shooter) {
		return GameManager.getGameTeam(shooter) != GameManager.getGameTeam(p) && p.getGameMode() == GameMode.ADVENTURE;
	}

	@EventHandler
	public void damageEvent(EntityDamageEvent e) {
		if (e.getEntity().getWorld().getName().equalsIgnoreCase("Lobby")) {
			e.setCancelled(true);
		} else {
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				if ((GameManager.immoveable.keySet().contains(p) && GameManager.immoveable.get(p).equals(p.getWorld())) || e.getEntity().getWorld().getName().equalsIgnoreCase("Lobby")) {
					e.setCancelled(true);
				} else {
					if (e.getCause() != DamageCause.ENTITY_ATTACK && e.getCause() != DamageCause.PROJECTILE && e.getCause() != DamageCause.THORNS) {
						boolean dieFrom = p.getHealth() - e.getDamage() <= 0;
						if (dieFrom) {
							e.setCancelled(true);
							World w = p.getWorld();
							Location eye = p.getEyeLocation();
							for (ItemStack i : ((Player) p).getInventory().getContents()) {
								if (i instanceof ItemStack) {
									w.dropItem(eye, i);
								}
							}
							((Player) p).getInventory().clear();
							ItemStack air = new ItemStack(Material.AIR);
							((Player) p).getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
							((Player) p).setGameMode(GameMode.SPECTATOR);
							((Player) p).playSound(p.getEyeLocation(), Sound.ENDERDRAGON_HIT, 1, 15);
							SpecialChat.sendTitle((Player) p, "\u00a77You died.", "", 5, 50, 5);
							p.setHealth(20);
							sendDeathMessage((Player) p, e.getCause().toString(), null, null);
							((Player) p).incrementStatistic(Statistic.DEATHS);
							GameManager.sendDeathEvent(p);
						}
					}
				}
			}
		}
	}

	public void damagePlayerWorldBorder(Player p, double dmg) {
		boolean dieFrom = p.getHealth() - dmg <= 0;
		if (dieFrom) {
			World w = p.getWorld();
			Location eye = p.getEyeLocation();
			for (ItemStack i : ((Player) p).getInventory().getContents()) {
				if (i instanceof ItemStack) {
					w.dropItem(eye, i);
				}
			}
			((Player) p).getInventory().clear();
			ItemStack air = new ItemStack(Material.AIR);
			((Player) p).getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
			((Player) p).setGameMode(GameMode.SPECTATOR);
			((Player) p).playSound(p.getEyeLocation(), Sound.ENDERDRAGON_HIT, 1, 15);
			SpecialChat.sendTitle((Player) p, "\u00a77You died.", "", 5, 50, 5);
			p.setHealth(20);
			sendDeathMessage((Player) p, "World Border", null, null);
			((Player) p).incrementStatistic(Statistic.DEATHS);
			GameManager.sendDeathEvent(p);
		} else {
			p.damage(dmg);
		}
	}

	public void damagePlayerGrenade(Player p, double dmg, Player thrower) {
		if (isValidDamage(p, thrower)) {
			boolean dieFrom = p.getHealth() - dmg <= 0;
			if (dieFrom) {
				World w = p.getWorld();
				Location eye = p.getEyeLocation();
				for (ItemStack i : ((Player) p).getInventory().getContents()) {
					if (i instanceof ItemStack) {
						w.dropItem(eye, i);
					}
				}
				((Player) p).getInventory().clear();
				ItemStack air = new ItemStack(Material.AIR);
				((Player) p).getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
				((Player) p).setGameMode(GameMode.SPECTATOR);
				((Player) p).playSound(p.getEyeLocation(), Sound.ENDERDRAGON_HIT, 1, 15);
				SpecialChat.sendTitle((Player) p, "\u00a77You died.", "", 5, 50, 5);
				p.setHealth(20);
				if (thrower == null) {
					sendDeathMessage((Player) p, "a stray grenade", null, null);
				} else {
					sendDeathMessage((Player) p, thrower.getName(), "a grenade", null);
					thrower.incrementStatistic(Statistic.PLAYER_KILLS);
					GameManager.sendKillEvent(thrower);
				}
				((Player) p).incrementStatistic(Statistic.DEATHS);
				GameManager.sendDeathEvent(p);
			} else {
				p.damage(dmg);
			}
		}
	}

	public void damagePlayerShoot(Player p, Projectile bullet) {
		EnumWeapon eWeapon = EnumWeapon.fromString(bullet.getName());
		if (eWeapon != null) {
			Weapon weapon = EnumWeapon.toWeapon(eWeapon);
			double dmg = weapon.damage - 6;
			Map<String, String> kmd = new HashMap<String, String>();
			int armorPenetration = 100;
			for (ItemStack i : p.getInventory().getArmorContents()) {
				if (i instanceof ItemStack) {
					if (i.getType().toString().startsWith("DIAMOND")) {
						armorPenetration -= 20;
					} else if (i.getType().toString().startsWith("IRON")) {
						armorPenetration -= 12;
					} else if (i.getType().toString().startsWith("LEATHER")) {
						armorPenetration -= 7;
					}
				}
			}
			dmg = armorPenetration * dmg / 100;
			kmd.put("Dmg", dmg + "");
			kmd.put("ArmorPenetration", armorPenetration + "%");
			if (bullet.getLocation().getY() >= p.getEyeLocation().getY() - 0.3) {
				dmg += 5;
				kmd.put("Headshot", "+5");
			}
			Player shooter = (Player) bullet.getShooter();
			if (isValidDamage(p, shooter)) {
				boolean dieFrom = p.getHealth() - dmg <= 0;
				if (dieFrom) {
					World w = p.getWorld();
					Location eye = p.getEyeLocation();
					for (ItemStack i : ((Player) p).getInventory().getContents()) {
						if (i instanceof ItemStack) {
							w.dropItem(eye, i);
						}
					}
					((Player) p).getInventory().clear();
					ItemStack air = new ItemStack(Material.AIR);
					((Player) p).getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
					((Player) p).setGameMode(GameMode.SPECTATOR);
					((Player) p).playSound(p.getEyeLocation(), Sound.ENDERDRAGON_HIT, 1, 15);
					SpecialChat.sendTitle((Player) p, "\u00a77You died.", "", 5, 50, 5);
					p.setHealth(20);
					sendDeathMessage((Player) p, shooter.getName(), eWeapon.toString(), kmd);
					shooter.incrementStatistic(Statistic.PLAYER_KILLS);
					GameManager.sendKillEvent(shooter);
					((Player) p).incrementStatistic(Statistic.DEATHS);
					GameManager.sendDeathEvent(p);
				} else {
					Vector v = bullet.getVelocity();
					v.setY(0.1);
					v.multiply(0.1);
					p.setVelocity(v);
					p.damage(dmg);
				}
			}
		}
	}

	private void sendDeathMessage(Player p, String killer, String method, Map<String, String> data) {
		List<String> dataString = new ArrayList<String>();
		if (data != null && !data.isEmpty()) {
			for (String d : data.keySet()) {
				dataString.add(d + ": " + data.get(d));
			}
		}
		String killMessage = GameTeamEnum.getColour(GameManager.getGameTeam(p)) + p.getName() + "\u00a77 was killed"
			+ (killer != null ? " by \u00a76" + killer + (method != null && !method.isEmpty() ? "\u00a77 using a \u00a76" + method + " \u00a78[\u00a7e" + String.join(", ", dataString) + "\u00a78]\u00a77" : "") : "") + ".";
		for (Player b1 : p.getWorld().getPlayers()) {
			b1.sendMessage(killMessage);
		}
		String bckm = "\u00a78[\u00a76" + p.getWorld().getName() + "\u00a78] " + killMessage;
		for (Player b1 : Bukkit.getWorld("Lobby").getPlayers()) {
			b1.sendMessage(bckm);
		}
		Bukkit.getConsoleSender().sendMessage(bckm);
	}

	@EventHandler(ignoreCancelled = true)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if ((GameManager.immoveable.keySet().contains(p) && GameManager.immoveable.get(p).equals(p.getWorld())) && e.getTo().getX() != e.getFrom().getX() && e.getTo().getZ() != e.getFrom().getX()
			&& !e.getTo().getWorld().getName().equalsIgnoreCase("Lobby")) {
			if (valid.containsKey(p)) {
				Location v = valid.get(p);
				if (v.getWorld().getName().equals(e.getTo().getWorld().getName()) && v.distance(e.getTo()) <= 5) {
					p.teleport(v);
					p.sendMessage("\u00a75Game> \u00a7dYou need to wait until the round starts to move.");
				} else {
					valid.put(p, e.getFrom());
				}
			} else {
				valid.put(p, e.getFrom());
			}
		} else {
			valid.put(p, e.getFrom());
			if (p.getItemInHand() instanceof ItemStack) {
				if (p.getItemInHand().getType() instanceof Material) {
					Weapon w = HaloPlus.weapons.get(EnumWeapon.toWeaponEnum(p.getItemInHand().getType()));
					if (w instanceof Weapon) {
						if (p.isSneaking()) {
							w.onZoom(p);
						} else {
							HaloPlus.weapons.get(EnumWeapon.toWeaponEnum(p.getItemInHand().getType())).onUnZoom(p);
						}
					}
				}
			}
		}
	}

	public void doReload(Player p, Weapon w) {
		if (!reload.containsKey(p.getName())) {
			reload.put(p.getName(), new Reload(p, w, (long) HaloPlus.getReloadSpeed(p, w.reload)));
		}
	}
}