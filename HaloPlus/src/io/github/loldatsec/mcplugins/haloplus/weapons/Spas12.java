package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Spas12 extends Weapon {

	public Spas12() {
		super.weaponEnum = EnumWeapon.Spas12;
		super.cost = 1500;
		super.damage = 10;
		super.reload = 6;
		super.speed = 0.6;
		super.clipSize = 8;
		super.punch = 6;
		super.recoil = 1;
		super.type = WeaponType.SHOTGUN;
		super.mat = Material.IRON_PICKAXE;
		super.sound = Sound.BURP;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
