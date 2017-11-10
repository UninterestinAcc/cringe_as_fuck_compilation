package co.reborncraft.syslogin_banmanager.api.futures.statistics;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import net.md_5.bungee.api.CommandSender;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;

public class PushStaffStatsFuture implements IFuture {
	private final String name;
	private final long joinMillis;
	private final InetAddress ip;

	public PushStaffStatsFuture(String name, long joinMillis, InetAddress ip) {
		this.name = name;
		this.joinMillis = joinMillis;
		this.ip = ip;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) {
		try (PreparedStatement stat = sqlConnection.prepareStatement("INSERT INTO `StaffStats` (`USERNAME`, `LOGIN`, `LOGOUT`, `IP`) VALUES (?,?,?,?)")) {
			stat.setString(1, name);
			stat.setTimestamp(2, Timestamp.from(Instant.from(Instant.ofEpochMilli(joinMillis).atZone(ZoneId.of("UTC")))));
			stat.setTimestamp(3, Timestamp.from(Instant.from(Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("UTC")))));
			stat.setString(4, ip.getHostAddress());
			stat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
