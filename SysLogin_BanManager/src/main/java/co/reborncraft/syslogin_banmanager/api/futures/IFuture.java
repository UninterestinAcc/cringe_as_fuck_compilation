package co.reborncraft.syslogin_banmanager.api.futures;

import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.SQLException;

public interface IFuture {
	CommandSender getRequester();

	void execute(Connection sqlConnection) throws SQLException;
}
