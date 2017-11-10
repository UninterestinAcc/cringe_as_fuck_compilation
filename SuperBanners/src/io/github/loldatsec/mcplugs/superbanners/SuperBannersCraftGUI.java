package io.github.loldatsec.mcplugs.superbanners;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class SuperBannersCraftGUI extends SuperBannersGUI {

	public static final PatternType[] patternTypes = new PatternType[] { PatternType.BASE, PatternType.BORDER, PatternType.BRICKS, PatternType.CIRCLE_MIDDLE, PatternType.CREEPER, PatternType.CROSS, PatternType.CURLY_BORDER, PatternType.DIAGONAL_LEFT, PatternType.DIAGONAL_LEFT_MIRROR, PatternType.DIAGONAL_RIGHT, PatternType.DIAGONAL_RIGHT_MIRROR, PatternType.FLOWER, PatternType.GRADIENT, PatternType.GRADIENT_UP, PatternType.HALF_HORIZONTAL, PatternType.HALF_HORIZONTAL_MIRROR, PatternType.HALF_VERTICAL, PatternType.HALF_VERTICAL_MIRROR, PatternType.MOJANG, PatternType.RHOMBUS_MIDDLE, PatternType.SKULL, PatternType.SQUARE_BOTTOM_LEFT, PatternType.SQUARE_BOTTOM_RIGHT, PatternType.SQUARE_TOP_LEFT, PatternType.SQUARE_TOP_RIGHT, PatternType.STRAIGHT_CROSS, PatternType.STRIPE_BOTTOM,
			PatternType.STRIPE_CENTER, PatternType.STRIPE_DOWNLEFT, PatternType.STRIPE_DOWNRIGHT, PatternType.STRIPE_LEFT, PatternType.STRIPE_MIDDLE, PatternType.STRIPE_RIGHT, PatternType.STRIPE_SMALL, PatternType.STRIPE_TOP, PatternType.TRIANGLE_BOTTOM, PatternType.TRIANGLE_TOP, PatternType.TRIANGLES_BOTTOM, PatternType.TRIANGLES_TOP };

	public SuperBannersCraftGUI(Player viewer) {
		super(viewer);
	}

	public void update() {
		super.update();
		int slot = 0;
		for (PatternType t : patternTypes) {
			ItemStack banner = sbinv.getItem(53);
			BannerMeta bm = (BannerMeta) banner.getItemMeta();
			bm.addPattern(new Pattern(getApplyColor(), t));
			bm.setDisplayName("\u00a7bPatternType." + t.toString());
			banner.setItemMeta(bm);
			sbinv.setItem(slot++, banner);
			sbinv.setItem(53, getBanner());
		}
	}

	public boolean click(InventoryClickEvent e) {
		if (super.click(e)) {
			try {
				if (sbinv.getItem(e.getSlot()).getType() == Material.BANNER) {
					banner = sbinv.getItem(e.getSlot());
					BannerMeta bm = (BannerMeta) banner.getItemMeta();
					bm.setDisplayName("\u00a74\u00a7lSuper Banner");
					banner.setItemMeta(bm);
					update();
				}
			} catch (NullPointerException npe) {
			}
		}
		return false;
	}
}