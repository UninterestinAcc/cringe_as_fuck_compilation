package io.github.loldatsec.mcplugs.offlineinventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class OfflineInventoriesMain extends JavaPlugin implements Listener {

	List<OfflineInventory> cachedOffInventories = new ArrayList<OfflineInventory>();
	private static boolean is19;

	public void onEnable() {
		String[] va = Bukkit.getVersion().split(":")[1].replace(" ", "").split("\\.");
		is19 = Integer.parseInt(va[1]) >= 9 || Integer.parseInt(va[0]) > 1;
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("offlineinventories.use")) {
				if (args.length >= 1) {
					Player sn = (Player) sender;
					try {
						OfflineInventory oi = new OfflineInventory(sn, args[0]);
						if (label.toLowerCase().contains("inv")) {
							oi.getInventory();
						} else {
							oi.getEnderChest();
						}
						cachedOffInventories.add(oi);
					} catch (NullPointerException e) {
						sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Specified player is not registered.");
					} catch (Exception e) {
						e.printStackTrace();
						sender.sendMessage(ChatColor.RED + "FATAL ERROR: Exception " + e.getClass().getSimpleName() + " reached.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "At least 1 argument was expected.");
				}
			} else {
				noPermission(sender);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Dood! You are console!");
		}
		return true;
	}

	public void noPermission(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "You do not have the permissions for this action.");
	}

	@EventHandler
	public void invClose(InventoryCloseEvent e) {
		for (OfflineInventory oi : cachedOffInventories) {
			if (oi.getViewer().equals(e.getPlayer())) {
				if (oi.closeInventory(e)) {
					cachedOffInventories.remove(oi);
					return;
				}
			}
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().endsWith("Chest | \u00a7lOffline")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void blockInteract(PlayerInteractEvent e) {
		if (e.getPlayer().hasPermission("offlineinventories.use")) {
			Player p = e.getPlayer();
			if (e.getClickedBlock() instanceof Block) {
				Block b = e.getClickedBlock();
				if (b instanceof Block) {
					BlockState t = b.getState();
					if (t instanceof Chest) {
						Chest c = (Chest) t;
						BlockFace af;
						if (b.getRelative(BlockFace.NORTH).getType() == b.getType()) {
							af = BlockFace.NORTH;
						} else if (b.getRelative(BlockFace.EAST).getType() == b.getType()) {
							af = BlockFace.EAST;
						} else if (b.getRelative(BlockFace.SOUTH).getType() == b.getType()) {
							af = BlockFace.SOUTH;
						} else if (b.getRelative(BlockFace.WEST).getType() == b.getType()) {
							af = BlockFace.WEST;
						} else {
							Inventory inv = Bukkit.createInventory(p, 27, "Chest | \u00a7lOffline");
							for (int slot = 0; slot <= 26; slot++) {
								inv.setItem(slot, c.getBlockInventory().getItem(slot));
							}
							p.openInventory(inv);
							return;
						}
						BlockState t0 = b.getRelative(af).getState();
						Inventory inv = Bukkit.createInventory(p, 54, "Double Chest | \u00a7lOffline");
						for (int slot = 0; slot <= 26; slot++) {
							inv.setItem(slot, c.getBlockInventory().getItem(slot));
						}
						for (int slot = 0; slot <= 26; slot++) {
							inv.setItem(slot + 27, ((Chest) t0).getBlockInventory().getItem(slot));
						}
						p.openInventory(inv);
						return;
					}
				}

			}
		}
	}

	public static boolean is19() {
		return is19;
	}
}
