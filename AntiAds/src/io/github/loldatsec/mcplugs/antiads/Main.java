package io.github.loldatsec.mcplugs.antiads;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	private Match stringMatch = new Match();

	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("aaconf")) {
			sender.sendMessage("§cError: " + "§4" + "You do not have sufficient permissions.");
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void command(PlayerCommandPreprocessEvent e) {
		stringMatch.advertisement(e.getMessage(), e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void chat(AsyncPlayerChatEvent e) {
		stringMatch.advertisement(e.getMessage(), e.getPlayer().getName());
	}
}
