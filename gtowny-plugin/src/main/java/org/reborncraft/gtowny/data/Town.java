package org.reborncraft.gtowny.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.data.inheritable.ModifiableTownyObject;
import org.reborncraft.gtowny.data.internal.ChunkType;
import org.reborncraft.gtowny.data.internal.TownOptions;
import org.reborncraft.gtowny.data.internal.TownTaxOptions;
import org.reborncraft.gtowny.data.internal.TownType;
import org.reborncraft.gtowny.data.local.ChunkLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Town implements ModifiableTownyObject {
	private final TownType type; // Immutable, you can't change from being wilderness to a normal town... etc...
	private final int townId;
	private final int ownerId;
	private String townDesc = "";
	private final TownBlockBank blockBank; // One instance or else...
	private TownSpawn spawn;
	private long moneyBank;
	private int townTaxingAmount; // The percentage someone is charged for their owned land's cost.
	private long abandonedSinceMillis;
	private final List<TownOptions> options;
	private final List<Integer> chunkIds;
	private final List<TownTaxOptions> taxOptions;
	private final List<TownWarp> warps;
	private final List<Integer> exiledIds;
	private String name;
	private final List<Integer> members;
	private int autoResell;

	public TownType getType() {
		return type;
	}

	public int getTownId() {
		return townId;
	}

	public List<Integer> getExiledIds() {
		return new ArrayList<>(exiledIds);
	}

	public List<Integer> getMemberIds() {
		return new ArrayList<>(members);
	}

	public Town(int townId, TownType type, int ownerId, TownBlockBank blockBank, String townDesc, TownSpawn spawn, long moneyBank, int townTaxCost, long abandonedSinceMillis, String name, List<TownOptions> options, List<TownWarp> warps, List<Integer> chunkIds, List<TownTaxOptions> taxOptions, List<Integer> exiledIds, List<Integer> members, int autoResell) {
		this.townId = townId;
		this.type = type;
		this.ownerId = ownerId;
		this.blockBank = blockBank;
		this.townDesc = townDesc;
		this.spawn = spawn;
		this.moneyBank = moneyBank;
		this.townTaxingAmount = townTaxCost;
		this.abandonedSinceMillis = abandonedSinceMillis;
		this.name = name;
		this.options = options;
		this.warps = warps;
		this.taxOptions = taxOptions;
		this.exiledIds = exiledIds;
		this.chunkIds = chunkIds;
		this.members = members;
		this.autoResell = autoResell;
	}

	public TownBlockBank getBlockBank() {
		return blockBank;
	}

	public long getMoneyBank() {
		return moneyBank;
	}

	public void setMoneyBank(long moneyBank) {
		this.moneyBank = moneyBank;
	}

	public TownType getTownType() {
		return type;
	}

	protected boolean addChunk(Chunk chunk) {
		if (chunk.getTownId() == getId() && !chunkIds.contains(chunk.getChunkId())) {
			chunkIds.add(chunk.getChunkId());
			pushUpdate();
			return true;
		}
		return false;
	}

	protected boolean removeChunk(Chunk chunk) {
		if (chunkIds.contains(chunk.getChunkId())) {
			chunkIds.remove((Integer) chunk.getChunkId()); // Yea fuck arraylist indexes
			pushUpdate();
			return true;
		}
		return false;
	}

	public void toggleOption(TownOptions opt) {
		if (options.contains(opt)) {
			options.remove(opt);
		} else {
			options.add(opt);
		}
		pushUpdate();
	}

	public List<Integer> getChunkIds() {
		return new ArrayList<>(chunkIds); // Prevent CME and other untrackable shitdoms.
	}

	public List<Chunk> getChunks() {
		return chunkIds.stream().map(TownyDataHandler::getChunkById).collect(Collectors.toList()); // Prevent CME and other untrackable shitdoms.
	}

	public Chunk getChunk(int chunkX, int chunkZ) {
		ChunkLocation cl = new ChunkLocation(chunkX, chunkZ);
		List<Integer> chunks = this.chunkIds.stream().filter(chunkId -> TownyDataHandler.getChunkById(chunkId).getChunkLocation() == cl).collect(Collectors.toList());
		return chunks.size() >= 1 ? TownyDataHandler.getChunkById(chunks.get(0)) : null;
	}

	public boolean addWarp(TownWarp warp) {
		if (warps.size() < 16) {
			if (!warps.contains(warp)) {
				warps.add(warp);
				pushUpdate();
				return true;
			}
		}
		return false;
	}

	public boolean removeWarp(TownWarp warp) {
		if (warps.contains(warp)) {
			warps.remove(warp);
			pushUpdate();
			return true;
		}
		return false;
	}

	public List<TownWarp> getWarps() {
		return new ArrayList<>(warps); // Prevent CME and other untrackable shitdoms.
	}

	public int getTownTaxingAmount() {
		return townTaxingAmount;
	}

	public void setTownTaxingAmount(int townTaxingAmount) {
		this.townTaxingAmount = townTaxingAmount;
	}

	public Location getSpawn() {
		return spawn.getLocation();
	}

	public void setSpawn(TownSpawn spawn) {
		this.spawn = spawn;
		pushUpdate();
	}

	public void setOptions(List<TownOptions> options) {
		this.options.clear();
		this.options.addAll(options);
		pushUpdate();
	}

	public List<TownOptions> getOptions() {
		return new ArrayList<>(options);
	}

	public void setTaxOptions(List<TownTaxOptions> taxOptions) {
		this.taxOptions.clear();
		this.taxOptions.addAll(taxOptions);
		pushUpdate();
	}

	public List<TownTaxOptions> getTaxOptions() {
		return new ArrayList<>(taxOptions);
	}

	public boolean getExiled(User user) {
		return (options.contains(TownOptions.ExileEveryone) && user.getTownId() != getId()) || exiledIds.stream().filter(exiledUser -> exiledUser == user.getUserId()).count() >= 1;
	}

	public void exile(User user) {
		if (exiledIds.stream().filter(exiledUser -> exiledUser == user.getUserId()).count() < 1) {
			exiledIds.add(user.getUserId());
			pushUpdate();
		}
	}

	public long getAbandonedSinceMillis() {
		return abandonedSinceMillis;
	}

	public void setAbandonedSinceMillis(long abandonedSinceMillis) {
		this.abandonedSinceMillis = abandonedSinceMillis;
		pushUpdate();
	}

	public String getTownDesc() {
		return townDesc;
	}

	public void setTownDesc(String townDesc) {
		this.townDesc = townDesc;
		pushUpdate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		pushUpdate();
	}

	@Override
	public String toString() {
		return "Town[name=" + name + ",type=" + type + ",bank=$" + moneyBank + "]";
	}

	public int getId() {
		return townId;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void addMember(Player p) {
		addMember(User.forPlayer(p));
	}

	public void removeMember(Player p) {
		removeMember(User.forPlayer(p));
	}

	public void addMember(User user) {
		if (user != null && !members.contains(user.getUserId())) {
			members.add(user.getUserId());
			user.setTownId(this.getId());
			pushUpdate();
		}
	}

	public void removeMember(User user) {
		if (members.contains(user.getUserId()) && ownerId != user.getUserId()) {
			members.remove(user.getUserId());
			user.setTownId(-1);
			user.setRankId(-1);
			getChunks().stream().filter(c -> c.getOwnerId() == user.getUserId()).forEach(c -> {
				c.setOwnerId(-1);
				c.getMembers().forEach(c::removeMember);
				c.setSalePrice(autoResell);
			});
			pushUpdate();
		}
	}

	public void unExile(int userId) {
		if (exiledIds.contains(userId)) {
			exiledIds.remove(userId);
			pushUpdate();
		}
	}

	public List<User> getMembers() {
		return members.stream().map(TownyDataHandler::getUserById).collect(Collectors.toList());
	}

	public User getOwner() {
		return TownyDataHandler.getUserById(ownerId);
	}

	public void claimChunk(Chunk chunk) {
		Town prevTown = chunk.getTown();
		if (prevTown != null) {
			if (prevTown.getChunkIds().contains(chunk.getChunkId())) {
				prevTown.removeChunk(chunk);
			}
		}
		chunk.setTownId(townId);
		chunk.setOwnerId(-1);
		chunk.setType(ChunkType.Normal);
		chunk.setSalePrice(-1);
		chunk.getMembers().forEach(chunk::removeMember);
		if (chunk.getTownId() < 0) {
			addChunk(chunk);
		}
	}

	public int getAutoResell() {
		return autoResell;
	}

	public void setAutoResell(int autoResell) {
		this.autoResell = autoResell;
		pushUpdate();
	}
}
