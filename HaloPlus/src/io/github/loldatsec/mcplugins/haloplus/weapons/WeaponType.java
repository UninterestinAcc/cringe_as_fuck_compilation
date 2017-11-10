package io.github.loldatsec.mcplugins.haloplus.weapons;

public enum WeaponType {
	SNIPER_RIFLE, PISTOL, ASSAULT_RIFLE, SMG, SHOTGUN;

	public static int toSlot(WeaponType wt) {
		if (wt == WeaponType.PISTOL) {
			return 0;
		} else {
			return 1;
		}
	}
}
