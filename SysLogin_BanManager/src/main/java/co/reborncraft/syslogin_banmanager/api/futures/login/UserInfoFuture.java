package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import co.reborncraft.syslogin_banmanager.api.objects.ExpiredPunishment;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// TODO
public class UserInfoFuture implements IFuture {
	private final ProxiedPlayer sender;
	private final String checkUser;
	private final Consumer<UserInfo> runOnComplete;

	public UserInfoFuture(ProxiedPlayer sender, String checkUser, Consumer<UserInfo> runOnComplete) {
		this.sender = sender;
		this.checkUser = checkUser;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public ProxiedPlayer getRequester() {
		return sender;
	}

	@Override
	public void execute(Connection sqlConnection) {
		List<Punishment> punishments = new ArrayList<>();
		try (
				PreparedStatement bansStatement = sqlConnection.prepareStatement("SELECT * FROM `UserBans` WHERE LOWER(`USERNAME`) = LOWER(?)");
				PreparedStatement mutesStatement = sqlConnection.prepareStatement("SELECT * FROM `UserMutes` WHERE LOWER(`USERNAME`) = LOWER(?)");
				PreparedStatement warnsStatement = sqlConnection.prepareStatement("SELECT * FROM `UserWarns` WHERE LOWER(`USERNAME`) = LOWER(?)");
				PreparedStatement ipsStatement = sqlConnection.prepareStatement("SELECT * FROM `IPs` WHERE LOWER(`USERNAME`) = LOWER(?)");
				PreparedStatement passwordHistoryStatement = sqlConnection.prepareStatement("SELECT * FROM `PasswordChanges` WHERE LOWER(`USERNAME`) = LOWER(?)");
				PreparedStatement loginInfoStatement = sqlConnection.prepareStatement("SELECT * FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER(?)");
		) {
			bansStatement.setString(1, checkUser);
			mutesStatement.setString(1, checkUser);
			warnsStatement.setString(1, checkUser);
			ipsStatement.setString(1, checkUser);
			passwordHistoryStatement.setString(1, checkUser);
			loginInfoStatement.setString(1, checkUser);
			try (
					ResultSet bans = bansStatement.executeQuery();
					ResultSet mutes = mutesStatement.executeQuery();
					ResultSet warns = warnsStatement.executeQuery();
					ResultSet ips = bansStatement.executeQuery();
					ResultSet passwordHistory = mutesStatement.executeQuery();
					ResultSet loginInfo = warnsStatement.executeQuery();
			) {
				while (bans.next()) {
					new ExpiredPunishment(
							Punishment.PunishmentTarget.USERNAME,
							Punishment.PunishmentType.BAN,
							bans.getString("USERNAME"),
							bans.getString("REASON"),
							bans.getString("LIFTED_BY"),
							bans.getLong("STARTTIME"),
							bans.getLong("ENDTIME"),
							bans.getString("BY")
					);
				}
				while (mutes.next()) {
					new ExpiredPunishment(
							Punishment.PunishmentTarget.USERNAME,
							Punishment.PunishmentType.BAN,
							mutes.getString("USERNAME"),
							mutes.getString("REASON"),
							mutes.getString("LIFTED_BY"),
							mutes.getLong("STARTTIME"),
							mutes.getLong("ENDTIME"),
							mutes.getString("BY")
					);
				}
				while (warns.next()) {
					new ExpiredPunishment(
							Punishment.PunishmentTarget.USERNAME,
							Punishment.PunishmentType.BAN,
							warns.getString("USERNAME"),
							warns.getString("REASON"),
							null,
							warns.getLong("STARTTIME"),
							0,
							warns.getString("BY")
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// TODO
	public class UserInfo {

	}
}
