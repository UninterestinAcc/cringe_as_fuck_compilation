package io.github.loldatsec.mcplugins.haloplus.grenades;

import org.bukkit.Material;
import org.bukkit.Sound;

public class Frag_Grenade extends Grenade {

	public Frag_Grenade() {
		super.cost = 300;
		super.damage = 17;
		super.dropoff = 3.2;
		super.type = GrenadeType.EXPLOSIVE;
		super.grenadeEnum = EnumGrenade.Frag_Grenade;
		super.mat = Material.SULPHUR;
		super.explode = Sound.EXPLODE;
		super.register();
	}
}
