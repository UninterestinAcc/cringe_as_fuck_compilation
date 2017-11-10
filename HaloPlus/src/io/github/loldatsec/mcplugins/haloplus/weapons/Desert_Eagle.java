package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Desert_Eagle extends Weapon {

	public Desert_Eagle() {
		super.weaponEnum = EnumWeapon.Desert_Eagle;
		super.cost = 800;
		super.damage = 16;
		super.reload = 2;
		super.speed = 0.3;
		super.clipSize = 22;
		super.punch = 5;
		super.recoil = 0.8;
		super.type = WeaponType.PISTOL;
		super.mat = Material.DIAMOND_AXE;
		super.sound = Sound.SKELETON_DEATH;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
