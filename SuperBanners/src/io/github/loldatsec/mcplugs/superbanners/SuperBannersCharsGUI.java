package io.github.loldatsec.mcplugs.superbanners;

import io.github.loldatsec.mcplugs.superbanners.presets.BannerChars;
import io.github.loldatsec.mcplugs.superbanners.presets.MonochromeBanner;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SuperBannersCharsGUI extends SuperBannersGUI {

	public SuperBannersCharsGUI(Player viewer) {
		super(viewer);
	}

	public void update() {
		super.update();
		int slot = 0;
		for (Pattern[] p : BannerChars.all) {
			sbinv.setItem(slot++,
					MonochromeBanner.create(getBaseColor(), getApplyColor(), p));
		}
	}

	public boolean click(InventoryClickEvent e) {
		if (super.click(e)) {
			try {
				if (sbinv.getItem(e.getSlot()).getType() == Material.BANNER) {
					e.setCancelled(true);
					getViewer().getInventory().addItem(sbinv.getItem(e.getSlot()));
					update();
				}
			} catch (NullPointerException npe) {
			}
		}
		return false;
	}
}