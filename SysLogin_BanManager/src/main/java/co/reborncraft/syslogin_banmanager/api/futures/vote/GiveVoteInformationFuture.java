package co.reborncraft.syslogin_banmanager.api.futures.vote;

import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class GiveVoteInformationFuture implements IDataFuture {
	private final ProxiedPlayer player;
	private final Consumer<VoteInformation> runOnComplete;

	public GiveVoteInformationFuture(ProxiedPlayer player, Consumer<VoteInformation> runOnComplete) {
		this.player = player;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return player;
	}

	@Override
	public void execute(Connection sqlConnection) throws SQLException {
		Map<Integer, String> voteSites = new HashMap<>();
		try (
				Statement statement = sqlConnection.createStatement();
				ResultSet result = statement.executeQuery("SELECT * FROM `VoteSites` ORDER BY `SITE_NUMBER` ASC");
		) {
			while (result.next()) {
				voteSites.put(result.getInt("SITE_NUMBER"), result.getString("LINK"));
			}
		}
		AtomicReference<Timestamp> lastVote = new AtomicReference<>();
		AtomicInteger votes = new AtomicInteger(-1);
		new PullVoteDataFuture(player.getName(), voteData -> {
			lastVote.set(voteData.getKey());
			votes.set(voteData.getValue());
		}).execute(sqlConnection);
		complete(runOnComplete, new VoteInformation(voteSites, votes.get(), lastVote.get()));
	}

	public class VoteInformation {

		private final Map<Integer, String> voteSites;
		private final int votes;
		private final Timestamp lastVoteTime;

		public VoteInformation(Map<Integer, String> voteSites, int votes, Timestamp lastVoteTime) {
			this.voteSites = voteSites;
			this.votes = votes;
			this.lastVoteTime = lastVoteTime;
		}

		public Map<Integer, String> getVoteSites() {
			return voteSites;
		}

		public int getVotes() {
			return votes;
		}

		public Timestamp getLastVoteTime() {
			return lastVoteTime;
		}
	}
}
