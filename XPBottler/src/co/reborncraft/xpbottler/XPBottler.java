package co.reborncraft.xpbottler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class XPBottler extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getInventory().getName().equals(getMessage("inventory.name"))) {
			Location eyeLocation = e.getPlayer().getEyeLocation();
			World world = eyeLocation.getWorld();
			for (ItemStack is : e.getInventory()) {
				if (is != null && is.getAmount() > 0) {
					world.dropItem(eyeLocation, is);
				}
			}
			e.getInventory().clear();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("xpbottler.reload")) {
				reloadConfig();
				sender.sendMessage(getMessage("reloaded"));
			} else {
				sender.sendMessage(getMessage("fail.noreloadperms"));
			}
			return true;
		}
		if (sender instanceof Player) {
			if (!getConfig().getBoolean("requirePermissions", false) || sender.hasPermission("xpbottler.use")) {
				Player p = (Player) sender;
				int totXP = p.getTotalExperience();
				double factor = getConfig().getDouble("conversionFactor", 22);
				if (factor <= 0) {
					throw new IllegalStateException("XP Conversion Factor is at or below 0!");
				}
				int bottles = (int) ((totXP / factor) + 0.5);
				if (bottles > 0) {
					if (bottles < 54 * 64) {
						p.setTotalExperience(0);
						p.setExp(0);
						p.setLevel(0);
						Inventory inv = Bukkit.createInventory(p, 54, getMessage("inventory.name"));
						for (int i = 0; i < 54 && bottles > 0; i++) {
							inv.setItem(i, new ItemStack(Material.EXP_BOTTLE, bottles < 64 ? bottles : 64));
							bottles -= 64;
						}
						p.openInventory(inv);
						sender.sendMessage(getMessage("bottled").replaceAll("\\{xptook}", totXP + ""));
					} else {
						sender.sendMessage(getMessage("fail.toomuchxp"));
					}
				} else {
					sender.sendMessage(getMessage("fail.notenoughxp"));
				}
			}
		} else {
			sender.sendMessage(getMessage("notplayer"));
		}
		return true;
	}

	private String getMessage(String messageNode) {
		return getConfig().getString("message." + messageNode, "&cNot configured properly: &7" + messageNode).replaceAll("&([0-9a-fA-Fk-oK-OrR])", "\u00a7$1");
	}

}
