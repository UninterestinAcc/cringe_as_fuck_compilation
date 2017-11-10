package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.responses.states.RegisterState;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.sql.*;
import java.util.function.Consumer;

public class RegisterFuture implements ILoginFuture {
	private final ProxiedPlayer p;
	private final String password;
	private final InetAddress ip;
	private final Consumer<RegisterState> runOnComplete;

	public RegisterFuture(ProxiedPlayer p, String password, InetAddress ip, Consumer<RegisterState> runOnComplete) {
		this.p = p;
		this.password = password;
		this.ip = ip;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public void execute(Connection sqlConnection) {
		RegisterState executionResult = RegisterState.DATABASE_ERROR;
		try (
				Statement registerCheckStatement = sqlConnection.createStatement();
				ResultSet registerCheck = registerCheckStatement.executeQuery("SELECT * FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER('" + p.getName() + "');")
		) {
			if (!registerCheck.next()) {
				boolean altDetected = false;
				if (!SysLogin_BanManager.getInstance().isAllowedToRegister(p.getName().toLowerCase())) {
					try (
							Statement altCheckStatement = sqlConnection.createStatement();
							ResultSet altCheck = altCheckStatement.executeQuery("SELECT * FROM `Logins` WHERE `REGISTER_IP` = '" + ip.getHostAddress() + "';")
					) {
						while (altCheck.next()) {
							Timestamp regTime = altCheck.getTimestamp("REGISTER_DATE");
							if (regTime != null) {
								altDetected = regTime.getTime() >= System.currentTimeMillis() - 2.592E9;
								if (altDetected) {
									break;
								}
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (altDetected) {
					executionResult = RegisterState.ALT_DETECTED;
				} else {
					try (PreparedStatement registerStatement = sqlConnection.prepareStatement("INSERT INTO `Logins` (`USERNAME`, `PASSWORD_ENCODED`, `REGISTER_IP`) VALUES (?,?,?);")) {
						registerStatement.setString(1, p.getName());
						registerStatement.setString(2, SysLogin_BanManager.getInstance().encodePassword(password));
						registerStatement.setString(3, ip.getHostAddress());
						registerStatement.execute();
						executionResult = RegisterState.SUCCESSFUL_REGISTER;
					}
				}

				if (executionResult == RegisterState.SUCCESSFUL_REGISTER) {
					loginSuccessAction(p, ip, null, sqlConnection);
				}
			} else {
				p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_LOGIN_MESSAGE));
				return;
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
