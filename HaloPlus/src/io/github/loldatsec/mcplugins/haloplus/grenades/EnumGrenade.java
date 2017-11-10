package io.github.loldatsec.mcplugins.haloplus.grenades;

import org.bukkit.Material;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;

public enum EnumGrenade {
	Molotov_Cocktail, Frag_Grenade;

	public static Material toMaterial(EnumGrenade ew) {
		return toGrenade(ew).mat;
	}

	public static Grenade toGrenade(EnumGrenade ew) {
		return HaloPlus.grenades.get(ew);
	}

	public static boolean isGrenade(Material m) {
		for (Grenade w : HaloPlus.grenades.values()) {
			if (w.mat == m) { return true; }
		}
		return false;
	}

	public static GrenadeType toType(EnumGrenade ew) {
		return HaloPlus.grenades.get(ew).type;
	}

	public static EnumGrenade toGrenadeEnum(Material m) {
		for (Grenade w : HaloPlus.grenades.values()) {
			if (w.mat == m) { return w.grenadeEnum; }
		}
		return null;
	}
}