package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class RestartLockListener implements Listener {
	@EventHandler (priority = EventPriority.LOWEST)
	public void onLogin(PreLoginEvent e) {
		if (SysLogin_BanManager.getInstance().isRestartQueued()) {
			e.setCancelled(true);
			e.setCancelReason(Utils.buildTextComponent("This edge server will restart within 60 seconds.\n\nPlease try joining again 1 minute later.", ChatColor.RED));
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPing(ProxyPingEvent e) {
		if (SysLogin_BanManager.getInstance().isRestartQueued()) {
			e.getResponse().setDescriptionComponent(Utils.buildTextComponent("This edge server will restart within 60 seconds.", ChatColor.RED));
		}
	}
}
