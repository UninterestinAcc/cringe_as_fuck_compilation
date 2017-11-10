package co.reborncraft.stackperms.api.structure;

import java.util.Map;

public interface Saveable {
	/**
	 * Saves data into storage
	 */
	void save();

	/**
	 * Serializes the structure into a map.
	 */
	Map<String, Object> serialize();
}
