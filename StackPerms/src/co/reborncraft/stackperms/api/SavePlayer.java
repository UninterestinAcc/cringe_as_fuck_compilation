package co.reborncraft.stackperms.api;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.structure.*;
import org.bukkit.OfflinePlayer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SavePlayer extends Permissible, Fixable, PermissionManagementType, Named, Groupable {

	/**
	 * @return the associated player
	 */
	OfflinePlayer getPlayer();

	/**
	 * Get permissions calculated for the group
	 */
	@Override
	default Map<String, Boolean> getPermissions() {
		Optional<Map<String, Boolean>> permsCached = StackPerms.getInterface().searchCache(this);
		if (permsCached.isPresent()) {
			return permsCached.get();
		}
		Map<String, Boolean> permsMap = new HashMap<>();
		getGroups().stream()
				.sorted(Comparator.comparingDouble(g -> {
					Optional<Stack> stack = g.getStack();
					return stack.isPresent() ? stack.get().getWeight() : 0D;
				}))
				.map(Group::getPermissions)
				.forEach(permsMap::putAll);
		permsMap.putAll(getExplicitPermissions());
		return StackPerms.getInterface().cacheAndReturn(this, permsMap);
	}

	/**
	 * Gets the permission that this player possesses
	 */
	default Map<String, Boolean> getCalculatedPermissions() {
		HashMap<String, Boolean> perms = new HashMap<>();
		getGroups().stream()
				.sorted(Comparator.comparingDouble(g -> {
					Optional<Stack> stack = g.getStack();
					return stack.map(Stack::getWeight).orElse(0D);
				}))
				.forEach(g -> perms.putAll(g.getPermissions()));
		perms.putAll(getExplicitPermissions());
		return perms;
	}

	/**
	 * Gets the calculated prefix
	 */
	default String getCalculatedPrefix() {
		if (getPrefix() != null) {
			return getPrefix();
		}
		return getGroups().stream()
				.sorted(Comparator.comparingDouble(g -> {
					Optional<Stack> stack = g.getStack();
					return stack.isPresent() ? stack.get().getWeight() : 0D;
				}))
				.map(Fixable::getPrefix)
				.collect(Collectors.joining(" "));
	}

	/**
	 * Gets the calculated tab prefix
	 */
	default String getCalculatedTabPrefix() {
		if (getTabPrefix() != null) {
			return getTabPrefix();
		}
		Optional<Group> opt = getGroups().stream().sorted(Comparator.comparingDouble(g -> {
			Optional<Stack> stack = g.getStack();
			return stack.isPresent() ? stack.get().getWeight() : 0D;
		})).findFirst();
		return opt.map(Group::getTabPrefix).orElse("");
	}

	/**
	 * Gets the calculated suffix
	 */
	default String getCalculatedSuffix() {
		if (getSuffix() != null) {
			return getSuffix();
		}
		Optional<Group> opt = getGroups().stream().sorted(Comparator.comparingDouble(g -> {
			Optional<Stack> stack = g.getStack();
			return stack.isPresent() ? stack.get().getWeight() : 0D;
		})).findFirst();
		return opt.map(Group::getSuffix).orElse("> ");
	}

	/**
	 * Remove all custom permissions and *fixes from the player
	 */
	void clearCustomData();
}
