package co.reborncraft.stackperms.utils;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.SavePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class StackPermissible extends PermissibleBase {
	private final Player p;

	public StackPermissible(Player p, ServerOperator opable) {
		super(opable);
		this.p = p;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return hasPermission(perm.getName());
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return isPermissionSet(perm.getName());
	}

	@Override
	public boolean isPermissionSet(String name) {
		return hasPermission(name);
	}

	@Override
	public boolean hasPermission(String perm) {
		SavePlayer sp = StackPerms.getInterface().getOrCreateSavePlayer(p.getName());
		Permission bukkitPerm;
		if (sp.hasPermission(perm)) {
			return true;
		} else if ((bukkitPerm = Bukkit.getPluginManager().getPermission(perm)) != null) {
			return bukkitPerm.getDefault().getValue(p.isOp());
		}
		return false;
	}

	@Override
	public void recalculatePermissions() {
	}

	@Override
	public synchronized void clearPermissions() {
		StackPerms.getInterface().destroyAll(p);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		throw new IllegalStateException("Permissibles are already hooked into by StackPerms.");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		throw new IllegalStateException("Permissibles are already hooked into by StackPerms.");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		throw new IllegalStateException("Permissibles are already hooked into by StackPerms.");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		throw new IllegalStateException("Permissibles are already hooked into by StackPerms.");
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		throw new IllegalStateException("Permissibles are already hooked into by StackPerms.");
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		throw new IllegalStateException("Permissibles are already hooked into by StackPerms.");
	}
}
