package io.github.loldatsec.mcplugs.halocore;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Capture {
	public static void capture(PlayerInteractEvent e) {
		Sign sign = (Sign) e.getClickedBlock().getState();
		if (sign.getLine(0).equalsIgnoreCase("§5[Capture]")
				&& e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			if (sign.getLine(2)
					.equalsIgnoreCase("§1" + e.getPlayer().getName())) {
				e.getPlayer().sendMessage(
						"§cYou already captured this station.");
			} else {
				if (e.getPlayer().hasPotionEffect(
						PotionEffectType.DAMAGE_RESISTANCE)) {
					e.getPlayer().sendMessage(
							"§cYou must debuff before capturing this station.");
				} else {
					sign.setLine(2, "§1" + e.getPlayer().getName());
					e.getPlayer()
							.addPotionEffect(
									new PotionEffect(
											PotionEffectType.DAMAGE_RESISTANCE,
											600, 1));
					e.getPlayer().incrementStatistic(Statistic.CRAFT_ITEM,
							Material.DIAMOND_SPADE);
					e.getPlayer().incrementStatistic(Statistic.PLAYER_KILLS,
							(int) (Math.random() * 9 + 1));
					Title.broadcast(
							e.getPlayer().getWorld().getPlayers(),
							"",
							"§2"
									+ e.getPlayer().getDisplayName()
									+ "§e["
									+ e.getPlayer().getStatistic(
											Statistic.CRAFT_ITEM,
											Material.DIAMOND_SPADE)
									+ "§e]§a captured §2" + sign.getLine(1),
							30, 60, 10);
				}
			}
		}
		sign.update();
	}

	public static void placeSign(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[Capture]")) {
			e.setLine(0, "§5[Capture]");
			e.getPlayer().sendMessage("§aStation placed.");
		}
	}
}