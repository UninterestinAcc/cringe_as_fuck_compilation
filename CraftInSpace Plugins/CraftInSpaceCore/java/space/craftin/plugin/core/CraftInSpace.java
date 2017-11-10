/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import space.craftin.plugin.commands.factions.FactionsCommandsContainer;
import space.craftin.plugin.commands.factions.ShipCommandsContainer;
import space.craftin.plugin.core.api.alliance.IAlliance;
import space.craftin.plugin.core.api.core.functions.IClaimable;
import space.craftin.plugin.core.api.core.functions.IIdentifiable;
import space.craftin.plugin.core.api.core.functions.IIdentifiableByName;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.api.physical.station.IStation;
import space.craftin.plugin.core.api.physical.vehicles.IDrone;
import space.craftin.plugin.core.api.physical.vehicles.IShip;
import space.craftin.plugin.core.api.physical.vehicles.IVehicle;
import space.craftin.plugin.core.impl.alliance.Alliance;
import space.craftin.plugin.core.impl.core.faction.Faction;
import space.craftin.plugin.core.impl.core.players.Astronaut;
import space.craftin.plugin.core.impl.core.utils.AxisAlignedBB;
import space.craftin.plugin.core.impl.physical.block.Block;
import space.craftin.plugin.core.impl.physical.station.Station;
import space.craftin.plugin.core.impl.physical.vehicles.Drone;
import space.craftin.plugin.core.impl.physical.vehicles.Ship;
import space.craftin.plugin.utils.BooleanWrappedType;
import space.craftin.plugin.utils.Producer;
import space.craftin.plugin.utils.Snowflake;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CraftInSpace extends JavaPlugin {
	public static final String VNAME_PREFIX = "\u00a77VEHICLE_";
	public static final String DNAME_PREFIX = "\u00a77DRONE_";

	private static CraftInSpace instance;
	private ProtocolManager protoManager;
	private ConcurrentMap<UUID, IAstronaut> astronautCache = new ConcurrentHashMap<>();
	private ConcurrentMap<Integer, ConcurrentMap<Integer, IIdentifiable>> identifiableCache = new ConcurrentHashMap<>();
	private ConcurrentMap<String, YamlConfiguration> configCache = new ConcurrentHashMap<>();

	public static CraftInSpace getInstance() {
		return instance;
	}

	public static List<ArmorStand> getNearbyArmorStands(Player p, final double distance) {
		if (distance <= 0) {
			throw new IllegalArgumentException("Cannot have a negative radius.");
		}
		final Chunk c = p.getLocation().getChunk();
		final double distanceSq = Math.pow(distance, 2);
		List<ArmorStand> armorStands = new ArrayList<>();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					armorStands.addAll(Arrays.stream(c.getWorld().getChunkAt(c.getX() + x, c.getZ() + z).getEntities())
							.filter(e -> e instanceof ArmorStand)
							.map(e -> (ArmorStand) e)
							.filter(e -> e.getEyeLocation().distanceSquared(p.getEyeLocation()) < distanceSq)
							.collect(Collectors.toList())
					);
				}
			}
		}
		return armorStands;
	}

	@Override
	public void onLoad() {
		protoManager = ProtocolLibrary.getProtocolManager();
	}

	public synchronized BooleanWrappedType<YamlConfiguration> getCustomConfig(String configName) {
		if (configCache.containsKey(configName)) {
			return new BooleanWrappedType<>(false, configCache.get(configName));
		} else {
			boolean newlyCreated = false;

			File configFile = new File(getDataFolder(), configName + ".yml");
			if (!configFile.exists()) {
				newlyCreated = true;
				URL jarConfig = CraftInSpace.class.getResource("/configs/" + configName + ".yml");
				try {
					if (configFile.createNewFile() && jarConfig != null) {
						FileUtils.copyURLToFile(jarConfig, configFile);
					} else {
						configFile.createNewFile();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			final YamlConfiguration customConfig = YamlConfiguration.loadConfiguration(configFile);
			if (customConfig.getList(configName) == null) {
				customConfig.set(configName, new ArrayList<>());
			}
			configCache.put(configName, customConfig);
			return new BooleanWrappedType<>(customConfig, newlyCreated);
		}
	}

	public synchronized void saveCustomConfig(String configName, YamlConfiguration config) {
		File customConfig = new File(getDataFolder(), configName + ".yml");
		try {
			if (!customConfig.exists()) {
				customConfig.createNewFile();
			}
			config.save(customConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized IIdentifiable cachedLoad(long snowflake, String configName) {
		if (snowflake <= 0) {
			return null;
		}
		int snowflakeSeq1 = (int) (snowflake >> 32);
		int snowflakeSeq2 = (int) snowflake;

		if (identifiableCache.containsKey(snowflakeSeq1) && identifiableCache.get(snowflakeSeq1).containsKey(snowflakeSeq2)) {
			return identifiableCache.get(snowflakeSeq1).get(snowflakeSeq2);
		}

		if (!identifiableCache.containsKey(snowflakeSeq1)) {
			identifiableCache.put(snowflakeSeq1, new ConcurrentHashMap<>());
		}
		YamlConfiguration conf = getCustomConfig(configName).getValue();
		Optional<IIdentifiable> result = conf.getList(configName).stream().filter(obj -> ((IIdentifiable) obj).getSnowflake() == snowflake).map(obj -> (IIdentifiable) obj).findFirst();
		if (result.isPresent()) {
			identifiableCache.get(snowflakeSeq1).put(snowflakeSeq2, result.get());
			return result.get();
		}
		return null;
	}

	public synchronized <T extends IIdentifiable> T cachedCreate(String configName, long snowflake, Producer<T> createMethod) {
		int snowflakeSeq1 = (int) (snowflake >> 32);
		int snowflakeSeq2 = (int) snowflake;

		T result = createMethod.apply();
		if (!identifiableCache.containsKey(snowflakeSeq1)) {
			identifiableCache.put(snowflakeSeq1, new ConcurrentHashMap<>());
		}
		YamlConfiguration conf = getCustomConfig(configName).getValue();
		((List<IIdentifiable>) conf.getList(configName)).add(result);
		identifiableCache.get(snowflakeSeq1).put(snowflakeSeq2, result);
		return result;
	}

	public Optional<? extends IIdentifiableByName> searchFor(String targetName, String configName) {
		return searchFor(IIdentifiableByName.class, obj -> obj.getName().equalsIgnoreCase(targetName), configName);
	}

	public <T extends IIdentifiable> Optional<T> searchFor(Class<T> clazz, Predicate<T> filter, String configName) {
		YamlConfiguration conf = getCustomConfig(configName).getValue();
		return conf.getList(configName)
				.parallelStream()
				.filter(clazz::isInstance)
				.map(obj -> (T) obj)
				.filter(filter)
				.findFirst();
	}

	public <T extends IVehicle> Optional<T> searchForVehicle(Predicate<T> filter, String... configNames) {
		Optional<T> opt;
		for (String configName : configNames) {
			opt = searchFor((Class<T>) IVehicle.class, filter, configName);
			if (opt != null && opt.isPresent()) {
				return opt;
			}
		}
		return Optional.empty();
	}

	public List<? extends IClaimable> getAllAssociated(Class<? extends IClaimable> clazz, long factionSnowflake, String configName) {
		YamlConfiguration conf = getCustomConfig(configName).getValue();
		return conf.getList(configName)
				.parallelStream()
				.filter(clazz::isInstance)
				.map(obj -> (IClaimable) obj)
				.filter(obj -> obj.getFactionSnowflake() == factionSnowflake)
				.collect(Collectors.toList());
	}

	@Override
	public void onEnable() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		instance = this;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			astronautCache.keySet().stream().filter(uuid -> !astronautCache.get(uuid).getPlayer().isOnline()).forEach(uuid -> astronautCache.remove(uuid));
		}, 0, 1200); // Minutely cache cleaner.

		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			configCache.forEach(this::saveCustomConfig);
		}, 100L, 200L);

		FactionsCommandsContainer facsCmdContainer = new FactionsCommandsContainer();
		for (String cmd : new String[]{"f", "fac", "facs", "faction", "factions"}) {
			getCommand(cmd).setExecutor(facsCmdContainer);
		}

		ShipCommandsContainer shipCmdContainer = new ShipCommandsContainer();
		for (String cmd : new String[]{"s", "ship"}) {
			getCommand(cmd).setExecutor(shipCmdContainer);
		}

		for (Class clazz : new Class[]{Alliance.class, Astronaut.class, AxisAlignedBB.class, Block.class, Drone.class, Faction.class, Ship.class, Station.class}) {
			ConfigurationSerialization.registerClass(clazz);
		}
	}

	@Override
	public void onDisable() {
		configCache.forEach(this::saveCustomConfig);
	}

	public ProtocolManager getProtoManager() {
		return protoManager;
	}

	public List<IAstronaut> getAllAstronauts() {
		YamlConfiguration conf = getCustomConfig("astronauts").getValue();
		return (List<IAstronaut>) conf.getList("astronauts");
	}

	public synchronized IAstronaut getOrCreateAstronaut(Player p) {
		return getOrCreateAstronaut(p.getUniqueId());
	}

	public synchronized IAstronaut getOrCreateAstronaut(UUID uuid) {
		if (astronautCache.containsKey(uuid)) {
			return astronautCache.get(uuid);
		} else {
			YamlConfiguration conf = getCustomConfig("astronauts").getValue();
			IAstronaut astronaut;
			Optional<IAstronaut> astronautSearch = ((List<IAstronaut>) conf.getList("astronauts"))
					.parallelStream()
					.filter(a -> a.getUuid().equals(uuid)).findFirst();
			if (astronautSearch.isPresent()) {
				astronaut = astronautSearch.get();
			} else {
				astronaut = new Astronaut(uuid);
				((List<IAstronaut>) conf.getList("astronauts")).add(astronaut);
			}

			astronautCache.put(uuid, astronaut);
			return astronaut;
		}
	}

	public synchronized IAlliance getAlliance(long id) {
		return (IAlliance) cachedLoad(id, "alliances");
	}

	public synchronized IFaction createFaction(String name, Player owner) {
		return createFaction(name, getOrCreateAstronaut(owner));
	}

	public synchronized IFaction createFaction(String name, IAstronaut owner) {
		if (getFactionByName(name).isPresent()) {
			return null;
		}
		try {
			long snowflake = Snowflake.getServer().generate();
			return cachedCreate("factions", snowflake, () -> new Faction(snowflake, name, owner));
		} catch (InterruptedException | InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized IFaction getFaction(long id) {
		return (IFaction) cachedLoad(id, "factions");
	}

	public synchronized IDrone getDrone(long id) {
		return (IDrone) cachedLoad(id, "drones");
	}

	public synchronized IShip getShip(long id) {
		return (IShip) cachedLoad(id, "ships");
	}

	public synchronized IStation getStation(long id) {
		return (IStation) cachedLoad(id, "stations");
	}

	public void playMissileExplodingParticles(Location loc) {
		loc.getWorld().playEffect(loc, Effect.EXPLOSION_HUGE, 1);
		playParticle(loc, Effect.FLAME, 2);
	}

	public void playParticle(Location loc, Effect eff) {
		playParticle(loc, eff, 1);
	}

	public synchronized void playParticle(Location loc, Effect eff, int particleQuality) {
		loc.getWorld().getPlayers().stream().filter(p -> p.getLocation().distanceSquared(loc) < 625).forEach(p -> {
			int q = getOrCreateAstronaut(p).getParticlesQuality();
			if (q >= particleQuality) p.playEffect(loc, eff, ((int) (Math.pow(q, 2) * 5)));
		});
	}

	public Optional<IFaction> getFactionByName(String name) {
		Optional<IIdentifiableByName> result = (Optional<IIdentifiableByName>) searchFor(name, "factions");
		if (result.isPresent()) {
			return Optional.ofNullable((IFaction) result.get());
		} else {
			return Optional.empty();
		}
	}

	public Optional<IAlliance> getAllianceByName(String name) {
		Optional<IIdentifiableByName> result = (Optional<IIdentifiableByName>) searchFor(name, "alliance");
		if (result.isPresent()) {
			return Optional.ofNullable((IAlliance) result.get());
		} else {
			return Optional.empty();
		}
	}

	public IShip createShip(Player p, IAstronaut astronaut) {
		try {
			long snowflake = Snowflake.getServer().generate();
			return cachedCreate("ships", snowflake, () -> new Ship(snowflake, p, astronaut.getFactionSnowflake()));
		} catch (InterruptedException | InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<IShip> getShips() {
		return (List<IShip>) getCustomConfig("ships").getValue().getList("ships");
	}
}
