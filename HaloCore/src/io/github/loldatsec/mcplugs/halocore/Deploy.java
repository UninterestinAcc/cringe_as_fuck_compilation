package io.github.loldatsec.mcplugs.halocore;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Deploy {
	public static void deploy(Player player) {
		Location spawn = player.getWorld().getSpawnLocation();
		World world = spawn.getWorld();
		int x = 0;
		int z = 0;
		for (;;) {
			x = (int) (spawn.getX() + (Math.random() * 148 - 74));
			z = (int) (spawn.getZ() + (Math.random() * 148 - 74));
			Location tgt = new Location(world, x, spawn.getY(), z);
			if (tgt.distance(spawn) > 10 && tgt.distance(spawn) < 74) {
				boolean active = false;
				for (int y = 100; y < 256; y++) {
					tgt.setY(y);
					if (active) {
						if (tgt.getBlock().getType() == Material.AIR
								&& tgt.add(0, 1, 0).getBlock().getType() == Material.AIR
								&& tgt.add(0, 1, 0).getBlock().getType() == Material.AIR
								&& tgt.add(0, 1, 0).getBlock().getType() == Material.AIR) {
							player.teleport(tgt.subtract(0, 3, 0));
							player.sendMessage("§7[§6Halo§7] §6Deployed you to §e"
									+ world.getName()
									+ ": x"
									+ tgt.getX()
									+ ", y" + tgt.getY() + ", z" + tgt.getZ());
							break;
						}
					} else {
						if (tgt.getBlock().getType() != Material.AIR) {
							active = true;
						}
					}
				}
				break;
			}
		}
	}
}
