package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class USP extends Weapon {

	public USP() {
		super.weaponEnum = EnumWeapon.USP;
		super.cost = 450;
		super.damage = 12;
		super.reload = 2;
		super.speed = 0.3;
		super.clipSize = 25;
		super.punch = 4;
		super.recoil = 0.4;
		super.type = WeaponType.PISTOL;
		super.mat = Material.GOLD_AXE;
		super.sound = Sound.SKELETON_HURT;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
