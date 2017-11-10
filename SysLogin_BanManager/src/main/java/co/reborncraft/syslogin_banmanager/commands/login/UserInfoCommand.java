package co.reborncraft.syslogin_banmanager.commands.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

// TODO
public class UserInfoCommand extends Command {
	public UserInfoCommand() {
		super("userinfo");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.USER_INFO)) {
			sender.sendMessage(Utils.buildTextComponent("To be made...", ChatColor.RED));
			// TODO
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
