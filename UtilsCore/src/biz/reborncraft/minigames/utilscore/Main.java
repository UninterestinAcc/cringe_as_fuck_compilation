package biz.reborncraft.minigames.utilscore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public Messenger msgins = null;
	public Scheduler ScheduledTasks = null;
	public UtilsListener uListener = null;
	public PlayerList playerListins = null;

	public void onEnable() {
		ScheduledTasks = new Scheduler(this);
		msgins = new Messenger();
		uListener = new UtilsListener();
		playerListins = new PlayerList(this);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord",
				playerListins);
		ScheduledTasks.scheduleSyncRepeatingTask(new Runnable() {
			@Override
			public void run() { // 10 Minutely scheduled memory cleaning
				msgins.clearOffline();
			}
		}, 12000L);
		ScheduledTasks.scheduleSyncRepeatingTask(new Runnable() {
			@Override
			public void run() { // Minutely fetch online staff
				playerListins.requestPlayerlist();
			}
		}, 1200L);
		Bukkit.getPluginManager().registerEvents(uListener, this);
	}

	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("status")
				|| cmd.getName().equalsIgnoreCase("gc")
				|| cmd.getName().equalsIgnoreCase("lag")
				|| cmd.getName().equalsIgnoreCase("mem")) {
			Status.send(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("msg")
				|| cmd.getName().equalsIgnoreCase("m")
				|| cmd.getName().equalsIgnoreCase("whisper")
				|| cmd.getName().equalsIgnoreCase("w")
				|| cmd.getName().equalsIgnoreCase("tell")
				|| cmd.getName().equalsIgnoreCase("t")
				|| cmd.getName().equalsIgnoreCase("pm")) {
			msgins.pm(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("reply")
				|| cmd.getName().equalsIgnoreCase("r")) {
			msgins.reply(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("list")
				|| cmd.getName().equalsIgnoreCase("online")
				|| cmd.getName().equalsIgnoreCase("who")
				|| cmd.getName().equalsIgnoreCase("people")) {
			playerListins.list(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("staff")) {
			playerListins.staffList(sender, false);
		}
		return false;
	}

	public void noPermission(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED
				+ "You do not have the permissions for this action.");
	}
}
