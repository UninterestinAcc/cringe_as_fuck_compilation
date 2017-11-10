package co.reborncraft.syslogin_banmanager.commands.utils.security;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class SecurityCheckCommand extends Command {
	public SecurityCheckCommand() {
		super("securitycheck");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.SECURITY_BYPASS)) {
			Map<String, ServerInfo> servers = SysLogin_BanManager.getInstance().getProxy().getServers();
			sender.sendMessage(Utils.buildTextComponent("Beginning audit...", ChatColor.GRAY));
			servers.entrySet().forEach(ent -> ent.getValue().ping((ping, ex) -> {
				if (ex == null) {
					int onlinePlayers = ping.getPlayers().getOnline();
					int maxPlayers = ping.getPlayers().getMax();
					boolean isProtected = ping.getDescriptionComponent().toPlainText().equals("[Security] Invalid origin.");
					sender.sendMessage(Utils.parseIntoComp("\u00a76" + ent.getKey() + "\u00a7e: \u00a79OnlinePlayers:" + onlinePlayers + ">\u00a7aMP-Payload:" + maxPlayers + "\u00a77, \u00a7bMOTDIsBound:" + isProtected));
				} else {
					sender.sendMessage(Utils.parseIntoComp("\u00a76" + ent.getKey() + "\u00a7c: " + ex.getClass().getSimpleName() + "/" + ex.getMessage()));
				}
			}));
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
