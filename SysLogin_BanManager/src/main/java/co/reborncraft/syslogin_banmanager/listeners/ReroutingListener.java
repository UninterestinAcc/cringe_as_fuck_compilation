package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Optional;

public class ReroutingListener implements Listener {
	@EventHandler (priority = EventPriority.LOW)
	public void onKick(ServerKickEvent e) {
		if (e.getState() == ServerKickEvent.State.CONNECTED) {
			Optional<ServerInfo> hub = SysLogin_BanManager.getInstance().getHubServer();
			hub.ifPresent(serverInfo -> {
				e.setCancelled(true);
				e.setCancelServer(serverInfo);
				e.getPlayer().sendMessage(Utils.buildTextComponent("You have been kicked from ", ChatColor.RED,
						Utils.buildTextComponent(e.getKickedFrom().getName(), ChatColor.GRAY),
						Utils.buildTextComponent(" for ", ChatColor.RED),
						Utils.buildTextComponent("", ChatColor.GRAY, e.getKickReasonComponent()),
						Utils.buildTextComponent(". Connecting you to a hub server...", ChatColor.RED)
				));
			});
		}
	}
}
