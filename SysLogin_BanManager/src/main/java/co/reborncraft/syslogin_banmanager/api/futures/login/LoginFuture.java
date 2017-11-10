package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.responses.states.LoginState;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class LoginFuture implements ILoginFuture {
	private final ProxiedPlayer p;
	private final String password;
	private final InetAddress ip;
	private final Consumer<LoginState> runOnComplete;

	public LoginFuture(ProxiedPlayer p, String password, InetAddress ip, Consumer<LoginState> runOnComplete) {
		this.p = p;
		this.ip = ip;
		this.password = password;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public void execute(Connection sqlConnection) {
		LoginState executionResult = LoginState.DATABASE_ERROR;
		try (
				Statement registerCheckStatement = sqlConnection.createStatement();
				ResultSet registerCheck = registerCheckStatement.executeQuery("SELECT * FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER('" + p.getName() + "');")
		) {
			if (registerCheck.next()) {
				if (!registerCheck.getString("USERNAME").equals(p.getName()) /* Case matching */) {
					executionResult = LoginState.BAD_USERNAME;
				} else {
					if (registerCheck.getString("PASSWORD_ENCODED").equalsIgnoreCase(SysLogin_BanManager.getInstance().encodePassword(password))) {
						try (
								Statement ipWhitelistCheckStatement = sqlConnection.createStatement();
								ResultSet ipWhitelistCheck = ipWhitelistCheckStatement.executeQuery("SELECT * FROM `WhitelistedLoginIPs` WHERE LOWER(`USERNAME`) = LOWER('" + p.getName() + "');")
						) {
							String ipString = ip.getHostAddress();
							boolean validLoginIP = true;
							while (ipWhitelistCheck.next()) {
								if (ipWhitelistCheck.getString("IP").equalsIgnoreCase(ipString)) {
									validLoginIP = true;
									break;
								} else {
									validLoginIP = false;
								}
							}
							executionResult = validLoginIP ? LoginState.SUCCESSFUL_LOGIN : LoginState.BAD_IP;
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						executionResult = LoginState.BAD_PASSWORD;
					}
				}
				if (executionResult == LoginState.SUCCESSFUL_LOGIN) {
					loginSuccessAction(p, ip, registerCheck.getString("ONLOGIN_SWITCHTO"),sqlConnection);
				}
			} else {
				p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_REGISTER_MESSAGE));
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
