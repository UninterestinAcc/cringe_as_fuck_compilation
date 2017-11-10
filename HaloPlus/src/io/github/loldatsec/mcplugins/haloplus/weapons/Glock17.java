package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Glock17 extends Weapon {

	public Glock17() {
		super.weaponEnum = EnumWeapon.Glock17;
		super.cost = 150;
		super.damage = 13;
		super.reload = 3;
		super.speed = 0.3;
		super.clipSize = 13;
		super.punch = 5;
		super.recoil = 0.4;
		super.type = WeaponType.PISTOL;
		super.mat = Material.STONE_AXE;
		super.sound = Sound.SKELETON_HURT;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
