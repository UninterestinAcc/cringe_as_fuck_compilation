package io.github.loldatsec.mcplugs.creativesecurity;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;

public class CreativeSecurity extends JavaPlugin implements Listener {

	public static final ItemStack AIR = new ItemStack(Material.AIR, 1);

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					for (int slot = 0; slot <= 39; slot++) {
						ItemStack i = p.getInventory().getItem(slot);
						try {
							if (i instanceof ItemStack && i.getType() != Material.AIR) {
								// NMS NBT modifiers
								net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(i);
								if (stack != null && stack.hasTag()) {
									NBTTagCompound tag = stack.getTag();
									if (tag.hasKey("AttributeModifiers")) {
										// Remove Attributes
										tag.remove("AttributeModifiers");
										stack.setTag(tag);
										i = CraftItemStack.asCraftMirror(stack);
									} else if (tag.hasKey("www.wurst-client.tk")) {
										// Remove Wurst Crashchest
										i = AIR;
									}
								}
								// Enchantment Security
								for (Entry<Enchantment, Integer> e : i.getEnchantments().entrySet()) {
									if (e.getValue() > 10) {
										i.removeEnchantment(e.getKey());
									}
								}
								// Potions & cmd blocks & tnt carts
								if (i.getType() == Material.POTION || (i.getType() == Material.COMMAND && !p.hasPermission("creativesecurity.commandblock"))) {
									i = AIR;
								} else if (i.getType() == Material.COMMAND && !p.hasPermission("creativesecurity.commandblock")) {
									i = AIR;
								} else if (i.getType() == Material.EXPLOSIVE_MINECART) {
									i = AIR;
									for (Minecart mc : p.getWorld().getEntitiesByClass(Minecart.class)) {
										if (mc.getLocation().distance(p.getLocation()) <= 15) {
											mc.remove();
										}
									}
								} else if (i.getType() == Material.ENDER_PORTAL || i.getType() == Material.ENDER_PORTAL_FRAME) {
									i = AIR;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (p.getInventory().getItem(slot) instanceof ItemStack && !i.equals(p.getInventory().getItem(slot))) {
							p.getInventory().setItem(slot, i);
						}
					}
				}
			}
		}, 0, 20);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("creativesecurity.cleaninventory")) {
			String pn = sender.getName();
			if (args.length >= 1) {
				pn = String.join(" ", args);
			}
			Player target = Bukkit.getPlayer(pn);
			if (!(target instanceof Player)) {
				@SuppressWarnings("deprecation")
				OfflinePlayer player = Bukkit.getOfflinePlayer(pn);
				MinecraftServer nms = MinecraftServer.getServer();
				GameProfile gp = new GameProfile(player.getUniqueId(), player.getName());
				EntityPlayer entity = new EntityPlayer(nms, nms.getWorldServer(0), gp, new PlayerInteractManager(nms.getWorldServer(0)));
				target = entity == null ? null : entity.getBukkitEntity();
				if (target != null) {
					target.loadData();
				}
			}
			target.getInventory().clear();
			target.getInventory().setArmorContents(new ItemStack[] { AIR, AIR, AIR, AIR });
			target.saveData();
			sender.sendMessage("\u00a7cInventory wiped for " + target.getName() + ". No hard feelings huh?");
		} else {
			sender.sendMessage("\u00a7cNo Permissions.");
		}
		return true;
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if (e.getTo().getBlock().getType() == Material.ENDER_PORTAL) {
			e.getTo().getBlock().setType(Material.AIR);
		}
	}
}
