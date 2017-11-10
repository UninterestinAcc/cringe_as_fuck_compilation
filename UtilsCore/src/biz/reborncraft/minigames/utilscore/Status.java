package biz.reborncraft.minigames.utilscore;

import java.lang.management.ManagementFactory;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class Status {
	public static void send(CommandSender sender) {
		if (sender.hasPermission("utils.status")) {
			long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
			sender.sendMessage("§e------------ §6Server Status §e------------");
			sender.sendMessage("§6Version: " + Bukkit.getVersion());
			Bukkit.dispatchCommand(sender, "tps");
			sender.sendMessage("§6Uptime: §c"
					+ (int) (Math.floor(uptime / 3600)) + " hours "
					+ (int) (Math.floor((uptime % 3600) / 60)) + " minutes "
					+ (int) (uptime % 60) + " seconds.");
			sender.sendMessage("§6Memory Usage: §e[§aUsed§e|§cFree§e/§bTotal§e] [§a"
					+ ((Runtime.getRuntime().totalMemory() - Runtime
							.getRuntime().freeMemory()) / 1024 / 1024)
					+ "MB§e|§c"
					+ +(Runtime.getRuntime().freeMemory() / 1024 / 1024)
					+ "MB§e/§b"
					+ (Runtime.getRuntime().totalMemory() / 1024 / 1024)
					+ "MB§e]");
			sender.sendMessage("§6Available CPU cores: §e"
					+ Runtime.getRuntime().availableProcessors() + "§6 cores.");
			for (World w : Bukkit.getWorlds()) {
				sender.sendMessage("§6World: §c" + w.getName() + "§6: §e"
						+ w.getEntities().size() + " §6entities (§e"
						+ w.getPlayers().size() + "§6 players, §e"
						+ w.getLivingEntities().size() + "§6 mobs.), §e"
						+ w.getLoadedChunks().length + " §6loaded chunks.");
			}
		} else {
			sender.sendMessage("§cError: " + "§4"
					+ "You do not have permission.");
		}
	}
}
