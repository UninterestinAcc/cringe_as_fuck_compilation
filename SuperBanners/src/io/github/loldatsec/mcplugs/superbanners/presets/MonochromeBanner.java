package io.github.loldatsec.mcplugs.superbanners.presets;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class MonochromeBanner {

	public static ItemStack create(DyeColor baseColor, DyeColor applyColor,
			Pattern[] template) {
		ItemStack b = new ItemStack(Material.BANNER, 1);
		BannerMeta bm = (BannerMeta) b.getItemMeta();
		bm.setBaseColor(baseColor);
		for (Pattern p : template) {
			if (p.getColor() == DyeColor.WHITE) {
				bm.addPattern(new Pattern(baseColor, p.getPattern()));
			} else {
				bm.addPattern(new Pattern(applyColor, p.getPattern()));
			}
		}
		b.setItemMeta(bm);
		return b;
	}
}
