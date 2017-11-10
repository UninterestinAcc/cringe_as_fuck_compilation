package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.statistics.PushStaffStatsFuture;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsListener implements Listener {
	@EventHandler (priority = EventPriority.LOW)
	public void onLogout(PlayerDisconnectEvent e) { // Staff stat
		if (SysLogin_BanManager.getInstance().isLoggedIn(e.getPlayer()) && SysLogin_BanManager.getInstance().isStaff(e.getPlayer().getName())) {
			long joinMillis = SysLogin_BanManager.getInstance().getLoginTime(e.getPlayer().getName());
			SysLogin_BanManager.getInstance().scheduleFuture(new PushStaffStatsFuture(e.getPlayer().getName(), joinMillis, e.getPlayer().getAddress().getAddress()));
		}
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onJoin(PreLoginEvent e) {
		SysLogin_BanManager.Statistics.getLogins().incrementAndGet();
		AtomicInteger concPlayers = SysLogin_BanManager.Statistics.getMaxConcurrentPlayers();
		int loggedInPlayersCount = SysLogin_BanManager.getInstance().getLoggedInPlayersCount();
		if (concPlayers.get() < loggedInPlayersCount) {
			concPlayers.set(loggedInPlayersCount);
		}
	}

	@EventHandler
	public void onLeave(PlayerDisconnectEvent e) {
		SysLogin_BanManager.Statistics.getLogouts().incrementAndGet();
	}
}
