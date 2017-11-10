package org.reborncraft.gtowny.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.reborncraft.gtowny.data.local.BlockStack;
import org.reborncraft.gtowny.data.local.ChunkLocation;
import org.reborncraft.gtowny.data.inheritable.ModifiableTownyObject;
import org.reborncraft.gtowny.data.internal.ChunkType;
import org.reborncraft.gtowny.data.internal.TownPermissions;
import org.reborncraft.gtowny.data.internal.TownType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Chunk implements ModifiableTownyObject {
	private final int chunkId;
	private final ChunkLocation chunkLocation;
	private final String world;
	private BlockStack terraformBlock = new BlockStack(0, 0);
	private int townId;
	private int ownerId;
	private ChunkType type;
	private final List<Integer> members;
	private int salePrice;
	private final int terraformMaxY;

	public Chunk(int chunkId, ChunkLocation chunkLocation, String world, int townId, int ownerId, ChunkType type, List<Integer> members, int salePrice, int terraformMaxY) {
		this.chunkId = chunkId;
		this.chunkLocation = chunkLocation;
		this.world = world;
		this.townId = townId;
		this.ownerId = ownerId;
		this.type = type;
		this.members = members;
		this.salePrice = salePrice;
		this.terraformMaxY = terraformMaxY;
	}

	public static double getFurthestAxialDistanceFromCenter(Location loc) {
		return getFurthestAxialDistanceFromCenter(loc.getBlockX(), loc.getBlockZ());
	}

	public static double getFurthestAxialDistanceFromCenter(final int worldX, final int worldZ) {
		ChunkLocation c = ChunkLocation.forWorldCords(worldX, worldZ);
		int extremeX = c.getX() * 29;
		int extremeZ = c.getZ() * 29;
		int x = Math.abs(extremeX - worldX);
		int z = Math.abs(extremeZ - worldZ);
		return x > z ? x : z;
	}

	public String getWorld() {
		return world;
	}

	public ChunkLocation getChunkLocation() {
		return chunkLocation;
	}

	public void setTerraforming(ItemStack terraformBlock) {
		this.terraformBlock = new BlockStack(terraformBlock);
	}

	public BlockStack getTerraformBlock() {
		return terraformBlock;
	}

	public int getTownId() {
		return hashCode() == Integer.MAX_VALUE ? TownType.WORLDBORDER.getBit() : townId;
	}

	public Location getCenter() {
		return new Location(Bukkit.getWorld(world), chunkLocation.getX() * 29, 0, chunkLocation.getZ() * 29);
	}

	public ChunkType getType() {
		return type;
	}

	public void setType(ChunkType type) {
		this.type = type;
		pushUpdate();
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwner(User owner) {
		this.ownerId = owner.getUserId();
		pushUpdate();
	}

	public boolean getExiled(User user) {
		return townId == -4;// Worldborder;
	}

	public int getChunkId() {
		return chunkId;
	}

	public int getSalePrice() {
		return salePrice;
	}

	public int getTerraformMaxY() {
		return terraformMaxY;
	}

	@Override
	public String toString() {
		return "Chunk[DB/ID=" + getChunkId() + ",TOWN=" + townId + ",x=" + getChunkLocation().getX() + ",z=" + getChunkLocation().getZ() + "]";
	}

	public void setTownId(int townId) {
		this.townId = townId;
		pushUpdate();
	}

	public void setSalePrice(int salePrice) {
		this.salePrice = salePrice;
		pushUpdate();
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public List<Integer> getMemberIds() {
		return new ArrayList<>(members);
	}

	public List<User> getMembers() {
		return members.stream().map(TownyDataHandler::getUserById).collect(Collectors.toList());
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
			pushUpdate();
		}
	}

	public void removeMember(User member) {
		if (members.contains(member.getUserId())) {
			members.remove(member.getUserId());
			pushUpdate();
		}
	}

	public Town getTown() {
		return TownyDataHandler.getTownById(townId);
	}

	public User getOwner() {
		return TownyDataHandler.getUserById(ownerId);
	}

	public boolean canBuild(User user) {
		return townId == -1 || members.contains(user.getUserId()) || ownerId == user.getUserId() || (user.getTownId() == townId && user.getRank().hasPermission(TownPermissions.TownBuild));
	}
}
