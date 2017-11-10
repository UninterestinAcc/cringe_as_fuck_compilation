package co.reborncraft.syslogin_banmanager.api.futures.banmanager;

import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PushPunishmentFuture implements IFuture {
	private final CommandSender staff;
	private final Punishment punishment;

	public PushPunishmentFuture(CommandSender staff, Punishment punishment) throws IllegalArgumentException {
		if (punishment.getPunishmentType() == Punishment.PunishmentType.KICK) {
			throw new IllegalArgumentException("Database does not record kicks.");
		}
		this.staff = staff;
		this.punishment = punishment;
	}

	@Override
	public CommandSender getRequester() {
		return staff;
	}

	@Override
	public void execute(Connection sqlConnection) {
		boolean notWarn = punishment.getPunishmentType() != Punishment.PunishmentType.WARN;
		String sql = "INSERT INTO `" +
				punishment.getTargetType().getDbPrefix() + punishment.getPunishmentType().getDbSuffix() +
				"` (`" + punishment.getTargetType().getDbIdentifier() +
				"`, `STARTTIME`, " +
				(notWarn ? "`ENDTIME`, " : "") +
				"`BY`, `REASON`) VALUES " +
				"(IFNULL((SELECT `USERNAME` FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER(?) LIMIT 1), ?), " + // Prefer the correct capitalization if available
				"?, ?, " +
				(notWarn ? "?, " : "") +
				"?)";
		try (PreparedStatement pushPrepare = sqlConnection.prepareStatement(sql)) {
			int i = 1;
			pushPrepare.setString(i++, punishment.getTarget());
			pushPrepare.setString(i++, punishment.getTarget());
			pushPrepare.setLong(i++, punishment.getStartTime());
			if (notWarn) {
				pushPrepare.setLong(i++, punishment.getEndTime());
			}
			pushPrepare.setString(i++, punishment.getBy());
			pushPrepare.setString(i, punishment.getReason());

			pushPrepare.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
