package co.reborncraft.stackperms.api;

import co.reborncraft.stackperms.api.structure.PermissionManagementType;
import co.reborncraft.stackperms.api.structure.Renameable;

import java.util.List;
import java.util.Optional;

public interface Stack extends Renameable, PermissionManagementType {
	/**
	 * The weight of this stack.
	 */
	double getWeight();

	/**
	 * Sets the weight of this stack
	 */
	void setWeight(double weight);

	/**
	 * The groups that are in this stack (ordered)
	 * <p>This is a clone of the list of groups in backend.</p>
	 */
	List<Group> getGroups();

	/**
	 * Change the group list (use this for reordering ranks)
	 */
	void setGroups(List<Group> groups);

	/**
	 * Adds the group to the stack on the lowest priority
	 */
	void addGroup(Group group);

	/**
	 * Removes the group from the stack
	 */
	void removeGroup(Group group);

	/**
	 * Returns the default group for this stack
	 */
	Optional<Group> getDefaultGroup();

	/**
	 * Sets the default group for this stack, null to disable
	 */
	void setDefaultGroup(Group group);
}
