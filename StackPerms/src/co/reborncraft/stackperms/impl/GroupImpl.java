package co.reborncraft.stackperms.impl;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.SavePlayer;
import co.reborncraft.stackperms.api.Stack;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GroupImpl implements Group {
	private String name;
	private String prefix;
	private String tabPrefix;
	private String suffix;
	private String stack;
	private double rank;
	private Map<String, Boolean> permissions = new HashMap<>();

	private AtomicBoolean saveQueued = new AtomicBoolean(false);

	public GroupImpl(String name) {
		this.name = name;
	}

	public GroupImpl(String name, ConfigurationSection sect) {
		this.name = name;
		prefix = sect.getString("prefix", null);
		tabPrefix = sect.getString("tabPrefix", null);
		suffix = sect.getString("suffix", null);

		stack = sect.getString("stack", null);
		rank = sect.getDouble("rank", 0D);

		if (sect.contains("permissions") && sect.isList("permissions")) {
			sect.getStringList("permissions").forEach(StackPerms.getInterface().deserializePermissionsInto(permissions));
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		List<SavePlayer> groupPlayers = StackPerms.getInterface().getPlayers().stream()
				.filter(p -> p.hasGroupExplicitly(this))
				.collect(Collectors.toList());
		groupPlayers.forEach(this::removePlayer);
		this.name = name;
		groupPlayers.forEach(this::addPlayer);
		save();
		StackPerms.getInterface().savePlayers();
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
	public void save() {
		if (!saveQueued.get()) {
			StackPerms.getInterface().saveGroup(this);
			saveQueued.set(true);
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
		if (stack != null) {
			serialize.put("stack", stack);
		}
		if (rank != 0D) {
			serialize.put("rank", rank);
		}
		if (permissions.size() > 0) {
			serialize.put("permissions", StackPerms.getInterface().serializePermissions(permissions));
		}
		return serialize;
	}

	@Override
	public double getRank() {
		return rank;
	}

	@Override
	public void setRank(double rank) {
		this.rank = rank;
		save();
	}

	@Override
	public Optional<Stack> getStack() {
		return StackPerms.getInterface().getStackByName(stack);
	}

	@Override
	public void setStack(Stack stack) {
		this.stack = stack == null ? null : stack.getName();
		save();
	}

	@Override
	public List<SavePlayer> getPlayers() {
		return StackPerms.getInterface().getPlayers().stream()
				.filter(p -> p.getGroups().contains(this))
				.collect(Collectors.toList());
	}

	@Override
	public boolean inSameStack(Group group) {
		return this.stack != null && group != null && group instanceof GroupImpl && this.stack.equals(((GroupImpl) group).stack);
	}

	@Override
	public void setPermissions(Map<String, Boolean> permissions) {
		this.permissions.clear();
		permissions.forEach(this.permissions::put);
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
	public Map<String, Boolean> getExplicitPermissions() {
		return permissions;
	}
}
