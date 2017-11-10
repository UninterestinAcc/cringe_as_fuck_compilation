package io.github.loldatsec.mcplugins.staffmode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

/**
 * Planned features: stealth chestviewer, packetsneak detector & alerter
 */
public class StaffMode extends JavaPlugin implements Listener {

	public static List<Player> staff = new ArrayList<Player>();
	public Map<Player, Long> cooldown = new HashMap<Player, Long>();
	public Map<Player, Player> follow = new HashMap<Player, Player>();
	public List<Player> devanishedStaff = new ArrayList<Player>();

	public void onDisable() {
		for (Player s : staff) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(s);
			}
			s.getInventory().clear();
			s.setAllowFlight(false);
			s.sendMessage("\u00a73StaffMode> \u00a7bA reload has forced you to become normal.");
		}
	}

	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				List<Player> remove = new ArrayList<Player>();
				for (Entry<Player, Player> fs : follow.entrySet()) {
					if (!fs.getKey().isOnline() || !fs.getValue().isOnline() || !staff.contains(fs.getValue())) {
						remove.add(fs.getKey());
					}
				}
				for (Player r : remove) {
					follow.remove(r);
				}
				for (Player p : staff) {
					for (Player everyone : Bukkit.getOnlinePlayers()) {
						if (everyone.hasPermission("staffmode.seevanish") || devanishedStaff.contains(p)) {
							everyone.showPlayer(p);
							// TODO Devanish a staff
						} else {
							everyone.hidePlayer(p);
						}
						if (p.getOpenInventory() instanceof InventoryView) {
							InventoryView inv = p.getOpenInventory();
							if (inv.getTitle().equalsIgnoreCase(everyone.getName() + "'s Inventory")) {
								updateInventory(everyone, p);
							}
						}
					}
					Player aim = aimingAt(p);
					if (aim instanceof Player) {
						sendActionBarChat(p, "\u00a7e\u00a7lTargetting: \u00a76\u00a7l" + aim.getName());
					} else {
						sendActionBarChat(p, "\u00a74\u00a7lTargetting AIR");
					}
					if (follow.values().contains(p)) {
						Player victim = null;
						for (Player v : follow.keySet()) {
							if (follow.get(v) == p) {
								victim = v;
								break;
							}
						}
						if (p.isSneaking()) {
							follow.remove(victim);
							p.sendMessage("\u00a73StaffMode> \u00a7bNo longer following " + victim.getName());
						}
					}
				}
			}
		}, 0, 5);
		Bukkit.getPluginManager().registerEvents(this, this);
		for (Player p : Bukkit.getOnlinePlayers()) {
			tryEnableStaffMode(p);
		}
	}

	public void tryEnableStaffMode(Player p) {
		if (p.hasPermission("staffmode.autoactivate")) {
			p.sendMessage("\u00a73StaffMode> \u00a7bAutomagically tried to activate staff mode.");
			Bukkit.dispatchCommand(p, "staffmode");
		}
	}

	@EventHandler
	public void logout(PlayerQuitEvent e) {
		if (staff.contains(e.getPlayer())) {
			staff.remove(e.getPlayer());
			e.getPlayer().getInventory().clear();
		}
	}

	@EventHandler
	public void login(PlayerJoinEvent e) {
		tryEnableStaffMode(e.getPlayer());
	}

	public static void sendActionBarChat(Player player, String text) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("staffmode.use")) {
			if (sender instanceof Player) {
				if (staff.contains((Player) sender)) {
					staff.remove((Player) sender);
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.showPlayer((Player) sender);
					}
					((Player) sender).getInventory().clear();
					((Player) sender).setAllowFlight(false);
				} else {
					Player s = (Player) sender;
					boolean empty = true;
					for (int i = 0; i < +39; i++) {
						if (s.getInventory().getItem(i) != null) {
							empty = false;
						}
					}
					if (empty) {
						staff.add(s);
						s.setAllowFlight(true);
						int slot = 0;
						s.getInventory().setItem(slot++, StaffModeItems.kbTester());
						s.getInventory().setItem(slot++, StaffModeItems.noDamageKbTester());
						s.getInventory().setItem(slot++, StaffModeItems.banStick());
						s.getInventory().setItem(slot++, StaffModeItems.reachyBanStick());
						s.getInventory().setItem(slot++, StaffModeItems.teleport());
						s.getInventory().setItem(slot++, StaffModeItems.follow());
						s.getInventory().setItem(slot++, StaffModeItems.devanish());
						s.getInventory().setItem(slot++, StaffModeItems.inspect());
					} else {
						sender.sendMessage("\u00a73StaffMode> \u00a7bPlease empty your inventory.");
						return true;
					}
				}
				sender.sendMessage("\u00a73StaffMode> \u00a7bToggled staffmode to \u00a79" + staff.contains((Player) sender));
			} else {
				sender.sendMessage("\u00a74Error> \u00a7cNot a player.");
			}
		} else {
			sender.sendMessage("\u00a74Error> \u00a7cNo permissions.");
		}
		return true;
	}

	@EventHandler
	public void iInteract(InventoryClickEvent e) {
		if (staff.contains((Player) e.getWhoClicked())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		if (staff.contains(e.getPlayer())) {
			e.setCancelled(true);
			if (e.getAction() != Action.PHYSICAL) {
				Player p = e.getPlayer();
				if (p.getItemInHand() instanceof ItemStack) {
					if (canUse(p)) {
						Player victim = aimingAt(p);
						if (p.getItemInHand().getType() == Material.SULPHUR) {
							// KB Tester
							if (victim instanceof Player) {
								doKb(victim, p);
								victim.damage(0);
								p.sendMessage("\u00a73StaffMode> \u00a7bKB tested " + victim.getName() + "(\u00a7cwith damage\u00a7b), make sure to wait a few seconds to see if he is lagging.");
							}
						} else if (p.getItemInHand().getType() == Material.SUGAR) {
							// KB Tester
							if (victim instanceof Player) {
								doKb(victim, p);
								p.sendMessage("\u00a73StaffMode> \u00a7bKB tested " + victim.getName() + "(no damage), make sure to wait a few seconds to see if he is lagging.");
							}
						} else if (p.getItemInHand().getType() == Material.BLAZE_ROD) {
							// Reachy Banstick
							if (victim instanceof Player) {
								if (p.isSneaking()) {
									Bukkit.dispatchCommand(p, "tempban " + victim.getName() + " 30d hacks, reachy ban stick @ " + Bukkit.getServerName());
								} else {
									p.sendMessage("\u00a73StaffMode> \u00a7bYou must sneak to use reachy ban stick.");
								}
							}
						} else if (p.getItemInHand().getType() == Material.SLIME_BALL) {
							if (victim instanceof Player) {
								teleport(victim, p);
								p.sendMessage("\u00a73StaffMode> \u00a7bTeleported you to " + victim.getName());
							} else {
								p.sendMessage("\u00a73StaffMode> \u00a7bYou are not targetting anyone.");
							}
						} else if (p.getItemInHand().getType() == Material.GHAST_TEAR) {
							if (follow.values().contains(p)) {
								Player dvvictim = null;
								for (Player v : follow.keySet()) {
									if (follow.get(v) == p) {
										dvvictim = v;
										break;
									}
								}
								follow.remove(dvvictim);
								p.sendMessage("\u00a73StaffMode> \u00a7bNo longer following " + dvvictim.getName());
							} else {
								if (p.getAllowFlight()) {
									if (victim instanceof Player) {
										teleport(victim, p);
									}
									follow.put(victim, p);
									p.setFlying(true);
									p.sendMessage("\u00a73StaffMode> \u00a7bNow following " + victim.getName());
								} else {
									p.sendMessage("\u00a73StaffMode/\u00a7cWarning\u00a73> \u00a7cYou need to be able to fly for follow to work properly.");
								}
							}
						} else if (p.getItemInHand().getType() == Material.FIREWORK_CHARGE) {
							// Devanish infront of target
							if (victim instanceof Player) {
								victim.showPlayer(p);
								p.sendMessage("\u00a73StaffMode> \u00a7b" + victim.getName() + " can see you briefly.");
							}
						} else if (p.getItemInHand().getType() == Material.BLAZE_POWDER) {
							// Inventory view
							Block b = p.getTargetBlock((Set<Material>) null, 6);
							if (b instanceof Block) {
								BlockState t = b.getState();
								if (t instanceof Chest) {
									p.sendMessage("\u00a73StaffMode> \u00a7bInspecting chest.");
									Chest c = (Chest) t;
									BlockFace af;
									if (b.getRelative(BlockFace.NORTH).getType() == b.getType()) {
										af = BlockFace.NORTH;
									} else if (b.getRelative(BlockFace.EAST).getType() == b.getType()) {
										af = BlockFace.EAST;
									} else if (b.getRelative(BlockFace.SOUTH).getType() == b.getType()) {
										af = BlockFace.SOUTH;
									} else if (b.getRelative(BlockFace.WEST).getType() == b.getType()) {
										af = BlockFace.WEST;
									} else {
										Inventory inv = Bukkit.createInventory(p, 27);
										for (int slot = 0; slot <= 26; slot++) {
											inv.setItem(slot, c.getBlockInventory().getItem(slot));
										}
										p.openInventory(inv);
										return;
									}
									BlockState t0 = b.getRelative(af).getState();
									Inventory inv = Bukkit.createInventory(p, 54);
									for (int slot = 0; slot <= 26; slot++) {
										inv.setItem(slot, c.getBlockInventory().getItem(slot));
									}
									for (int slot = 0; slot <= 26; slot++) {
										inv.setItem(slot + 27, ((Chest) t0).getBlockInventory().getItem(slot));
									}
									p.openInventory(inv);
									return;
								}
							}
							if (victim instanceof Player) {
								p.sendMessage("\u00a73StaffMode> \u00a7bShowing inventory of " + victim.getName());
								showInventory(victim, p);
							}
						}
					} else {
						p.sendMessage("\u00a73StaffMode/\u00a7cWarning\u00a73> \u00a7cYou can do 1 action every 0.6 seconds.");
					}
					// More tools?
				}
			}
		}
	}

	public void doKb(Player victim, Player staff) {
		double x = 0;
		double z = 0;
		double yaw = staff.getLocation().getYaw();
		if (yaw > 45 && yaw <= 135) {
			x = -0.5;
		} else if (yaw > 135 && yaw <= 225) {
			z = -0.5;
		} else if (yaw > 225 && yaw <= 315) {
			x = 0.5;
		} else {
			z = 0.5;
		}
		Vector vel = new Vector(x, 0.5, z);
		victim.setVelocity(vel);
	}

	public boolean canUse(Player staff) {
		if (cooldown.containsKey(staff)) {
			if (cooldown.get(staff) + 600 > System.currentTimeMillis()) {
				// 600ms debuff
				return false;
			}
		}
		cooldown.put(staff, System.currentTimeMillis());
		return true;
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if (follow.containsKey(e.getPlayer())) {
			vecMove(e.getPlayer(), follow.get(e.getPlayer()));
		} else if (follow.containsValue(e.getPlayer())) {
			Player victim = null;
			for (Player v : follow.keySet()) {
				if (follow.get(v) == e.getPlayer()) {
					victim = v;
					break;
				}
			}
			vecMove(victim, e.getPlayer());
		}
	}

	@EventHandler
	public void hunger(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player && staff.contains((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && staff.contains((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && staff.contains((Player) e.getEntity())) {
			e.setCancelled(true);
			Player damager = null;
			if (e.getDamager() instanceof Player) {
				damager = (Player) e.getDamager();
			} else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
				damager = (Player) ((Projectile) e.getDamager()).getShooter();
			}
			if (damager instanceof Player) {
				((Player) e.getEntity()).sendMessage("\u00a73StaffMode> \u00a7bMitigated damage from \u00a79" + damager.getName());
			}
		}
	}

	public Player aimingAt(Player staff) {
		Player p = null;
		if (follow.containsValue(staff)) {
			for (Player v : follow.keySet()) {
				if (follow.get(v) == staff) {
					p = v;
					break;
				}
			}
		} else {
			Location bp = staff.getTargetBlock((Set<Material>) null, 64).getLocation();
			for (Player e : staff.getWorld().getPlayers()) {
				if (e != staff) {
					if (p instanceof Player) {
						if (e.getLocation().distance(bp) < p.getLocation().distance(bp)) {
							p = e;
						}
					} else {
						p = e;
					}
				}
			}
			p = p == null ? null : p.getLocation().distance(bp) <= 10 ? p : null;
		}
		return p;
	}

	@EventHandler
	public void pickupItem(PlayerPickupItemEvent e) {
		if (staff.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		if (staff.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	public void teleport(Player victim, Player staff) {
		double pitch = ((victim.getLocation().getPitch() + 90) * Math.PI) / 180;
		double yaw = ((victim.getLocation().getYaw() + 90) * Math.PI) / 180;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double z = Math.sin(pitch) * Math.sin(yaw);
		x = 0 - x * 2;
		z = 0 - z * 2;
		Location to = victim.getLocation();
		to = to.add(new Vector(x, 1, z));
		to.setPitch(40F);
		staff.teleport(to);
	}

	public void vecMove(Player victim, Player staff) {
		double pitch = ((victim.getLocation().getPitch() + 90) * Math.PI) / 180;
		double yaw = ((victim.getLocation().getYaw() + 90) * Math.PI) / 180;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double z = Math.sin(pitch) * Math.sin(yaw);
		x = 0 - x * 2;
		z = 0 - z * 2;
		Location to = victim.getLocation();
		to = to.add(new Vector(x, 1, z));
		to.setPitch(40F);
		if (!to.getWorld().equals(staff.getWorld()) || to.distance(staff.getLocation()) > 7) {
			staff.teleport(to);
		} else {
			Vector move = to.toVector().subtract(staff.getLocation().toVector());
			move.divide(new Vector(12, 12, 12));
			staff.setVelocity(move);
		}
	}

	public void showInventory(Player victim, Player staff) {
		Inventory inv = Bukkit.createInventory(staff, 45, victim.getName() + "'s Inventory");
		staff.openInventory(inv);
		updateInventory(victim, staff);
	}

	public void updateInventory(Player victim, Player staff) {
		InventoryView inv = staff.getOpenInventory();
		int slot;
		for (slot = 0; slot <= 35; slot++) {
			inv.setItem(slot, victim.getInventory().getItem(slot));
		}
		inv.setItem(slot++, victim.getInventory().getHelmet());
		inv.setItem(slot++, victim.getInventory().getChestplate());
		inv.setItem(slot++, victim.getInventory().getLeggings());
		inv.setItem(slot++, victim.getInventory().getBoots());
		slot++;
		inv.setItem(slot++, StaffModeItems.healthItem(victim));
		inv.setItem(slot++, StaffModeItems.potEffectsItem(victim));
		staff.updateInventory();
	}
}
