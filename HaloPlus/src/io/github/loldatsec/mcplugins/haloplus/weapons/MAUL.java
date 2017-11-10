package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MAUL extends Weapon {

	public MAUL() {
		super.weaponEnum = EnumWeapon.MAUL;
		super.cost = 2300;
		super.damage = 11;
		super.reload = 6;
		super.speed = 0.5;
		super.clipSize = 12;
		super.punch = 7;
		super.recoil = 1;
		super.type = WeaponType.SHOTGUN;
		super.mat = Material.DIAMOND_PICKAXE;
		super.sound = Sound.BURP;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
