package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;

public class M4_Carbine extends Weapon {

	public M4_Carbine() {
		super.weaponEnum = EnumWeapon.M4_Carbine;
		super.cost = 2800;
		super.damage = 12;
		super.reload = 5;
		super.speed = 0.15;
		super.clipSize = 45;
		super.punch = 6;
		super.recoil = 0.1;
		super.type = WeaponType.ASSAULT_RIFLE;
		super.mat = Material.STONE_SPADE;
		super.sound = Sound.WITHER_SHOOT;
		super.register();
	}
}
