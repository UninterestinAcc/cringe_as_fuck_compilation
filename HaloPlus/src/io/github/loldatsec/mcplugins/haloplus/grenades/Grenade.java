package io.github.loldatsec.mcplugins.haloplus.grenades;

import java.util.Arrays;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.game.GameManager;
import io.github.loldatsec.mcplugins.haloplus.utils.AimingVector;

public abstract class Grenade {

	/**
	 * DataHolding: Grenade Cost
	 */
	public int cost;
	/**
	 * DataHolding: Explosion Damage
	 */
	public int damage;
	/**
	 * DataHolding: Explosion Damage Dropoff Rate
	 */
	public double dropoff;
	/**
	 * DataHolding: Type of grenade
	 */
	public GrenadeType type;
	/**
	 * DataHolding: Name of grenade
	 */
	public EnumGrenade grenadeEnum;
	/**
	 * DataHolding: Type of material
	 */
	public Material mat;
	/**
	 * DataHolding: Sound to play on explode
	 */
	public Sound explode;

	/**
	 * Register this weapon with HaloPlus.weapons
	 */
	public void register() {
		HaloPlus.grenades.put(grenadeEnum, this);
	}

	/**
	 * Gets the weapon in item
	 * 
	 * Auto-Integrated, no need to override
	 */
	public ItemStack getItem() {
		ItemStack i = new ItemStack(mat);
		ItemMeta m = i.getItemMeta();
		m.setLore(
			Arrays.asList(new String[] { "\u00a7a\u00a7lGrenade Type: " + type.toString().toUpperCase(), "", "\u00a7e\u00a7lCost: " + cost, "", "\u00a7c\u00a7lDamage: " + damage, "\u00a7c\u00a7lDamage Dropoff: " + dropoff + "/block" }));
		m.setDisplayName("\u00a7f\u00a7l" + this.getClass().getSimpleName().toString().replace("_", " "));
		i.setItemMeta(m);
		return i;
	}

	public static void onThrow(Player p) {
		ItemStack t = p.getInventory().getItemInHand();
		if (t != null && EnumGrenade.isGrenade(t.getType()) && !GameManager.immoveable.keySet().contains(p)) {
			p.getInventory().setItem(2, new ItemStack(Material.AIR));
			p.updateInventory();
			Item g = p.getWorld().dropItem(p.getEyeLocation(), t);
			g.setCustomName(p.getName());
			g.setItemStack(t);
			g.setVelocity(new AimingVector(p.getEyeLocation()).setPower(2).toVector());
		}
	}

	public static void onExplode(Location l, EnumGrenade gren, Player thrower) {
		if (HaloPlus.grenades.get(gren).type == GrenadeType.EXPLOSIVE) {
			l.getWorld().playEffect(l, Effect.EXPLOSION_HUGE, 0);
		}
		Grenade g = EnumGrenade.toGrenade(gren);
		l.getWorld().playSound(l, g.explode, 4, 32);
		for (Player p : l.getWorld().getPlayers()) {
			double dmg = g.damage - (p.getLocation().distance(l) * g.dropoff);
			if (dmg > 0) {
				if (HaloPlus.grenades.get(gren).type == GrenadeType.FIREBOMB) {
					p.setFireTicks((int) (dmg * 20 * 3));
					p.getWorld().playEffect(l, Effect.MOBSPAWNER_FLAMES, 0, 5);
					p.getWorld().playEffect(p.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 0, 2);
				}
				HaloPlus.shoot.damagePlayerGrenade(p, dmg, thrower);
			}
		}
	}
}
