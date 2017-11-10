package co.reborncraft.syslogin_banmanager.commands.banmanager;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.syslogin_banmanager.listeners.EventLogger;
import co.reborncraft.utils.Utils;
import jdk.nashorn.internal.runtime.ParserException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class BanCommand extends Command {
	public BanCommand() {
		super("ban", null, "tempban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.MODERATOR)) {
			if (args.length >= 3) {
				final String pName = args[0];
				final String endString = args[1];
				final String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				final ProxiedPlayer player = SysLogin_BanManager.getInstance().getPlayer(pName);
				long endTime;
				if (sender == player) {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_DENY_SELF_PUNISH));
					return;
				}
				if (pName.matches(SysLogin_BanManager.PLAYERNAME_REGEX)) {
					if (sender != SysLogin_BanManager.getInstance().getProxy().getConsole() && !Permissions.permissed(sender, Permissions.SECURITY_BYPASS) && SysLogin_BanManager.getInstance().getStaffList().stream().anyMatch(sn -> sn.equalsIgnoreCase(pName))) {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REJECTED.replaceAll("\\{name}", pName)));
						return;
					}
					try {
						if (endString.toLowerCase().matches(SysLogin_BanManager.PERMANENT_REGEX)) {
							if (Permissions.permissed(sender, Permissions.ADVANCED_MODERATOR)) {
								endTime = 0;
							} else {
								sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
								return;
							}
						} else {
							long time = Utils.parseTime(endString);
							if (time > 5184000) {
								throw new ParserException("Temporary bans can't be longer than 60 days!");
							}
							endTime = System.currentTimeMillis() / 1000 + time;
						}
						Punishment punishment = new Punishment(
								Punishment.PunishmentTarget.USERNAME,
								Punishment.PunishmentType.BAN,
								pName,
								reason,
								System.currentTimeMillis() / 1000,
								endTime,
								sender.getName()
						);
						if (SysLogin_BanManager.getInstance().getPunishmentCache().punish(sender, punishment)) {
							if (player != null) {
								player.disconnect(punishment.toBanTextComponent());
							}
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_SUCCESS.replaceAll("\\{name}", pName)));
							EventLogger.logPunishment(punishment);
						} else {
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REJECTED.replaceAll("\\{name}", pName)));
						}
					} catch (ParserException | NumberFormatException e) {
						sender.sendMessage(Utils.buildTextComponent(e.getClass().getSimpleName() + ": " + e.getMessage(), ChatColor.RED));
					}
				} else {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_INVALID_USERNAME));
				}
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_BAN_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
