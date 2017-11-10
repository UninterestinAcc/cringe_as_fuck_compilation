package co.reborncraft.syslogin_banmanager.api.futures.management;

import co.reborncraft.syslogin_banmanager.api.futures.IDataFuture;
import net.md_5.bungee.api.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public class PushStaffFuture implements IDataFuture {
	private final CommandSender staff;
	private final String target;
	private final long permissions;
	private final boolean demote;
	private final Consumer<Boolean> runOnComplete;

	public PushStaffFuture(CommandSender staff, String target, long permissions, boolean demote, Consumer<Boolean> runOnComplete) throws IllegalArgumentException {
		this.staff = staff;
		this.target = target;
		this.permissions = permissions;
		this.demote = demote;
		this.runOnComplete = runOnComplete;
	}

	@Override
	public CommandSender getRequester() {
		return staff;
	}

	@Override
	public void execute(Connection sqlConnection) {
		boolean success = false;
		if (demote) {
			try (PreparedStatement pushPrepare = sqlConnection.prepareStatement("DELETE FROM `Staffs` WHERE LOWER(`USERNAME`) = LOWER(?)")) {
				pushPrepare.setString(1, target);

				pushPrepare.executeUpdate();
				success = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try (PreparedStatement pushPrepare = sqlConnection.prepareStatement("INSERT INTO `Staffs` (`USERNAME`, `PERMISSIONS`) VALUES (IFNULL((SELECT `USERNAME` FROM `Logins` WHERE LOWER(`USERNAME`) = LOWER(?) LIMIT 1), ?), ?) ON DUPLICATE KEY UPDATE `PERMISSIONS`=?")) {
				int i = 1;
				pushPrepare.setString(i++, target);
				pushPrepare.setString(i++, target);
				pushPrepare.setLong(i++, permissions);
				pushPrepare.setLong(i, permissions);

				pushPrepare.executeUpdate();
				success = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		complete(runOnComplete, success);
	}
}
