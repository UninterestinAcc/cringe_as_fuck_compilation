package io.github.loldatsec.mcplugins.haloplus.grenades;

import org.bukkit.Material;
import org.bukkit.Sound;

public class Molotov_Cocktail extends Grenade {

	public Molotov_Cocktail() {
		super.cost = 500;
		super.damage = 10;
		super.dropoff = 2.7;
		super.type = GrenadeType.FIREBOMB;
		super.grenadeEnum = EnumGrenade.Molotov_Cocktail;
		super.mat = Material.GLOWSTONE_DUST;
		super.explode = Sound.FIRE_IGNITE;
		super.register();
	}
}
