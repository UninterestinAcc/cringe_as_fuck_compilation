package io.github.loldatsec.mcplugins.haloplus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.game.Game;
import io.github.loldatsec.mcplugins.haloplus.game.GameManager;
import io.github.loldatsec.mcplugins.haloplus.game.GameTeam;
import io.github.loldatsec.mcplugins.haloplus.game.GameTeamEnum;
import io.github.loldatsec.mcplugins.haloplus.grenades.EnumGrenade;
import io.github.loldatsec.mcplugins.haloplus.weapons.EnumWeapon;
import io.github.loldatsec.mcplugins.haloplus.weapons.WeaponType;
import net.md_5.bungee.api.ChatColor;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class GeneralListener implements Listener {

	@EventHandler
	public void eat(PlayerItemConsumeEvent e) {
		e.setCancelled(true);
		HaloPlus.shoot.s.scheduleSyncDelayedTask(HaloPlus.shoot.h, new Runnable() {

			@Override
			public void run() {
				for (PotionEffect pe : e.getPlayer().getActivePotionEffects()) {
					e.getPlayer().removePotionEffect(pe.getType());
				}
			}
		}, 1);
	}

	@EventHandler
	public void hungerReplacement(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void healthChange(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void pickupItem(PlayerPickupItemEvent e) {
		e.setCancelled(true);
		ItemStack i = e.getItem().getItemStack();
		PlayerInventory inv = e.getPlayer().getInventory();
		if (EnumWeapon.toWeaponEnum(i.getType()) != null) {
			WeaponType pt = EnumWeapon.toType(EnumWeapon.toWeaponEnum(i.getType()));
			ItemStack c = inv.getItem(WeaponType.toSlot(pt));
			if (c == null) {
				inv.setItem(WeaponType.toSlot(pt), i);
				e.getItem().remove();
			}
		} else if (EnumGrenade.toGrenadeEnum(i.getType()) != null) {
			ItemStack c = inv.getItem(2);
			if (c == null) {
				inv.setItem(2, i);
				e.getItem().remove();
			}
		}
	}

	@EventHandler
	public void movement(PlayerMoveEvent e) {
		if (e.getTo().getBlock().getType() == Material.ENDER_PORTAL) {
			GameManager.joinGame(e.getPlayer());
		}
	}

	public void playerLoginLogoutEvent(Player p) {
		p.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());
		p.getInventory().clear();
		ItemStack air = new ItemStack(Material.AIR);
		p.getInventory().setHelmet(air);
		p.getInventory().setChestplate(air);
		p.getInventory().setLeggings(air);
		p.getInventory().setBoots(air);
		p.setGameMode(GameMode.ADVENTURE);
		p.setHealth(20);
		for (PotionEffect pot : p.getActivePotionEffects()) {
			p.removePotionEffect(pot.getType());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void login(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		playerLoginLogoutEvent(p);
		e.setJoinMessage(null);
		p.setTexturePack("http://www.reborncraft.biz/downloads/RCHalo.zip");
		// GameManagerDisplayInterpreter.showInventory(p);
	}

	@EventHandler
	public void logout(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		playerLoginLogoutEvent(p);
		e.setQuitMessage(null);
	}

	@EventHandler(ignoreCancelled = true)
	public void asyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		Player p = e.getPlayer();
		String level = "\u00a7a" + HaloPlus.getLevel(p) + " ";
		e.setFormat(level + ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(e.getPlayer()).getPrefix()) + GameTeamEnum.getColour(GameManager.getGameTeam(e.getPlayer())) + e.getPlayer().getDisplayName()
			+ ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(e.getPlayer()).getSuffix() + ": " + e.getMessage().replace("&", "& ").replace("%", "%%")));
		String bcf = "\u00a78[\u00a76" + p.getWorld().getName() + "\u00a78] " + e.getFormat();
		Bukkit.getConsoleSender().sendMessage(bcf);
		if (e.getMessage().startsWith("!")) {
			for (Player p1 : Bukkit.getOnlinePlayers()) {
				p1.sendMessage("\u00a7e\u00a7lGLOBAL " + e.getFormat());
			}
		} else if (e.getMessage().startsWith("@")) {
			if (GameManager.getGame(p) instanceof Game && GameManager.getGame(p).getTeam(p) instanceof GameTeam) {
				for (Player p1 : GameManager.getGame(p).getTeam(p).members.keySet()) {
					p1.sendMessage("\u00a7d\u00a7lTEAM " + e.getFormat());
				}
			} else {
				p.sendMessage("\u00a7d\u00a7lTEAM " + e.getFormat());
			}
		} else {
			for (Player p1 : e.getPlayer().getWorld().getPlayers()) {
				p1.sendMessage(e.getFormat());
			}
			if (!p.getWorld().getName().equalsIgnoreCase("Lobby")) {
				for (Player p2 : Bukkit.getWorld("Lobby").getPlayers()) {
					p2.sendMessage(bcf);
				}
			}
		}
	}

	@EventHandler
	public void gameInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getName().equalsIgnoreCase("\u00a70\u00a7lRunning Halo Games")) {
			e.getPlayer().sendMessage("\u00a75Game> \u00a7dYou can re-open the selection inventory by doing /game");
		}
	}

	@EventHandler
	public void gameSelectEvent(InventoryClickEvent e) {
		if (e.getInventory().getName().equalsIgnoreCase("\u00a70\u00a7lRunning Halo Games")) {
			Player p = (Player) e.getWhoClicked();
			ItemStack i = e.getCurrentItem();
			if (i instanceof ItemStack && i.getType() != Material.AIR) {
				ItemMeta im = i.getItemMeta();
				if (im instanceof ItemMeta && im.getDisplayName() != null) {
					String world = im.getDisplayName().replaceAll("\u00a7[0-9a-r]", "");
					if (world.equalsIgnoreCase("Lobby")) {
						p.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());
					} else {
						GameManager.joinGame(p, world);
					}
				}
			}
		}
	}
}
