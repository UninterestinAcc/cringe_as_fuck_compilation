package co.reborncraft.syslogin_banmanager.api.futures.banmanager;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.syslogin_banmanager.api.objects.PunishmentCache;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class PullPunishmentsFuture implements IDataFuture {
	private final Consumer<PunishmentCache> runOnComplete;

	public PullPunishmentsFuture(Consumer<PunishmentCache> runOnComplete) {
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return SysLogin_BanManager.getInstance().getProxy().getConsole();
	}

	@Override
	public void execute(Connection sqlConnection) throws SQLException {
		PunishmentCache punishments;

		try (
				Statement userBansStatement = sqlConnection.createStatement();
				ResultSet userBans = userBansStatement.executeQuery("SELECT * FROM `UserBans` WHERE `ENDTIME` <=0 OR `ENDTIME` > " + (System.currentTimeMillis() / 1000));
				Statement ipBansStatement = sqlConnection.createStatement();
				ResultSet ipBans = ipBansStatement.executeQuery("SELECT * FROM `IPBans` WHERE `ENDTIME` <=0 OR `ENDTIME` > " + (System.currentTimeMillis() / 1000));
				Statement ispBansStatement = sqlConnection.createStatement();
				ResultSet ispBans = ispBansStatement.executeQuery("SELECT * FROM `ISPBans` WHERE `ENDTIME` <=0 OR `ENDTIME` > " + (System.currentTimeMillis() / 1000));
				Statement userMutesStatement = sqlConnection.createStatement();
				ResultSet userMutes = userMutesStatement.executeQuery("SELECT * FROM `UserMutes` WHERE `ENDTIME` <=0 OR `ENDTIME` > " + (System.currentTimeMillis() / 1000))
		) {
			ConcurrentMap<String, Punishment> userBansMap = new ConcurrentHashMap<>();
			ConcurrentMap<String, Punishment> ipBansMap = new ConcurrentHashMap<>();
			ConcurrentMap<String, Punishment> ispBansMap = new ConcurrentHashMap<>();
			ConcurrentMap<String, Punishment> userMutesMap = new ConcurrentHashMap<>();
			while (userBans.next()) {
				userBansMap.put(userBans.getString("USERNAME").toLowerCase(), new Punishment(
						Punishment.PunishmentTarget.USERNAME,
						Punishment.PunishmentType.BAN,
						userBans.getString("USERNAME"),
						userBans.getString("REASON"),
						userBans.getLong("STARTTIME"),
						userBans.getLong("ENDTIME"),
						userBans.getString("BY")
				));
			}
			while (userMutes.next()) {
				userMutesMap.put(userMutes.getString("USERNAME").toLowerCase(), new Punishment(
						Punishment.PunishmentTarget.USERNAME,
						Punishment.PunishmentType.MUTE,
						userMutes.getString("USERNAME"),
						userMutes.getString("REASON"),
						userMutes.getLong("STARTTIME"),
						userMutes.getLong("ENDTIME"),
						userMutes.getString("BY")
				));
			}
			while (ipBans.next()) {
				ipBansMap.put(ipBans.getString("IP").toLowerCase(), new Punishment(
						Punishment.PunishmentTarget.IP,
						Punishment.PunishmentType.BAN,
						ipBans.getString("IP"),
						ipBans.getString("REASON"),
						ipBans.getLong("STARTTIME"),
						ipBans.getLong("ENDTIME"),
						ipBans.getString("BY")
				));
			}
			while (ispBans.next()) {
				ispBansMap.put(ispBans.getString("ASN").toLowerCase(), new Punishment(
						Punishment.PunishmentTarget.ISP,
						Punishment.PunishmentType.BAN,
						ispBans.getString("ASN"),
						ispBans.getString("REASON"),
						ispBans.getLong("STARTTIME"),
						ispBans.getLong("ENDTIME"),
						ispBans.getString("BY")
				));
			}
			punishments = new PunishmentCache(userBansMap, userMutesMap, ipBansMap, ispBansMap);
		}
		complete(runOnComplete, punishments);
	}
}
