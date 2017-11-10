package co.reborncraft.syslogin_banmanager.api.futures.motd;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PullServerListFuture implements IDataFuture {
	private final Consumer<Map<String, ServerInfo>> runOnComplete;

	public PullServerListFuture(Consumer<Map<String, ServerInfo>> runOnComplete) {
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) throws SQLException {
		Map<String, ServerInfo> servers;
		try (
				Statement statement = sqlConnection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM `ServerList`");
		) {
			servers = new HashMap<>();
			while (resultSet.next()) {
				String name = resultSet.getString("SERVER_NAME");
				String[] address = resultSet.getString("SERVER_ADDRESS").split(":");
				servers.put(
						name,
						SysLogin_BanManager.getInstance().getProxy().constructServerInfo(
								name,
								InetSocketAddress.createUnresolved(address[0], address.length == 2 ? Integer.parseInt(address[1]) : 25565),
								"Message",
								false
						)
				);
			}
		}
		complete(runOnComplete, servers);
	}
}
