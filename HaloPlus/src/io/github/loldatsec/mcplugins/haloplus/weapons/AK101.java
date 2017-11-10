package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AK101 extends Weapon {

	public AK101() {
		super.weaponEnum = EnumWeapon.AK101;
		super.cost = 2500;
		super.damage = 9;
		super.reload = 4;
		super.speed = 0.1;
		super.clipSize = 40;
		super.punch = 6;
		super.recoil = 1.4;
		super.type = WeaponType.ASSAULT_RIFLE;
		super.mat = Material.DIAMOND_SPADE;
		super.sound = Sound.WITHER_SHOOT;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
