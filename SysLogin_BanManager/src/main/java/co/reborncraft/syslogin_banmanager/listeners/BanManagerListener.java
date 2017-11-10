package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.PunishmentCache;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;
import java.util.Arrays;

public class BanManagerListener implements Listener {
	@EventHandler
	public void onPreLogin(PreLoginEvent e) { // Composite ban check
		if (!e.isCancelled()) {
			InetAddress remoteAddr = e.getConnection().getAddress().getAddress();
			final PunishmentCache punishmentCache = SysLogin_BanManager.getInstance().getPunishmentCache();
			if (punishmentCache != null) {
				if (punishmentCache.isBanned(e.getConnection().getName(), remoteAddr)) {
					e.setCancelled(true);
					e.setCancelReason(punishmentCache
							.getBanReason(e.getConnection().getName(), remoteAddr)
							.toBanTextComponent()
					);
				}
			} else {
				e.setCancelled(true);
				e.setCancelReason(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + "\n\u00a7dPlease wait for a few seconds; loading data..."));
			}
		}
	}

	@EventHandler
	public void onChat(ChatEvent e) { // Mute check
		String m = e.getMessage().toLowerCase();
		if (!e.isCancelled() && e.getSender() instanceof ProxiedPlayer && (!m.startsWith("/") || Arrays.stream(SysLogin_BanManager.getMuteMessageFilters()).anyMatch(m::matches))) {
			final PunishmentCache punishmentCache = SysLogin_BanManager.getInstance().getPunishmentCache();
			if (punishmentCache.isMuted(((ProxiedPlayer) e.getSender()))) {
				e.setCancelled(true);
				Arrays.stream(punishmentCache
						.getMuteReason(((ProxiedPlayer) e.getSender()))
						.toMuteTextComponents())
						.forEach(((ProxiedPlayer) e.getSender())::sendMessage);
			}
		}
	}
}
