package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class P99 extends Weapon {

	public P99() {
		super.weaponEnum = EnumWeapon.P99;
		super.cost = 300;
		super.damage = 14;
		super.reload = 3;
		super.speed = 0.3;
		super.clipSize = 17;
		super.punch = 5;
		super.recoil = 0.4;
		super.type = WeaponType.PISTOL;
		super.mat = Material.IRON_AXE;
		super.sound = Sound.SKELETON_DEATH;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
