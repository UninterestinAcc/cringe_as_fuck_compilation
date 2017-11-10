package io.github.loldatsec.mcplugins.haloplus.utils;

import org.bukkit.entity.Player;

import io.github.loldatsec.mcplugins.haloplus.weapons.Weapon;

public class Reload {

	public Player source;
	public Weapon weapon;
	public long timeleft;

	public Reload(Player src, Weapon weap, long timeleft) {
		this.source = src;
		this.weapon = weap;
		this.timeleft = timeleft;
	}

	public Reload timeleftDecrement() {
		timeleft--;
		return this;
	}
}
