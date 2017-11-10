package co.reborncraft.combateverywhere;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CombatEverywhere extends JavaPlugin implements Listener {
	public Map<Player, Long> combat = new HashMap<>();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			combat.keySet().stream()
					.collect(Collectors.toList())
					.forEach(p -> {
						if (isInCombat(p)) {
							if (p.isOnline()) {
								((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + getConfiguredMessage(Message.COMBAT_ACTIONBARINFO, null, combat.get(p)) + "\"}"), (byte) 2));
							}
						} else {
							if (p.isOnline()) {
								p.sendMessage(getConfiguredMessage(Message.END_TIMEOUT, null, 0));
							}
							combat.remove(p);
						}
					});
		}, 100, 20);
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onCombat(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager() instanceof Projectile ? (Entity) ((Projectile) e.getDamager()).getShooter() : e.getDamager();
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player && e.getDamager() != e.getEntity()) {
			if (isInCombat((Player) damager) && isInCombat(((Player) e.getEntity()))) {
				e.setCancelled(false);
			}
			if (!e.isCancelled()) {
				placeIntoCombat(((Player) e.getDamager()));
				placeIntoCombat(((Player) e.getEntity()));
			}
		}
	}

	private void placeIntoCombat(Player p) {
		if (!isInCombat(p)) {
			p.sendMessage(getConfiguredMessage(Message.COMBAT_INITIATE, null, System.currentTimeMillis()));
		}
		combat.put(p, System.currentTimeMillis());
	}

	private boolean isInCombat(Player p) {
		return combat.containsKey(p) && combat.get(p) > System.currentTimeMillis() - (getTimeout() * 1000);
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		Player player = e.getEntity();
		Player killer = player.getKiller();
		if (killer != null) {
			if (combat.containsKey(killer)) {
				combat.remove(killer);
			}
			if (killer.isOnline()) {
				killer.sendMessage(getConfiguredMessage(Message.END_TIMEOUT, player, 0));
			}
		}
		if (combat.containsKey(player)) {
			combat.remove(player);
		}
		if (player.isOnline()) {
			player.sendMessage(getConfiguredMessage(Message.END_TIMEOUT, killer, 0));
		}
	}

	@EventHandler
	public void teleport(PlayerTeleportEvent e) {
		switch (e.getCause()) {
			case CHORUS_FRUIT: {
			}
			case ENDER_PEARL: {
				return;
			}
			default: {
				Player p = e.getPlayer();
				if (isInCombat(p)) {
					if (e.getTo().getWorld() != e.getFrom().getWorld() || e.getTo().distanceSquared(e.getFrom()) > 64) {
						e.setCancelled(true);
						p.sendMessage(getConfiguredMessage(Message.DENY_TELEPORT, null, combat.get(p)));
					}
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void logout(PlayerQuitEvent e) {
		if (isInCombat(e.getPlayer())) {
			e.setQuitMessage(getConfiguredMessage(Message.END_LOGOUT_BROADCAST, e.getPlayer(), 0));
			e.getPlayer().setHealth(0);
		}
	}

	private String getConfiguredMessage(Message msg, Player p, long startMillis) {
		return getPrefix() + getConfig()
				.getString("messages." + msg.getConfigStr())
				.replaceAll("&([0-9a-fA-Fk-oK-orR])", "\u00a7$1")
				.replaceAll("\\{player}", p != null ? p.getName() : "")
				.replaceAll("\\{time}", (30 - ((System.currentTimeMillis() - startMillis + 1000) / 1000)) + "");
	}

	private String getPrefix() {
		return getConfig().getString("prefix").replaceAll("&([0-9a-fA-Fk-oK-orR])", "\u00a7$1") + " ";
	}

	private int getTimeout() {
		return getConfig().getInt("timeout");
	}

	public enum Message {
		COMBAT_INITIATE("combat.initiate"),
		COMBAT_ACTIONBARINFO("combat.actionbarinfo"),
		DENY_TELEPORT("deny.teleport"),
		END_TIMEOUT("end.timeout"),
		END_LOGOUT_BROADCAST("end.logout-broadcast");

		private final String configStr;

		Message(String configStr) {
			this.configStr = configStr;
		}

		public String getConfigStr() {
			return configStr;
		}
	}
}
