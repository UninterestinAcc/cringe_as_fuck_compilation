package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;

public class AWP extends Weapon {

	public AWP() {
		super.weaponEnum = EnumWeapon.AWP;
		super.cost = 3000;
		super.damage = 22;
		super.reload = 6;
		super.speed = 1;
		super.clipSize = 10;
		super.punch = 7;
		super.recoil = 0.2;
		super.type = WeaponType.SNIPER_RIFLE;
		super.mat = Material.GOLD_HOE;
		super.sound = Sound.BURP;
		super.register();
	}
}
