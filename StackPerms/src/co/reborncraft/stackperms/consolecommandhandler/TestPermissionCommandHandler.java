package co.reborncraft.stackperms.consolecommandhandler;

import co.reborncraft.stackperms.api.SavePlayer;
import org.bukkit.command.CommandSender;

public class TestPermissionCommandHandler implements PermissionsManagementCommand {
	@Override
	public void execute(CommandSender sender, SavePlayer sp, String[] args) {
		if (args.length >= 1) {
			for (String perm : args) {
				sender.sendMessage(sp.getName() + "> " + perm + ": " + sp.hasPermission(perm));
			}
		} else {
			sender.sendMessage("\u00a74\u00a7lError! \u00a77At least 1 permission needs to be specified.");
		}
	}
}
