/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen.generator;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import space.craftin.plugin.worldgen.populator.CIS_PlanetPop;

import java.util.ArrayList;
import java.util.List;

public class CIS_PlanetGen extends CIS_AsteroidGen {
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		List<BlockPopulator> pops = new ArrayList<>();
		pops.addAll(super.getDefaultPopulators(world));
		pops.add(new CIS_PlanetPop());
		return pops;
	}
}
