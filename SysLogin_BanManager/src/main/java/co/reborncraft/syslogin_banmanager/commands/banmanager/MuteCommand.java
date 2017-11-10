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

public class MuteCommand extends Command {
	public MuteCommand() {
		super("mute", null, "tempmute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.HELPER)) {
			if (args.length >= 3) {
				final String pName = args[0];
				final String endString = args[1];
				final String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				final ProxiedPlayer player = SysLogin_BanManager.getInstance().getPlayer(pName);
				if (player != null) {
					if (sender == player) {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_DENY_SELF_PUNISH));
						return;
					}
					if (sender != SysLogin_BanManager.getInstance().getProxy().getConsole() && !Permissions.permissed(sender, Permissions.SECURITY_BYPASS) && SysLogin_BanManager.getInstance().getStaffList().stream().anyMatch(sn -> sn.equalsIgnoreCase(pName))) {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REJECTED.replaceAll("\\{name}", pName)));
						return;
					}

					try {
						long time = Utils.parseTime(endString);
						if (time > 86400) {
							throw new ParserException("Mutes can't be longer than 1 day!");
						}
						Punishment punishment = new Punishment(
								Punishment.PunishmentTarget.USERNAME,
								Punishment.PunishmentType.MUTE,
								player.getName(),
								reason,
								System.currentTimeMillis() / 1000,
								System.currentTimeMillis() / 1000 + time,
								sender.getName()
						);
						if (SysLogin_BanManager.getInstance().getPunishmentCache().punish(sender, punishment)) {
							player.sendMessage(punishment.toMuteTextComponents());
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_SUCCESS.replaceAll("\\{name}", player.getName())));
							EventLogger.logPunishment(punishment);
						} else {
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REJECTED.replaceAll("\\{name}", player.getName())));
						}
					} catch (ParserException | NumberFormatException e) {
						sender.sendMessage(Utils.buildTextComponent(e.getClass().getSimpleName() + ": " + e.getMessage(), ChatColor.RED));
					}
				} else {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_PLAYER_NOT_FOUND));
				}
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_MUTE_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
