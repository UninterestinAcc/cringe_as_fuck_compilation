package org.reborncraft.gtowny.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
			p.sendMessage(MessageFormatter.teleport("spawn"));
		} else {
			sender.sendMessage(MessageFormatter.error("Must be a player to teleport to spawn."));
		}
		return true;
	}
}