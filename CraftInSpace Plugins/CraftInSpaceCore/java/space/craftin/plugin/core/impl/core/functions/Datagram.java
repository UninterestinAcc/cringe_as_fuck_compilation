/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.core.functions;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import space.craftin.plugin.core.api.core.functions.IMoveableBlockCollection;
import space.craftin.plugin.core.api.core.functions.NoSerialization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Datagram implements ConfigurationSerializable {
	protected Datagram() {
	}

	public Datagram(Map<String, Object> deserialize) {
		Arrays.stream(this.getClass().getDeclaredFields()).forEach(field -> {
			if (field.getAnnotation(NoSerialization.class) == null) {
				field.setAccessible(true);
				if (deserialize.containsKey(field.getName())) {
					Object obj = deserialize.get(field.getName());

					try {
						field.set(this, obj);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		});

		if (this instanceof IMoveableBlockCollection) {
			((IMoveableBlockCollection) this).getBlocks().removeAll(
					((IMoveableBlockCollection) this).getBlocks().stream()
							.filter(block -> block.getEntity() == null)
							.collect(Collectors.toList())
			);
		}
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		Arrays.stream(this.getClass().getDeclaredFields()).forEach(field -> {
			if (field.getAnnotation(NoSerialization.class) == null) {
				field.setAccessible(true);
				try {
					Object obj = field.get(this);

					map.put(field.getName(), obj);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		});
		return map;
	}

	@Override
	public String toString() {
		final Map<String, Object> serialized = serialize();
		return this.getClass().getSimpleName() + "[" + serialized.keySet().stream().map(fieldName ->
				fieldName + "=" + serialized.get(fieldName)
		).collect(Collectors.joining(", ")) + "]";
	}
}
