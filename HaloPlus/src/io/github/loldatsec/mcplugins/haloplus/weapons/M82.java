package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class M82 extends Weapon {

	public M82() {
		super.weaponEnum = EnumWeapon.M82;
		super.cost = 5000;
		super.damage = 27;
		super.reload = 4;
		super.speed = 1.2;
		super.clipSize = 6;
		super.punch = 8;
		super.recoil = 0.6;
		super.type = WeaponType.SNIPER_RIFLE;
		super.mat = Material.DIAMOND_HOE;
		super.sound = Sound.DRINK;
		super.register();
	}

	public boolean onShoot(Player p) {
		if (p.isSneaking()) { return super.onShoot(p); }
		return false;
	}
}
