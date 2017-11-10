/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.craftin.plugin.commands.utils.CommandHandler;
import space.craftin.plugin.commands.utils.CommandsContainer;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.physical.vehicles.IShip;
import space.craftin.plugin.core.impl.core.ChatUtil;

public class ShipCommandsContainer implements CommandsContainer {
	@Override
	public void defaultSubcommand(CommandSender sender, String[] args) {
	}

	@CommandHandler (name = "launch", requirePlayer = true, requiredPermission = "craftinspace.faction.create")
	public void launch(Player p, String[] args) {
		// TODO Request components from player inventory?

		final IAstronaut astronaut = CraftInSpace.getInstance().getOrCreateAstronaut(p);
		if (astronaut.getFactionSnowflake() != 0) {
			final IShip ship = CraftInSpace.getInstance().createShip(p, astronaut);
			ship.claim(astronaut.getFaction());
		} else {
			ChatUtil.send(p, ChatUtil.ChatMessage.SHIPCREATE_FAIL + " You need to be in a faction to do that.");
		}
	}
}
