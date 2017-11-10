package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import co.reborncraft.syslogin_banmanager.api.futures.vote.PullVoteDataFuture;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public interface ILoginFuture extends IDataFuture {
	default void loginSuccessAction(ProxiedPlayer p, InetAddress ip, String autoswitchServer, Connection sqlConnection) {
		try (PreparedStatement addLoginEntry = sqlConnection.prepareStatement("INSERT INTO `IPs` (`USERNAME`, `IP`, `LAST_USED`) VALUES (?, ?, CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE `LAST_USED`=CURRENT_TIMESTAMP")) {
			addLoginEntry.setString(1, p.getName());
			addLoginEntry.setString(2, ip.getHostAddress());
			addLoginEntry.execute();
		} catch (SQLException ignored) {
			// SQL will complain just because Username/IPs entries have to be unique, so laziness tells me to avoid querying it before adding it.
		}
		if (SysLogin_BanManager.getInstance().isOnline(p)) { // Just checking if the player is online.
			SysLogin_BanManager.getInstance().login(p.getName());
			p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + " \u00a77Please be aware that no hacks are allowed, \u00a7cif we find hacks while screen-sharing,\u00a77 you will be \u00a74\u00a7lbanned permanently!"));
			p.getServer().sendData("Reborncraft", "LoginEvent".getBytes(StandardCharsets.UTF_8));
			if (autoswitchServer != null && !autoswitchServer.isEmpty()) {
				Map<String, ServerInfo> servers = SysLogin_BanManager.getInstance().getProxy().getServers();
				if (servers.containsKey(autoswitchServer)) {
					p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_AUTOSWITCH_ATTEMPT));
					p.connect(servers.get(autoswitchServer));
				} else {
					p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_AUTOSWITCH_SERVER_NOT_FOUND));
				}
			}

			try {
				new PullVoteDataFuture(p.getName(), null).execute(sqlConnection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
