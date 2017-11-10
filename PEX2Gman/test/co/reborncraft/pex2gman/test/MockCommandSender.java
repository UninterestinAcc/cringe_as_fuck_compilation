package co.reborncraft.pex2gman.test;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class MockCommandSender implements CommandSender {
	@Override
	public void sendMessage(String s) {
		System.out.println("===================\n  SendMessage: " + s);
	}

	@Override
	public void sendMessage(String[] strings) {
		System.out.println("===================\n  SendMessage: " + String.join(", ", strings));
	}

	@Override
	public Server getServer() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isPermissionSet(String s) {
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return false;
	}

	@Override
	public boolean hasPermission(String s) {
		return false;
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return false;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int i) {
		return null;
	}

	@Override
	public void removeAttachment(PermissionAttachment permissionAttachment) {

	}

	@Override
	public void recalculatePermissions() {

	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public void setOp(boolean b) {

	}
}
