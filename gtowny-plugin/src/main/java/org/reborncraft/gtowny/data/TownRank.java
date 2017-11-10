package org.reborncraft.gtowny.data;


import org.reborncraft.gtowny.data.inheritable.ModifiableTownyObject;
import org.reborncraft.gtowny.data.internal.TownPermissions;

import java.util.List;
import java.util.stream.Collectors;

public final class TownRank implements ModifiableTownyObject {
	private String name;
	private final List<TownPermissions> permissions;
	private final int id;

	public TownRank(String name, int id, List<TownPermissions> permissions) {
		this.name = name;
		this.id = id;
		this.permissions = permissions;
	}

	public void setName(String name) {
		this.name = name;
		pushUpdate();
	}

	public boolean hasPermission(TownPermissions perm) {
		return perm != null && (permissions.contains(perm) || permissions.contains(TownPermissions.TownAdmin));
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public List<TownPermissions> getPermissions() {
		return permissions;
	}

	@Override
	public String toString() {
		return "TownRank[name=" + name + ",perms=" + permissions.stream().map(TownPermissions::toString).collect(Collectors.joining("|")) + "]";
	}
}
