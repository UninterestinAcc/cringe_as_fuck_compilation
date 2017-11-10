package co.reborncraft.syslogin_banmanager.api.futures.vote;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import net.md_5.bungee.api.CommandSender;

import java.sql.*;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Consumer;

public class PullVoteDataFuture implements IDataFuture {
	private final String player;
	private final Consumer<Map.Entry<Timestamp, Integer>> runOnComplete;

	public PullVoteDataFuture(String player, Consumer<Map.Entry<Timestamp, Integer>> runOnComplete) {
		this.player = player;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) throws SQLException {
		Timestamp lastVote = null;
		int votes = -1;
		try (PreparedStatement statement = sqlConnection.prepareStatement("SELECT `LAST_VOTE`,`VOTES` FROM `Logins` WHERE LOWER(`USERNAME`) = ?")) {
			statement.setString(1, player);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					lastVote = result.getTimestamp("LAST_VOTE");
					votes = result.getInt("VOTES");
				}
			}
		}
		SysLogin_BanManager.getInstance().getVoteReminderManager().setVoteTime(player, lastVote == null ? 0 : lastVote.getTime() / 1000);
		complete(runOnComplete, new AbstractMap.SimpleEntry<>(lastVote, votes));
	}
}
