package co.reborncraft.syslogin_banmanager.commands.utils.antibot;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.listeners.AntiBotListener;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ToggleAntiBotCommand extends Command {
	public ToggleAntiBotCommand() {
		super("toggleantibot", null, "antibottoggle");
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (Permissions.permissed(commandSender, Permissions.DEBUG_COMMANDS)) {
			AntiBotListener.toggleAntibot();
			commandSender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_ANTIBOT_TOGGLED.replaceAll("\\{state}", AntiBotListener.isAntibotEnabled() ? "on" : "off")));
		} else {
			commandSender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
