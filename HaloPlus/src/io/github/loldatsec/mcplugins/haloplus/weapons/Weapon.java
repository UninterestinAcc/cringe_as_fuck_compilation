package io.github.loldatsec.mcplugins.haloplus.weapons;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.utils.AimingVector;

/**
 * Planned subclasses (weapons)
 * 
 * Sniper Class:
 * 
 * - AWP (Zoomable) G. Hoe
 * 
 * - M82 (Must zoom) D. Hoe
 *
 *
 * Assault Class:
 * 
 * - AK-47 Iron Shovel
 * 
 * - AK-101 D. Shovel
 * 
 * - HK416 (Zoomable) G. Shovel
 * 
 * - M4 Carbine (Zoomable) Stone Shovel
 *
 * 
 * Pistol Class:
 * 
 * - Glock-17 Stone Axe
 * 
 * - P99 Iron Axe
 * 
 * - Desert Eagle D. Axe
 * 
 * - USP G. Axe
 * 
 * 
 * Shotgun Class:
 * 
 * - Spas 12 Iron Pick
 * 
 * - MAUL D. Pick
 */
public abstract class Weapon {

	/**
	 * DataHolding: Bullet Damage
	 */
	public EnumWeapon weaponEnum;
	/**
	 * DataHolding: Bullet Damage
	 */
	public int cost;
	/**
	 * DataHolding: Bullet Damage
	 */
	public int damage;
	/**
	 * DataHolding: Reload Speed
	 */
	public double reload;
	/**
	 * DataHolding: Time between shots.
	 */
	public double speed;
	/**
	 * DataHolding: Amount of bullets in clip
	 */
	public int clipSize;
	/**
	 * DataHolding: Impact knockback.
	 */
	public double punch;
	/**
	 * DataHolding: Shooter knockback.
	 */
	public double recoil;
	/**
	 * DataHolding: Type of weapon
	 */
	public WeaponType type;
	/**
	 * DataHolding: Type of material
	 */
	public Material mat;
	/**
	 * DataHolding: Sound to play on shoot
	 */
	public Sound sound;

	/**
	 * Register this weapon with HaloPlus.weapons
	 */
	public void register() {
		HaloPlus.weapons.put(weaponEnum, this);
	}

	/**
	 * Stuff to do on shoot
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean onShoot(Player p) {
		if (p.getItemInHand() instanceof ItemStack) {
			if (p.getItemInHand().getType() == mat) {
				int rep = type == WeaponType.SHOTGUN ? 10 : 1;
				for (int r = 0; r < rep; r++) {
					Vector v;
					if (type == WeaponType.SHOTGUN) {
						Location manip = p.getEyeLocation();
						manip.setYaw((float) (manip.getYaw() + (Math.random() - 0.5) * 3));
						manip.setPitch((float) (manip.getPitch() + (Math.random() - 0.5) * 3));
						v = new AimingVector(manip).toVector();
					} else {
						v = new AimingVector(p.getEyeLocation()).toVector();
					}
					Projectile bullet;
					if (type == WeaponType.SNIPER_RIFLE) {
						bullet = (Projectile) p.shootArrow();
						((Arrow) bullet).setCritical(true);
					} else {
						bullet = (Projectile) p.throwSnowball();
					}
					bullet.setShooter(p);
					bullet.setCustomName(weaponEnum.toString());
					bullet.setVelocity(v.multiply(punch));
					/**
					 * Sound.BURP (LOUD, POWERFUL)
					 * 
					 * Sound.SKELETON_DEATH (Subtle, pistol)
					 * 
					 * Sound.SKELETON_HURT (Weak, pistol, shatter)
					 * 
					 * Sound.DRINK (Loud, Powerful, sniper)
					 * 
					 * Sound.ANVIL_USE (Soft, machinegun)
					 * 
					 * Sound.WITHER_HIT (Reload)
					 * 
					 * Sound.WITHER_SHOOT (Loud Machine gun shot)
					 * 
					 */
					p.getWorld().playSound(p.getEyeLocation(), sound, 4, 32);
				}
			}
		}
		return true;
	}

	/**
	 * Gets the weapon in item
	 * 
	 * Auto-Integrated, no need to override
	 */
	public ItemStack getItem() {
		ItemStack i = new ItemStack(mat);
		ItemMeta m = i.getItemMeta();
		m.setLore(Arrays.asList(new String[] { "\u00a7a\u00a7lWeapon Type: " + type.toString().replace("_", " ").toUpperCase(), "", "\u00a7e\u00a7lClip Size: " + clipSize, "\u00a7e\u00a7lCost: " + cost, "",
				"\u00a7c\u00a7lDamage: " + damage, "\u00a7c\u00a7lKnockback: " + punch, "\u00a7c\u00a7lRecoil: " + recoil, "", "\u00a7f\u00a7lReload Speed: " + reload, "\u00a7f\u00a7lRounds per second: " + (1 / speed) }));
		m.setDisplayName("\u00a7f\u00a7l" + this.getClass().getSimpleName().toString().replace("_", " "));
		i.setItemMeta(m);
		return i;
	}

	/**
	 * Override if no scope is wanted
	 */
	public void onZoom(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 2));
		p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
		p.updateInventory();
	}

	/**
	 * Override if no scope is wanted
	 */
	public void onUnZoom(Player p) {
		p.removePotionEffect(PotionEffectType.SLOW);
		if (p.hasPermission("haloplus.kit.donator")) {
			p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		} else {
			p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
		}
		p.updateInventory();
	}
}
