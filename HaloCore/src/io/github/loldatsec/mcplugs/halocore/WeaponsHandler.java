package io.github.loldatsec.mcplugs.halocore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WeaponsHandler extends Weapons {

	public JavaPlugin main = null;
	public Map<String, String> lastExplosion = new HashMap<String, String>();

	public WeaponsHandler(JavaPlugin main) {
		this.main = main;
	}

	public void damageEvent(EntityDamageByEntityEvent e) {
		if (!e.isCancelled()) {
			if (e.getEntity() instanceof Player) {
				pvpdbf((Player) e.getEntity());
			}
			if (e.getDamager() instanceof LivingEntity) {
				try {
					Material hand = ((LivingEntity) e.getDamager()).getEquipment().getItemInHand().getType();
					if (hand == Material.BLAZE_ROD) {
						e.setDamage(4);
						e.getEntity().setFireTicks(30);
					} else if (hand == Material.STICK) {
						e.setDamage(2);
					} else if (e.getCause() == DamageCause.ENTITY_ATTACK) {
						e.setCancelled(true);
					}
					if (!e.getDamager().isOnGround()) {
						e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.CRIT, 100, 10);
						e.setDamage(e.getDamage() * 1.5);
					}
					String weapon = "";
					if (((LivingEntity) e.getDamager()).getEquipment().getItemInHand().getType() != Material.AIR) {
						weapon = " with a §e[§6" + ((LivingEntity) e.getDamager()).getEquipment().getItemInHand().getItemMeta().getDisplayName().split("§7 ")[0] + "§e]";
					}
					if (e.getEntity() instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK) {
						if (e.getDamager() instanceof Player) {
							ActionBarChat.send((Player) e.getEntity(), "§2" + ((Player) e.getDamager()).getDisplayName() + "§b is hitting you" + weapon);
						} else if (e.getDamager() instanceof LivingEntity) {
							ActionBarChat.send((Player) e.getEntity(), "§bA §2" + e.getDamager().getCustomName() + "§b is hitting you" + weapon);
						}
					} else if (e.getEntity() instanceof Player && e.getCause() == DamageCause.ENTITY_EXPLOSION) {
						if (e.getDamager() instanceof Player) {
							ActionBarChat.send((Player) e.getEntity(), "§2" + ((Player) e.getDamager()).getDisplayName() + "§b is exploding" + weapon);
						} else if (e.getDamager() instanceof LivingEntity) {
							ActionBarChat.send((Player) e.getEntity(), "§bA §2" + e.getDamager().getCustomName() + "§b is exploding" + weapon);
						}
					}
				} catch (NullPointerException npe) {
				}
			} else if (e.getDamager() instanceof Projectile) {
				if (e.getEntity() instanceof Creeper) {
					e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 4, false);
				}
				if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
					LivingEntity shooter = (LivingEntity) ((Projectile) e.getDamager()).getShooter();
					if (e.getEntity() instanceof Player) {
						if (shooter.equals(e.getEntity())) {
							e.setCancelled(true);
							return;
						}
						String weapon = "";
						if (shooter.getEquipment().getItemInHand().getType() != Material.AIR) {
							weapon = " with a §e[§6" + shooter.getEquipment().getItemInHand().getItemMeta().getDisplayName().split("§7 ")[0] + "§e]";
						}
						if (shooter instanceof Player) {
							ActionBarChat.send((Player) e.getEntity(), "§2" + ((Player) shooter).getDisplayName() + "§b is shooting you" + weapon);
						} else if (shooter instanceof PigZombie) {
							ActionBarChat.send((Player) e.getEntity(), "§2" + shooter.getCustomName() + "§bA §b is shooting you" + weapon);
						}
					}
				}
				if (e.getDamager() instanceof Egg) {
					e.setDamage(4);
					if (e.getEntity() instanceof Player) {
						((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
					}
					spawnFirework(e.getEntity().getLocation(), Color.BLACK, 0);
				} else if (e.getDamager() instanceof Snowball) {
					e.setDamage(3);
					if (e.getEntity() instanceof Player) {
						((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1));
					}
				} else if (e.getDamager() instanceof Arrow) {
					e.setDamage(5);
					if (((Arrow) e.getDamager()).isCritical()) {
						e.setDamage(7);
						if (e.getEntity() instanceof Player) {
							((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 15, 1));
							((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 3));
							((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1));
						}
						Color color = digest(((Player) ((Projectile) e.getDamager()).getShooter()).getName());
						spawnFirework(e.getEntity().getLocation(), color, 0);
					}
				}
				if (e.getDamager().getCustomName() != "") {
					try {
						e.setDamage(Integer.parseInt(e.getDamager().getCustomName()));
					} catch (NumberFormatException nfEx) {
					}
				}
			} else if (e.getDamager() instanceof TNTPrimed) {
				try {
					if (!e.getDamager().getCustomName().equalsIgnoreCase(((Player) e.getEntity()).getName())) {
						lastExplosion.put(((Player) e.getEntity()).getName(), e.getDamager().getCustomName());
					}
				} catch (NullPointerException | ClassCastException ex) {
				}
			}
		}
	}

	public void deathEvent(PlayerDeathEvent e) {
		e.getEntity().getWorld().playEffect(e.getEntity().getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 100, 2);
		if (e.getEntity().getKiller() instanceof Player) {
			Player killer = e.getEntity().getKiller();
			e.setDeathMessage("§2" + e.getEntity().getDisplayName() + "§b was killed by §2" + killer.getDisplayName() + "§b.");
			Inv.addSkull(killer, e.getEntity().getName());
			killer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 1));
			killer.playSound(killer.getEyeLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
			killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
			killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 4));
		} else if (e.getEntity().getLastDamageCause().getCause() == DamageCause.BLOCK_EXPLOSION || e.getEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_EXPLOSION) {
			try {
				Player killer = Bukkit.getPlayerExact(lastExplosion.get(e.getEntity().getName()));
				killer.incrementStatistic(Statistic.PLAYER_KILLS, 1);
				e.setDeathMessage("§2" + e.getEntity().getName() + "§b was killed by §2" + killer.getName() + "§b.");
				Inv.addSkull(killer, e.getEntity().getName());
				killer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 1));
				killer.playSound(killer.getEyeLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
				killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
				killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 4));
			} catch (NullPointerException | IllegalArgumentException ex) {
				e.setDeathMessage("§2" + e.getEntity().getDisplayName() + "§b was blown up.");
			}
		} else if (e.getEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK || e.getEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_EXPLOSION) {
			e.setDeathMessage("§2" + e.getEntity().getDisplayName() + "§b was slain by §eCovenants.");
		} else {
			e.setDeathMessage("§2" + e.getEntity().getDisplayName() + "§b died of unknown causes.");
		}
		clearInv(e.getEntity());
		for (PotionEffect effect : e.getEntity().getActivePotionEffects()) {
			e.getEntity().removePotionEffect(effect.getType());
		}
		e.getEntity().setFoodLevel(20);
		e.getEntity().setHealth(20);
		if (!e.getEntity().getWorld().getName().equalsIgnoreCase("Arena")) {
			e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation());
			e.getEntity().setVelocity(new Vector(0, 0, 0));
		}
		e.getEntity().updateInventory();
		try {
			lastExplosion.remove(e.getEntity().getName());
		} catch (NullPointerException | IllegalArgumentException ex) {
		}
	}

	public void interactEvent(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			Material hand = e.getItem().getType();
			if (hand.equals(Material.ENDER_PEARL)) {
				e.setCancelled(true);
			}
			if (e.getPlayer().getLocation().distance(e.getPlayer().getWorld().getSpawnLocation()) >= 18) {
				if (e.getPlayer().getFoodLevel() == 20) {
					if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (hand.equals(Material.ENDER_PEARL)) {
							Location target = e.getPlayer().getTargetBlock((Set<Material>) null, 64).getLocation();
							e.getPlayer().setVelocity(vectorize(e.getPlayer(), 0.15F, new Location(e.getPlayer().getWorld(), target.getX(), e.getPlayer().getEyeLocation().getY(), target.getZ())));
							debuff(e.getPlayer(), 2);
							useAmmo(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot(), false);
						} else if (hand.equals(Material.CAKE)) {
							for (Player player : e.getPlayer().getWorld().getEntitiesByClass(Player.class)) {
								for (int raid = 1; raid <= 4; raid++) {
									if (e.getPlayer().getLocation().distance(player.getLocation()) <= raid) {
										player.setHealth(20);
										ActionBarChat.send(player, "§cA health pack healed you.");
									}
								}
							}
							debuff(e.getPlayer(), 2);
							useAmmo(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot(), false);
						} else if (hand.equals(Material.IRON_AXE)) {
							if (hasAmmo(e.getPlayer())) {
								launchSnowball(e.getPlayer(), 0.4F, 2, false, 4);
							}
						} else if (hand.equals(Material.DIAMOND_AXE)) {
							if (hasAmmo(e.getPlayer())) {
								launchArrow(e.getPlayer(), 0.12F, 1, true, 6, false);
							}
						} else if (hand.equals(Material.IRON_HOE) || hand.equals(Material.STONE_AXE) || hand.equals(Material.GOLD_HOE) || hand.equals(Material.DIAMOND_HOE)) {
							zoom(e.getPlayer());
						}
					} else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
						e.setCancelled(true);
						if (hand.equals(Material.IRON_HOE)) {
							if (hasAmmo(e.getPlayer())) {
								launchSnowball(e.getPlayer(), 1, 1, true, 8);
								debuff(e.getPlayer(), 1);
							}
						} else if (hand.equals(Material.STONE_AXE)) {
							if (hasAmmo(e.getPlayer())) {
								launchSnowball(e.getPlayer(), 0.4F, 1, true, 7);
							}
						} else if (hand.equals(Material.STONE_HOE)) {
							if (hasAmmo(e.getPlayer())) {
								for (int sp = 0; sp < 12; sp++) {
									launchSnowball(e.getPlayer(), 0.4F, sp, false, sp + 5);
								}
								debuff(e.getPlayer(), 1);
							}
						} else if (hand.equals(Material.STONE_SWORD)) {
							if (hasAmmo(e.getPlayer())) {
								launchRocket(e.getPlayer());
								debuff(e.getPlayer(), 2);
							}
						} else if (hand.equals(Material.GOLD_HOE)) {
							if (hasAmmo(e.getPlayer())) {
								launchEggs(e.getPlayer(), 0.4F, 1, true, 9);
								debuff(e.getPlayer(), 1);
							}
						} else if (hand.equals(Material.GOLD_AXE)) {
							if (hasAmmo(e.getPlayer())) {
								shock(e.getPlayer());
								debuff(e.getPlayer(), 3);
							}
						} else if (hand.equals(Material.IRON_SWORD)) {
							if (hasAmmo(e.getPlayer())) {
								annihilate(e.getPlayer());
								debuff(e.getPlayer(), 1);
							}
						} else if (hand.equals(Material.GOLD_SWORD)) {
							if (hasAmmo(e.getPlayer())) {
								ultimatum(e.getPlayer());
								debuff(e.getPlayer(), 2);
							}
						} else if (hand.equals(Material.DIAMOND_HOE)) {
							if (hasAmmo(e.getPlayer())) {
								launchArrow(e.getPlayer(), 0.5F, 0, true, 9, false);
								debuff(e.getPlayer(), 2);
							}
						} else if (hand.equals(Material.IRON_BLOCK)) {
							TNTPrimed tnt = (TNTPrimed) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getTargetBlock((Set<Material>) null, 255).getLocation(), EntityType.PRIMED_TNT);
							tnt.setCustomName(e.getPlayer().getName());
							tnt.setFuseTicks(15);
							useAmmo(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot(), false);
							debuff(e.getPlayer(), 2);
						} else if (hand.equals(Material.DIAMOND_BLOCK)) {
							TNTPrimed tnt = (TNTPrimed) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getTargetBlock((Set<Material>) null, 255).getLocation(), EntityType.PRIMED_TNT);
							tnt.setCustomName(e.getPlayer().getName());
							tnt.setFuseTicks(1);
							useAmmo(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot(), false);
							debuff(e.getPlayer(), 1);
						} else if (hand.equals(Material.BEACON)) {
							launchAirStrike(e.getPlayer());
							useAmmo(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot(), false);
							debuff(e.getPlayer(), 10);
						} else {
							e.setCancelled(false);
						}
					}
				}
			} else {
				e.setCancelled(true);
			}
		}
	}

	public void queueReload(Player player) {
		if (player.getExp() == 1) {
			player.setExp(0.98F);
		}
	}

	public boolean hasAmmo(Player player) {
		try {
			ItemStack hand = player.getItemInHand();
			String[] itemName = hand.getItemMeta().getDisplayName().split("§7 《§8");
			int ammo = Integer.parseInt(itemName[2].substring(0, itemName[2].length() - 3));
			ammo--;
			String put = "";
			if (ammo <= 0) {
				queueReload(player);
				put += "Out Of Ammo§7";
			} else {
				put += ammo;
			}
			put += "§7》";
			itemName[2] = put;
			ItemMeta im = hand.getItemMeta();
			im.setDisplayName(String.join("§7 《§8", itemName));
			hand.setItemMeta(im);
			player.setItemInHand(hand);
			player.updateInventory();
			return true;
		} catch (NumberFormatException | NullPointerException | ArrayIndexOutOfBoundsException e) {
		}
		queueReload(player);
		return false;
	}

	public void debuff(Player player, int removeHunger) {
		player.setFoodLevel(player.getFoodLevel() - removeHunger);
	}

	public void reload(Player player) {
		Material gun = player.getItemInHand().getType();
		if (gun.equals(Material.IRON_AXE)) {
			player.setItemInHand(assault());
		} else if (gun.equals(Material.IRON_HOE)) {
			player.setItemInHand(sniper());
		} else if (gun.equals(Material.STONE_AXE)) {
			player.setItemInHand(pistol());
		} else if (gun.equals(Material.STONE_HOE)) {
			player.setItemInHand(shotgun());
		} else if (gun.equals(Material.STONE_SWORD)) {
			player.setItemInHand(rocketLauncher());
		} else if (gun.equals(Material.GOLD_HOE)) {
			player.setItemInHand(railgun());
		} else if (gun.equals(Material.GOLD_AXE)) {
			player.setItemInHand(electric());
		} else if (gun.equals(Material.IRON_SWORD)) {
			player.setItemInHand(annihilator());
		} else if (gun.equals(Material.GOLD_SWORD)) {
			player.setItemInHand(ultimatum());
		} else if (gun.equals(Material.DIAMOND_AXE)) {
			player.setItemInHand(tank());
		} else if (gun.equals(Material.DIAMOND_HOE)) {
			player.setItemInHand(battery());
		} else {
			return;
		}
		player.updateInventory();
		player.playSound(player.getEyeLocation(), Sound.LEVEL_UP, 1, 1);
		equipArmor(player);
		ActionBarChat.send(player, "§2Reloaded");
	}

	public void zoom(Player player) {
		if (player.hasPotionEffect(PotionEffectType.SLOW)) {
			player.removePotionEffect(PotionEffectType.SLOW);
		}
		if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 4));
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5, 0));
	}

	public void useAmmo(Player player, int slot, boolean doreload) {
		ItemStack ammo = player.getInventory().getItem(slot);
		if (ammo == null || ammo.getAmount() <= 1) {
			player.getInventory().setItem(slot, new ItemStack(Material.AIR, 1));
			if (doreload) {
				player.getInventory().setHelmet(redWool(player.getName(), "Reloading:"));
				player.setExp(0.98F);
				player.updateInventory();
			}
		} else {
			ammo.setAmount(ammo.getAmount() - 1);
			player.getInventory().setItem(slot, ammo);
		}
		player.updateInventory();
		pvpdbf(player);
	}

	public void launchRocket(Player player) {
		try {
			TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getTargetBlock((Set<Material>) null, 255).getLocation(), EntityType.PRIMED_TNT);
			tnt.setCustomName(player.getName());
			tnt.setVelocity(vectorize(player, 0.4F, player.getTargetBlock((Set<Material>) null, 200).getLocation()));
			tnt.setFuseTicks(5);
			player.playSound(player.getEyeLocation(), Sound.FIREWORK_LAUNCH, 0.3F, 1);
		} catch (IllegalStateException ise) {
		}
	}

	@SuppressWarnings("deprecation")
	public void launchSnowball(Player player, float speed, float spread, boolean aim, int dmg) {
		try {
			Snowball snow = (Snowball) player.throwSnowball();
			Location aimpos = player.getTargetBlock((Set<Material>) null, 200).getLocation();
			if (aim) {
				aimpos = aim(player, 3, true);
			}
			snow.setCustomName(dmg + "");
			snow.setVelocity(vectorize(player, speed, aimpos.add(new Vector(Math.random() * spread, Math.random() * spread, Math.random() * spread))));
			player.playSound(player.getEyeLocation(), Sound.BLAZE_HIT, 0.2F, 1);
		} catch (IllegalStateException ise) {
		}
	}

	@SuppressWarnings("deprecation")
	public void launchArrow(Player player, float speed, int spread, boolean isCrit, int dmg, boolean isFlame) {
		try {
			Arrow arrow = (Arrow) player.shootArrow();
			Location aimpos = player.getTargetBlock((Set<Material>) null, 200).getLocation();
			if (isCrit) {
				aimpos = aim(player, 7, true);
			}
			arrow.setVelocity(vectorize(player, speed, aimpos.add(new Vector(Math.random() * spread, Math.random() * spread, Math.random() * spread))));
			arrow.setCritical(isCrit);
			if (isFlame) {
				arrow.setFireTicks(Integer.MAX_VALUE);
			}
			arrow.setCustomName(dmg + "");
			player.playSound(player.getEyeLocation(), Sound.ANVIL_LAND, 0.1F, 1);
		} catch (IllegalStateException ise) {
		}
	}

	@SuppressWarnings("deprecation")
	public void launchEggs(Player player, float speed, int count, boolean aim, int dmg) {
		try {
			Location aimpos = player.getTargetBlock((Set<Material>) null, 200).getLocation();
			if (aim) {
				aimpos = aim(player, 5, true);
			}
			for (int countx = 0 - count; countx <= count; countx++) {
				for (int countz = 0 - count; countz <= count; countz++) {
					Egg egg = (Egg) player.throwEgg();
					egg.setVelocity(vectorize(player, speed, aimpos.add(new Vector(countx, 0, countz))));
					egg.setCustomName(dmg + "");
				}
			}
			player.playSound(player.getEyeLocation(), Sound.CHICKEN_EGG_POP, 0.2F, 1);
		} catch (IllegalStateException ise) {
		}
	}

	public Location aim(Player player, int aimRadius, boolean checkZoom) {
		Location aimpos = player.getTargetBlock((Set<Material>) null, 128).getLocation();
		if (player.hasPotionEffect(PotionEffectType.SLOW) || !checkZoom) {
			for (LivingEntity eInWorld : player.getWorld().getEntitiesByClass(LivingEntity.class)) {
				for (int raid = 1; raid <= aimRadius; raid++) {
					if (!(eInWorld instanceof Projectile)) {
						try {
							if (eInWorld.getLocation().distance(aimpos) < raid) {
								if (!eInWorld.equals(player) && eInWorld instanceof LivingEntity) {
									aimpos = eInWorld.getEyeLocation();
									if (eInWorld instanceof Player) {
										ActionBarChat.send(player, "§bAim adjusted at §2" + ((Player) eInWorld).getDisplayName());
									} else if (eInWorld.isCustomNameVisible()) {
										ActionBarChat.send(player, "§bAim adjusted at §2" + eInWorld.getCustomName());
									} else {
										ActionBarChat.send(player, "§bAim adjusted at §2" + eInWorld.getType().toString());
									}
									return aimpos;
								}
							}
						} catch (IllegalArgumentException iae) {
							return aimpos;
						}
					}
				}
			}
		}
		return aimpos;
	}

	@SuppressWarnings("deprecation")
	public void annihilate(Player player) {
		try {
			Location aimpos = player.getTargetBlock((Set<Material>) null, 128).getLocation();
			aimpos = aim(player, 7, false);
			for (int sp = 0; sp <= 15; sp++) {
				Snowball sb = player.throwSnowball();
				sb.setVelocity(vectorize(player, 0.5F, aimpos.add(new Vector(Math.random() * sp / 10, Math.random() * sp / 10, Math.random() * sp / 10))));
				sb.setCustomName("" + (7 + sp));
			}
			player.playSound(player.getEyeLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
		} catch (IllegalStateException ise) {
		}
	}

	public void shock(Player player) {
		try {
			Location aimpos = aim(player, 4, false);
			Arrow a = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ARROW);
			a.setVelocity(vectorize(player, 2, aimpos));
			a.setShooter(player);
			a.setCustomName("12");
			a.setCritical(true);
			for (LivingEntity e : player.getWorld().getEntitiesByClass(LivingEntity.class)) {
				if (e.getLocation().distance(aimpos) <= 3) {
					e.teleport(e);
					e.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));
					e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0));
					e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 150, 0));
					try {
						ActionBarChat.send((Player) e, "§dYou have been electrically shocked.");
					} catch (ClassCastException cce) {
					}
				}
			}
			player.playSound(player.getEyeLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
		} catch (IllegalStateException ise) {
		}
	}

	@SuppressWarnings("deprecation")
	public void ultimatum(Player player) {
		try {
			Location aimpos = player.getTargetBlock((Set<Material>) null, 128).getLocation();
			aimpos = aim(player, 15, false);
			for (int sp = 0; sp <= 25; sp++) {
				Arrow arrow = player.shootArrow();
				arrow.setVelocity(vectorize(player, 0.6F, aimpos.add(new Vector(Math.random() * sp / 10, Math.random() * sp / 10, Math.random() * sp / 10))));
				arrow.setCustomName("" + (8 + sp));
			}
			player.playSound(player.getEyeLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
		} catch (IllegalStateException ise) {
		}
	}

	public void launchAirStrike(Player player) {
		try {
			for (int ac = -5; ac <= 5; ac++) {
				for (int ai = -5; ai <= 5; ai++) {
					((Projectile) player.getWorld().spawnEntity(player.getTargetBlock((Set<Material>) null, 255).getLocation().add(new Vector(ac, ((ac * ac) + (ai * ai)) + 5, ai)), EntityType.ARROW)).setShooter(player);
					if ((ac * ac == 25 && ai * ai == 25) || (ac == 0 && ai == 0)) {
						TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getTargetBlock((Set<Material>) null, 255).getLocation().add(new Vector(ac, 680, ai)), EntityType.PRIMED_TNT);
						tnt.setVelocity(new Vector(0, -15, 0));
						tnt.setCustomName(player.getName());
					}
				}
			}
		} catch (IllegalStateException ise) {
		}
	}

	public Firework spawnFirework(Location loc, Color color, int pow) {
		Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
		data.addEffects(FireworkEffect.builder().withColor(color).with(Type.BALL_LARGE).build());
		data.setPower(pow);
		firework.setFireworkMeta(data);
		return firework;
	}

	public Vector vectorize(LivingEntity shooter, float speed, Location lookpos) {
		Location currentpos = shooter.getEyeLocation();
		Vector targetVector = lookpos.toVector().subtract(currentpos.toVector());
		return targetVector.multiply(speed);
	}

	public void pvpdbf(Player player) {
		if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
			player.removePotionEffect(PotionEffectType.WEAKNESS);
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
	}

	public int getpvpdbfTicksLeft(Player player) {
		int r = 0;
		for (PotionEffect pe : player.getActivePotionEffects()) {
			if (pe.getType().equals(PotionEffectType.WEAKNESS)) {
				r = pe.getDuration();
				break;
			}
		}
		return r;
	}
}
