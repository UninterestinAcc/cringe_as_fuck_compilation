package org.reborncraft.gtowny;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.reborncraft.gtowny.cmds.*;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.data.Chunk;
import org.reborncraft.gtowny.data.TownyDataHandler;
import org.reborncraft.gtowny.data.User;
import org.reborncraft.gtowny.data.local.ChunkLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GTowny extends JavaPlugin implements Listener {
	private static File dataFolder = null;
	private static TownyDataHandler dataHandler = null;
	private static final Map<Player, List<String>> messageRecords = new HashMap<>();
	private final ConcurrentHashMap<Player, Location> lastValid = new ConcurrentHashMap<>();
	private final Map<Entity, Location> blacklistedEntityLastPos = new HashMap<>();

	private static GTowny instance;
	private static GTownyListener listener;
	private static GTownyScoreboardManager scoreboardMan;

	private static ChunkCommand chunkCE;
	private static TownCommand townCE;
	private static TownyCommand townyCE;
	private static GTownyCommand gtownyCE;
	private static UserCommand userCE;
	private static GTownyVaultInterface economy;

	@Override
	public void onEnable() {
		// Hook up...
		instance = this;

		// DataFolder initialization.
		dataFolder = new File(getDataFolder().getAbsolutePath() + "/" + "data");
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}

		// Config initialization.
		getConfig().options().copyDefaults(true); // NOTE: CONFIG SHOULD ONLY CONTAIN SQL CREDENTIALS, ALL OTHER DATA SHOULD GO IN THE TOWNY DATA DIR.
		saveConfig();

		// DataHandler initialization.
		dataHandler = new TownyDataHandler(getConfig().getString("sqlserver"), getConfig().getInt("sqlport"), getConfig().getString("sqlusername"), getConfig().getString("sqlpassword"));

		// GTownySubcommand Execution
		townCE = new TownCommand();
		getCommand("t").setExecutor(townCE);
		getCommand("town").setExecutor(townCE);
		getCommand("towns").setExecutor(townCE);

		chunkCE = new ChunkCommand();
		getCommand("c").setExecutor(chunkCE);
		getCommand("chunk").setExecutor(chunkCE);
		getCommand("chunks").setExecutor(chunkCE);

		userCE = new UserCommand();
		getCommand("user").setExecutor(userCE);

		townyCE = new TownyCommand();
		getCommand("towny").setExecutor(townyCE);

		gtownyCE = new GTownyCommand();
		getCommand("gtowny").setExecutor(gtownyCE);

		getCommand("spawn").setExecutor(new SpawnCommand());

		getCommand("tc").setExecutor(new TCCommand());

		listener = new GTownyListener();
		Bukkit.getPluginManager().registerEvents(listener, this);

		scoreboardMan = new GTownyScoreboardManager();
		Bukkit.getPluginManager().registerEvents(scoreboardMan, this);

		economy = new GTownyVaultInterface();

		Bukkit.getWorlds().forEach(world -> {
			WorldBorder worldBorder = world.getWorldBorder();
			worldBorder.setCenter(0, 0);
			worldBorder.setSize(950245);
			worldBorder.setDamageAmount(1);
			worldBorder.setDamageBuffer(1);
			worldBorder.setWarningDistance(5);
			worldBorder.setWarningTime(10);
		});

		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			long time = System.currentTimeMillis();
			System.out.println("Loading all chunks...");
			TownyDataHandler.getAllChunks();
			System.out.println("Loaded all chunks. (" + (System.currentTimeMillis() - time) + "ms)");
		});
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getWorlds().forEach(world -> world.getEntitiesByClasses(FallingBlock.class, TNTPrimed.class).forEach(fb -> {
			Location current = fb.getLocation();
			if (blacklistedEntityLastPos.containsKey(fb)) {
				Location last = blacklistedEntityLastPos.get(fb);
				ChunkLocation lastChunkLoc = ChunkLocation.forLocation(last);
				ChunkLocation currentChunkLoc = ChunkLocation.forLocation(current);
				if (!lastChunkLoc.equals(currentChunkLoc)) {
					Chunk lastChunk = TownyDataHandler.getOrCreateChunk(lastChunkLoc, last.getWorld().getName());
					Chunk currentChunk = TownyDataHandler.getOrCreateChunk(currentChunkLoc, current.getWorld().getName());
					if (currentChunk.getTownId() != -1 && lastChunk.getTownId() != currentChunk.getTownId()) {
						fb.teleport(last);
						current = last;
						fb.setVelocity(new Vector());
					}
				}
			}
			blacklistedEntityLastPos.put(fb, current);
		})), 0, 1); // Block-entity tracker / safety
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers().forEach(p -> {
			User u = User.forPlayer(p);
			if (u.isShieldActive()) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1));
			}
		}), 0, 20L); // Energy shield manager

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> scoreboardMan.updateScoreboards(), 0, 15L); // NameTag pusher
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			AtomicInteger deletes = new AtomicInteger(0);
			lastValid.keySet().stream().filter(player -> !player.isOnline()).collect(Collectors.toList()).forEach(player -> {
				lastValid.remove(player);
				deletes.getAndIncrement();
			});
			blacklistedEntityLastPos.keySet().stream().filter(blp -> !blp.isValid()).collect(Collectors.toList()).forEach(blp -> {
				blacklistedEntityLastPos.remove(blp);
				deletes.getAndIncrement();
			});
			TownyDataHandler.getInvites().forEach((town, invites) -> invites.forEach((userId, time) -> {
				if (time < System.currentTimeMillis()) {
					invites.remove(userId, time);
				}
			}));
			System.out.println("[GTowny] Deleted " + deletes.get() + " entries with garbage cleaner.");
		}, 200, 1200L); // Garbage cleaner
	}

	@Override
	public void onDisable() {
	}

	public static GTowny getInstance() {
		return instance;
	}

	public static File getGTownyDataFolder() {
		return dataFolder;
	}

	public static TownyDataHandler getGTownyDataHandler() {
		return dataHandler;
	}

	public static ChunkCommand getChunkCE() {
		return chunkCE;
	}

	public static TownCommand getTownCE() {
		return townCE;
	}

	public static TownyCommand getTownyCE() {
		return townyCE;
	}

	public static GTownyCommand getGtownyCE() {
		return gtownyCE;
	}

	public static TownyDataHandler getDataHandler() {
		return TownyDataHandler.getInstance();
	}

	public static UserCommand getUserCE() {
		return userCE;
	}

	public Map<Entity, Location> getBlacklistedEntityLastPosMap() {
		return blacklistedEntityLastPos;
	}

	public ConcurrentHashMap<Player, Location> getLastValidMap() {
		return lastValid;
	}

	public static GTownyListener getListener() {
		return listener;
	}

	public static GTownyScoreboardManager getScoreboardMan() {
		return scoreboardMan;
	}

	public static GTownyVaultInterface getVault() {
		return economy;
	}

	public static String getTownPrefixForPlayer(Player p) {
		return MessageFormatter.getTownName(User.forPlayer(p).getTown()) + " " + ChatColor.AQUA;
	}

	public static String getTownRankPrefixForPlayer(Player p) {
		User user = User.forPlayer(p);
		return ChatColor.YELLOW + user.getRank().getName() + " " + ChatColor.AQUA;
	}

	public static List<String> getRecentMessages(Player p) {
		return messageRecords.containsKey(p) ? messageRecords.get(p) : new ArrayList<>();
	}

	public static Map<Player, List<String>> getMessageRecords() {
		return messageRecords;
	}
}
