package co.reborncraft.syslogin_banmanager.api.objects;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

public enum Permissions {
	HELPER(0, "Issue warns, kicks and mutes."),
	MODERATOR(1, "Issue temp bans."),
	ADVANCED_MODERATOR(2, "Revoke punishments, perm ban."),
	ADMIN(3, "Alt check and allow register."),
	USER_INFO(4, "Get detailed user info."),
	SECURITY_BYPASS(5, "Bypass security checks."),
	DEBUG_COMMANDS(6, "Access to debugging commands."),
	MANAGE(7, "Modify perms of staffs.");

	// Can have 0-62 for signed 64 long.

	private final int order;
	private final String description;

	Permissions(int order, String description) {
		this.order = order;
		this.description = description;
	}

	public static String getRank(long permissions) {
		if (permissions == -1) {
			return "Owner / Sr. Admin";
		} else if ((permissions >> 3) % 2 == 1) {
			return "Administrator";
		} else if ((permissions >> 2) % 2 == 1) {
			return "Sr. Moderator";
		} else if ((permissions >> 1) % 2 == 1) {
			return "Moderator";
		} else if (permissions % 2 == 1) {
			return "Helper";
		}
		return "No Ranks";
	}


	public static Permissions fromOrder(int pos) {
		for (Permissions perm : values()) {
			if (perm.order() == pos) {
				return perm;
			}
		}
		return null;
	}

	public static long allPermissions() {
		return -1;
	}

	public static Permissions[] get(long bits) {
		return bits == -1 ? values() : Arrays.stream(values()).filter(perm -> has(perm, bits)).toArray(Permissions[]::new);
	}

	public static long addPermission(Permissions perm, long bits) {
		if (bits == -1) {
			return bits;
		}
		if (!has(perm, bits)) {
			bits += 1 << perm.order();
		}
		return bits;
	}

	public static long removePermission(Permissions perm, long bits) {
		if (bits == -1) {
			return bits;
		}
		if (has(perm, bits)) {
			bits -= (1 << perm.order());
		}
		return bits;

	}

	public static long toBits(Permissions... perms) {
		long bits = 0;
		if (perms != null) {
			for (Permissions perm : perms) {
				bits += 1 << perm.order();
			}
		}
		return bits;
	}

	public static boolean has(Permissions perm, long bits) {
		return (bits >> perm.order()) % 2 == 1 || bits == -1;
	}

	public static boolean permissed(CommandSender sender, Permissions perm) {
		return !(sender instanceof ProxiedPlayer) || Permissions.has(perm, SysLogin_BanManager.getInstance().getStaffPermissions(sender.getName()));
	}

	public static TextComponent toChatRepresentation(long bits) {
		TextComponent wrapper = new TextComponent();
		wrapper.setColor(ChatColor.AQUA);
		if (bits == 0 || bits < -1) {
			wrapper.setText("None.");
		} else {
			for (Permissions perm : get(bits)) {
				TextComponent permComp = new TextComponent(perm.name());

				TextComponent permIDComp = new TextComponent("[" + perm.order() + "] ");
				permIDComp.setColor(ChatColor.YELLOW);
				permComp.addExtra(permIDComp);

				TextComponent permDescComp = new TextComponent(perm.getDescription());
				permDescComp.setColor(ChatColor.DARK_GREEN);

				permComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{permDescComp}));

				wrapper.addExtra(permComp);
			}
		}
		return wrapper;
	}

	public int order() {
		return order;
	}

	public String getDescription() {
		return description;
	}
}
