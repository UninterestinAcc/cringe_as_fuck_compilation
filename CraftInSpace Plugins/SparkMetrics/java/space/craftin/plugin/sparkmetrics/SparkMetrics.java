/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.sparkmetrics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

public class SparkMetrics extends JavaPlugin implements Listener {
	private YamlConfiguration logConfig;
	private File logFile;

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		log(e.getPlayer(), "J");
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		log(e.getPlayer(), "Q");
	}

	private void log(Player p, String separator) {
		final BigInteger ip = new BigInteger(p.getAddress().getAddress().getAddress());
		try (
				FileOutputStream fos = new FileOutputStream(new File(getDataFolder(), "metrics.txt"), true);
				PrintWriter pw = new PrintWriter(fos);
		) {
			pw.println(System.currentTimeMillis() + separator + ip.toString());
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}
}
