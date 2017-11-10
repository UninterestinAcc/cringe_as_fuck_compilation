/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.impl.core.ChatUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class BlocksManager extends JavaPlugin implements Listener {

	public static void updateItem(ItemStack i) {
		if (i != null && i.getType() != null && i.getType() == Material.SKULL_ITEM) {
			SkullMeta skullMeta = (SkullMeta) i.getItemMeta();
			IBlock.BlockType type = IBlock.BlockType.forName(skullMeta.getOwner());
			if (type != null) {
				i.setItemMeta(IBlock.getSkullForType(type).getItemMeta());
			}
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		updateItem(e.getItem().getItemStack());
	}

	@EventHandler
	public void onItemClick(InventoryClickEvent e) {
		updateItem(e.getCurrentItem());
	}

	@EventHandler
	public void checkAllItems(InventoryOpenEvent e) {
		Inventory clickedInv = e.getInventory();
		for (int slotId = 0; slotId < clickedInv.getSize(); slotId++) {
			ItemStack i = clickedInv.getItem(slotId);
			updateItem(i);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Inventory inv = Bukkit.createInventory(p, 54);
			AtomicInteger i = new AtomicInteger(0);
			Arrays.stream(IBlock.BlockType.values()).forEach(blockType -> {
				ItemStack skull = blockType.getSkull();
				skull.setAmount(64);
				inv.setItem(i.getAndIncrement(), skull);
			});
			p.openInventory(inv);
		} else {
			ChatUtil.send(sender, ChatUtil.ChatMessage.NOT_PLAYER);
		}
		return true;
	}
}
