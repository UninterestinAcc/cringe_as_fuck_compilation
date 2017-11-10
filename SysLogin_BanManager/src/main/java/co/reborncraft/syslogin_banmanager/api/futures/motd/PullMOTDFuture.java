package co.reborncraft.syslogin_banmanager.api.futures.motd;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class PullMOTDFuture implements IDataFuture {
	private final Consumer<String> runOnComplete;

	public PullMOTDFuture(Consumer<String> runOnComplete) {
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) throws SQLException {
		String message;
		try (
				Statement fetchStatement = sqlConnection.createStatement();
				ResultSet fetch = fetchStatement.executeQuery("SELECT `MESSAGE` FROM `MOTD` ORDER BY `CREATETIME` DESC LIMIT 1")
		) {
			if (fetch.next()) {
				message = fetch.getString(1);
			} else {
				message = "Can't find message, please set up the message on MySQL.";
			}
		}
		complete(runOnComplete, message);
	}
}
