package co.reborncraft.stackperms.impl;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.SavePlayer;
import co.reborncraft.stackperms.api.Stack;
import co.reborncraft.stackperms.api.StackPermsInterface;
import co.reborncraft.stackperms.api.structure.Named;
import co.reborncraft.stackperms.api.structure.Permissible;
import co.reborncraft.stackperms.api.structure.Saveable;
import co.reborncraft.stackperms.utils.ReflectionsUtil;
import co.reborncraft.stackperms.utils.StackPermissible;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class StackPermsImpl implements StackPermsInterface {
	private final List<Stack> stacks = new ArrayList<>();
	private final List<Group> groups = new ArrayList<>();
	private final List<SavePlayer> players = new ArrayList<>();
	private final Map<Class<? extends Saveable>, Runnable> savingBuffer = new HashMap<>();
	private Map<Player, Object> hooks = new HashMap<>();
	private Map<String, Set<String>> childToParentPermsIndex = new HashMap<>();
	private Map<Permissible, Map<String, Boolean>> permissionsCache = new HashMap<>();
	private AtomicBoolean saverLock = new AtomicBoolean(false);

	@Override
	public void populateData() {
		players.clear();
		groups.clear();
		stacks.clear();
		ConcurrentMap<String, YamlConfiguration> confMap = StackPerms.getInstance().getConfMap();
		ConfigurationSection confPlayers = confMap.get("players").getConfigurationSection("players");
		if (confPlayers != null) {
			confPlayers.getKeys(false).forEach(playerName ->
					players.add(new SavePlayerImpl(
							playerName,
							confPlayers.getConfigurationSection(playerName)
					)));
		}
		ConfigurationSection confGroups = confMap.get("groups").getConfigurationSection("groups");
		if (confGroups != null) {
			confGroups.getKeys(false).forEach(groupName ->
					groups.add(new GroupImpl(
							groupName,
							confGroups.getConfigurationSection(groupName)
					)));
		}
		ConfigurationSection confStacks = confMap.get("stacks").getConfigurationSection("stacks");
		if (confStacks != null) {
			confStacks.getKeys(false).forEach(stackName ->
					stacks.add(new StackImpl(
							stackName,
							confStacks.getConfigurationSection(stackName)
					)));
		}
	}

	@Override
	public List<Group> getGroups(Player p) {
		return getOrCreateSavePlayer(p.getName()).getGroups();
	}

	@Override
	public String getLastUsername(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid).getName();
	}

	@Override
	public void hookIntoPlayer(Player player) {
		try {
			Class<?> clazz = ReflectionsUtil.getCBClass("entity.CraftHumanEntity");
			Field permField = clazz.getDeclaredField("perm");
			permField.setAccessible(true);
			hooks.put(player, permField.get(player));
			permField.set(player, new StackPermissible(player, player));
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unhookPlayer(Player player) {
		try {
			Class<?> clazz = ReflectionsUtil.getCBClass("entity.CraftHumanEntity");
			Field permField = clazz.getDeclaredField("perm");
			permField.setAccessible(true);
			if (hooks.containsKey(player)) {
				permField.set(player, hooks.get(player));
				hooks.remove(player);
			} else {
				player.kickPlayer("Error occurred during permissions unhook.");
			}
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delBukkitPermissibleCacheForPlayer(Player player) {
		if (hooks.containsKey(player)) {
			hooks.remove(player);
		}
	}

	@Override
	public void gcHooks() {
		hooks.keySet().stream()
				.filter(p -> !p.isOnline())
				.collect(Collectors.toList())
				.forEach(hooks::remove);
	}

	@Override
	public void destroyAll(Player p) {
		SavePlayer sp = getOrCreateSavePlayer(p.getName());
		sp.clearCustomData();
		sp.clearGroups();
	}

	@Override
	public Optional<Group> getGroupByName(String name) {
		return groups.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst();
	}

	@Override
	public List<Group> getDeclaredGroups() {
		return new ArrayList<>(groups);
	}

	@Override
	public Optional<Stack> getStackByName(String name) {
		return stacks.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
	}

	@Override
	public List<Stack> getDeclaredStacks() {
		return new ArrayList<>(stacks);
	}

	@Override
	public SavePlayer getOrCreateSavePlayer(String p) {
		Optional<SavePlayer> first = players.stream().filter(sp -> sp.getName().equalsIgnoreCase(p)).findFirst();
		return first.isPresent() ? first.get() : createSavePlayer(p);
	}

	@Override
	public SavePlayer createSavePlayer(String p) {
		return new SavePlayerImpl(p);
	}

	@Override
	public List<SavePlayer> getPlayers() {
		return new ArrayList<>(players);
	}

	@Override
	public List<String> searchForParents(String perm) {
		perm = perm.toLowerCase();
		return childToParentPermsIndex.containsKey(perm) ? new ArrayList<>(childToParentPermsIndex.get(perm)) : new ArrayList<>();
	}

	@Override
	public Map<String, Set<String>> getIndexes() {
		return new HashMap<>(childToParentPermsIndex);
	}

	@Override
	public void indexPermissions() {
		StackPerms.getInstance().getLogger().info("Beginning an index on permissions.");
		long nano = System.nanoTime();
		Map<String, Set<String>> childToParentIndex = new HashMap<>();
		Bukkit.getPluginManager().getPermissions().forEach(perm -> {
			if (perm.getChildren() != null) {
				perm.getChildren().entrySet().stream()
						.filter(Map.Entry::getValue)
						.map(Map.Entry::getKey)
						.map(String::toLowerCase)
						.forEach(child -> {
							if (!childToParentIndex.containsKey(child)) {
								childToParentIndex.put(child, new HashSet<>());
							}
							childToParentIndex.get(child).add(perm.getName().toLowerCase());
						});
			}
		});
		childToParentPermsIndex = childToParentIndex;
		StackPerms.getInstance().getLogger().info("Indexing of permissions took " + (System.nanoTime() - nano) + "ns.");
	}

	private void overwriteCheck(List<? extends Named> namedEntities, Named named) {
		final String name = named.getName();
		namedEntities.stream()
				.filter(n -> n.getName().equalsIgnoreCase(name))
				.collect(Collectors.toList())
				.forEach(namedEntities::remove);
	}

	private void writeToSaveBuffer(Class<? extends Saveable> saveClass, Runnable saveLambda) {
		savingBuffer.put(saveClass, saveLambda);
	}

	@Override
	public void savePlayer(SavePlayer savePlayer) {
		String saveName = savePlayer.getPlayer().getName();
		Map<String, Object> serialized = savePlayer.serialize();
		if (serialized.size() > 0) {
			if (!players.contains(savePlayer)) {
				overwriteCheck(players, savePlayer);
				players.add(savePlayer);
			}
			savePlayers();
		} else {
			players.stream().filter(sp -> sp.getPlayer().getName().equalsIgnoreCase(saveName)).findAny().ifPresent(players::remove);
		}
	}

	@Override
	public void savePlayers() {
		writeToSaveBuffer(SavePlayer.class, () -> {
			try {
				while (saverLock.get()) {
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			saverLock.set(true);
			Map<String, Object> saveMap = new HashMap<>();
			synchronized (players) {
				players.forEach(sp -> saveMap.put(sp.getPlayer().getName(), sp.serialize()));
			}
			saveFile("players", saveMap);
			saverLock.set(false);
		});
	}

	@Override
	public void saveGroup(Group group) {
		if (!groups.contains(group)) {
			overwriteCheck(groups, group);
			groups.add(group);
		}
		saveGroups();
	}

	@Override
	public void saveGroups() {
		writeToSaveBuffer(Group.class, () -> {
			try {
				while (saverLock.get()) {
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			saverLock.set(true);
			Map<String, Object> saveMap = new HashMap<>();
			synchronized (groups) {
				groups.forEach(g -> saveMap.put(g.getName(), g.serialize()));
			}
			saveFile("groups", saveMap);
			saverLock.set(false);
		});
	}

	@Override
	public void saveStack(Stack stack) {
		if (!stacks.contains(stack)) {
			overwriteCheck(stacks, stack);
			stacks.add(stack);
		}
		saveStacks();
	}

	@Override
	public void saveStacks() {
		writeToSaveBuffer(Stack.class, () -> {
			try {
				while (saverLock.get()) {
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			saverLock.set(true);
			Map<String, Object> saveMap = new HashMap<>();
			synchronized (stacks) {
				stacks.forEach(g -> saveMap.put(g.getName(), g.serialize()));
			}
			saveFile("stacks", saveMap);
			saverLock.set(false);
		});
	}

	private void saveFile(String saveName, Map<String, Object> saveMap) {
		YamlConfiguration conf = StackPerms.getInstance().getConfMap().get(saveName);
		conf.set(saveName, saveMap);
		File ymlFile = new File(StackPerms.getInstance().getDataFolder(), "permissions/" + saveName + ".yml");
		File savFile = new File(StackPerms.getInstance().getDataFolder(), "permissions/" + saveName + ".sav");
		try {
			conf.save(savFile);
			if (ymlFile.exists()) {
				if (!ymlFile.delete()) {
					StackPerms.getInstance().getLogger().log(Level.SEVERE, "Failed to delete old " + saveName + ".yml");
				}
			}
			if (!savFile.renameTo(ymlFile)) {
				StackPerms.getInstance().getLogger().log(Level.SEVERE, "Failed to rename " + saveName + ".sav -> " + saveName + ".yml");
			} else {
				StackPerms.getInstance().getLogger().log(Level.INFO, "Saved " + saveName + ".yml");
			}
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Boolean> cacheAndReturn(Permissible source, Map<String, Boolean> permissions) {
		/*
		permissionsCache.put(source, permissions);
		return new HashMap<>(permissions);
		*/
		return permissions;
	}

	@Override
	public Optional<Map<String, Boolean>> searchCache(Permissible permissible) {
		return permissionsCache.entrySet().stream()
				.filter(e -> e.getKey().equals(permissible))
				.map(Map.Entry::getValue)
				.map(m -> (Map<String, Boolean>) new HashMap<>(m))
				.findAny();
	}

	@Override
	public void dropPermissionsCache() {
		permissionsCache.clear();
	}

	@Override
	public void dropPermissionsCache(Permissible permissible) {
		if (permissionsCache.containsKey(permissible)) {
			permissionsCache.remove(permissible);
		}
	}

	@Override
	public void deleteStacksIfPresent(Collection<String> keys) {
		stacks.stream()
				.filter(na -> {
					String name = na.getName();
					return keys.stream().anyMatch(name::equalsIgnoreCase);
				})
				.collect(Collectors.toList())
				.forEach(stacks::remove);
		saveStacks();
	}

	@Override
	public void deletePlayersIfPresent(Collection<String> keys) {
		players.stream()
				.filter(na -> {
					String name = na.getName();
					return keys.stream().anyMatch(name::equalsIgnoreCase);
				})
				.collect(Collectors.toList())
				.forEach(players::remove);
		savePlayers();
	}

	@Override
	public void runSaveRoutine() {
		Set<Runnable> runs;
		synchronized (savingBuffer) {
			runs = new HashSet<>(savingBuffer.values());
			savingBuffer.clear();
		}
		runs.forEach(Runnable::run);
	}

	@Override
	public void deleteGroupsIfPresent(Collection<String> keys) {
		groups.stream()
				.filter(na -> {
					String name = na.getName();
					return keys.stream().anyMatch(name::equalsIgnoreCase);
				})
				.collect(Collectors.toList())
				.forEach(groups::remove);
		saveGroups();
	}
}
