package co.reborncraft.syslogin_banmanager.commands.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.login.ChangeAutoSwitchFuture;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;

public class AutoSwitchCommand extends Command {
	public AutoSwitchCommand() {
		super("AutoSwitch");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (args.length == 1) {
				String arg = args[0];
				String newServer = null;
				if (arg.equalsIgnoreCase("clear")) {
					newServer = "";
				} else {
					Optional<String> server = SysLogin_BanManager.getInstance().getProxy().getServers().keySet().stream().filter(arg::equalsIgnoreCase).findFirst();
					if (server.isPresent()) {
						newServer = server.get();
					}
				}
				if (newServer != null) {
					SysLogin_BanManager.getInstance().scheduleFuture(new ChangeAutoSwitchFuture(((ProxiedPlayer) sender), newServer, success -> {
						if (success) {
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_AUTOSWITCH_UPDATED));
						} else {
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.DATABASE_ERROR));
						}
					}));
				} else {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_AUTOSWITCH_CMD_SERV_NOT_FOUND));
				}
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_AUTOSWITCH_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_PLAYER_ONLY_ERROR));
		}
	}
}
