/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import space.craftin.plugin.worldgen.generator.CIS_AsteroidGen;
import space.craftin.plugin.worldgen.generator.CIS_PlanetGen;
import space.craftin.plugin.worldgen.generator.CIS_VoidGen;

public class CraftInSpaceWorldGenPlugin extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			if (!Bukkit.getWorlds().stream().filter(w -> w.getName().equalsIgnoreCase("Universe")).findAny().isPresent()) {
				Bukkit.createWorld(new WorldCreator("Universe").environment(World.Environment.THE_END).generator(new CIS_PlanetGen()));
			}

			Bukkit.getWorlds().forEach(world -> {
				world.setKeepSpawnInMemory(true);
				world.setAutoSave(true);
				world.setPVP(false);
				world.setStorm(false);
				world.setGameRuleValue("doDaylightCycle", "false");
				world.setGameRuleValue("doMobSpawning", "false");
				world.setFullTime(18000);
			});
		}, 0);
	}

	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		if (id.equalsIgnoreCase("ASTEROID")) {
			return new CIS_AsteroidGen();
		} else if (id.equalsIgnoreCase("PLANET")) {
			return new CIS_PlanetGen();
		} else {
			return new CIS_VoidGen();
		}
	}
}
