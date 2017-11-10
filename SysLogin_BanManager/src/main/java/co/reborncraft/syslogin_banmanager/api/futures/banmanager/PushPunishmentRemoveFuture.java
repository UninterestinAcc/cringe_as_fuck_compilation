package co.reborncraft.syslogin_banmanager.api.futures.banmanager;

import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PushPunishmentRemoveFuture implements IFuture {
	private final CommandSender staff;
	private final Punishment punishment;

	public PushPunishmentRemoveFuture(CommandSender staff, Punishment punishment) throws IllegalArgumentException {
		if (punishment.getPunishmentType() != Punishment.PunishmentType.BAN && punishment.getPunishmentType() != Punishment.PunishmentType.MUTE) {
			throw new IllegalArgumentException("Only bans and mutes can be revoked.");
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
		String sql = "UPDATE `" + punishment.getTargetType().getDbPrefix() + punishment.getPunishmentType().getDbSuffix() +
				"` SET `ENDTIME`=?, `LIFTED_BY`=? WHERE LOWER(`" + punishment.getTargetType().getDbIdentifier() +
				"`) = ? AND `STARTTIME` = ?";
		try (PreparedStatement pushPrepare = sqlConnection.prepareStatement(sql)) {
			int i = 1;
			pushPrepare.setLong(i++, System.currentTimeMillis() / 1000);
			pushPrepare.setString(i++, staff.getName());
			pushPrepare.setString(i++, punishment.getTarget());
			pushPrepare.setLong(i, punishment.getStartTime());

			pushPrepare.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
