/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.core.faction;

import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.alliance.IAlliance;
import space.craftin.plugin.core.api.core.functions.IClaimable;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.api.physical.station.IStation;
import space.craftin.plugin.core.api.physical.vehicles.IDrone;
import space.craftin.plugin.core.api.physical.vehicles.IShip;
import space.craftin.plugin.core.impl.core.functions.Datagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Faction extends Datagram implements IFaction {
	private long snowflake;
	private String name;
	private String desc;

	public Faction(Map<String, Object> deserialize) {
		super(deserialize);
	}

	public Faction(long snowflake, String name, IAstronaut chief) {
		this.snowflake = snowflake;
		this.name = name;
		this.desc = "Default faction description. `/f desc <Description>' to change";
		setChief(chief);
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
		return desc;
	}

	@Override
	public void setDescription(String desc) {
		this.desc = desc;
	}

	@Override
	public IAstronaut getChief() {
		final Optional<IAstronaut> chief = getAstronauts().stream()
				.filter(astronaut -> astronaut.getFactionRank() == FactionRank.CHIEF)
				.findFirst();
		if (chief.isPresent()) {
			return chief.get();
		}
		return null;
	}

	@Override
	public void setChief(IAstronaut chief) {
		final IAstronaut oldChief = getChief();
		chief.setFaction(this);
		chief.setFactionRank(FactionRank.CHIEF);
		if (oldChief != null) {
			oldChief.setFactionRank(FactionRank.MOD);
			if (oldChief.getAllianceRank() == IAlliance.AllianceRank.COMMANDER) {
				chief.setAllianceRank(IAlliance.AllianceRank.COMMANDER);
				oldChief.setAllianceRank(IAlliance.AllianceRank.MOD);
			}
		}
	}

	@Override
	public List<IAstronaut> getAstronauts() {
		return CraftInSpace.getInstance().getAllAstronauts().stream().filter(a -> a.getFactionSnowflake() == snowflake).collect(Collectors.toList());
	}

	@Override
	public void addAstronaut(IAstronaut astronaut) {
		astronaut.setFaction(this);
	}

	@Override
	public void removeAstronaut(IAstronaut astronaut) {
		astronaut.setFaction(null);
	}

	@Override
	public List<IClaimable> getClaims() {
		List<IClaimable> allClaims = new ArrayList<>();
		allClaims.addAll(getDrones());
		allClaims.addAll(getSpaceships());
		allClaims.addAll(getStations());
		return allClaims;
	}

	@Override
	public void claim(IClaimable claim) {
		claim.claim(this);
	}

	@Override
	public void declaim(IClaimable claim) {
		if (claim.getFactionSnowflake() == snowflake) {
			claim.detatchFromFaction();
		}
	}

	@Override
	public List<IDrone> getDrones() {
		return (List<IDrone>) CraftInSpace.getInstance().getAllAssociated(IDrone.class, snowflake, "drones");
	}

	@Override
	public List<IShip> getSpaceships() {
		return (List<IShip>) CraftInSpace.getInstance().getAllAssociated(IShip.class, snowflake, "ships");
	}

	@Override
	public List<IStation> getStations() {
		return (List<IStation>) CraftInSpace.getInstance().getAllAssociated(IStation.class, snowflake, "stations");
	}

	@Override
	public long getSnowflake() {
		return snowflake;
	}

	@Override
	public void promote(IAstronaut astronaut) {
		if (astronaut.getFactionSnowflake() == snowflake) {
			astronaut.setFactionRank(FactionRank.MOD);
		}
	}

	@Override
	public void demote(IAstronaut astronaut) {
		if (astronaut.getFactionSnowflake() == snowflake) {
			astronaut.setFactionRank(FactionRank.MEMBER);
		}
	}
}
