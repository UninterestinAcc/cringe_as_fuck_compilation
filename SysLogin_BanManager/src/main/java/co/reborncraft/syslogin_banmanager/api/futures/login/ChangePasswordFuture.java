package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import co.reborncraft.syslogin_banmanager.api.responses.states.ChangePasswordState;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.sql.*;
import java.util.function.Consumer;

public class ChangePasswordFuture implements IDataFuture {
	private final ProxiedPlayer p;
	private final String password;
	private final InetAddress ip;
	private final Consumer<ChangePasswordState> runOnComplete;

	public ChangePasswordFuture(ProxiedPlayer p, String newPassword, InetAddress ip, Consumer<ChangePasswordState> runOnComplete) {
		this.p = p;
		this.ip = ip;
		this.password = newPassword;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public void execute(Connection sqlConnection) {
		final String encodedPassword = SysLogin_BanManager.getInstance().encodePassword(password);
		ChangePasswordState executionResult = ChangePasswordState.DATABASE_ERROR;
		try (Statement getOldPassStatement = sqlConnection.createStatement();
			 ResultSet getOldPass = getOldPassStatement.executeQuery("SELECT `PASSWORD_ENCODED` FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER('" + p.getName() + "');")
		) {
			if (getOldPass.next()) {
				String oldPass = getOldPass.getString(1);
				if (!oldPass.equals(encodedPassword)) {
					try (PreparedStatement changeStatement = sqlConnection.prepareStatement("UPDATE `Logins` SET `PASSWORD_ENCODED`=? WHERE `USERNAME`=?")) {
						changeStatement.setString(1, encodedPassword);
						changeStatement.setString(2, p.getName());
						changeStatement.execute();
						executionResult = ChangePasswordState.SUCCESSFUL_CHANGE;
					}
					try (PreparedStatement recordChangeStatement = sqlConnection.prepareStatement("INSERT INTO `PasswordChanges` (`USERNAME`, `IP`, `OLD_PASSWORD`, `NEW_PASSWORD`) VALUES (?,?,?,?);")) {
						recordChangeStatement.setString(1, p.getName());
						recordChangeStatement.setString(2, ip.getHostAddress());
						recordChangeStatement.setString(3, oldPass);
						recordChangeStatement.setString(4, encodedPassword);
						recordChangeStatement.execute();
					}
				} else {
					executionResult = ChangePasswordState.SAME_PASSWORD;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		complete(runOnComplete, executionResult);
	}

	@Override
	public ProxiedPlayer getRequester() {
		return p;
	}
}
