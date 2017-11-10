package co.reborncraft.syslogin_banmanager.commands.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class AllowRegisterCommand extends Command {
	public AllowRegisterCommand() {
		super("allowregister");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.ADMIN)) {
			if (args.length == 1) {
				SysLogin_BanManager.getInstance().allowRegister(args[0]);
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_ALLOW_REGISTER_SUCCESS));
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_ALLOW_REGISTER_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
