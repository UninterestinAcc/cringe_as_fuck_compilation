package co.reborncraft.syslogin_banmanager.commands.banmanager;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.syslogin_banmanager.listeners.EventLogger;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class KickCommand extends Command {
	public KickCommand() {
		super("kick");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.HELPER)) {
			if (args.length >= 2) {
				final String pName = args[0];
				final String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				final ProxiedPlayer player = SysLogin_BanManager.getInstance().getPlayer(pName);
				if (sender == player) {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_DENY_SELF_PUNISH));
					return;
				}
				if (sender != SysLogin_BanManager.getInstance().getProxy().getConsole() && !Permissions.permissed(sender, Permissions.SECURITY_BYPASS) && SysLogin_BanManager.getInstance().getStaffList().stream().anyMatch(sn -> sn.equalsIgnoreCase(pName))) {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REJECTED.replaceAll("\\{name}", pName)));
					return;
				}
				
				Punishment punishment = new Punishment(
						Punishment.PunishmentTarget.USERNAME,
						Punishment.PunishmentType.KICK,
						pName,
						reason,
						System.currentTimeMillis() / 1000,
						0,
						sender.getName()
				);
				if (player != null) {
					player.disconnect(punishment.toKickTextComponent());
				}
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_SUCCESS.replaceAll("\\{name}", player.getName())));
				EventLogger.logPunishment(punishment);
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_KICK_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
