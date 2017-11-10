package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;

public class HK416 extends Weapon {

	public HK416() {
		super.weaponEnum = EnumWeapon.HK416;
		super.cost = 1400;
		super.damage = 11;
		super.reload = 4;
		super.speed = 0.1;
		super.clipSize = 25;
		super.punch = 6;
		super.recoil = 1.4;
		super.type = WeaponType.ASSAULT_RIFLE;
		super.mat = Material.GOLD_SPADE;
		super.sound = Sound.WITHER_SHOOT;
		super.register();
	}
}
