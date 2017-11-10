/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.core.functions;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface IIdentifiable extends ConfigurationSerializable {
	long getSnowflake();
}
