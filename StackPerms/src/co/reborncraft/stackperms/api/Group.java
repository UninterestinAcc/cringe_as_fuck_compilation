package co.reborncraft.stackperms.api;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.structure.Fixable;
import co.reborncraft.stackperms.api.structure.Permissible;
import co.reborncraft.stackperms.api.structure.PermissionManagementType;
import co.reborncraft.stackperms.api.structure.Renameable;

import java.util.*;

public interface Group extends Permissible, Fixable, Renameable, PermissionManagementType {
	/**
	 * Rank of this group in the stack
	 */
	double getRank();

	/**
	 * Changes the group's rank in the stack
	 */
	void setRank(double rank);

	/**
	 * Gets the stack associated with this group
	 * <p>
	 * If you wish to change the group's stack, please do it through the Stack interface.
	 */
	Optional<Stack> getStack();

	/**
	 * Sets the stack of the group
	 */
	void setStack(Stack stack);

	/**
	 * Gets the players in this group by their name
	 */
	List<SavePlayer> getPlayers();

	/**
	 * Get permissions calculated for the group
	 */
	@Override
	default Map<String, Boolean> getPermissions() {
		Optional<Map<String, Boolean>> permsCached = StackPerms.getInterface().searchCache(this);
		if (permsCached.isPresent()) {
			return permsCached.get();
		}
		Optional<Stack> stack = getStack();
		List<Group> groups = stack.isPresent() ? stack.get().getGroups() : new ArrayList<>();
		groups.sort(Comparator.comparingDouble(g -> -g.getRank()));
		Map<String, Boolean> perms = new HashMap<>();
		boolean p = false;
		for (Group group : groups) {
			if (group == this) {
				break;
			}
			perms.putAll(group.getExplicitPermissions());
		}
		perms.putAll(this.getExplicitPermissions());
		return StackPerms.getInterface().cacheAndReturn(this, perms);
	}

	/**
	 * Check if the two groups are in the same stack
	 */
	boolean inSameStack(Group group);

	/**
	 * Adds players to the current group
	 */
	default void addPlayer(SavePlayer player) {
		player.addGroup(this);
	}

	/**
	 * Removes players to the current group if they are in this
	 */
	default void removePlayer(SavePlayer player) {
		player.removeGroup(this);
	}
}
