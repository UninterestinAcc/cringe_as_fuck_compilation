package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.login.RequestPlayerToLoginFuture;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Arrays;

public class LoginProtectionListener implements Listener {
	/**
	 * Send the player a request to login.
	 */
	private static void sendLoginRequest(ProxiedPlayer p) {
		SysLogin_BanManager.getInstance().scheduleFuture(new RequestPlayerToLoginFuture(p));
	}

	/**
	 * Prevents users with invalid usernames from joining.
	 * <p>
	 * Also prevents SQL Injections in RequestLoginFuture.
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void preLoginNameValidityCheck(PreLoginEvent e) {
		if (!e.getConnection().getName().matches(SysLogin_BanManager.PLAYERNAME_REGEX)) {
			e.setCancelled(true);
			e.setCancelReason(Utils.parseIntoComp(SysLogin_BanManager.SL_INVALID_USERNAME));
		} else if (Arrays.stream(SysLogin_BanManager.BANNED_NAMES).anyMatch(bannedName -> e.getConnection().getName().equalsIgnoreCase(bannedName))) {
			e.setCancelled(true);
			e.setCancelReason(Utils.parseIntoComp(SysLogin_BanManager.BANNED_USERNAME));
		}
	}

	/**
	 * Send the player a request to login after logging in to the server initially.
	 */
	@EventHandler (priority = EventPriority.LOW)
	public void onLogin(PostLoginEvent e) {
		SysLogin_BanManager.getInstance().logout(e.getPlayer().getName()); // More of that *just in case*
		sendLoginRequest(e.getPlayer());
	}

	/**
	 * Garbage clean the entry from login-authorized.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onLeave(PlayerDisconnectEvent e) {
		SysLogin_BanManager.getInstance().logout(e.getPlayer().getName());
	}

	/**
	 * Prevent server switching (from hub) if the player is not yet logged in.
	 */
	@EventHandler (priority = EventPriority.LOW)
	public void onServerConnect(ServerConnectEvent e) {
		if (!SysLogin_BanManager.getInstance().isLoggedIn(e.getPlayer())) {
			if (!e.getTarget().getName().toLowerCase().matches(SysLogin_BanManager.HUB_REGEX)) {
				sendLoginRequest(e.getPlayer());
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent e) {
		e.getPlayer().unsafe();
	}

	/**
	 * Prevent chat if the player is not yet logged in.
	 */
	@EventHandler (priority = EventPriority.LOW)
	public void onChat(ChatEvent e) {
		if (e.getSender() instanceof ProxiedPlayer && !SysLogin_BanManager.getInstance().isLoggedIn((ProxiedPlayer) e.getSender())) {
			String m = e.getMessage().toLowerCase();
			if (!m.matches("^/(l(ogin)?|reg(ister)?) .+$")) {
				sendLoginRequest((ProxiedPlayer) e.getSender());
				e.setCancelled(true);
			}
		}
	}

	/**
	 * Disallow tab completion before login. Just because randy whores love to use their own username for login.
	 */
	@EventHandler (priority = EventPriority.LOW)
	public void onTabComplete(TabCompleteEvent e) {
		if (e.getSender() instanceof ProxiedPlayer && !SysLogin_BanManager.getInstance().isLoggedIn((ProxiedPlayer) e.getSender())) {
			e.setCancelled(true);
		}
	}
}
