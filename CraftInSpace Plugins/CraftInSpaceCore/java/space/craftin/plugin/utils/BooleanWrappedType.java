/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.utils;

public class BooleanWrappedType<T> {
	private final T value;
	private final boolean state;

	public BooleanWrappedType(T value) {
		this(value, true);
	}

	public BooleanWrappedType(T value, boolean state) {
		this.value = value;
		this.state = state;
	}

	public BooleanWrappedType(boolean state, T value) {
		this(value, state);
	}

	public boolean isState() {
		return state;
	}

	public T getValue() {
		return value;
	}
}
