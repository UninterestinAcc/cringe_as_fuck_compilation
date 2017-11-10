package org.reborncraft.gtowny.data;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.data.inheritable.ModifiableTownyObject;
import org.reborncraft.gtowny.data.local.ChunkLocation;

import java.util.UUID;

public final class User implements ModifiableTownyObject {
	private final String name;
	private final int userId;
	private int townId;
	private int rankId;
	private long shieldLastsUntil;
	private final UUID uuid;
	private boolean inTownChat;

	public User(String name, UUID uuid, int userId, int townId, int rankId, long shieldLastsUntil, boolean inTownChat) {
		this.name = name;
		this.uuid = uuid;
		this.userId = userId;
		this.townId = townId;
		this.rankId = rankId;
		this.shieldLastsUntil = shieldLastsUntil;
		this.inTownChat = inTownChat;
	}

	public String getName() {
		return name;
	}

	public int getUserId() {
		return userId;
	}

	public int getTownId() {
		if (userId == -1) {
			return -1;
		} else if (userId == -2) {
			return -5;
		}

		return townId;
	}

	public int getRankId() {
		return rankId;
	}

	public void setRankId(int rankId) {
		if (rankId != this.rankId) {
			this.rankId = rankId;
			pushUpdate();
		}
	}

	@Override
	public String toString() {
		return name + ", DB/USER_ID:" + userId + ", TOWN:" + townId;
	}

	public long getShieldLastsUntil() {
		return shieldLastsUntil;
	}

	public boolean isShieldActive() {
		return shieldLastsUntil > System.currentTimeMillis();
	}

	public void setShieldLastsUntil(long shieldLastsUntil) {
		this.shieldLastsUntil = shieldLastsUntil;
		pushUpdate();
	}

	public void setTownId(int townId) {
		this.townId = townId;
		pushUpdate();
	}

	public Town getTown() {
		return TownyDataHandler.getTownById(getTownId());
	}

	public TownRank getRank() {
		return TownyDataHandler.getRankById(rankId);
	}

	public Chunk getCurrentChunk() {
		Player p = Bukkit.getPlayer(uuid);
		ChunkLocation loc = ChunkLocation.forLocation(p.getLocation());
		return TownyDataHandler.getOrCreateChunk(loc, p.getWorld().getName());
	}

	public boolean canBuild(Chunk c) {
		return c.canBuild(this);
	}

	public void setInTownChat(boolean inTownChat) {
		this.inTownChat = inTownChat;
		pushUpdate();
	}

	public boolean isInTownChat() {
		if (inTownChat && townId <= 0) setInTownChat(false);
		return inTownChat;
	}

	public static User forPlayer(Player p) {
		return TownyDataHandler.getOrCreateUser(p);
	}

	public static User forName(String name) {
		return TownyDataHandler.getUserByName(name);
	}
}
