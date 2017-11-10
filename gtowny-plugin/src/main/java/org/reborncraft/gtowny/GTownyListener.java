package org.reborncraft.gtowny;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
import org.reborncraft.gtowny.chat.ChatOutput;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.data.Chunk;
import org.reborncraft.gtowny.data.Town;
import org.reborncraft.gtowny.data.TownyDataHandler;
import org.reborncraft.gtowny.data.User;
import org.reborncraft.gtowny.data.internal.TownOptions;
import org.reborncraft.gtowny.data.internal.TownPermissions;
import org.reborncraft.gtowny.data.local.ChunkLocation;

import java.util.*;
import java.util.stream.Collectors;

public class GTownyListener implements Listener {
	@EventHandler (ignoreCancelled = true)
	public void blockForm(EntityChangeBlockEvent e) {
		if (e.getEntity() instanceof FallingBlock) {
			FallingBlock fb = (FallingBlock) e.getEntity();
			Location current = fb.getLocation();
			if (GTowny.getInstance().getBlacklistedEntityLastPosMap().containsKey(fb)) {
				// Destroy check
				Location last = GTowny.getInstance().getBlacklistedEntityLastPosMap().get(fb);
				ChunkLocation lastChunkLoc = ChunkLocation.forLocation(last);
				ChunkLocation currentChunkLoc = ChunkLocation.forLocation(current);
				if (!lastChunkLoc.equals(currentChunkLoc)) {
					Chunk lastChunk = TownyDataHandler.getOrCreateChunk(lastChunkLoc, last.getWorld().getName());
					Chunk currentChunk = TownyDataHandler.getOrCreateChunk(currentChunkLoc, current.getWorld().getName());
					if (currentChunk.getTownId() != -1 && lastChunk.getTownId() != currentChunk.getTownId()) {
						e.setCancelled(true);
					}
				}
			} else {
				GTowny.getInstance().getBlacklistedEntityLastPosMap().put(fb, current);
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onMove(PlayerMoveEvent e) {
		boolean hasAdmin = e.getPlayer().hasPermission("gtowny.admin");
		ChunkLocation fromLoc = ChunkLocation.forLocation(e.getFrom());
		String fromWorld = e.getFrom().getWorld().getName();
		ChunkLocation toLoc = ChunkLocation.forLocation(e.getTo());
		String toWorld = e.getTo().getWorld().getName();
		Chunk fromChunk = TownyDataHandler.getOrCreateChunk(fromLoc, fromWorld);
		Chunk toChunk = TownyDataHandler.getOrCreateChunk(toLoc, toWorld);
		Town fromTown = fromChunk.getTown();
		Town toTown = toChunk.getTown();
		User user = User.forPlayer(e.getPlayer());
		boolean fromPvp = fromTown.getOptions().contains(TownOptions.PvP);
		boolean toPvp = toTown.getOptions().contains(TownOptions.PvP);

		if (!hasAdmin && (toTown.getExiled(user) || toChunk.getExiled(user))) {
			if (e.getTo().getX() != e.getFrom().getX() && e.getTo().getX() != e.getFrom().getX()) {
				ChatOutput.title(e.getPlayer(), "", ChatColor.RED + "You cannot enter " + ChatColor.GRAY + toTown.getName() + ChatColor.RED + ".");
				Location tpTo = GTowny.getInstance().getLastValidMap().containsKey(e.getPlayer()) ? GTowny.getInstance().getLastValidMap().get(e.getPlayer()) : e.getFrom();
				e.getPlayer().teleport(tpTo, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		} else {
			if (e.getPlayer().isSneaking() && !e.getPlayer().isFlying()) {
				if (!hasAdmin && fromTown == TownyDataHandler.TOWN_SAFEZONE && (toTown == TownyDataHandler.TOWN_WARZONE || toTown == TownyDataHandler.TOWN_WILDERNESS)) {
					// Boost.
					double hypotenuse = Math.sqrt(Math.pow(e.getFrom().getX() - e.getTo().getX(), 2) + Math.pow(e.getFrom().getZ() - e.getTo().getZ(), 2));
					double sine = (e.getFrom().getX() - e.getTo().getX()) / hypotenuse;
					double cosine = (e.getFrom().getZ() - e.getTo().getZ()) / hypotenuse;

					Vector kb = new Vector(sine / 1.5, 0.3, cosine / 1.5);
					e.getPlayer().setVelocity(kb);
					e.getPlayer().sendMessage(ChatColor.RED + "Please don't safezone. :P");
				}
			}
			GTowny.getInstance().getLastValidMap().put(e.getPlayer(), e.getTo());
			String fromChunkDesc = MessageFormatter.formatChunkDesc(fromChunk);
			String toChunkDesc = MessageFormatter.formatChunkDesc(toChunk);
			if (!fromChunkDesc.equals(toChunkDesc) || fromPvp != toPvp) {
				GTowny.getScoreboardMan().updateSideBar(e.getPlayer());
				ChatOutput.title(e.getPlayer(), MessageFormatter.getPvPString(toPvp), (!fromChunkDesc.equals(toChunkDesc) ? ChatColor.GOLD + "Entering " + toChunkDesc : ""));
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (!e.getPlayer().hasPermission("gtowny.admin")) {
			ChunkLocation loc = ChunkLocation.forLocation(e.getBlock().getLocation());
			Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, e.getBlock().getWorld().getName());
			User user = User.forPlayer(e.getPlayer());
			if (!(chunk.getMemberIds().contains(user.getUserId()) || chunk.getOwnerId() == user.getUserId())) {
				Town town = chunk.getTown();
				if (town.getOwnerId() != user.getUserId()) {
					if (user.getTownId() == town.getId()) {
						if (!user.getRank().getPermissions().contains(TownPermissions.TownBuild)) {
							e.setCancelled(true);
						}
					} else {
						e.setCancelled(true);
					}
				}
			}
			if (e.isCancelled()) {
				ChatOutput.actionBar(e.getPlayer(), ChatColor.DARK_RED + "" + ChatColor.BOLD + "You can't build here.");
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockInteract(PlayerInteractEvent e) {
		if (!e.getPlayer().hasPermission("gtowny.admin")) {
			Material clicked = e.getClickedBlock().getType();
			if (clicked == Material.CHEST ||
					clicked == Material.TRAPPED_CHEST ||
					clicked == Material.JUKEBOX ||
					clicked == Material.NOTE_BLOCK ||
					clicked == Material.FURNACE ||
					clicked == Material.ACACIA_DOOR ||
					clicked == Material.BIRCH_DOOR ||
					clicked == Material.DARK_OAK_DOOR ||
					clicked == Material.JUNGLE_DOOR ||
					clicked == Material.SPRUCE_DOOR ||
					clicked == Material.TRAP_DOOR ||
					clicked == Material.WOODEN_DOOR) {
				ChunkLocation loc = ChunkLocation.forLocation(e.getClickedBlock().getLocation());
				Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, e.getClickedBlock().getWorld().getName());
				User user = User.forPlayer(e.getPlayer());
				if (!(chunk.getMemberIds().contains(user.getUserId()) || chunk.getOwnerId() == user.getUserId())) {
					Town town = chunk.getTown();
					if (town.getOwnerId() != user.getUserId()) {
						if (user.getTownId() == town.getId()) {
							if (!user.getRank().getPermissions().contains(TownPermissions.TownBuild)) {
								e.setCancelled(true);
							}
						} else {
							e.setCancelled(true);
						}
					}
				}
				if (e.isCancelled()) {
					ChatOutput.actionBar(e.getPlayer(), ChatColor.DARK_RED + "" + ChatColor.BOLD + "You can't open that.");
				}
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!e.getPlayer().hasPermission("gtowny.admin")) {
			ChunkLocation loc = ChunkLocation.forLocation(e.getBlock().getLocation());
			Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, e.getBlock().getWorld().getName());
			User user = User.forPlayer(e.getPlayer());
			if (!(chunk.getMemberIds().contains(user.getUserId()) || chunk.getOwnerId() == user.getUserId())) {
				Town town = chunk.getTown();
				if (town.getOwnerId() != user.getUserId()) {
					if (user.getTownId() == town.getId()) {
						if (!user.getRank().getPermissions().contains(TownPermissions.TownBuild)) {
							e.setCancelled(true);
						}
					} else {
						e.setCancelled(true);
					}
				}
			}
			if (e.isCancelled()) {
				ChatOutput.actionBar(e.getPlayer(), ChatColor.DARK_RED + "" + ChatColor.BOLD + "You can't build here.");
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (!(e.getEntity() instanceof ArmorStand) && !(e.getEntity() instanceof Item)) {
			e.setCancelled(!TownyDataHandler.getOrCreateChunk(ChunkLocation.forLocation(e.getEntity().getLocation()), e.getEntity().getWorld().getName()).getTown().getOptions().contains(TownOptions.MobSpawning));
		}
	}


	@EventHandler (ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent e) {
		Map<ChunkLocation, Boolean> explode = new HashMap<>();
		for (Block b : new ArrayList<>(e.blockList())) {
			ChunkLocation loc = ChunkLocation.forLocation(b.getLocation());
			if (b.getType() == Material.TNT) {
				e.blockList().remove(b); // Prevent chain reactions lagging the fuck out of the server.
			} else if (!explode.containsKey(loc)) {
				Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, b.getWorld().getName());
				Town town = chunk.getTown();
				explode.put(loc, town.getOptions().contains(TownOptions.Explosions));
			}
			if (!explode.get(loc)) {
				e.blockList().remove(b);
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent e) {
		Map<ChunkLocation, Boolean> explode = new HashMap<>();
		for (Block b : new ArrayList<>(e.blockList())) {
			ChunkLocation loc = ChunkLocation.forLocation(b.getLocation());
			if (b.getType() == Material.TNT) e.blockList().remove(b);
			if (!explode.containsKey(loc)) {
				Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, b.getWorld().getName());
				Town town = chunk.getTown();
				explode.put(loc, town.getOptions().contains(TownOptions.Explosions));
			}
			if (!explode.get(loc)) {
				e.blockList().remove(b);
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e) {
		ChunkLocation loc = ChunkLocation.forLocation(e.getBlock().getLocation());
		Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, e.getBlock().getWorld().getName());
		Town town = chunk.getTown();
		if (!town.getOptions().contains(TownOptions.Fire)) {
			e.setCancelled(true);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent e) {
		ChunkLocation loc = ChunkLocation.forLocation(e.getBlock().getLocation());
		Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, e.getBlock().getWorld().getName());
		Town town = chunk.getTown();
		if (!town.getOptions().contains(TownOptions.Fire)) {
			e.setCancelled(true);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockMove(BlockFromToEvent e) {
		ChunkLocation toLoc = ChunkLocation.forLocation(e.getToBlock().getLocation());
		ChunkLocation fromLoc = ChunkLocation.forLocation(e.getBlock().getLocation());
		if (!toLoc.equals(fromLoc)) {
			Chunk toChunk = TownyDataHandler.getOrCreateChunk(toLoc, e.getBlock().getWorld().getName());
			Chunk fromChunk = TownyDataHandler.getOrCreateChunk(fromLoc, e.getBlock().getWorld().getName());
			if ((toChunk.getTownId() != fromChunk.getTownId() && toChunk.getOwnerId() != fromChunk.getOwnerId() && toChunk.getTownId() >= 0) || toChunk.getTownId() < -1) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onPiston(BlockPistonExtendEvent e) {
		List<ChunkLocation> toLocs = new ArrayList<>();
		toLocs.add(ChunkLocation.forLocation(e.getBlock().getRelative(e.getDirection()).getLocation()));
		e.getBlocks().forEach(b -> toLocs.add(ChunkLocation.forLocation(b.getRelative(e.getDirection()).getLocation())));
		ChunkLocation fromLoc = ChunkLocation.forLocation(e.getBlock().getLocation());
		Optional<ChunkLocation> toLoc = toLocs.stream().filter(loc -> !loc.equals(fromLoc)).findFirst();
		if (toLoc.isPresent()) {
			Chunk toChunk = TownyDataHandler.getOrCreateChunk(toLoc.get(), e.getBlock().getWorld().getName());
			Chunk fromChunk = TownyDataHandler.getOrCreateChunk(fromLoc, e.getBlock().getWorld().getName());
			if ((toChunk.getTownId() != fromChunk.getTownId() && toChunk.getOwnerId() != fromChunk.getOwnerId() && toChunk.getTownId() >= 0) || toChunk.getTownId() < -1) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onPiston(BlockPistonRetractEvent e) {
		List<ChunkLocation> toLocs = new ArrayList<>();
		toLocs.add(ChunkLocation.forLocation(e.getBlock().getRelative(e.getDirection().getOppositeFace()).getLocation()));
		e.getBlocks().forEach(b -> toLocs.add(ChunkLocation.forLocation(b.getRelative(e.getDirection().getOppositeFace()).getLocation())));
		ChunkLocation fromLoc = ChunkLocation.forLocation(e.getBlock().getLocation());
		Optional<ChunkLocation> toLoc = toLocs.stream().filter(loc -> !loc.equals(fromLoc)).findFirst();
		if (toLoc.isPresent()) {
			Chunk toChunk = TownyDataHandler.getOrCreateChunk(toLoc.get(), e.getBlock().getWorld().getName());
			Chunk fromChunk = TownyDataHandler.getOrCreateChunk(fromLoc, e.getBlock().getWorld().getName());
			if ((toChunk.getTownId() != fromChunk.getTownId() && toChunk.getOwnerId() != fromChunk.getOwnerId() && toChunk.getTownId() >= 0) || toChunk.getTownId() < -1) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void entityDamage(EntityDamageEvent e) {
		ChunkLocation entityLoc = ChunkLocation.forLocation(e.getEntity().getLocation());
		String world = e.getEntity().getWorld().getName();
		Chunk entityChunk = TownyDataHandler.getOrCreateChunk(entityLoc, world);
		Town entityTown = entityChunk.getTown();
		if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
			e.setCancelled(!entityTown.getOptions().contains(TownOptions.Explosions));
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void entityDamageByEntity(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		Entity damager = e.getDamager();
		if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Entity) {
			damager = (Entity) ((Projectile) e.getDamager()).getShooter();
		}
		ChunkLocation entityLoc = ChunkLocation.forLocation(entity.getLocation());
		ChunkLocation damagerLoc = ChunkLocation.forLocation(damager.getLocation());
		String world = entity.getWorld().getName();
		Chunk entityChunk = TownyDataHandler.getOrCreateChunk(entityLoc, world);
		Town entityTown = entityChunk.getTown();
		if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
			e.setCancelled(!entityTown.getOptions().contains(TownOptions.Explosions));
		}
		if (!e.isCancelled() && damager instanceof Player) {
			if (e.getEntity() instanceof Player) {
				if (entityTown.getOptions().contains(TownOptions.PvP)) {
					e.setCancelled(true);
				} else if (!entityLoc.equals(damagerLoc)) {
					Chunk damagerChunk = TownyDataHandler.getOrCreateChunk(damagerLoc, world);
					Town damagerTown = damagerChunk.getTown();
					e.setCancelled(!damagerTown.getOptions().contains(TownOptions.PvP));
				}
				if (e.isCancelled()) {
					ChatOutput.actionBar((Player) e.getDamager(), ChatColor.DARK_RED + "" + ChatColor.BOLD + "You can't PvP here!");
				}
			} else {
				if (!e.getDamager().hasPermission("gtowny.admin")) {
					User damagerUser = User.forPlayer((Player) damager);
					if (entityTown.getId() > 0 && !entityChunk.canBuild(damagerUser)) {
						e.setCancelled(true);
					}
				}
				if (e.isCancelled()) {
					ChatOutput.actionBar((Player) e.getDamager(), ChatColor.DARK_RED + "" + ChatColor.BOLD + "You can't damage mobs.");
				}
			}
		}
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void townChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		User u = User.forPlayer(p);
		if (u.isInTownChat()) {
			e.getRecipients().stream().filter(rec -> u.getTownId() != User.forPlayer(rec).getTownId()).collect(Collectors.toList()).forEach(rec -> e.getRecipients().remove(rec));
			e.setFormat(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "TC" + ChatColor.DARK_PURPLE + "] " + ChatColor.GOLD + "[" + ChatColor.YELLOW + u.getRank().getName() + ChatColor.GOLD + "] " + ChatColor.YELLOW + p.getName() + ": " + ChatColor.AQUA + e.getMessage()
			);
		} else {
			e.setFormat(GTowny.getVault().getPermissionsPrefix(p) + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + u.getTown().getName() + ChatColor.DARK_GREEN + "] " + ChatColor.AQUA + p.getName() + ": " + ChatColor.GRAY + e.getMessage());
		}
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void chatLogger(AsyncPlayerChatEvent e) {
		Map<Player, List<String>> records = GTowny.getMessageRecords();
		records.keySet().stream().filter(p -> !p.isOnline()).collect(Collectors.toList()).forEach(records::remove);
		e.getRecipients().forEach(re -> {
			List<String> orig;
			if (records.containsKey(re)) {
				orig = records.get(re);
				if (orig.size() >= 5) {
					Optional<String> oldest = orig.stream().findFirst();
					if (oldest.isPresent()) {
						orig.remove(oldest.get());
					}
				}
			} else {
				orig = new ArrayList<>();
				records.put(re, orig);
			}
			orig.add(e.getFormat());
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		User user = User.forPlayer(e.getPlayer());
		long t = System.currentTimeMillis() + 20000;
		if (user.getShieldLastsUntil() < t) {
			user.setShieldLastsUntil(t);
		}
	}
}
