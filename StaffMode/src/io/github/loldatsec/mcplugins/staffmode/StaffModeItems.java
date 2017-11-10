package io.github.loldatsec.mcplugins.staffmode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class StaffModeItems {

	public static ItemStack kbTester() {
		ItemStack i = new ItemStack(Material.SULPHUR);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a7c\u00a7lKB Tester");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack noDamageKbTester() {
		ItemStack i = new ItemStack(Material.SUGAR);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a7b\u00a7lKB Tester (No Damage)");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack banStick() {
		ItemStack i = new ItemStack(Material.STICK);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a77\u00a7lBan Stick");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack reachyBanStick() {
		ItemStack i = new ItemStack(Material.BLAZE_ROD);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a76\u00a7lReachy Ban Stick");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack teleport() {
		ItemStack i = new ItemStack(Material.SLIME_BALL);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a7d\u00a7lTeleporter");
		i.setItemMeta(im);
		return i;
	}

	@Deprecated
	public static ItemStack randomTeleport() {
		ItemStack i = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a7e\u00a7lRandomized Teleporter");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack follow() {
		ItemStack i = new ItemStack(Material.GHAST_TEAR);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a78\u00a7lFollow Player");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack inspect() {
		ItemStack i = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a73\u00a7lInspect Inventory");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack devanish() {
		ItemStack i = new ItemStack(Material.FIREWORK_CHARGE);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a7e\u00a7lDevanish to target for 0.25 seconds");
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack healthItem(Player victim) {
		ItemStack i = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a7c\u00a7lHealth of " + victim.getName());
		im.setLore(Arrays.asList(
			new String[] { "\u00a7cHealth: " + (int) victim.getHealth(), "\u00a76FireTicks: " + (victim.getFireTicks() < 0 ? 0 : victim.getFireTicks()), "\u00a7eHunger: " + victim.getFoodLevel(), "\u00a7aXP Level: " + victim.getLevel() }));
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack potEffectsItem(Player victim) {
		ItemStack i = new ItemStack(Material.GLASS_BOTTLE);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("\u00a7b\u00a7lPotion effects of " + victim.getName());
		List<String> lore = new ArrayList<String>();
		for (PotionEffect pe : victim.getActivePotionEffects()) {
			lore.add("\u00a73" + (pe.getAmplifier() + 1) + " * " + pe.getType().getName() + " - " + (int) (pe.getDuration() / 1200) + " min " + (pe.getDuration() / 20 % 60) + " sec");
		}
		im.setLore(lore);
		i.setItemMeta(im);
		return i;
	}
}
