package co.reborncraft.stackperms.impl;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.Stack;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StackImpl implements Stack {
	private String name;
	private String defaultGroup;
	private double weight;
	private AtomicBoolean saveQueued = new AtomicBoolean(false);

	public StackImpl(String name) {
		this.name = name;
	}

	public StackImpl(String name, ConfigurationSection sect) {
		this.name = name;
		this.defaultGroup = sect.getString("defaultGroup", null);
		this.weight = sect.getDouble("weight", 0D);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		List<Group> groups = getGroups();
		groups.forEach(this::removeGroup);
		this.name = name;
		groups.forEach(this::addGroup);
		save();
		StackPerms.getInterface().saveGroups();
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
		save();
	}

	@Override
	public List<Group> getGroups() {
		return StackPerms.getInterface().getDeclaredGroups().stream()
				.filter(g -> {
					Optional<Stack> stack = g.getStack();
					return stack.isPresent() && stack.get().equals(this);
				})
				.sorted(Comparator.comparingDouble(Group::getRank))
				.collect(Collectors.toList());
	}

	@Override
	public void setGroups(List<Group> groups) {
		getGroups().stream()
				.filter(g -> !groups.contains(g))
				.forEach(g -> g.setStack(null));
		AtomicInteger inc = new AtomicInteger();
		groups.forEach(g -> {
			g.setStack(this);
			g.setRank(inc.getAndIncrement());
		});
	}

	@Override
	public void addGroup(Group group) {
		group.setStack(this);
	}

	@Override
	public void removeGroup(Group group) {
		Optional<Stack> gs = group.getStack();
		if (gs.isPresent() && gs.get() == this) {
			group.setStack(null);
			save();
		}
	}

	@Override
	public Optional<Group> getDefaultGroup() {
		return defaultGroup == null ? Optional.empty() : StackPerms.getInterface().getGroupByName(defaultGroup);
	}

	@Override
	public void setDefaultGroup(Group group) {
		defaultGroup = group == null ? null : group.getName();
		save();
	}

	@Override
	public void save() {
		if (!saveQueued.get()) {
			StackPerms.getInterface().saveStack(this);
			saveQueued.set(true);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		saveQueued.set(false);
		Map<String, Object> serialize = new HashMap<>();
		if (defaultGroup != null) {
			serialize.put("defaultGroup", defaultGroup);
		}
		if (weight != 0D) {
			serialize.put("weight", 0D);
		}
		return serialize;
	}
}
