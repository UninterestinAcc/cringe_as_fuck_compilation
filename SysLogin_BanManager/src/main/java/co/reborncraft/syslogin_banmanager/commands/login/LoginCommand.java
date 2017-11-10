package co.reborncraft.syslogin_banmanager.commands.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.login.LoginFuture;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.syslogin_banmanager.api.responses.states.LoginState;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LoginCommand extends Command {
	public LoginCommand() {
		super("login", null, "l");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (SysLogin_BanManager.getInstance().isLoggedIn((ProxiedPlayer) sender)) {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_ALREADY_LOGGED_IN_MESSAGE));
			} else if (args.length >= 1) {
				SysLogin_BanManager.getInstance().scheduleFuture(new LoginFuture(((ProxiedPlayer) sender), args[0], ((ProxiedPlayer) sender).getPendingConnection().getAddress().getAddress(), state -> {
					if (state.name().startsWith("BAD")) {
						if (state == LoginState.BAD_IP) {
							Punishment punishment = new Punishment(
									Punishment.PunishmentTarget.IP,
									Punishment.PunishmentType.BAN,
									((ProxiedPlayer) sender).getAddress().getAddress().getHostAddress(),
									"Trying to log in as a protected staff.",
									System.currentTimeMillis() / 1000,
									System.currentTimeMillis() / 1000 + 5184000, // 60 days
									"AntiAlts"
							);
							SysLogin_BanManager.getInstance().getPunishmentCache().punish(SysLogin_BanManager.getInstance().getProxy().getConsole(), punishment);
							((ProxiedPlayer) sender).disconnect(punishment.toBanTextComponent());
						} else {
							((ProxiedPlayer) sender).disconnect(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + "\n" + state.getMessage()));
						}
					} else {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + " " + state.getMessage()));
					}
				}));
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_LOGIN_MESSAGE));
			}
		}
	}
}
