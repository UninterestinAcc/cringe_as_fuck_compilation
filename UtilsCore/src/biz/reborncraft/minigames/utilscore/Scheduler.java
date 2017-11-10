package biz.reborncraft.minigames.utilscore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Scheduler {
	public JavaPlugin mainPlugin = null;

	public Scheduler(JavaPlugin p) {
		mainPlugin = p;
	}

	public void scheduleSyncRepeatingTask(Runnable r, long delay) {
		Bukkit.getScheduler()
				.scheduleSyncRepeatingTask(mainPlugin, r, 0, delay);
	}

	public void scheduleDelayedTask(Runnable r, long delay) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, r, delay);
	}
}
