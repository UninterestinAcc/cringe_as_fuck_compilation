package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RequestPlayerToLoginFuture implements IFuture {

	private final ProxiedPlayer p;

	public RequestPlayerToLoginFuture(ProxiedPlayer p) {
		this.p = p;
	}

	@Override
	public void execute(Connection sqlConnection) throws SQLException {
		try (
				Statement registerCheckStatement = sqlConnection.createStatement();
				ResultSet registerCheck = registerCheckStatement.executeQuery("SELECT * FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER('" + p.getName() + "');")
		) {
			if (registerCheck.next()) { // So there is an entry for the login.
				p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_LOGIN_MESSAGE));
			} else {
				p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_REGISTER_MESSAGE));
			}
		}
	}

	@Override
	public ProxiedPlayer getRequester() {
		return p;
	}
}
