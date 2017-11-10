package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;

public enum EnumWeapon {
	AK101, AK47, AWP, Desert_Eagle, Glock17, HK416, M4_Carbine, M82, MAUL, P99, Spas12, USP;

	public static Material toMaterial(EnumWeapon ew) {
		return toWeapon(ew).mat;
	}

	public static Weapon toWeapon(EnumWeapon ew) {
		return HaloPlus.weapons.get(ew);
	}

	public static boolean isWeapon(Material m) {
		for (Weapon w : HaloPlus.weapons.values()) {
			if (w.mat == m) { return true; }
		}
		return false;
	}

	public static WeaponType toType(EnumWeapon ew) {
		return HaloPlus.weapons.get(ew).type;
	}

	public static EnumWeapon fromString(String n) {
		if (n == null || n == "") { return null; }
		n = n.toLowerCase();
		switch (n) {
			case "ak101":
				return AK101;
			case "ak47":
				return AK47;
			case "awp":
				return AWP;
			case "desert_eagle":
				return Desert_Eagle;
			case "glock17":
				return Glock17;
			case "hk416":
				return HK416;
			case "m4_carbine":
				return M4_Carbine;
			case "m82":
				return M82;
			case "maul":
				return MAUL;
			case "p99":
				return P99;
			case "spas12":
				return Spas12;
			case "usp":
				return USP;
		}
		return null;
	}

	public static EnumWeapon toWeaponEnum(Material m) {
		for (Weapon w : HaloPlus.weapons.values()) {
			if (w.mat == m) { return w.weaponEnum; }
		}
		return null;
	}
}