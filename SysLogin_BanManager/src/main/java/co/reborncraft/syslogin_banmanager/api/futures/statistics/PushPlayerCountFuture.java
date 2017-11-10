package co.reborncraft.syslogin_banmanager.api.futures.statistics;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class PushPlayerCountFuture implements IFuture {
	private final int logins;
	private final int logouts;
	private final int concMaxPlayers;

	public PushPlayerCountFuture(int logins, int logouts, int concMaxPlayers) {
		this.logins = logins;
		this.logouts = logouts;
		this.concMaxPlayers = concMaxPlayers;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) {
		try (PreparedStatement push = sqlConnection.prepareStatement("INSERT INTO `PlayerStatistics` (`LOGIN_COUNT`, `LOGOUT_COUNT`, `PLAYER_COUNT`, `HOST`, `TIME`) VALUES (?,?,?,?,?)")) {
			push.setInt(1, logins);
			push.setInt(2, logouts);
			push.setInt(3, concMaxPlayers);
			push.setString(4, SysLogin_BanManager.getInstance().getServerVariables().get("server-id"));
			push.setTimestamp(5, Timestamp.from(Instant.now()));
			push.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
