package io.github.loldatsec.mcplugins.haloplus.utils;

import java.util.ArrayList;
import java.util.List;

public class ExclusiveList<T> {

	public List<T> list;

	public ExclusiveList(List<T> l) {
		list = l;
	}

	public ExclusiveList() {
		list = new ArrayList<T>();
	}

	public void add(T o) {
		if (!contains(o)) {
			list.add(o);
		}
	}

	public void remove(T o) {
		if (contains(o)) {
			list.remove(o);
		}
	}

	public boolean contains(T o) {
		return list.contains(o);
	}

	public void clear() {
		list.clear();
	}

	public int size() {
		return list.size();
	}
}
