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
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.impl.core.ChatUtil;

public class FactionsCommandsContainer implements CommandsContainer {
	@Override
	public void defaultSubcommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			show((Player) sender, new String[0]);
		} else {
			ChatUtil.send(sender, ChatUtil.ChatMessage.NOT_PLAYER);
		}
	}

	@CommandHandler (name = "info", requirePlayer = true)
	public void show(Player p, String[] args) {
		IFaction faction;
		if (args.length == 0) {
			faction = CraftInSpace.getInstance().getOrCreateAstronaut(p).getFaction();
		} else {
			faction = CraftInSpace.getInstance().getFactionByName(args[0]).get();
		}
	}

	@CommandHandler (name = "leave", requirePlayer = true)
	public void leave(Player p, String[] args) {
		final IAstronaut astronaut = CraftInSpace.getInstance().getOrCreateAstronaut(p);
		if (astronaut.getFactionSnowflake() != 0) {
			ChatUtil.send(p, ChatUtil.ChatMessage.LEAVE_FACTION_OK);
			astronaut.setFaction(null);
		}
	}

	@CommandHandler (name = "create", requirePlayer = true, requiredPermission = "craftinspace.faction.create")
	public void create(Player p, String[] args) {
		final IAstronaut astronaut = CraftInSpace.getInstance().getOrCreateAstronaut(p);
		if (astronaut.getFactionSnowflake() <= 0) {
			final IFaction faction = CraftInSpace.getInstance().createFaction(args[0], astronaut);
			if (faction != null) {
				ChatUtil.send(p, ChatUtil.ChatMessage.FACCREATE_OK);
			} else {
				ChatUtil.send(p, ChatUtil.ChatMessage.FACCREATE_FAIL + " Name duplicate?");
			}
		} else {
			ChatUtil.send(p, ChatUtil.ChatMessage.FACCREATE_FAIL + " Already in a faction.");
		}
	}
}
