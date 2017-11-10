package io.github.loldatsec.mcplugs.vanish;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Vanish extends JavaPlugin implements Listener, CommandExecutor {

	private List<String> vanished = new ArrayList<String>();

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player vp : Bukkit.getOnlinePlayers()) {
					if (vanished.contains(vp.getName())) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.hidePlayer(vp);
						}
					}
				}
			}
		}, 0, 10);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("vanish.use")) {
			if (sender instanceof Player) {
				if (vanished.contains(sender.getName())) {
					vanished.remove(sender.getName());
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.showPlayer((Player) sender);
					}
					((Player) sender).removePotionEffect(PotionEffectType.INVISIBILITY);
				} else {
					vanished.add(sender.getName());
					((Player) sender).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
				}
				sender.sendMessage("\u00a76Toggled vanish to \u00a7c" + vanished.contains(sender.getName()) + "\u00a76.");
			} else {
				sender.sendMessage("\u00a74You are console, you are already invisible.");
			}
		} else {
			sender.sendMessage("\u00a74No permissions.");
		}
		return true;
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (vanished.contains(p.getName())) {
				e.setCancelled(true);
				p.sendMessage("\u00a74You are currently vanished!");
			}
		}
	}
}
