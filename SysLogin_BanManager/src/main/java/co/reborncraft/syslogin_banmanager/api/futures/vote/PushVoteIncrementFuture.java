package co.reborncraft.syslogin_banmanager.api.futures.vote;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class PushVoteIncrementFuture implements IFuture {
	private final String player;

	public PushVoteIncrementFuture(String player) {
		this.player = player;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) {
		Calendar calendar = Calendar.getInstance();
		int currentMonth = calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH) + 1;
		try (
				PreparedStatement total = sqlConnection.prepareStatement("UPDATE `Logins` SET `VOTES`=`VOTES`+1,`LAST_VOTE`=CURRENT_TIMESTAMP WHERE LOWER(`USERNAME`) = ?");
				PreparedStatement monthly = sqlConnection.prepareStatement("INSERT INTO `MonthlyVoteData` " +
						"(`USERNAME`,`MONTH`,`VOTES`) " +
						"VALUES (" +
						"IFNULL((SELECT `USERNAME` FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER(?) LIMIT 1), \'INVALID_VOTE\'), " + // Prefer the correct capitalization if available
						"?,1) " +
						"ON DUPLICATE KEY UPDATE `MONTH`=?,`VOTES`=(CASE WHEN `MONTH` = ? THEN `VOTES`+1 ELSE 1 END)")
		) {
			total.setString(1, player.toLowerCase());
			total.executeUpdate();
			monthly.setString(1, player);
			monthly.setInt(2, currentMonth);
			monthly.setInt(3, currentMonth);
			monthly.setInt(4, currentMonth);
			monthly.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
