package io.github.loldatsec.mcplugins.haloplus.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.grenades.EnumGrenade;
import io.github.loldatsec.mcplugins.haloplus.weapons.EnumWeapon;

public class BuyWeaponsInterface {

	public static final ItemStack AIR = new ItemStack(Material.AIR);
	public static ItemStack NOWEAPONS = new ItemStack(Material.REDSTONE, 1);
	public static final EnumWeapon[] pistols = new EnumWeapon[] { EnumWeapon.Glock17, EnumWeapon.P99, EnumWeapon.USP, EnumWeapon.Desert_Eagle };
	public static final EnumWeapon[] shotgun = new EnumWeapon[] { EnumWeapon.Spas12, EnumWeapon.MAUL };
	public static final EnumWeapon[] assault = new EnumWeapon[] { EnumWeapon.HK416, EnumWeapon.AK47, EnumWeapon.AK101, EnumWeapon.M4_Carbine };
	public static final EnumWeapon[] sniper = new EnumWeapon[] { EnumWeapon.AWP, EnumWeapon.M82 };
	public static final EnumGrenade[] grenades = new EnumGrenade[] { EnumGrenade.Frag_Grenade, EnumGrenade.Molotov_Cocktail };

	public static void showInterface(Player p) {
		PlayerInventory inv = p.getInventory();
		int i = 9;
		for (EnumWeapon ew : pistols) {
			inv.setItem(i, HaloPlus.weapons.get(ew).getItem());
			i++;
		}
		i += 1;
		for (EnumWeapon ew : shotgun) {
			inv.setItem(i, HaloPlus.weapons.get(ew).getItem());
			i++;
		}
		i = 18;
		for (EnumWeapon ew : assault) {
			inv.setItem(i, HaloPlus.weapons.get(ew).getItem());
			i++;
		}
		i += 1;
		for (EnumGrenade eg : grenades) {
			inv.setItem(i, HaloPlus.grenades.get(eg).getItem());
			i++;
		}
		inv.setItem(22, AIR);
		i = 27;
		for (EnumWeapon ew : sniper) {
			inv.setItem(i, HaloPlus.weapons.get(ew).getItem());
			i++;
		}
	}

	public static void hideInterface(Player p) {
		PlayerInventory inv = p.getInventory();
		for (int i = 9; i <= 35; i++) {
			inv.setItem(i, AIR);
		}
		NOWEAPONS.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		ItemMeta m = NOWEAPONS.getItemMeta();
		m.setDisplayName("\u00a7c\u00a7lERROR");
		m.setLore(Arrays.asList(new String[] { "\u00a7cRound has already started.", "\u00a7fYou shall not buy!" }));
		NOWEAPONS.setItemMeta(m);
		inv.setItem(22, NOWEAPONS);
	}
}
