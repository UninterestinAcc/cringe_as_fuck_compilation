package co.reborncraft.syslogin_banmanager.commands.banmanager;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnmuteCommand extends Command {
	public UnmuteCommand() {
		super("unmute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.ADVANCED_MODERATOR)) {
			if (args.length >= 1) {
				final String target = args[0];
				if (SysLogin_BanManager.getInstance().getPunishmentCache().unmute(sender, target)) {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_REVOKE_SUCCESS.replaceAll("\\{name}", target)));
				} else {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_TARGET_NOT_FOUND));
				}
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.BM_UNMUTE_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
