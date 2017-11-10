package io.github.loldatsec.mcplugs.halocore;

import java.util.ArrayList;
import java.util.zip.CRC32;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Weapons {
	public boolean getKit(Player player, String kit) {
		if (kit.equalsIgnoreCase("Private")) {
			clearInv(player);
			equipArmor(player);
			player.getInventory().setItem(0, assault());
			player.getInventory().setItem(1, eGrenade(5));
			player.getInventory().setItem(8, boost(1));
			receiveKit(player, kit);
		} else if (kit.equalsIgnoreCase("Knight")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 200
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, sniper());
				player.getInventory().setItem(1, dagger());
				player.getInventory().setItem(8, boost(2));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 200);
			}
		} else if (kit.equalsIgnoreCase("Freak")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 800
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, pistol());
				player.getInventory().setItem(1, dagger());
				player.getInventory().setItem(2, airStrike(1));
				player.getInventory().setItem(8, boost(2));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 800);
			}
		} else if (kit.equalsIgnoreCase("Mutant")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 1500
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, shotgun());
				player.getInventory().setItem(1, dagger());
				player.getInventory().setItem(2, airStrike(2));
				player.getInventory().setItem(8, boost(2));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 1500);
			}
		} else if (kit.equalsIgnoreCase("Grenadier")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 2000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, pistol());
				player.getInventory().setItem(1, dagger());
				player.getInventory().setItem(2, healthPack(1));
				player.getInventory().setItem(3, eGrenade(15));
				player.getInventory().setItem(4, airStrike(5));
				player.getInventory().setItem(8, boost(2));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 2000);
			}
		} else if (kit.equalsIgnoreCase("Titan")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 3000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, shotgun());
				player.getInventory().setItem(1, dagger());
				player.getInventory().setItem(2, pistol());
				player.getInventory().setItem(3, airStrike(5));
				player.getInventory().setItem(8, boost(1));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 3000);
			}
		} else if (kit.equalsIgnoreCase("Elite")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 5000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, shotgun());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(3));
				player.getInventory().setItem(3, pistol());
				player.getInventory().setItem(8, boost(1));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 5000);
			}
		} else if (kit.equalsIgnoreCase("Deathadder")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 10000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, shotgun());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(4));
				player.getInventory().setItem(3, airStrike(5));
				player.getInventory().setItem(8, boost(2));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 10000);
			}
		} else if (kit.equalsIgnoreCase("Militant")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 15000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, shotgun());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(4));
				player.getInventory().setItem(3, pistol());
				player.getInventory().setItem(4, airStrike(8));
				player.getInventory().setItem(8, boost(3));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 15000);
			}
		} else if (kit.equalsIgnoreCase("Skilled")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 25000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, railgun());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(4));
				player.getInventory().setItem(3, eGrenade(8));
				player.getInventory().setItem(8, boost(3));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 25000);
			}
		} else if (kit.equalsIgnoreCase("Sentry")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 40000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, electric());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(2));
				player.getInventory().setItem(3, airStrike(2));
				player.getInventory().setItem(8, boost(3));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 40000);
			}
		} else if (kit.equalsIgnoreCase("Annihilator")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 70000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, annihilator());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(4));
				player.getInventory().setItem(3, airStrike(6));
				player.getInventory().setItem(8, boost(2));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 70000);
			}
		} else if (kit.equalsIgnoreCase("Ultimatum")) {
			if (player.getStatistic(Statistic.PLAYER_KILLS) >= 100000
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, ultimatum());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(5));
				player.getInventory().setItem(3, airStrike(7));
				player.getInventory().setItem(8, boost(3));
				receiveKit(player, kit);
			} else {
				noPermission(player, kit, 100000);
			}
		} else if (kit.equalsIgnoreCase("Tank")) {
			if (player.hasPermission("halocore.kit.donor")
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, tank());
				player.getInventory().setItem(1, saber());
				player.getInventory().setItem(2, healthPack(5));
				player.getInventory().setItem(3, airStrike(2));
				player.getInventory().setItem(4, pGrenade(8));
				player.getInventory().setItem(8, boost(3));
				receiveKit(player, kit);
			} else {
				donorOnly(player, kit);
			}
		} else if (kit.equalsIgnoreCase("Battery")) {
			if (player.hasPermission("halocore.kit.donor")
					|| player.hasPermission("halocore.kit.all")) {
				clearInv(player);
				equipArmor(player);
				player.getInventory().setItem(0, battery());
				player.getInventory().setItem(1, pGrenade(4));
				player.getInventory().setItem(2, boost(2));
				player.getInventory().setItem(3, saber());
				player.getInventory().setItem(4, healthPack(3));
				receiveKit(player, kit);
			} else {
				donorOnly(player, kit);
			}
		} else {
			noKit(player, kit);
		}
		player.updateInventory();
		return true;
	}

	public void receiveKit(Player player, String kit) {
		player.sendMessage("§6Received kit §e" + kit.toLowerCase());
		player.playSound(player.getEyeLocation(), Sound.FIREWORK_LARGE_BLAST2,
				1, 1);
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, 600, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200000,
				0));
	}

	public void noKit(Player player, String kit) {
		player.sendMessage("§cError: §4Kit §c" + kit.toLowerCase()
				+ "§4 does not exist.");
	}

	public void noPermission(Player player, String kit, int reqKills) {
		player.sendMessage("§cError: §4You cant access kit §c"
				+ kit.toLowerCase() + "§4 because you don't have more than §c"
				+ reqKills + "§4 kills.");
	}

	public void donorOnly(Player player, String kit) {
		player.sendMessage("§6Only donators can access §e" + kit.toLowerCase()
				+ "§6, donate at §b" + "http://donate.reborncraft.info");
	}

	public void equipArmor(Player player) {
		String pname = player.getName();
		if (player.hasPermission("halocore.diamondhelmet")) {
			ItemStack item = new ItemStack(Material.DIAMOND_HELMET, 1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§b" + pname + "_Helmet");
			item.setItemMeta(meta);
			player.getInventory().setHelmet(item);
		} else {
			player.getInventory().setHelmet(
					armor(Material.LEATHER_HELMET, pname + "_Helmet"));
		}
		player.getInventory().setChestplate(
				armor(Material.LEATHER_CHESTPLATE, pname + "_Chestplate"));
		player.getInventory().setLeggings(
				armor(Material.LEATHER_LEGGINGS, pname + "_Leggings"));
		player.getInventory().setBoots(
				armor(Material.LEATHER_BOOTS, pname + "_Boots"));
	}

	public void clearInv(Player player) {
		ItemStack air = new ItemStack(Material.AIR, 1);
		player.getInventory().setItem(0, air);
		player.getInventory().setItem(1, air);
		player.getInventory().setItem(2, air);
		player.getInventory().setItem(3, air);
		player.getInventory().setItem(4, air);
		player.getInventory().setItem(5, air);
		player.getInventory().setItem(6, air);
		player.getInventory().setItem(7, air);
		player.getInventory().setItem(8, air);
		player.getInventory().setItem(39, air);
		player.getInventory().setItem(38, air);
		player.getInventory().setItem(37, air);
		player.getInventory().setItem(36, air);
		player.updateInventory();
		player.setExp(1);
	}

	public final ArrayList<String> leftLore() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Click left to fire.");
		lore.add("§7Click right to zoom.");
		return lore;
	}

	public final ArrayList<String> leftNZLore() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Click left to fire.");
		return lore;
	}

	public final String aAMultiTitle = "§7 《§8⊕◇ L§7》";
	public final String leftTitle = "§7 《§8○ L | R ⊕§7》";
	public final String leftMultiTitle = "§7 《§8◇ L | R ⊕§7》";
	public final String leftMultiNZTitle = "§7 《§8◇ L§7》";

	public final ArrayList<String> rightLore() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Hold right to rapidfire.");
		return lore;
	}

	public final String rightTitle = "§7 《§8R ●§7》";
	public final String rightMultiTitle = "§7 《§8R ◆§7》";

	public final ArrayList<String> explosiveLore() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Click left to hurl.");
		return lore;
	}

	public final String explosiveTitle = "§7 《§8L ☢§7》";

	public String ammoTitle(int amt) {
		return "§7 《§8" + amt + "§7》";
	}

	// Throwables
	public ItemStack eGrenade(int amt) {
		ItemStack item = new ItemStack(Material.IRON_BLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = explosiveLore();
		lore.add("§aThe classic explosive grenade.");
		meta.setLore(lore);
		meta.setDisplayName("§aExplosive Grenade" + explosiveTitle);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
		item.setAmount(amt);
		return item;
	}

	public ItemStack airStrike(int amt) {
		ItemStack item = new ItemStack(Material.BEACON, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = explosiveLore();
		lore.add("§aThe airstrike caller.");
		meta.setLore(lore);
		meta.setDisplayName("§aAirstrike" + explosiveTitle);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 7);
		item.setAmount(amt);
		return item;
	}

	public ItemStack healthPack(int amt) {
		ItemStack item = new ItemStack(Material.CAKE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§cPlace to use.");
		lore.add("§cHeals everyone up to 20hp in a 4 block radius.");
		meta.setLore(lore);
		meta.setDisplayName("§cHealth pack§7 《§8R ❤§7》");
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
		item.setAmount(amt);
		return item;
	}

	public ItemStack pGrenade(int amt) {
		ItemStack item = new ItemStack(Material.DIAMOND_BLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = explosiveLore();
		lore.add("§bThe plasma grenade.");
		meta.setLore(lore);
		meta.setDisplayName("§bPlasma Grenade" + explosiveTitle);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
		item.setAmount(amt);
		return item;
	}

	public ItemStack boost(int amt) {
		ItemStack item = new ItemStack(Material.ENDER_PEARL, amt);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Right click for a speed boost.");
		meta.setLore(lore);
		meta.setDisplayName("§aBoost§7 《§8R ⭆§7》");
		item.setItemMeta(meta);
		return item;
	}

	// Projectile-firing weapons
	public final ItemStack assault() {
		ItemStack item = new ItemStack(Material.IRON_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = rightLore();
		lore.add("§aThe classic assault rifle.");
		meta.setLore(lore);
		meta.setDisplayName("§aAssault Rifle" + rightTitle + ammoTitle(120));
		item.setItemMeta(meta);
		return item;
	}

	public final ItemStack sniper() {
		ItemStack item = new ItemStack(Material.IRON_HOE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftLore();
		lore.add("§aThe classic sniper rifle.");
		lore.add("§dZoom for automatic aiming.");
		meta.setLore(lore);
		meta.setDisplayName("§aSniper Rifle" + leftTitle + ammoTitle(12));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		return item;
	}

	public final ItemStack pistol() {
		ItemStack item = new ItemStack(Material.STONE_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftLore();
		lore.add("§aThe classic pistol.");
		lore.add("§dZoom for automatic aiming.");
		meta.setLore(lore);
		meta.setDisplayName("§aPistol" + leftTitle + ammoTitle(40));
		item.setItemMeta(meta);
		return item;
	}

	public final ItemStack shotgun() {
		ItemStack item = new ItemStack(Material.STONE_HOE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftNZLore();
		lore.add("§aThe classic shotgun.");
		meta.setLore(lore);
		meta.setDisplayName("§aShotgun" + leftMultiNZTitle + ammoTitle(60));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
		return item;
	}

	public final ItemStack rocketLauncher() {
		ItemStack item = new ItemStack(Material.STONE_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftNZLore();
		lore.add("§aThe classic rocket launcher.");
		meta.setLore(lore);
		meta.setDisplayName("§aRocket Launcher" + leftTitle + ammoTitle(8));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
		return item;
	}

	public final ItemStack railgun() {
		ItemStack item = new ItemStack(Material.GOLD_HOE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftLore();
		lore.add("§eA modern railgun.");
		lore.add("§dZoom for automatic aiming.");
		meta.setLore(lore);
		meta.setDisplayName("§eRailgun" + leftMultiTitle + ammoTitle(80));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
		return item;
	}

	public final ItemStack electric() {
		ItemStack item = new ItemStack(Material.GOLD_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftNZLore();
		lore.add("§dA weapon that gives electric shocks.");
		meta.setLore(lore);
		meta.setDisplayName("§dTaser" + leftMultiNZTitle + ammoTitle(25));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
		return item;
	}

	public final ItemStack annihilator() {
		ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftLore();
		lore.add("§6The second most powerful weapon.");
		lore.add("§dAutomatic percision-aiming.");
		meta.setLore(lore);
		meta.setDisplayName("§6AA Shotgun" + aAMultiTitle + ammoTitle(40));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
		return item;
	}

	public final ItemStack ultimatum() {
		ItemStack item = new ItemStack(Material.GOLD_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftLore();
		lore.add("§6The most powerful weapon.");
		lore.add("§dAutomatic percision-aiming.");
		meta.setLore(lore);
		meta.setDisplayName("§6AA Double Shotgun" + aAMultiTitle
				+ ammoTitle(60));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
		return item;
	}

	// Donator gear
	public final ItemStack tank() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = rightLore();
		lore.add("§bA heavy futuristic assault weapon.");
		meta.setLore(lore);
		meta.setDisplayName("§bTank" + rightTitle + ammoTitle(600));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		return item;
	}

	public final ItemStack battery() {
		ItemStack item = new ItemStack(Material.DIAMOND_HOE, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = leftLore();
		lore.add("§bA heavy futuristic sniper gun.");
		lore.add("§dZoom for automatic aiming.");
		meta.setLore(lore);
		meta.setDisplayName("§bBattery" + leftTitle + ammoTitle(120));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
		return item;
	}

	public final ItemStack dagger() {
		ItemStack item = new ItemStack(Material.STICK, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§aSomething useful for stabbing people.");
		lore.add("§7+2 Damage");
		meta.setLore(lore);
		meta.setDisplayName("§aDagger");
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		return item;
	}

	public final ItemStack saber() {
		ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§6Something useful for slicing people.");
		lore.add("§61.5 seconds of fire.");
		lore.add("§7+4 Damage");
		meta.setLore(lore);
		meta.setDisplayName("§6Saber");
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
		item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
		return item;
	}

	public final ItemStack armor(Material armorPiece, String pname) {
		ItemStack item = new ItemStack(armorPiece, 1);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(digest(pname));
		meta.setDisplayName("§2" + pname);
		item.setItemMeta(meta);
		return item;
	}

	public final ItemStack redWool(String pname, String evt) {
		ItemStack item = new ItemStack(Material.WOOL, 1);
		item.setDurability((short) 14);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§4" + evt + " " + pname);
		item.setItemMeta(meta);
		return item;
	}

	// Digest string into colour
	public Color digest(String str) {
		if (str != null) {
			CRC32 crc = new CRC32();
			crc.update(str.getBytes());
			int digest = (int) (crc.getValue() % Integer.MAX_VALUE);
			return Color.fromBGR(digest % 2097152);
		}
		return Color.WHITE;
	}
}
