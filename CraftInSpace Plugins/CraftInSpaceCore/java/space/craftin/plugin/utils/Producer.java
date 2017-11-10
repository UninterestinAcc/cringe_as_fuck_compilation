/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.utils;

/**
 * As opposed to a consumer, we have a producer.
 *
 * @param <T> Type of output expected.
 */
public interface Producer<T> {
	T apply();
}
