package io.github.loldatsec.mcplugs.halocore;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin implements Listener {
	public DeathBattle DeathBattleInstance = new DeathBattle(this);
	public WeaponsHandler WeaponsInstance = new WeaponsHandler(this);

	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				// Run code (every tick)
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getExp() < 1) {
						player.playSound(player.getEyeLocation(),
								Sound.SUCCESSFUL_HIT, (float) 0.1, 1);
						player.setExp((float) (player.getExp() - 0.02));
						if (player.getExp() <= 0) {
							WeaponsInstance.reload(player);
							player.setExp(1);
						}
					}
					player.setLevel(player.getStatistic(Statistic.PLAYER_KILLS));
					((CraftPlayer) player).getHandle().getDataWatcher()
							.watch(9, (byte) 0);
					if (player
							.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
						if (player.getInventory().getHelmet() != null) {
							player.getInventory()
									.getHelmet()
									.addUnsafeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL,
											2);
						}
						if (player.getInventory().getChestplate() != null) {
							player.getInventory()
									.getChestplate()
									.addUnsafeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL,
											2);
						}
						if (player.getInventory().getLeggings() != null) {
							player.getInventory()
									.getLeggings()
									.addUnsafeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL,
											2);
						}
						if (player.getInventory().getBoots() != null) {
							player.getInventory()
									.getBoots()
									.addUnsafeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL,
											2);
						}
					} else {
						if (player.getInventory().getHelmet() != null
								&& player.getInventory().getHelmet()
										.getEnchantments() != null) {
							player.getInventory()
									.getHelmet()
									.removeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL);
						}
						if (player.getInventory().getChestplate() != null
								&& player.getInventory().getChestplate()
										.getEnchantments() != null) {
							player.getInventory()
									.getChestplate()
									.removeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL);
						}
						if (player.getInventory().getLeggings() != null
								&& player.getInventory().getLeggings()
										.getEnchantments() != null) {
							player.getInventory()
									.getLeggings()
									.removeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL);
						}
						if (player.getInventory().getBoots() != null
								&& player.getInventory().getBoots()
										.getEnchantments() != null) {
							player.getInventory()
									.getBoots()
									.removeEnchantment(
											Enchantment.PROTECTION_ENVIRONMENTAL);
						}
					}
				}
				for (World w : Bukkit.getWorlds()) {
					for (Item drop : w.getEntitiesByClass(Item.class)) {
						drop.remove();
					}
					for (ExperienceOrb drop : w
							.getEntitiesByClass(ExperienceOrb.class)) {
						drop.remove();
					}
					for (Projectile p : w.getEntitiesByClass(Projectile.class)) {
						if (p.isDead() || p.isOnGround() || p.isInsideVehicle()
								|| !p.isValid()) {
							p.remove();
						}
					}
				}
			}
		}, 0, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				// Run code (every .5 secs)
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getFoodLevel() < 20) {
						player.setFoodLevel(player.getFoodLevel() + 1);
						if (player.getFoodLevel() == 20) {
							player.playSound(player.getEyeLocation(),
									Sound.FIRE_IGNITE, 1, 1);
						}
					}
					if (player.getLocation().distance(
							player.getWorld().getSpawnLocation()) >= 18
							&& player.getGameMode() == GameMode.SURVIVAL
							&& player.getWorld()
									.getEntitiesByClass(Monster.class).size() <= 800
							&& 0.2 >= Math.random()) {
						Location spawnpos = player.getLocation();
						double rnd = Math.random();
						if (rnd <= 0.25) {
							spawnpos.add(5 + Math.random() * 11, 0,
									5 + Math.random() * 11);
						} else if (rnd <= 0.5) {
							spawnpos.add(-5 - Math.random() * 11, 0,
									-5 - Math.random() * 11);
						} else if (rnd <= 0.75) {
							spawnpos.add(5 + Math.random() * 11, 0,
									5 + Math.random() * 11);
						} else {
							spawnpos.add(-5 - Math.random() * 11, 0,
									5 + Math.random() * 11);
						}
						if (spawnpos.getBlock().getType() == Material.AIR
								&& spawnpos
										.getWorld()
										.getBlockAt((int) spawnpos.getX(), 6,
												(int) spawnpos.getZ())
										.getType() == Material.WOOL) {
							if (Math.random() <= 0.4) {
								Creeper c = (Creeper) player.getWorld()
										.spawnEntity(spawnpos,
												EntityType.CREEPER);
								c.setCanPickupItems(false);
								c.setCustomName("§aCovenant Bomber");
								c.setCustomNameVisible(true);
								c.setHealth(1);
								c.setPowered(true);
							} else {
								Zombie z = (Zombie) player.getWorld()
										.spawnEntity(spawnpos,
												EntityType.ZOMBIE);
								z.setCanPickupItems(false);
								z.setHealth(1);
								z.setCustomName("§2Covenant Soldier");
								z.getEquipment().setItemInHand(
										WeaponsInstance.dagger());
								z.getEquipment().setHelmet(
										new ItemStack(Material.IRON_HELMET, 1));
								z.setVillager(true);
								z.setBaby(false);
								z.setTarget(player);
								z.getEquipment().setItemInHandDropChance(0);
								z.getEquipment().setHelmetDropChance(0);
								z.setRemoveWhenFarAway(true);
								z.setCustomNameVisible(true);
							}
						}
					}

				}
			}
		}, 0, 10);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				// Run code (every 5 min)
				int c = 0;
				for (World w : Bukkit.getWorlds()) {
					for (Entity e : w.getEntities()) {
						if (!(e instanceof Player || e instanceof LightningStrike)) {
							e.remove();
							c++;
						}
					}
				}
				Chat.bc("§7[§6Halo§7] §6Removed §e" + c + " §6entities.");
			}
		}, 0, 6000);
	}

	public void onDisable() {
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("onvoteaction")) {
			if (sender instanceof Player) {
				sender.sendMessage("§cError: " + "§4" + "You are not console.");
			} else if (args.length >= 1) {
				if (Bukkit.getPlayer(args[0]) instanceof Player) {
					Player p = Bukkit.getPlayer(args[0]);
					int extra = 0;
					try {
						extra = (int) (Math.random()
								* (p.getStatistic(Statistic.PLAYER_KILLS)) / 50);
					} catch (NumberFormatException nfex) {

					}
					if (extra < 100) {
						extra = 100;
					}
					p.incrementStatistic(Statistic.PLAYER_KILLS, extra);
					Chat.bc("§e[§6Vote§e] §2" + p.getDisplayName()
							+ " §avoted for RebornCraft and got a extra§e "
							+ extra + " §akillcounts! §e/vote§a to vote.");
					p.incrementStatistic(Statistic.PLAYER_KILLS, extra);
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("distancetospawn")) {
			double dst = 0;
			if (sender instanceof Player) {
				Player p = (Player) sender;
				dst = p.getLocation().distance(p.getWorld().getSpawnLocation());
			}
			sender.sendMessage("§6You are §e" + dst + " §6blocks from spawn.");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("deathbattle")) {
			DeathBattleInstance.startGame(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("addkills")) {
			if (sender instanceof Player) {
				if (!sender.hasPermission("halocore.changekillcount")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
					return true;
				}
			}
			if (args.length >= 2) {
				if (Bukkit.getPlayer(args[0]) instanceof Player) {
					Player player = Bukkit.getPlayer(args[0]);
					try {
						player.incrementStatistic(Statistic.PLAYER_KILLS,
								Integer.parseInt(args[1]));
						return true;
					} catch (NumberFormatException nFEx) {
						sender.sendMessage("§cError: " + "§4"
								+ "Failed to parse kill count to a integer.");
						return true;
					}
				} else {
					sender.sendMessage("§cError: " + "§4Player not online.");
					return true;
				}
			} else {
				sender.sendMessage("§cError: " + "§4Insufficient arguments.");
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("remkills")) {
			if (sender instanceof Player) {
				if (!sender.hasPermission("halocore.changekillcount")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
					return true;
				}
			}
			if (args.length >= 2) {
				if (Bukkit.getPlayer(args[0]) instanceof Player) {
					Player player = Bukkit.getPlayer(args[0]);
					try {
						player.decrementStatistic(Statistic.PLAYER_KILLS,
								Integer.parseInt(args[1]));
						return true;
					} catch (NumberFormatException nFEx) {
						sender.sendMessage("§cError: " + "§4"
								+ "Failed to parse kill count to a integer.");
						return true;
					}
				} else {
					sender.sendMessage("§cError: " + "§4Player not online.");
					return true;
				}
			} else {
				sender.sendMessage("§cError: " + "§4Insufficient arguments.");
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("setkills")) {
			if (sender instanceof Player) {
				if (!sender.hasPermission("halocore.changekillcount")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
					return true;
				}
			}
			if (args.length >= 2) {
				if (Bukkit.getPlayer(args[0]) instanceof Player) {
					Player player = Bukkit.getPlayer(args[0]);
					try {
						player.setStatistic(Statistic.PLAYER_KILLS,
								Integer.parseInt(args[1]));
						return true;
					} catch (NumberFormatException nFEx) {
						sender.sendMessage("§cError: " + "§4"
								+ "Failed to parse kill count to a integer.");
						return true;
					}
				} else {
					sender.sendMessage("§cError: " + "§4Player not online.");
					return true;
				}
			} else {
				sender.sendMessage("§cError: " + "§4Insufficient arguments.");
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("spawn")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.spawn")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
				} else {
					if (player.getWorld().getName().equalsIgnoreCase("Arena")) {
						player.setGameMode(GameMode.SURVIVAL);
						sender.sendMessage("§6Teleporting...");
						player.teleport(Bukkit.getWorld("Lobby")
								.getSpawnLocation());
					} else {
						int time = WeaponsInstance
								.getpvpdbfTicksLeft((Player) sender);
						if (time <= 0) {
							sender.sendMessage("§6Teleporting...");
							player.teleport(player.getWorld()
									.getSpawnLocation());
						} else {
							sender.sendMessage("§cError: §4Please wait §c"
									+ Math.ceil(time / 20)
									+ " §4seconds before teleporting.");
						}
					}
				}
			} else {
				sender.sendMessage("§cError: " + "§4Only a player can do that.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("ce")
				|| cmd.getName().equalsIgnoreCase("clearent")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.entities")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
					return true;
				}
			}
			int ke = 0;
			for (World w : Bukkit.getWorlds()) {
				for (Entity ent : w.getEntities()) {
					if (!(ent instanceof Player)) {
						ent.remove();
						ke++;
					}
				}
			}
			sender.sendMessage("§6Cleared §e" + ke + "§6 entities.");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("gmc")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.gmc")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
				} else {
					player.setGameMode(GameMode.CREATIVE);
					sender.sendMessage("§6" + "Set gamemode to Creative.");
				}
			} else {
				sender.sendMessage("§cError: " + "§4Only a player can do that.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("gms")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.gms")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
				} else {
					if (player.getWorld().getName().equalsIgnoreCase("Arena")) {
						sender.sendMessage("§aYou are in the arena world!");
					} else {
						player.setGameMode(GameMode.SURVIVAL);
						sender.sendMessage("§6" + "Set gamemode to Survival.");
					}
				}
			} else {
				sender.sendMessage("§cError: " + "§4Only a player can do that.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("fly")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.fly")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
				} else {
					player.setAllowFlight(!player.getAllowFlight());
					sender.sendMessage("§6Toggled flying to " + "§c"
							+ player.getAllowFlight() + "§6.");
				}
			} else {
				sender.sendMessage("§cError: " + "§4Only a player can do that.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("gmspec")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.gmspec")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
				} else {
					player.setGameMode(GameMode.SPECTATOR);
					sender.sendMessage("§6" + "Set gamemode to Spectator.");
				}
			} else {
				sender.sendMessage("§cError: " + "§4Only a player can do that.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("ci")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.ci")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
				} else {
					player.getInventory().clear();
					ItemStack air = new ItemStack(Material.AIR, 1);
					player.getInventory().setItem(39, air);
					player.getInventory().setItem(38, air);
					player.getInventory().setItem(37, air);
					player.getInventory().setItem(36, air);
					for (PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}
					player.updateInventory();
					sender.sendMessage("§6" + "Your inventory has been ed.");
				}
			} else {
				sender.sendMessage("§cError: " + "§4Only a player can do that.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("tpo")
				|| cmd.getName().equalsIgnoreCase("tpto")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPermission("halocore.tpo")) {
					sender.sendMessage("§cError: " + "§4"
							+ "You do not have permission.");
				} else {
					if (args.length >= 1 && Bukkit.getPlayer(args[0]) != null) {
						player.teleport(Bukkit.getPlayer(args[0]));
						sender.sendMessage("§6" + "Teleported you to §c"
								+ Bukkit.getPlayer(args[0]).getName() + "§6.");
					} else {
						sender.sendMessage("§cError: " + "§4Player not found.");
					}
				}
			} else {
				sender.sendMessage("§cError: " + "§4Only a player can do that.");
			}
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		WeaponsInstance.clearInv(e.getPlayer());
		Title.send(e.getPlayer(), "§eWelcome to " + "§aRcHalo!", "§6"
				+ "Have fun shooting!", 70, 600, 10);
		e.getPlayer().teleport(Bukkit.getWorld("LOBBY").getSpawnLocation());
		e.setJoinMessage(null);
		if (e.getPlayer().hasPermission("halocore.loginbc")
				&& !e.getPlayer().hasPermission("halocore.supresslogin")) {
			Chat.bc("§6Welcome back §e" + e.getPlayer().getName() + "§6!");
		} else {
			e.getPlayer().sendMessage(
					"§6Welcome back, §e" + e.getPlayer().getName() + "§6.");
		}
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		WeaponsInstance.clearInv(e.getPlayer());
		e.setQuitMessage(null);
		if (e.getPlayer().hasPermission("halocore.loginbc")
				&& !e.getPlayer().hasPermission("halocore.supresslogin")) {
			Chat.bc("§e" + e.getPlayer().getName() + "§6" + " logged out.");
		}
		DeathBattleInstance.highLogoutEvent(e);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Monster) {
			if (e.getEntity().getKiller() instanceof Player) {
				Player p = ((Player) e.getEntity().getKiller());
				double chance = 0;
				if (e.getEntity() instanceof Zombie) {
					chance = 0.2;
				} else if (e.getEntity() instanceof Creeper) {
					chance = 0.4;
				}
				if (Math.random() <= chance) {
					p.incrementStatistic(Statistic.PLAYER_KILLS, 1);
				}
			}
		}
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent e) {
		if (e.getEntity() instanceof Creeper) {
			e.setYield(15);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		Title.send(e.getEntity(), "§7You died.", null, 0, 60, 10);
		WeaponsInstance.deathEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeathHigh(PlayerDeathEvent e) {
		DeathBattleInstance.highDeathEvent(e);
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
		e.setCancelled(true);
		e.getPlayer().sendMessage("§cDropping of items is disabled.");
		e.getPlayer().updateInventory();
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if (e.getPlayer().getLocation()
				.distance(e.getPlayer().getWorld().getSpawnLocation()) > 200) {
			e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
		}
	}

	@EventHandler
	public void onPlayerInvClickEvent(InventoryClickEvent e) {
		if (e.getWhoClicked().hasPermission("halocore.allowinvclick")) {

		} else {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("halocore.setsign")) {
			if (e.getLine(0).equalsIgnoreCase("[KIT]")) {
				e.getPlayer().sendMessage("§aKit sign placed.");
				e.setLine(0, "§1[Kit]");
			} else if (e.getLine(0).equalsIgnoreCase("[World]")) {
				e.getPlayer().sendMessage("§aWorld sign placed.");
				e.setLine(0, "§2[World]");
			} else if (e.getLine(0).equalsIgnoreCase("[Deploy]")) {
				e.getPlayer().sendMessage("§aDeploy sign placed.");
				e.setLine(0, "§3[Deploy]");
			} else {
				Capture.placeSign(e);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK
				|| e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			if (block instanceof Block) {
				if (block.getType().equals(Material.WALL_SIGN)
						|| block.getType().equals(Material.SIGN_POST)) {
					Sign sign = (Sign) block.getState();
					if (sign.getLine(1) != null) {
						Capture.capture(e);
						if (sign.getLine(0).equalsIgnoreCase("§1[Kit]")) {
							WeaponsInstance.getKit(e.getPlayer(),
									sign.getLine(1));
						} else if (sign.getLine(0).equalsIgnoreCase(
								"§3[Deploy]")) {
							Deploy.deploy(e.getPlayer());
						} else if (sign.getLine(0)
								.equalsIgnoreCase("§2[World]")) {
							if (Bukkit.getWorld(sign.getLine(1)) != null) {
								e.getPlayer().teleport(
										Bukkit.getWorld(sign.getLine(1))
												.getSpawnLocation());
								e.getPlayer().sendMessage("§6Teleporting...");
							} else {
								e.getPlayer()
										.sendMessage(
												"§cError: " + "§4"
														+ "World not found.");
							}
						}
					}
					return;
				}
			}
		}
		if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("LOBBY")
				|| e.getPlayer().isOp()) {
			if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
				WeaponsInstance.interactEvent(e);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		DeathBattleInstance.highDamageEvent(e);
		WeaponsInstance.damageEvent(e);
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		} else if (e.getCause() == DamageCause.FIRE) {
			e.setCancelled(true);
		} else if (e.getCause() == DamageCause.FIRE_TICK) {
			e.setCancelled(true);
		} else if (e.getCause() == DamageCause.DROWNING) {
			e.setCancelled(true);
		} else if (e.getCause() == DamageCause.FALLING_BLOCK) {
			e.setCancelled(true);
		} else if (e.getCause() == DamageCause.ENTITY_EXPLOSION
				|| e.getCause() == DamageCause.BLOCK_EXPLOSION) {
			e.setDamage(10.0);
		}
	}

	@EventHandler
	public void onPlayerAsyncChatEvent(AsyncPlayerChatEvent e) {
		String worldStr = "§7[§e" + e.getPlayer().getWorld().getName() + "§7] ";
		String killStr = "";
		String captureStr = "";
		if (e.getPlayer().getStatistic(Statistic.PLAYER_KILLS) >= 1) {
			killStr += "§a("
					+ e.getPlayer().getStatistic(Statistic.PLAYER_KILLS) + ") ";
		} else {
			killStr += "§7(0) ";
		}
		if (e.getPlayer().getStatistic(Statistic.CRAFT_ITEM,
				Material.DIAMOND_SPADE) >= 1) {
			captureStr += "§e["
					+ e.getPlayer().getStatistic(Statistic.CRAFT_ITEM,
							Material.DIAMOND_SPADE) + "] ";
		} else {
			captureStr += "§8[0] ";
		}
		e.setFormat(worldStr + killStr + captureStr + e.getFormat());
		String m = e.getMessage().toLowerCase();
		if (m.contains("hac") || m.contains("hak") || m.contains("hax")
				|| m.contains("h@") || m.contains("h4")) {
			e.getPlayer().sendMessage("§cYou salty? Don't call people hacks!");
			e.setMessage(":(");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.CAKE_BLOCK) {
			e.setCancelled(true);
		} else if (!e.getPlayer().hasPermission("halocore.build")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockDestroy(BlockBreakEvent e) {
		if (!e.getPlayer().hasPermission("halocore.build")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if (e.getEntity().getFireTicks() >= 1) {
			TNTPrimed tnt = (TNTPrimed) e
					.getEntity()
					.getWorld()
					.spawnEntity(e.getEntity().getLocation(),
							EntityType.PRIMED_TNT);
			tnt.setFuseTicks(0);
			if (e.getEntity().getShooter() instanceof Player) {
				tnt.setCustomName(((Player) e.getEntity().getShooter())
						.getName());
			}
		}
		e.getEntity().remove();
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent e) {
		e.setCancelled(true);
		e.getItem().remove();
	}

	@EventHandler
	public void onPlayerXPChangeEvent(PlayerExpChangeEvent e) {
		e.getPlayer().setExp(0.98F);
		e.setAmount(0);
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		DeathBattleInstance.sendCommandEvent(e);
	}

}
