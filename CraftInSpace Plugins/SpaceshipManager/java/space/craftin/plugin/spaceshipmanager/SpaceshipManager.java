/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.spaceshipmanager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.physical.vehicles.IVehicle;
import space.craftin.plugin.spaceshipmanager.listener.ControllerListener;

import java.util.Optional;

public class SpaceshipManager extends JavaPlugin {
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new ControllerListener(), this);


		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			CraftInSpace.getInstance().getShips().forEach(ship->{

			});
		}, 0, 1);
	}

	private Optional<IVehicle> retrieve(String nameId) {
		return nameId.startsWith(CraftInSpace.VNAME_PREFIX)
				? Optional.ofNullable(CraftInSpace.getInstance().getShip(Long.parseLong(nameId.substring(CraftInSpace.VNAME_PREFIX.length()))))
				: Optional.empty();
	}
}
