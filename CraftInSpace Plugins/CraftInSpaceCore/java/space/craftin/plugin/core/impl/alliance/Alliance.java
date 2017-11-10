/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.alliance;

import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.alliance.IAlliance;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.impl.core.functions.Datagram;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Alliance extends Datagram implements IAlliance {
	private long snowflake;
	private List<Long> factions;
	private String name;
	private String description;

	public Alliance(Map<String, Object> deserialize) {
		super(deserialize);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}

	@Override
	public IAstronaut getCommander() {
		final Optional<IAstronaut> commander = CraftInSpace.getInstance().getAllAstronauts().parallelStream()
				.filter(astronaut -> astronaut.getAllianceRank() == AllianceRank.COMMANDER)
				.filter(astronaut -> factions.contains(astronaut.getFactionSnowflake()))
				.findFirst();
		if (commander.isPresent()) {
			return commander.get();
		}
		return null;
	}

	@Override
	public void setCommander(IAstronaut commander) {
		final IAstronaut oldCommander = getCommander();
		commander.setAllianceRank(AllianceRank.COMMANDER);
		if (oldCommander != null) {
			oldCommander.setAllianceRank(AllianceRank.MOD);
		}
	}

	public List<IFaction> getFactions() {
		return factions.stream().map(id -> CraftInSpace.getInstance().getFaction(id)).collect(Collectors.toList());
	}

	@Override
	public boolean addFaction(IFaction faction) {
		if (!factions.contains(faction.getSnowflake())) {
			factions.add(faction.getSnowflake());
			return true;
		}
		return false;
	}

	@Override
	public boolean removeFaction(IFaction faction) {
		if (factions.contains(faction.getSnowflake())) {
			factions.remove(faction.getSnowflake());
			return true;
		}
		return false;
	}

	@Override
	public long getSnowflake() {
		return snowflake;
	}

	@Override
	public void promote(IAstronaut astronaut) {
		if (factions.contains(astronaut.getFactionSnowflake())) {
			astronaut.setAllianceRank(AllianceRank.MOD);
		}
	}

	@Override
	public void demote(IAstronaut astronaut) {
		if (factions.contains(astronaut.getFactionSnowflake())) {
			astronaut.setAllianceRank(AllianceRank.MEMBER);
		}
	}
}
