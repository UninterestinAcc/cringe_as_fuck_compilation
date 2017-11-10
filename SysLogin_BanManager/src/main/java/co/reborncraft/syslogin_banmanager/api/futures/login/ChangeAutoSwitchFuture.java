package co.reborncraft.syslogin_banmanager.api.futures.login;

import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public class ChangeAutoSwitchFuture implements IDataFuture {
	private final ProxiedPlayer p;
	private final String target;
	private final Consumer<Boolean> runOnComplete;

	public ChangeAutoSwitchFuture(ProxiedPlayer p, String target, Consumer<Boolean> runOnComplete) {
		this.p = p;
		this.target = target;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public void execute(Connection sqlConnection) {
		boolean success = false;
		try (
				PreparedStatement updateStatement = sqlConnection.prepareStatement("UPDATE `Logins` SET ONLOGIN_SWITCHTO=? WHERE `USERNAME` = ?");
		) {
			updateStatement.setString(1, target);
			updateStatement.setString(2, p.getName());
			updateStatement.executeUpdate();
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		complete(runOnComplete, success);
	}

	@Override
	public ProxiedPlayer getRequester() {
		return p;
	}
}
