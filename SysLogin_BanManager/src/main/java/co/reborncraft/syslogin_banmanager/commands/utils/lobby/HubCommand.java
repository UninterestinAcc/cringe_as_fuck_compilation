package co.reborncraft.syslogin_banmanager.commands.utils.lobby;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {
	public HubCommand() {
		super("hub", "", "lobby");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			SysLogin_BanManager.getInstance().getHubServer()
					.ifPresent(((ProxiedPlayer) sender)::connect);
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_PLAYER_ONLY_ERROR));
		}
	}
}
