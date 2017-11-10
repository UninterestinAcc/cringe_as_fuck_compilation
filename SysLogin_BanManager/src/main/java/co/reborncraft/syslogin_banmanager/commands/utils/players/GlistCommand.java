package co.reborncraft.syslogin_banmanager.commands.utils.players;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.commands.utils.staff.StaffsCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GlistCommand extends Command {
	public GlistCommand() {
		super("glist", "", "globallist", "gonline", "globalonline", "gwho", "globalwho");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		SysLogin_BanManager.getInstance().getProxy().getServers().keySet().forEach(server -> PlayersCommand.showPlayerStats(sender, server));
		StaffsCommand.sendStaffsOnlineMessage(sender);
		StaffsCommand.sendProtip(sender);
	}
}
