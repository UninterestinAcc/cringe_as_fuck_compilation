package co.reborncraft.rtp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class RTP extends JavaPlugin {
	private File lastTeleportData;
	private YamlConfiguration lastTeleportConf;
	private Runnable reloadRunnable;

	private String secondsToString(long totalSeconds) {
		long days = totalSeconds / 86400L;
		long hours = totalSeconds % 86400L / 3600L;
		long minutes = totalSeconds % 3600L / 60L;
		long seconds = totalSeconds % 60L;
		return (totalSeconds >= 86400L ? days + " day" + (days != 1L ? "s" : "") + " " : "") + (totalSeconds >= 3600L ? hours + " hour" + (hours != 1L ? "s" : "") + " " : "") + (totalSeconds >= 60L ? minutes + " minute" + (minutes != 1L ? "s" : "") + " " : "") + seconds + " second" + (seconds != 1L ? "s" : "");
	}

	@Override
	public void onEnable() {
		reloadRunnable = () -> {
			try {
				lastTeleportConf.save(lastTeleportData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};

		saveDefaultConfig();

		lastTeleportData = new File(getDataFolder(), "lastTeleports.yml");
		lastTeleportConf = YamlConfiguration.loadConfiguration(lastTeleportData);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, reloadRunnable, 24000, 24000);
	}

	@Override
	public void onDisable() {
		reloadRunnable.run();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("rtp.reload")) {
				reloadConfig();
				sender.sendMessage(getMessage("reloaded"));
			} else {
				sender.sendMessage(getMessage("fail.noreloadperms"));
			}
			return true;
		}
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (checkCooldown(p)) {
				Location loc = null;
				World w = p.getWorld();
				int iter = 0;
				while (loc == null) {
					loc = generateLocation(w);
					iter++;
					if (iter > 500) {
						p.sendMessage(getMessage("fail.toomanyiter"));
						return true;
					}
				}
				if (p.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND)) {
					lastTeleportConf.set(p.getName(), System.currentTimeMillis());
					p.sendMessage(getMessage("success").replaceAll("\\{location}", locToString(loc)));
				} else {
					p.sendMessage(getMessage("fail.blocked"));
				}
			} else {
				p.sendMessage(getMessage("cooldown").replaceAll("\\{time}", secondsToString(lastTeleportConf.getLong(p.getName().toLowerCase()) - System.currentTimeMillis() + getCooldown())));
			}
		} else {
			sender.sendMessage(getMessage("notplayer"));
		}
		return true;
	}

	private Location generateLocation(World w) {
		double minRad = getConfig().getDouble("radius.min", 300);
		double maxRad = getConfig().getDouble("radius.max", 10000);
		double range = maxRad - minRad;
		Location randLoc = w.getSpawnLocation().add((Math.random() < 0.5 ? 1 : -1) * Math.random() * range + minRad, 127, (Math.random() < 0.5 ? 1 : -1) * Math.random() * range + minRad);
		while (randLoc.getBlock().getType() != Material.AIR || randLoc.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR || !randLoc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
			randLoc.subtract(0, 1, 0);
			if (randLoc.getBlockY() <= 1) {
				return null;
			}
		}
		return randLoc;
	}

	private String locToString(Location loc) {
		return "X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z:" + loc.getBlockZ();
	}

	private long getCooldown() {
		return getConfig().getLong("cooldown", 86400);
	}

	private String getMessage(String messageNode) {
		return getConfig().getString("message." + messageNode, "&cNot configured properly: &7" + messageNode).replaceAll("&([0-9a-fA-Fk-oK-OrR])", "\u00a7$1");
	}

	private boolean checkCooldown(Player p) {
		return p.hasPermission("rtp.bypasscooldown") || lastTeleportConf.getLong(p.getName(), 0) > System.currentTimeMillis() - getCooldown();
	}
}
