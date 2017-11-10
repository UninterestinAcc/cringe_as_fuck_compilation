package io.github.loldatsec.mcplugs.offlineinventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OfflineInventory {

	private Player viewer = null;
	private Player target = null;
	public final String inventoryPrefix = "\u00a7l";
	public final String inventorySuffix = "'s ";
	private boolean isOnline = false;
	public static String version = null;

	private Class<? extends Object> getNMSClass(String nmsClassString) throws ClassNotFoundException {
		if (version == null) {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		}
		String name = "net.minecraft.server." + version + nmsClassString;
		Class<? extends Object> nmsClass = Class.forName(name);
		return nmsClass;
	}

	private Class<? extends Object> getGameProfileClass() {
		try {
			return Class.forName("com.mojang.authlib.GameProfile");
		} catch (ClassNotFoundException e) {
			try {
				return Class.forName("net.minecraft.utils.com.mojang.authlib.GameProfile");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}

	@SuppressWarnings ("deprecation")
	public OfflineInventory(Player sender, String pn) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassCastException {
		viewer = sender;
		target = Bukkit.getPlayerExact(pn);
		if (target == null) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(pn);
			Class<? extends Object> gameProfileClass = getGameProfileClass();
			Class<? extends Object> msClass = getNMSClass("MinecraftServer");
			Class<? extends Object> wsClass = getNMSClass("WorldServer");
			Class<? extends Object> wClass = getNMSClass("World");
			Class<? extends Object> pimClass = getNMSClass("PlayerInteractManager");
			Class<? extends Object> epClass = getNMSClass("EntityPlayer");
			Object gameProfile = gameProfileClass.getConstructor(UUID.class, String.class).newInstance(player.getUniqueId(), player.getName());
			Object minecraftServer = msClass.getMethod("getServer").invoke(null);
			Object worldServer = msClass.getMethod("getWorldServer", Integer.TYPE).invoke(minecraftServer, 0);
			Object playerInteractManager = pimClass.getConstructor(wClass).newInstance(worldServer);
			Object entityPlayer = epClass.getConstructor(msClass, wsClass, gameProfileClass, pimClass).newInstance(minecraftServer, worldServer, gameProfile, playerInteractManager);
			target = entityPlayer == null ? null : (Player) epClass.getMethod("getBukkitEntity").invoke(entityPlayer);
			if (target != null) {
				target.loadData();
			}
		} else {
			isOnline = true;
		}
	}


	public Player getViewer() {
		return viewer;
	}

	public Player getTarget() {
		return target;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void save() {
		target.saveData();
		if (isOnline) {
			target.updateInventory();
		}
	}

	public Inventory getEnderChest() {
		Inventory i = Bukkit.createInventory(viewer, 27, inventoryPrefix + target.getName() + inventorySuffix + "Enderchest");
		i.setContents(target.getEnderChest().getContents());
		viewer.openInventory(i);
		return i;
	}

	public void setEnderChest(Inventory i) {
		target.getEnderChest().setContents(i.getContents());
		save();
	}

	public Inventory getInventory() {
		PlayerInventory pi = target.getInventory();
		Inventory i = Bukkit.createInventory(viewer, 45, inventoryPrefix + target.getName() + inventorySuffix + "Inventory");
		i.setContents(pi.getContents());
		i.setItem(36, pi.getHelmet());
		i.setItem(37, pi.getChestplate());
		i.setItem(38, pi.getLeggings());
		i.setItem(39, pi.getBoots());
		if (OfflineInventoriesMain.is19()) {
			i.setItem(40, pi.getItem(40));
		}
		ItemStack infoStack = new ItemStack(Material.REDSTONE_BLOCK, 0);
		ItemMeta infoMeta = infoStack.getItemMeta();
		infoMeta.setDisplayName(ChatColor.GOLD + target.getName() + "'s Details");
		infoMeta.setLore(Arrays.asList(new String[]{
				"\u00a7cHealth: " + (int) (target.getHealthScale() + 0.5),
				"\u00a76FireTicks: " + (target.getFireTicks() < 0 ? 0 : target.getFireTicks()),
				"\u00a7eHunger: " + target.getFoodLevel(),
				"\u00a7aXP Level: " + target.getLevel()
		}));
		infoStack.setItemMeta(infoMeta);
		if (!OfflineInventoriesMain.is19()) {
			i.setItem(40, infoStack);
		}
		for (int c = 41; c <= 44; c++) {
			i.setItem(c, infoStack);
		}
		viewer.openInventory(i);
		return i;
	}

	public void setInventory(Inventory i) {
		List<ItemStack> inv = Arrays.asList(i.getContents());
		PlayerInventory ni = target.getInventory();
		for (int s = 0; s <= 35; s++) {
			ni.setItem(s, inv.get(s));
		}
		ni.setHelmet(inv.get(36));
		ni.setChestplate(inv.get(37));
		ni.setLeggings(inv.get(38));
		ni.setBoots(inv.get(39));
		if (OfflineInventoriesMain.is19()) {
			ni.setItem(40, inv.get(40));
		}
		save();
	}

	public boolean closeInventory(InventoryCloseEvent e) {
		String title = e.getInventory().getTitle();
		if (title.startsWith(inventoryPrefix + target.getName() + inventorySuffix)) {
			if (title.endsWith("Enderchest")) {
				setEnderChest(e.getInventory());
			} else if (title.endsWith("Inventory")) {
				setInventory(e.getInventory());
			} else {
				return false;
			}
			return true;
		}
		return false;
	}
}
