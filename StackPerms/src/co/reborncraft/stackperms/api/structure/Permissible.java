package co.reborncraft.stackperms.api.structure;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.SavePlayer;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.stream.Stream;

public interface Permissible {
	/**
	 * Gets the permissions of this object.
	 */
	Map<String, Boolean> getPermissions();

	/**
	 * Sets permission according to the list (if the permission is prefixed with - then it's explicitly no)
	 */
	default void setPermissions(List<String> permissions) {
		Map<String, Boolean> set = new HashMap<>();
		permissions.forEach(perm -> {
			boolean isNegative = perm.startsWith("-");
			set.put(isNegative ? perm.substring(1) : perm, !isNegative);
		});
		setPermissions(set);
	}

	/**
	 * Sets the permissions according to the map
	 */
	void setPermissions(Map<String, Boolean> permissions);

	/**
	 * Adds the permission, taking a leading - into account.
	 */
	default void addPermission(String perm) {
		boolean isNegative = perm.startsWith("-");
		addPermission(isNegative ? perm.substring(1) : perm, !isNegative);
	}

	/**
	 * Bulk adds permissions, taking a leading - into account.
	 */
	default void addPermissions(Collection<String> permissions) {
		addPermissions(permissions.stream());
	}

	/**
	 * Bulk adds permissions, taking a leading - into account.
	 */
	default void addPermissions(Stream<String> permissions) {
		Map<String, Boolean> permissionMap = new HashMap<>();
		permissions
				.filter(p -> p.matches("^([\\w\\-.]+(\\.*)?|\\*)$"))
				.forEach(StackPerms.getInterface().deserializePermissionsInto(permissionMap));
		addPermissions(permissionMap);
	}

	/**
	 * Adds the permission, but takes the explicit yes and no instead of -.
	 */
	default void addPermission(String perm, boolean explicitYes) {
		addPermissions(Collections.singletonMap(perm, explicitYes));
	}

	/**
	 * Bulk adds permissions, but takes the explicit yes and no instead of -.
	 */
	void addPermissions(Map<String, Boolean> permissions);

	/**
	 * Removes the permission, searches for the explicit yes/no state.
	 */
	default void removePermission(String perm) {
		boolean isNegative = perm.startsWith("-");
		removePermission(isNegative ? perm.substring(1) : perm, !isNegative);
	}

	/**
	 * Removes the permission, searches for the explicit yes/no state.
	 */
	default void removePermission(String perm, boolean explicitYes) {
		removePermissions(Collections.singletonMap(perm, explicitYes));
	}

	/**
	 * Bulk removes permissions, searches for the explicit yes/no state.
	 */
	default void removePermissions(Collection<String> permissions) {
		removePermissions(permissions.stream());
	}

	/**
	 * Bulk removes permissions, searches for the explicit yes/no state.
	 */
	default void removePermissions(Stream<String> permissions) {
		Map<String, Boolean> permissionMap = new HashMap<>();
		permissions.forEach(StackPerms.getInterface().deserializePermissionsInto(permissionMap));
		removePermissions(permissionMap);
	}

	/**
	 * Bulk removes permissions, searches for the explicit yes/no state.
	 */
	void removePermissions(Map<String, Boolean> permissions);

	/**
	 * Gets the permissions that are explicitly declared by this object.
	 */
	Map<String, Boolean> getExplicitPermissions();

	/**
	 * Check if the object has the permission.
	 */
	default boolean hasPermission(String perm) {
		return hasPermission(perm, true);
	}

	/**
	 * Check if the object has the permission.
	 */
	default boolean hasPermission(String in, boolean recursive) {
		String perm = in.toLowerCase();
		Optional<Map.Entry<String, Boolean>> first = getPermissions().entrySet().stream().filter(p -> {
			String pk = p.getKey().toLowerCase();
			if (pk.equals("*")) {
				return true;
			} else if (pk.endsWith("*")) {
				String pref = pk.substring(0, pk.length() - 1);
				return perm.startsWith(pref);
			} else {
				return pk.equalsIgnoreCase(perm);
			}
		}).sorted(Comparator.comparingInt(e -> e.getKey().length())).findFirst();
		Permission bukkitPerm = Bukkit.getPluginManager().getPermission(perm);
		boolean isOp = (this instanceof SavePlayer) && ((SavePlayer) this).getPlayer().isOp();
		boolean fallback = bukkitPerm != null && bukkitPerm.getDefault().getValue(isOp);
		if ((first.isPresent() ? first.get().getValue() : fallback)) {
			return true;
		}
		if (recursive && bukkitPerm != null) {
			List<String> permissions = StackPerms.getInterface().searchForParents(perm);
			return permissions.stream().anyMatch(this::hasPermission);
		}
		return false;
	}
}
