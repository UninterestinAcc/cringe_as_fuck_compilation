package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import co.reborncraft.syslogin_banmanager.api.responses.states.AltCheckState;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AltCheckFuture implements IDataFuture {
	private final CommandSender sender;
	private final String checkUser;
	private final Consumer<AltCheckResult> runOnComplete;

	public AltCheckFuture(CommandSender sender, String checkUser, Consumer<AltCheckResult> runOnComplete) {
		this.sender = sender;
		this.checkUser = checkUser;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return sender;
	}

	@Override
	public void execute(Connection sqlConnection) {
		Map<String, List<String>> ipAlts = new HashMap<>();
		List<String> passAlts = new ArrayList<>();
		AltCheckState executionState = AltCheckState.DATABASE_ERROR;
		try (
				PreparedStatement getIPAltsStatement = sqlConnection.prepareStatement("SELECT `USERNAME`,`IP` FROM `IPs` WHERE `IP` IN (SELECT `IP` FROM `IPs` WHERE LOWER(`USERNAME`) = LOWER(?));");
				PreparedStatement getPassAltsStatement = sqlConnection.prepareStatement("SELECT `USERNAME` FROM `Logins` WHERE `PASSWORD_ENCODED` IN (SELECT `PASSWORD_ENCODED` FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER(?));")
		) {
			getIPAltsStatement.setString(1, checkUser);
			getPassAltsStatement.setString(1, checkUser);

			try (ResultSet getIPAlts = getIPAltsStatement.executeQuery()) {
				while (getIPAlts.next()) {
					String username = getIPAlts.getString("USERNAME");
					if (!username.equalsIgnoreCase(checkUser)) {
						hmapInitOrGet(ipAlts, username).add(getIPAlts.getString("IP"));
					}
				}
			}
			try (ResultSet getPassAlts = getPassAltsStatement.executeQuery()) {
				while (getPassAlts.next()) {
					String username = getPassAlts.getString(1);
					if (!username.equalsIgnoreCase(checkUser)) {
						passAlts.add(username);
					}
				}
			}
			if (ipAlts.size() + passAlts.size() > 0) {
				executionState = AltCheckState.ALTS_FOUND;
			} else {
				executionState = AltCheckState.ALTS_NOT_FOUND;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		complete(runOnComplete, new AltCheckResult(executionState, ipAlts, passAlts));
	}

	private List<String> hmapInitOrGet(Map<String, List<String>> map, String key) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<>());
		}
		return map.get(key);
	}

	public class AltCheckResult {
		private final AltCheckState state;
		private final Map<String, List<String>> ipAlts;
		private final List<String> passAlts;

		public AltCheckResult(AltCheckState state, Map<String, List<String>> ipAlts, List<String> passAlts) {
			this.state = state;
			this.ipAlts = ipAlts;
			this.passAlts = passAlts;
		}

		public AltCheckState getState() {
			return state;
		}

		public Map<String, List<String>> getIpAlts() {
			return ipAlts;
		}

		public List<String> getPassAlts() {
			return passAlts;
		}

		@Override
		public String toString() {
			return state.name() + "\nIPAlts:\n" +
					ipAlts.keySet().stream()
							.map(name -> name + ": " + String.join(", ", ipAlts.get(name)))
							.collect(Collectors.joining("\n")) + "\nPassAlts: " +
					String.join(", ", passAlts);

		}
	}
}
