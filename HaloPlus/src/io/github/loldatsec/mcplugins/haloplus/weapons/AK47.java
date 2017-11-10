package io.github.loldatsec.mcplugins.haloplus.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AK47 extends Weapon {

	public AK47() {
		super.weaponEnum = EnumWeapon.AK47;
		super.cost = 2200;
		super.damage = 9;
		super.reload = 4;
		super.speed = 0.1;
		super.clipSize = 30;
		super.punch = 5;
		super.recoil = 1.4;
		super.type = WeaponType.ASSAULT_RIFLE;
		super.mat = Material.IRON_SPADE;
		super.sound = Sound.ANVIL_USE;
		super.register();
	}

	public void onZoom(Player p) {
	}
}
