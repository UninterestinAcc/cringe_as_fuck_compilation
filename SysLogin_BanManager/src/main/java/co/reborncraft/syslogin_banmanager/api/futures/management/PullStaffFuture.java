package co.reborncraft.syslogin_banmanager.api.futures.management;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PullStaffFuture implements IDataFuture {
	private final Consumer<Map<String, Long>> runOnComplete;

	public PullStaffFuture(Consumer<Map<String, Long>> runOnComplete) {
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) throws SQLException {
		Map<String, Long> staffs;
		try (
				Statement retrieveStatement = sqlConnection.createStatement();
				ResultSet retrieved = retrieveStatement.executeQuery("SELECT * FROM `Staffs`;")
		) {
			staffs = new HashMap<>();
			while (retrieved.next()) {
				staffs.put(retrieved.getString("USERNAME"), retrieved.getLong("PERMISSIONS"));
			}
		}
		complete(runOnComplete, staffs);
	}
}
