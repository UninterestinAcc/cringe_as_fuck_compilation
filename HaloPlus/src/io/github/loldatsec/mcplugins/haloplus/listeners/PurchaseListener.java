package io.github.loldatsec.mcplugins.haloplus.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.loldatsec.mcplugins.haloplus.game.GameManager;
import io.github.loldatsec.mcplugins.haloplus.grenades.EnumGrenade;
import io.github.loldatsec.mcplugins.haloplus.weapons.EnumWeapon;

public class PurchaseListener implements Listener {

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
		if (e.getCurrentItem() instanceof ItemStack && e.getSlot() >= 9 && (EnumWeapon.isWeapon(e.getCurrentItem().getType()) || EnumGrenade.isGrenade(e.getCurrentItem().getType()))) {
			if (GameManager.sendEvent(e)) {
				// Success
			}
		}
	}
}
