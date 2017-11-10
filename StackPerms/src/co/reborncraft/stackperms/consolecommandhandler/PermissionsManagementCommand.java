package co.reborncraft.stackperms.consolecommandhandler;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.SavePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface PermissionsManagementCommand extends CommandExecutor {
	@Override
	default boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp() || sender.hasPermission("stackperms.commands")) {
			if (args.length >= 1) {
				String[] strippedArgs = new String[args.length - 1];
				System.arraycopy(args, 1, strippedArgs, 0, args.length - 1);
				execute(sender, StackPerms.getInterface().getOrCreateSavePlayer(args[0]), strippedArgs);
			} else {
				sender.sendMessage("\u00a74\u00a7lError! \u00a77Not enough arguments, please at least specify the player.");
			}
		} else {
			sender.sendMessage("\u00a74\u00a7lError! \u00a77You don't have permissions to do this!");
		}
		return true;
	}

	void execute(CommandSender sender, SavePlayer sp, String[] args);
}
