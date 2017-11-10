package co.reborncraft.stackperms.impl;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.SavePlayer;
import co.reborncraft.stackperms.api.Stack;
import co.reborncraft.stackperms.api.StackPermsInterface;
import co.reborncraft.stackperms.api.structure.Named;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SavePlayerImpl implements SavePlayer {
	private final String playerName;
	private List<String> groups = new ArrayList<>();
	private Map<String, Boolean> permissions = new HashMap<>();
	private String prefix = null;
	private String tabPrefix = null;
	private String suffix = null;
	private AtomicBoolean saveQueued = new AtomicBoolean(false);

	public SavePlayerImpl(String playerName) {
		this.playerName = playerName;
	}

	public SavePlayerImpl(String name, ConfigurationSection sect) {
		playerName = name;
		prefix = sect.getString("prefix", null);
		tabPrefix = sect.getString("tabPrefix", null);
		suffix = sect.getString("suffix", null);
		if (sect.contains("permissions") && sect.isList("permissions")) {
			sect.getStringList("permissions").forEach(StackPerms.getInterface().deserializePermissionsInto(permissions));
		}
		if (sect.contains("groups") && sect.isList("groups")) {
			sect.getStringList("groups").forEach(groups::add);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		saveQueued.set(false);
		Map<String, Object> serialize = new HashMap<>();
		if (prefix != null) {
			serialize.put("prefix", prefix);
		}
		if (tabPrefix != null) {
			serialize.put("tabPrefix", tabPrefix);
		}
		if (suffix != null) {
			serialize.put("suffix", suffix);
		}
		if (permissions.size() > 0) {
			serialize.put("permissions", StackPerms.getInterface().serializePermissions(permissions));
		}
		if (groups.size() > 0) {
			StackPermsInterface spi = StackPerms.getInterface();
			serialize.put("groups", groups.stream()
					.map(spi::getGroupByName)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.map(Named::getName)
					.collect(Collectors.toList())
			);
		}
		return serialize;
	}

	@Override
	public void save() {
		if (!saveQueued.get()) {
			StackPerms.getInterface().savePlayer(this);
			saveQueued.set(true);
		}
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		save();
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
		save();
	}

	@Override
	public String getTabPrefix() {
		return tabPrefix;
	}

	@Override
	public void setTabPrefix(String tabPrefix) {
		this.tabPrefix = tabPrefix;
		save();
	}

	@Override
	public void setPermissions(Map<String, Boolean> permissions) {
		this.permissions.clear();
		permissions.forEach(this.permissions::put);
		save();
	}

	@Override
	public Map<String, Boolean> getExplicitPermissions() {
		return new HashMap<>(permissions);
	}

	@Override
	public List<Group> getGroups() {
		StackPermsInterface spi = StackPerms.getInterface();
		List<Group> explicitGroups = getExplicitGroups();

		spi.getDeclaredStacks().stream()
				.map(Stack::getDefaultGroup)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(defaultGroup -> explicitGroups.stream().noneMatch(g -> g.inSameStack(defaultGroup)))
				.forEach(explicitGroups::add);

		return explicitGroups;
	}

	@Override
	public List<Group> getDefaultGroups() {
		StackPermsInterface spi = StackPerms.getInterface();
		List<Group> explicitGroups = getExplicitGroups();
		return spi.getDeclaredStacks().stream()
				.map(Stack::getDefaultGroup)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(defaultGroup -> explicitGroups.stream().noneMatch(g -> g.inSameStack(defaultGroup)))
				.collect(Collectors.toList());
	}

	@Override
	public List<Group> getExplicitGroups() {
		StackPermsInterface spi = StackPerms.getInterface();
		return groups.stream()
				.map(spi::getGroupByName)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	@Override
	public boolean hasGroupExplicitly(Group group) {
		String groupName = group.getName();
		return groups.stream().anyMatch(groupName::equalsIgnoreCase);
	}

	@Override
	public void removeGroup(Group group) {
		String groupName = group.getName();
		groups.removeIf(groupName::equalsIgnoreCase);
		save();
	}

	@Override
	public void addGroup(Group group) {
		String groupName = group.getName();
		group.getStack().ifPresent(s -> s.getGroups().stream()
				.map(Named::getName)
				.forEach(groups::remove));
		if (groups.stream().noneMatch(groupName::equalsIgnoreCase)) {
			groups.add(groupName);
		}
		save();
	}

	@Override
	public void addPermissions(Map<String, Boolean> permissions) {
		permissions.forEach(this.permissions::put);
		save();
	}

	@Override
	public void removePermissions(Map<String, Boolean> permissions) {
		this.permissions.entrySet().stream()
				.filter(ps -> permissions.containsKey(ps.getKey()) && permissions.get(ps.getKey()) == ps.getValue())
				.collect(Collectors.toList())
				.forEach(ps -> this.permissions.remove(ps.getKey(), ps.getValue()));
		save();
	}

	@Override
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(playerName);
	}

	@Override
	public void clearGroups() {
		groups.clear();
		save();
	}

	@Override
	public void cleanGroupStacking() {
		List<String> newGroups = new ArrayList<>();
		Map<Stack, Group> highestGroup = new HashMap<>();
		groups.forEach(g -> {
			StackPerms.getInterface().getGroupByName(g).ifPresent(group -> {
				Optional<Stack> s = group.getStack();
				if (s.isPresent()) {
					Stack stack = s.get();
					if (highestGroup.containsKey(stack)) {
						if (highestGroup.get(stack).getRank() < group.getRank()) {
							highestGroup.put(stack, group);
						}
					} else {
						highestGroup.put(stack, group);
					}
				} else {
					newGroups.add(group.getName());
				}
			});
		});
		highestGroup.values().forEach(g -> newGroups.add(g.getName()));
		groups.clear();
		groups.addAll(newGroups);
		save();
	}

	@Override
	public void clearCustomData() {
		prefix = null;
		suffix = null;
		tabPrefix = null;
		groups.clear();
		permissions.clear();
		save();
	}

	@Override
	public String getName() {
		return playerName;
	}
}
