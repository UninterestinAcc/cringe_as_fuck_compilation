package io.github.loldatsec.mcplugs.superbanners;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SuperBannersGUIElements {

	public static ItemStack baseColorChooser(DyeColor baseColor) {
		@SuppressWarnings("deprecation")
		ItemStack base = new ItemStack(Material.WOOL, 1, baseColor.getData());
		ItemMeta baseMeta = base.getItemMeta();
		baseMeta.setDisplayName("\u00a76\u00a7lBanner Color");
		base.setItemMeta(baseMeta);
		return base;
	}

	public static ItemStack applyColorChooser(DyeColor applyColor) {
		@SuppressWarnings("deprecation")
		ItemStack apply = new ItemStack(Material.STAINED_GLASS_PANE, 1, applyColor.getData());
		ItemMeta applyMeta = apply.getItemMeta();
		applyMeta.setDisplayName("\u00a76\u00a7lApply Color");
		apply.setItemMeta(applyMeta);
		return apply;
	}

	public static ItemStack undo() {
		ItemStack b = new ItemStack(Material.REDSTONE_BLOCK, 1);
		ItemMeta bMeta = b.getItemMeta();
		bMeta.setDisplayName("\u00a7cUndo");
		b.setItemMeta(bMeta);
		return b;
	}

	@Deprecated
	public static ItemStack redo() {
		ItemStack b = new ItemStack(Material.SLIME_BLOCK, 1);
		ItemMeta bMeta = b.getItemMeta();
		bMeta.setDisplayName("\u00a7aRedo");
		b.setItemMeta(bMeta);
		return b;
	}
}
