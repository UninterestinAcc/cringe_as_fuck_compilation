package io.github.loldatsec.mcplugs.superbanners;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class SuperBannersGUI {

	protected Inventory sbinv = null;
	private DyeColor baseColor = DyeColor.WHITE;
	private Player viewer = null;
	private DyeColor applyColor = DyeColor.BLACK;
	public static final String prefix = "\u00a74\u00a7lSuperBanners";
	protected ItemStack banner = null;

	public SuperBannersGUI(Player viewer) {
		this.viewer = viewer;
		sbinv = Bukkit.createInventory(viewer, 54, prefix);
		banner = new ItemStack(Material.BANNER, 1);
		BannerMeta bm = (BannerMeta) banner.getItemMeta();
		bm.setBaseColor(baseColor);
		bm.setDisplayName("\u00a74\u00a7lSuper Banner");
		banner.setItemMeta(bm);
		update();
		viewer.openInventory(sbinv);
	}

	public boolean click(InventoryClickEvent e) {
		e.setCancelled(true);
		if (e.getClick() == ClickType.RIGHT) {
			if (e.getSlot() == 51) {
				decrementApplyColor();
			} else if (e.getSlot() == 52) {
				decrementBaseColor();
			}
		} else if (e.getClick() == ClickType.LEFT) {
			if (e.getSlot() == 45) {
				BannerMeta bm = (BannerMeta) banner.getItemMeta();
				if (bm.getPatterns().size() >= 1) {
					bm.removePattern(bm.getPatterns().size() - 1);
				}
				banner.setItemMeta(bm);
				sbinv.setItem(53, banner);
			} else if (e.getSlot() == 51) {
				incrementApplyColor();
			} else if (e.getSlot() == 52) {
				incrementBaseColor();
			} else if (e.getSlot() == 53) {
				viewer.getInventory().addItem(banner);
				viewer.closeInventory();
			} else {
				return true;
			}
		} else {
			return false;
		}
		update();
		return false;
	}

	public void update() {
		sbinv.setItem(45, SuperBannersGUIElements.undo());
		sbinv.setItem(51, SuperBannersGUIElements.applyColorChooser(applyColor));
		sbinv.setItem(52, SuperBannersGUIElements.baseColorChooser(baseColor));
		sbinv.setItem(53, banner);
	}

	public ItemStack getBanner() {
		return banner;
	}

	public Player getViewer() {
		return viewer;
	}

	public void setViewer(Player viewer) {
		this.viewer = viewer;
	}

	public DyeColor getBaseColor() {
		return baseColor;
	}

	public void incrementBaseColor() {
		baseColor = incrementColor(baseColor);
		updateBannerColor();
	}

	public void decrementBaseColor() {
		baseColor = decrementColor(baseColor);
		updateBannerColor();
	}

	public void updateBannerColor() {
		BannerMeta bm = (BannerMeta) banner.getItemMeta();
		bm.setBaseColor(baseColor);
		banner.setItemMeta(bm);
		sbinv.setItem(53, banner);
	}

	public DyeColor getApplyColor() {
		return applyColor;
	}

	public void incrementApplyColor() {
		applyColor = incrementColor(applyColor);
	}

	public void decrementApplyColor() {
		applyColor = decrementColor(applyColor);
	}

	public DyeColor incrementColor(DyeColor sc) {
		if (sc == DyeColor.WHITE) {
			return (DyeColor.ORANGE);
		} else if (sc == DyeColor.ORANGE) {
			return (DyeColor.MAGENTA);
		} else if (sc == DyeColor.MAGENTA) {
			return (DyeColor.LIGHT_BLUE);
		} else if (sc == DyeColor.LIGHT_BLUE) {
			return (DyeColor.YELLOW);
		} else if (sc == DyeColor.YELLOW) {
			return (DyeColor.LIME);
		} else if (sc == DyeColor.LIME) {
			return (DyeColor.PINK);
		} else if (sc == DyeColor.PINK) {
			return (DyeColor.GRAY);
		} else if (sc == DyeColor.GRAY) {
			return (DyeColor.SILVER);
		} else if (sc == DyeColor.SILVER) {
			return (DyeColor.CYAN);
		} else if (sc == DyeColor.CYAN) {
			return (DyeColor.PURPLE);
		} else if (sc == DyeColor.PURPLE) {
			return (DyeColor.BLUE);
		} else if (sc == DyeColor.BLUE) {
			return (DyeColor.BROWN);
		} else if (sc == DyeColor.BROWN) {
			return (DyeColor.GREEN);
		} else if (sc == DyeColor.GREEN) {
			return (DyeColor.RED);
		} else if (sc == DyeColor.RED) {
			return (DyeColor.BLACK);
		} else if (sc == DyeColor.BLACK) {
			return (DyeColor.WHITE);
		} else {
			return DyeColor.WHITE;
		}
	}

	public DyeColor decrementColor(DyeColor sc) {
		if (sc == DyeColor.WHITE) {
			return (DyeColor.BLACK);
		} else if (sc == DyeColor.ORANGE) {
			return (DyeColor.WHITE);
		} else if (sc == DyeColor.MAGENTA) {
			return (DyeColor.ORANGE);
		} else if (sc == DyeColor.LIGHT_BLUE) {
			return (DyeColor.MAGENTA);
		} else if (sc == DyeColor.YELLOW) {
			return (DyeColor.LIGHT_BLUE);
		} else if (sc == DyeColor.LIME) {
			return (DyeColor.YELLOW);
		} else if (sc == DyeColor.PINK) {
			return (DyeColor.LIME);
		} else if (sc == DyeColor.GRAY) {
			return (DyeColor.PINK);
		} else if (sc == DyeColor.SILVER) {
			return (DyeColor.GRAY);
		} else if (sc == DyeColor.CYAN) {
			return (DyeColor.SILVER);
		} else if (sc == DyeColor.PURPLE) {
			return (DyeColor.CYAN);
		} else if (sc == DyeColor.BLUE) {
			return (DyeColor.PURPLE);
		} else if (sc == DyeColor.BROWN) {
			return (DyeColor.BLUE);
		} else if (sc == DyeColor.GREEN) {
			return (DyeColor.BROWN);
		} else if (sc == DyeColor.RED) {
			return (DyeColor.GREEN);
		} else if (sc == DyeColor.BLACK) {
			return (DyeColor.RED);
		} else {
			return DyeColor.WHITE;
		}
	}
}
