package co.reborncraft.namecolor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class NameColor extends JavaPlugin implements Listener {
	private final String INVENTORY_TITLE = "\u00a70\u00a7n\u00a7oName Color Changer";

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onInventory(InventoryClickEvent e) {
		if (e.getClickedInventory().getName().equals(INVENTORY_TITLE)) {
			e.setCancelled(true);
			if (e.getWhoClicked() instanceof Player) {
				ItemStack targetItem = e.getCurrentItem();
				if (targetItem != null) {
					ItemMeta meta = targetItem.getItemMeta();
					if (meta != null) {
						String displayName = meta.getDisplayName();
						if (displayName != null) {
							if (displayName.length() >= 2 && displayName.charAt(0) == 0xA7) {
								setName((Player) e.getWhoClicked(), displayName);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player && sender.hasPermission("donor.color")) {
			Inventory inv = Bukkit.createInventory(((Player) sender), 45, INVENTORY_TITLE);
			inv.setMaxStackSize(1);

			int slot = 0;
			inv.setItem(slot++, generateTunic(Color.fromRGB(0), "0"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x00, 0x00, 0xAA), "1"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x00, 0xAA, 0x00), "2"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x00, 0xAA, 0xAA), "3"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xAA, 0x00, 0x00), "4"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xAA, 0x00, 0xAA), "5"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0xAA, 0x00), "6"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xAA, 0xAA, 0xAA), "7"));

			slot++;

			inv.setItem(slot++, generateTunic(Color.fromRGB(0x15, 0x15, 0x15), "8"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x15, 0x15, 0x3F), "9"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x15, 0x3F, 0x15), "a"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x55, 0xFF, 0xFF), "b"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0x55, 0x55), "c"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0x55, 0xFF), "d"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0xFF, 0x55), "e"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0xFF, 0xFF), "f"));

			slot++;

			inv.setItem(slot++, generateTunic(Color.fromRGB(0), "0l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x00, 0x00, 0xAA), "1l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x00, 0xAA, 0x00), "2l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x00, 0xAA, 0xAA), "3l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xAA, 0x00, 0x00), "4l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xAA, 0x00, 0xAA), "5l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0xAA, 0x00), "6l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xAA, 0xAA, 0xAA), "7l"));

			slot++;

			inv.setItem(slot++, generateTunic(Color.fromRGB(0x15, 0x15, 0x15), "8l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x15, 0x15, 0x3F), "9l"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x15, 0x3F, 0x15), "al"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0x55, 0xFF, 0xFF), "bl"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0x55, 0x55), "cl"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0x55, 0xFF), "dl"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0xFF, 0x55), "el"));
			inv.setItem(slot++, generateTunic(Color.fromRGB(0xFF, 0xFF, 0xFF), "fl"));

			slot += 9;

			ItemStack resetChest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			ItemMeta meta = resetChest.getItemMeta();
			meta.setDisplayName("\u00a7rReset Colors");
			resetChest.setItemMeta(meta);
			inv.setItem(slot, resetChest);
			((Player) sender).openInventory(inv);
		} else {
			sender.sendMessage("\u00a7cNo permissions.");
		}
		return true;
	}

	private ItemStack generateTunic(Color colour, String colourCode) {
		String[] colours = colourCode.split("");
		ItemStack plate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		LeatherArmorMeta meta = (LeatherArmorMeta) plate.getItemMeta();
		meta.setDisplayName("\u00a7" + String.join("\u00a7", colours) + "Color Code &" + String.join("&", colours));
		meta.setColor(colourCode.startsWith("l") ? colour.mixColors(Color.fromRGB(0xFF, 0xFF, 0xFF)) : colour);
		plate.setItemMeta(meta);
		return plate;
	}

	private void setName(Player player, String itemDisplayName) {
		String name = itemDisplayName.replaceAll("^((\u00a7[a-fA-Fl-oL-O0-9])*).+$", "$1") + player.getName();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nick " + player.getName() + " " + (name.equals(player.getName()) ? "off" : name));
	}
}
