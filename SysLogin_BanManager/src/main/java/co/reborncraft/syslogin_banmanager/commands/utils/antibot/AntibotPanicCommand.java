package co.reborncraft.syslogin_banmanager.commands.utils.antibot;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.listeners.AntiBotListener;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class AntibotPanicCommand extends Command {
	public AntibotPanicCommand() {
		super("antibotpanic", null, "stoplogin");
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (Permissions.permissed(commandSender, Permissions.DEBUG_COMMANDS)) {
			if (AntiBotListener.isAntibotEnabled()) {
				AntiBotListener.toggleAntibotPanic();
				commandSender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aAntibot panic mode is \u00a7d" + (AntiBotListener.isAntibotEnabled() ? "enabled" : "disabled")));
			} else {
				commandSender.sendMessage(Utils.buildTextComponent("Antibot has to be enabled for panic to be enabled.", ChatColor.RED));
			}
		} else {
			commandSender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
