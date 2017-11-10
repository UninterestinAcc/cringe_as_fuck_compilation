package co.reborncraft.stackperms.api.structure;

import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.Stack;

import java.util.List;
import java.util.Optional;

public interface Groupable {
	/**
	 * Gets groups the groupable is in.
	 *
	 * @return Returns default for all ladders if none is found, if there are no defaults, an empty list will be returned.
	 */
	List<Group> getGroups();

	/**
	 * Gets all default groups (not explicitly declared but still posessed by the player.)
	 */
	List<Group> getDefaultGroups();

	/**
	 * Gets groups the groupable is in that are explicitly declared.
	 */
	List<Group> getExplicitGroups();

	/**
	 * Check if the groupable has this group
	 */
	boolean hasGroupExplicitly(Group group);

	/**
	 * Removes group from groupable
	 */
	void removeGroup(Group group);

	/**
	 * Adds group to groupable
	 */
	void addGroup(Group group);

	/**
	 * Promotes the player, removing their lower ranking groups in the same stack.
	 *
	 * @return false if the promoting group's rank is lower than one that the player already (of the same stack.)
	 */
	default boolean promoteIfPossible(Group group) {
		Optional<Stack> addingStack = group.getStack();
		if (addingStack.isPresent() && getExplicitGroups().stream()
				.anyMatch(existingGroup -> {
					Optional<Stack> existingStack = existingGroup.getStack();
					return existingStack.isPresent() && existingStack.get().equals(addingStack.get()) && existingGroup.getRank() < group.getRank();
				})) {
			return false;
		}
		addGroup(group);
		return true;
	}

	/**
	 * Disassociates group data with the groupable
	 */
	void clearGroups();

	/**
	 * Fixes all the group stacking problem by cleaning out lower ranks.
	 */
	void cleanGroupStacking();
}
