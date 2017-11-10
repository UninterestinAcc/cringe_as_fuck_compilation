package co.reborncraft.stackperms.api;

import co.reborncraft.stackperms.api.structure.Permissible;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface StackPermsInterface {
	/**
	 * Populates the permissions cache from data store.
	 */
	void populateData();

	/**
	 * Gets the groups the player is in.
	 */
	List<Group> getGroups(Player p);

	/**
	 * Gets the last username of the player
	 */
	String getLastUsername(UUID uuid);

	/**
	 * Hooks into the player's PermissibleBase.
	 */
	void hookIntoPlayer(Player player);

	/**
	 * Unhooks the player's custom PermissibleBase.
	 */
	void unhookPlayer(Player player);

	/**
	 * Deletes the player's cached PermissibleBase.
	 */
	void delBukkitPermissibleCacheForPlayer(Player player);

	/**
	 * Clean off the hooks of all offline players.
	 */
	void gcHooks();

	/**
	 * Disassociate a player with all groups and explicit permissions.
	 */
	void destroyAll(Player p);

	/**
	 * Searches for a group by name
	 */
	Optional<Group> getGroupByName(String name);

	/**
	 * Gets all groups that are declared
	 */
	List<Group> getDeclaredGroups();

	/**
	 * Searches for a stack by name
	 */
	Optional<Stack> getStackByName(String name);

	/**
	 * Gets all stacks that are declated
	 */
	List<Stack> getDeclaredStacks();

	/**
	 * Gets the player data object for name
	 */
	SavePlayer getOrCreateSavePlayer(String p);

	/**
	 * Creates a SavePlayer
	 */
	SavePlayer createSavePlayer(String p);

	/**
	 * Returns all players that have a database entry
	 */
	List<SavePlayer> getPlayers();

	/**
	 * Searches the indexPermissions for parents of the perm.
	 */
	List<String> searchForParents(String perm);

	/**
	 * Dumps a clone of the index from indexPermissions.
	 */
	Map<String, Set<String>> getIndexes();

	/**
	 * Indexes children with their parent permissions
	 */
	void indexPermissions();

	/**
	 * Saves the SavePlayer
	 */
	void savePlayer(SavePlayer savePlayer);

	/**
	 * Save all players (ie. group name update re-serialize)
	 */
	void savePlayers();

	/**
	 * Saves the Group
	 */
	void saveGroup(Group group);

	/**
	 * Save all groups
	 */
	void saveGroups();

	/**
	 * Saves the Stack
	 */
	void saveStack(Stack stack);

	/**
	 * Save all stacks
	 */
	void saveStacks();

	/**
	 * Cache and return the permissions
	 */
	Map<String, Boolean> cacheAndReturn(Permissible source, Map<String, Boolean> permissions);

	/**
	 * Returns the cache result from search (if it exists)
	 */
	Optional<Map<String, Boolean>> searchCache(Permissible permissible);

	/**
	 * Invalidates the permissions cache for all permissibles
	 */
	void dropPermissionsCache();

	/**
	 * Invalidates the permissions cache for a specific permissible
	 */
	void dropPermissionsCache(Permissible permissible);

	/**
	 * Deletes the stack if the stack is present (delete by name)
	 */
	default void deleteStackIfPresent(String key) {
		deleteStacksIfPresent(Collections.singletonList(key));
	}

	/**
	 * Deletes the group if the group is present (delete by name)
	 */
	default void deleteGroupIfPresent(String key) {
		deleteGroupsIfPresent(Collections.singletonList(key));
	}

	/**
	 * Deletes the player if the player is present (delete by name)
	 */
	default void deletePlayerIfPresent(String key) {
		deletePlayersIfPresent(Collections.singletonList(key));
	}

	/**
	 * Bulk deletes the stacks if present
	 */
	void deleteStacksIfPresent(Collection<String> keys);

	/**
	 * Bulk deletes the groups if present
	 */
	void deleteGroupsIfPresent(Collection<String> keys);

	/**
	 * Bulk deletes the players if present
	 */
	void deletePlayersIfPresent(Collection<String> keys);

	/**
	 * Turns
	 */
	default List<String> serializePermissions(Map<String, Boolean> permissions) {
		return permissions.entrySet().stream()
				.map(p -> (p.getValue() ? "" : "-") + p.getKey())
				.collect(Collectors.toList());
	}

	/**
	 * Returns a consumer that adds to {target} while deserializing a List of permissions that may contain a leading -
	 */
	default Consumer<String> deserializePermissionsInto(Map<String, Boolean> target) {
		return perm -> {
			boolean not = perm.startsWith("-");
			target.put(not ? perm.toLowerCase().substring(1) : perm, !not);
		};
	}

	/**
	 * Saves everything that is queued to save.
	 */
	void runSaveRoutine();
}
