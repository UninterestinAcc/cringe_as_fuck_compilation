package org.reborncraft.gtowny.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.reborncraft.gtowny.GTowny;

public class TCCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		GTowny.getTownCE().onCommand(sender, cmd, label, new String[]{"chat"});
		return true;
	}
}
