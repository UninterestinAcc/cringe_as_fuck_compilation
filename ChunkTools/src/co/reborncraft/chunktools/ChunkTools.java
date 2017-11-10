package co.reborncraft.chunktools;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkTools extends JavaPlugin {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length >= 1) {
				switch (args[0].toLowerCase()) {
					case "regen": {
						if (sender.hasPermission("chunktool.regen")) {
							Chunk chunk = ((Player) sender).getLocation().getChunk();
							((Player) sender).getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
							sender.sendMessage("The chunk you are standing on has been regenerated.");
						} else {
							sender.sendMessage("\u00a7cYou don't have permissions to do this.");
						}
						return true;
					}
				}
			}
			sender.sendMessage("/chunktool <reload>");
		} else {
			sender.sendMessage("You need to be a player to do this.");
		}
		return true;
	}
}
