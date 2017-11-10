package org.reborncraft.gtowny.data;

import javafx.util.Pair;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.data.inheritable.ModifiableTownyObject;
import org.reborncraft.gtowny.data.internal.*;
import org.reborncraft.gtowny.data.local.BlockStack;
import org.reborncraft.gtowny.data.local.ChunkLocation;
import org.reborncraft.gtowny.data.sql.SQLInstance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class TownyDataHandler {
	public static final Town TOWN_WILDERNESS = new Town(TownType.WILDERNESS.getBit(), TownType.WILDERNESS, -1, null, "Common and uninteresting.", null, 0, 0, 0, TownType.WILDERNESS.toString(), Arrays.asList(TownOptions.PvP, TownOptions.Fire, TownOptions.Explosions, TownOptions.MobSpawning), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), -1);
	public static final Town TOWN_WARZONE = new Town(TownType.WARZONE.getBit(), TownType.WARZONE, -1, null, "All (land) rights reserved.", null, 0, 0, 0, TownType.WARZONE.toString(), Arrays.asList(TownOptions.PvP, TownOptions.MobSpawning), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), -1);
	public static final Town TOWN_SAFEZONE = new Town(TownType.SAFEZONE.getBit(), TownType.SAFEZONE, -1, null, "All (land) rights reserved.", null, 0, 0, 0, TownType.SAFEZONE.toString(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), -1);
	public static final Town TOWN_WORLDBORDER = new Town(TownType.WORLDBORDER.getBit(), TownType.WORLDBORDER, -1, null, "Unclaimable, might as well make it inaccessible.", null, 0, 0, 0, TownType.WORLDBORDER.toString(), Collections.singletonList(TownOptions.ExileEveryone), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), -1);
	public static final Town TOWN_LOADFAIL = new Town(TownType.LOADFAIL.getBit(), TownType.LOADFAIL, -2, null, "Error occurred during initialization, freezing chunk.", null, 0, 0, 0, TownType.LOADFAIL.toString(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), -1);

	public static final TownRank RANK_CITIZEN = new TownRank("Citizen", -1, Collections.singletonList(TownPermissions.Member));
	public static final TownRank RANK_MEMBER_PLUS = new TownRank("Clerk", 1, Arrays.asList(TownPermissions.Member, TownPermissions.ManageTownMembers));
	public static final TownRank RANK_STAFF = new TownRank("Minister", 2, Arrays.asList(TownPermissions.Member, TownPermissions.ManageTownMembers, TownPermissions.Claim, TownPermissions.TownBuild, TownPermissions.Terraform, TownPermissions.LandManagement));
	public static final TownRank RANK_MANAGER = new TownRank("Manager", 3, Arrays.asList(TownPermissions.values()));
	public static final TownRank RANK_MAYOR = new TownRank("Mayor", 4, Arrays.asList(TownPermissions.values()));

	private static final User USER_NOBODY = new User("Nobody", UUID.randomUUID(), -1, TOWN_WILDERNESS.getTownId(), -1, 0, false);
	private static final User USER_LOADFAIL = new User("LOAD_FAIL", UUID.randomUUID(), -2, TOWN_LOADFAIL.getTownId(), -1, 0, false);

	private static final ConcurrentHashMap<UUID, User> userMap = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Integer, Town> townMap = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<ChunkLocation, ConcurrentHashMap<String, Chunk>> chunkMap = new ConcurrentHashMap<>();

	private final String sqlHost;
	private final int sqlPort;
	private final String sqlUser;
	private final String sqlPass;

	private SQLInstance sqlInstance; // Mutable / reinitializable.
	private static TownyDataHandler instance;
	private static final ConcurrentMap<Town, ConcurrentMap<Integer, Long>> invites = new ConcurrentHashMap<>();

	public TownyDataHandler(String sqlHost, int sqlPort, String sqlUser, String sqlPass) {
		this.sqlHost = sqlHost;
		this.sqlPort = sqlPort;
		this.sqlUser = sqlUser;
		this.sqlPass = sqlPass;

		TownyDataHandler.instance = this;
	}

	public static TownyDataHandler getInstance() {
		return instance;
	}

	public static ConcurrentMap<Town, ConcurrentMap<Integer, Long>> getInvites() {
		return invites;
	}

	/**
	 * This is blocking
	 */
	public SQLInstance getSQLInstance() throws SQLException {
		try {
			if (sqlInstance == null || sqlInstance.getSQLConnection().isClosed()) {
				sqlInstance = new SQLInstance(sqlHost, sqlPort, sqlUser, sqlPass, "GTowny");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return sqlInstance;
	}


	public static void pushObject(ModifiableTownyObject object) {
		if (object instanceof Chunk) {
			updateChunk((Chunk) object);
		} else if (object instanceof User) {
			updateUser((User) object);
		} else if (object instanceof Town) {
			updateTown((Town) object);
		} else if (object instanceof TownBlockBank) {
			updateBlockBank((TownBlockBank) object);
		} else if (object instanceof TownRank) {
			updateTownRank((TownRank) object);
		}
	}


	public static void updateChunk(Chunk chunk) {
		if (chunk.getChunkLocation().hashCode() == Integer.MAX_VALUE) return;
		try {
			getInstance().getSQLInstance().doUpdate("UPDATE `Chunks` SET " +
					"`TOWN_ID`=" + chunk.getTownId() + ", " +
					"`CHUNK_OWNER`=" + chunk.getOwnerId() + ", " +
					"`CHUNK_TYPE`=" + chunk.getType().getBit() + ", " +
					"`CHUNK_SALE_PRICE`=" + chunk.getSalePrice() + ", " +
					"`CHUNK_TERRAFORMING`=" + (chunk.getTerraformBlock().getBlockId() != 0) + ", " +
					"`CHUNK_TERRAFORM_BLOCK`=" + chunk.getTerraformBlock().getBlockId() + ", " +
					"`CHUNK_TERRAFORM_BLOCKDAMAGE`=" + chunk.getTerraformBlock().getDamage() + ", " +
					"`CHUNK_TERRAFORM_MAX_Y`=" + chunk.getTerraformMaxY() +
					" WHERE `CHUNK_ID`=" + chunk.getChunkId());
			getInstance().getSQLInstance().doUpdate("DELETE FROM `ChunkMembers` WHERE `CHUNK_ID` = " + chunk.getChunkId());
			String members = chunk.getMemberIds().stream().map(member -> "(" + chunk.getChunkId() + ", " + member + ")").collect(Collectors.joining(", "));
			if (members.length() >= 1) {
				getInstance().getSQLInstance().doUpdate("INSERT INTO `ChunkMembers` (`CHUNK_ID, `USER_ID`) VALUES " + members);
			}
		} catch (SQLException e) {
			printStackTrace(e);
		}
	}

	public static void updateUser(User user) {
		if (user.getUserId() <= 0) return;
		try {
			getInstance().getSQLInstance().doUpdate("UPDATE `Users` SET " +
					"`TOWN_ID`=" + user.getTownId() + ", " +
					"`TOWN_RANK_ID`=" + user.getRankId() + ", " +
					"`USER_NAME`='" + user.getName() + "', " +
					"`USER_SHIELD_LASTS_UNTIL`=" + user.getShieldLastsUntil() + ", " +
					"`USER_IN_TOWN_CHAT`=" + user.isInTownChat() +
					" WHERE `USER_ID`=" + user.getUserId());
		} catch (SQLException e) {
			printStackTrace(e);
		}
	}

	public static void updateTown(Town town) {
		if (town.getId() <= 0) return;
		PreparedStatement ps = null;
		try {
			ps = getInstance().getSQLInstance().getSQLConnection().prepareStatement("UPDATE `Towns` SET " +
					"`TOWN_NAME`=?, " +
					"`TOWN_DESC`=?, " +
					"`TOWN_OPTIONS`=?, " +
					"`TOWN_SPAWN_X`=?, " +
					"`TOWN_SPAWN_Y`=?, " +
					"`TOWN_SPAWN_Z`=?, " +
					"`TOWN_SPAWN_PITCH`=?, " +
					"`TOWN_SPAWN_YAW`=?, " +
					"`TOWN_SPAWN_WORLD`=?, " +
					"`TOWN_TAX_TYPE`=?, " +
					"`TOWN_ABANDONED_SINCE`=?, " +
					"`TOWN_BANK`=?, " +
					"`TOWN_TAX_PERCENTAGE`=?," +
					"`TOWN_CHUNKS_AUTORESELL`=?" +
					" WHERE `TOWN_ID`=" + town.getId());
			ps.setString(1, town.getName());
			ps.setString(2, town.getTownDesc());
			ps.setInt(3, TownOptions.toValue(town.getOptions()));
			ps.setInt(4, town.getSpawn().getBlockX());
			ps.setInt(5, town.getSpawn().getBlockY());
			ps.setInt(6, town.getSpawn().getBlockZ());
			ps.setDouble(7, town.getSpawn().getPitch());
			ps.setDouble(8, town.getSpawn().getYaw());
			ps.setString(9, town.getSpawn().getWorld().getName());
			ps.setInt(10, TownTaxOptions.toValue(town.getTaxOptions()));
			ps.setLong(11, town.getAbandonedSinceMillis());
			ps.setLong(12, town.getMoneyBank());
			ps.setInt(13, town.getTownTaxingAmount());
			ps.executeUpdate();
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
		updateExiles(town);
		updateWarps(town);
		updateBlockBank(town.getBlockBank());
	}

	public static void updateExiles(Town town) {
		List<Integer> exiles = town.getExiledIds();
		try {
			int amt = getInstance().getSQLInstance().countQueryResults("SELECT `USER_ID` FROM `TownExiles` WHERE `TOWN_ID` = " + town.getId());
			if (amt != exiles.size()) {
				getInstance().getSQLInstance().doUpdate("DELETE FROM `TownExiles` WHERE `TOWN_ID` = " + town.getId());
				exiles.forEach(user -> {
					try {
						getInstance().getSQLInstance().doUpdate("INSERT INTO `TownExiles` (`TOWN_ID`, `USER_ID`) VALUES (" + town.getId() + ", " + user + ")");
					} catch (SQLException e) {
						printStackTrace(e);
					}
				});
			}
		} catch (SQLException e) {
			printStackTrace(e);
		}
	}

	public static void updateWarps(Town town) {
		try {
			getInstance().getSQLInstance().doUpdate("DELETE FROM `TownWarps` WHERE `TOWN_ID` = " + town.getId());
		} catch (SQLException e) {
			printStackTrace(e);
		}
		if (town.getWarps().size() > 0) {
			for (TownWarp warp : town.getWarps()) {
				PreparedStatement warpsUpdate = null;
				try {
					warpsUpdate = getInstance().getSQLInstance().getSQLConnection().prepareStatement("INSERT INTO `TownWarps` (`TOWN_ID`, `TOWN_WARP_NAME`, `TOWN_WARP_X`, `TOWN_WARP_Y`, `TOWN_WARP_Z`, `TOWN_WARP_PITCH`, `TOWN_WARP_YAW`, `TOWN_WARP_WORLD`) VALUES (?, ?, ?, ?, ?, ?, ?)");
					warpsUpdate.setInt(1, town.getId());
					warpsUpdate.setString(2, warp.getName());
					warpsUpdate.setInt(3, warp.getX());
					warpsUpdate.setInt(4, warp.getY());
					warpsUpdate.setInt(5, warp.getZ());
					warpsUpdate.setDouble(6, warp.getPitch());
					warpsUpdate.setDouble(7, warp.getYaw());
					warpsUpdate.setString(8, warp.getWorld());
					warpsUpdate.executeUpdate();
					warpsUpdate.close();
				} catch (SQLException e) {
					printStackTrace(e);
				} finally {
					if (warpsUpdate != null) {
						try {
							warpsUpdate.close();
						} catch (SQLException e) {
							printStackTrace(e);
						}
					}
				}
			}
		}
	}

	public static void updateBlockBank(TownBlockBank bank) {
		try {
			getInstance().getSQLInstance().doUpdate("DELETE FROM `TownInventory` WHERE `TOWN_ID` = " + bank.getTown().getId());
			List<String> values = new ArrayList<>();
			bank.getBlockList().forEach((stack, amt) -> values.add("(" + bank.getTown().getId() + ", " + stack.getBlockId() + ", " + stack.getDamage() + ", " + amt + ")"));
			if (values.size() >= 1) {
				getInstance().getSQLInstance().doUpdate("INSERT INTO `TownInventory` (`TOWN_ID`, `TOWN_INVENTORY_BLOCK`, `TOWN_INVENTORY_BLOCKDAMAGE`, `TOWN_INVENTORY_BLOCK_AMOUNT`) VALUES " + String.join(", ", values));
			}
		} catch (SQLException e) {
			printStackTrace(e);
		}
	}

	public static void updateTownRank(TownRank rank) {
		PreparedStatement ps = null;

		try {
			ps = getInstance().getSQLInstance().getSQLConnection().prepareStatement("UPDATE `TownRanks` SET " +
					"`TOWN_RANK_NAME`= ?" +
					"`TOWN_RANK_PERMISSIONS`=" + TownPermissions.toValue(rank.getPermissions()) +
					" WHERE `TOWN_RANK_ID`=" + rank.getId());
			ps.setString(1, rank.getName());
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
	}

	public static boolean createTown(Player owner, String townName) {
		User ownerUser = getOrCreateUser(owner);
		boolean dupe = getTownByName(townName) != null;
		if (!dupe) {
			PreparedStatement insert = null;
			try {
				insert = getInstance().getSQLInstance().getSQLConnection().prepareStatement(
						"INSERT INTO `Towns` " +
								"(TOWN_NAME, TOWN_OWNER, TOWN_OPTIONS, TOWN_SPAWN_X, TOWN_SPAWN_Y, TOWN_SPAWN_Z, TOWN_SPAWN_PITCH, TOWN_SPAWN_YAW, TOWN_SPAWN_WORLD, TOWN_TAX_TYPE, TOWN_ABANDONED_SINCE, TOWN_BANK, TOWN_TAX_PERCENTAGE)" +
								" VALUES " +
								" (?, ?, 0, ?, ?, ?, ?, ?, ?, 0, 0, 0, 0)"
				);
				insert.setString(1, townName);
				insert.setInt(2, ownerUser.getUserId());
				insert.setInt(3, owner.getLocation().getBlockX());
				insert.setInt(4, owner.getLocation().getBlockY());
				insert.setInt(5, owner.getLocation().getBlockZ());
				insert.setDouble(6, owner.getLocation().getPitch());
				insert.setDouble(7, owner.getLocation().getYaw());
				insert.setString(8, owner.getLocation().getWorld().getName());
				insert.executeUpdate();
			} catch (SQLException e) {
				printStackTrace(e);
			} finally {
				if (insert != null) {
					try {
						insert.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
			}

			PreparedStatement getId = null;
			ResultSet getIdResult = null;
			try {
				getId = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT `TOWN_ID` FROM `Towns` WHERE TOWN_NAME LIKE ?");
				getId.setString(1, townName);
				getIdResult = getId.executeQuery();
				if (getIdResult.next()) {
					getOrCreateUser(owner).setTownId(getIdResult.getInt("TOWN_ID"));
					ownerUser.setRankId(4);
					return true;
				}
			} catch (SQLException e) {
				printStackTrace(e);
			} finally {
				try {
					if (getIdResult != null) {
						getIdResult.close();
					}
				} catch (SQLException e) {
					printStackTrace(e);
				}
				try {
					if (getId != null) {
						getId.close();
					}
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
		return false;
	}

	public static Chunk getOrCreateChunk(ChunkLocation loc, String world) {
		Chunk chunk = new Chunk(-1, loc, world, TOWN_LOADFAIL.getId(), -2, ChunkType.Normal, new ArrayList<>(), -1, 0);
		if (loc.hashCode() == Integer.MAX_VALUE) {
			return new Chunk(-1, loc, world, TownType.WORLDBORDER.getBit(), -1, ChunkType.Normal, new ArrayList<>(), -1, 0);
		} else if (chunkMap.containsKey(loc) && chunkMap.get(loc).containsKey(world)) {
			chunk = chunkMap.get(loc).get(world);
		} else {
			PreparedStatement check = null;
			ResultSet r = null;
			Statement getMembers = null;
			ResultSet membersRS = null;
			PreparedStatement ps = null;
			PreparedStatement getCID = null;
			ResultSet rCID = null;
			try {
				check = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT * FROM `Chunks` WHERE `CHUNK_X` = " + loc.getX() + " AND `CHUNK_Z` = " + loc.getZ());
				r = check.executeQuery();
				if (r.next()) {
					getMembers = getInstance().getSQLInstance().getSQLConnection().createStatement();
					membersRS = getMembers.executeQuery("SELECT * FROM `ChunkMembers` WHERE `CHUNK_ID` = " + r.getInt("CHUNK_ID"));
					List<Integer> members = new ArrayList<>();
					while (membersRS.next()) {
						members.add(membersRS.getInt("USER_ID"));
					}
					chunk = new Chunk(r.getInt("CHUNK_ID"), loc, world, r.getInt("TOWN_ID"), r.getInt("CHUNK_OWNER"), ChunkType.forBit(r.getInt("CHUNK_TYPE")), members, r.getInt("CHUNK_SALE_PRICE"), r.getInt("CHUNK_TERRAFORM_MAX_Y"));
				} else {
					ps = getInstance().getSQLInstance().getSQLConnection().prepareStatement("INSERT INTO `Chunks` " +
							"(`TOWN_ID`, `CHUNK_OWNER`, `CHUNK_X`, `CHUNK_Z`, `CHUNK_WORLD`, `CHUNK_TYPE`, `CHUNK_SALE_PRICE`, `CHUNK_TERRAFORMING`, `CHUNK_TERRAFORM_BLOCK`, `CHUNK_TERRAFORM_BLOCKDAMAGE`, `CHUNK_TERRAFORM_MAX_Y`) " +
							"VALUES " +
							"(-1, -1, " + loc.getX() + ", " + loc.getZ() + ", ?, 0, -1, false, 0, 0, 0)");
					ps.setString(1, world);
					ps.executeUpdate();
					getCID = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT `CHUNK_ID` FROM `Chunks` WHERE `CHUNK_X` = " + loc.getX() + " AND `CHUNK_Z` = " + loc.getZ());
					rCID = check.executeQuery();
					if (rCID.next()) {
						chunk = new Chunk(rCID.getInt("CHUNK_ID"), loc, world, TownType.WILDERNESS.getBit(), -1, ChunkType.Normal, new ArrayList<>(), -1, 0);
					}
				}
			} catch (SQLException e) {
				printStackTrace(e);
			} finally {
				if (rCID != null) {
					try {
						rCID.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (getCID != null) {
					try {
						getCID.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (membersRS != null) {
					try {
						membersRS.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (getMembers != null) {
					try {
						getMembers.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (r != null) {
					try {
						r.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (check != null) {
					try {
						check.close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
			}
		}
		if (chunk.getTownId() != TOWN_LOADFAIL.getId()) {
			ConcurrentHashMap<String, Chunk> putMap = chunkMap.containsKey(loc) ? chunkMap.get(loc) : new ConcurrentHashMap();
			putMap.put(world, chunk);
			chunkMap.put(loc, putMap);
		}
		return chunk;
	}

	public static void getAllChunks() {
		PreparedStatement check = null;
		ResultSet r = null;
		Statement getMembers = null;
		ResultSet membersRS = null;
		try {
			check = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT * FROM `Chunks`");
			r = check.executeQuery();
			while (r.next()) {
				getMembers = getInstance().getSQLInstance().getSQLConnection().createStatement();
				membersRS = getMembers.executeQuery("SELECT * FROM `ChunkMembers` WHERE `CHUNK_ID` = " + r.getInt("CHUNK_ID"));
				List<Integer> members = new ArrayList<>();
				while (membersRS.next()) {
					members.add(membersRS.getInt("USER_ID"));
				}
				ChunkLocation loc = new ChunkLocation(r.getInt("CHUNK_X"), r.getInt("CHUNK_Z"));
				ConcurrentHashMap<String, Chunk> putMap = chunkMap.containsKey(loc) ? chunkMap.get(loc) : new ConcurrentHashMap();
				putMap.put(r.getString("CHUNK_WORLD"), new Chunk(r.getInt("CHUNK_ID"), loc, r.getString("CHUNK_WORLD"), r.getInt("TOWN_ID"), r.getInt("CHUNK_OWNER"), ChunkType.forBit(r.getInt("CHUNK_TYPE")), members, r.getInt("CHUNK_SALE_PRICE"), r.getInt("CHUNK_TERRAFORM_MAX_Y")));
				chunkMap.put(loc, putMap);
			}
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (membersRS != null) {
				try {
					membersRS.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (getMembers != null) {
				try {
					getMembers.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (r != null) {
				try {
					r.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (check != null) {
				try {
					check.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
	}

	public static User getOrCreateUser(Player player) {
		User user = USER_LOADFAIL;
		if (userMap.containsKey(player.getUniqueId())) {
			user = userMap.get(player.getUniqueId());
		} else {
			Pair<Statement, ResultSet> result = null;
			Pair<Statement, ResultSet> selectResult = null;
			try {
				result = getInstance().getSQLInstance().doQuery("SELECT * FROM `Users` WHERE `USER_UUID` LIKE '" + player.getUniqueId() + "'");
				if (result.getValue().next()) {
					user = new User(result.getValue().getString("USER_NAME"), UUID.fromString(result.getValue().getString("USER_UUID")), result.getValue().getInt("USER_ID"), result.getValue().getInt("TOWN_ID"), result.getValue().getInt("TOWN_RANK_ID"), result.getValue().getLong("USER_SHIELD_LASTS_UNTIL"), result.getValue().getBoolean("USER_IN_TOWN_CHAT"));
				} else {
					getInstance().getSQLInstance().doUpdate("INSERT INTO `Users` (TOWN_ID, TOWN_RANK_ID, USER_NAME, USER_SHIELD_LASTS_UNTIL, USER_UUID) VALUES (-1, -1, '" + player.getName() + "', 0, " + "'" + player.getUniqueId() + "')");
					selectResult = getInstance().getSQLInstance().doQuery("SELECT `USER_ID` FROM `Users` WHERE `USER_UUID` LIKE '" + player.getUniqueId().toString() + "'");
					if (selectResult.getValue().next()) {
						user = new User(player.getName(), player.getUniqueId(), selectResult.getValue().getInt("USER_ID"), -1, 0, 0, false);
					}
				}
			} catch (SQLException e) {
				printStackTrace(e);
			} finally {
				if (result != null) {
					try {
						if (result.getValue() != null) {
							result.getValue().close();
						}
					} catch (SQLException e) {
						printStackTrace(e);
					}
					try {
						if (result.getKey() != null) {
							result.getKey().close();
						}
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (selectResult != null) {
					try {
						if (selectResult.getValue() != null) {
							selectResult.getValue().close();
						}
					} catch (SQLException e) {
						printStackTrace(e);
					}
					try {
						if (selectResult.getKey() != null) {
							selectResult.getKey().close();
						}
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
			}
		}
		if (user != null && user != USER_LOADFAIL) {
			userMap.put(player.getUniqueId(), user);
		}
		return user;
	}

	public static User getUserById(int userId) {
		User user = USER_NOBODY;
		Optional<User> optUser = userMap.values().stream().filter(u -> u.getUserId() == userId).findFirst();
		if (userId == -2) {
			user = USER_LOADFAIL;
		} else if (userId == -1) {
			user = USER_NOBODY;
		} else if (optUser.isPresent()) {
			user = optUser.get();
		} else {
			Pair<Statement, ResultSet> result = null;
			try {
				result = getInstance().getSQLInstance().doQuery("SELECT * FROM `Users` WHERE `USER_ID` =" + userId);
				if (result.getValue().next()) {
					user = new User(result.getValue().getString("USER_NAME"), UUID.fromString(result.getValue().getString("USER_UUID")), result.getValue().getInt("USER_ID"), result.getValue().getInt("TOWN_ID"), result.getValue().getInt("TOWN_RANK_ID"), result.getValue().getLong("USER_SHIELD_LASTS_UNTIL"), result.getValue().getBoolean("USER_IN_TOWN_CHAT"));
				}
			} catch (SQLException e) {
				printStackTrace(e);
			} finally {
				if (result != null) {
					if (result.getValue() != null) {
						try {
							result.getValue().close();
						} catch (SQLException e) {
							printStackTrace(e);
						}
					}
					if (result.getKey() != null) {
						try {
							result.getKey().close();
						} catch (SQLException e) {
							printStackTrace(e);
						}
					}
				}
			}
		}
		return user;
	}

	public static TownRank getTownRank(int userId) {
		return getUserById(userId).getRank();
	}

	public static TownRank getRankById(int rankId) {
		if (rankId == 1) {
			return RANK_MEMBER_PLUS;
		} else if (rankId == 2) {
			return RANK_STAFF;
		} else if (rankId == 3) {
			return RANK_MANAGER;
		} else if (rankId == 4) {
			return RANK_MAYOR;
		}
		return RANK_CITIZEN;
	}


	public static TownBlockBank getBlockBankByTownId(int townId) {
		ConcurrentHashMap<BlockStack, Integer> blockMap = new ConcurrentHashMap<>();
		Pair<Statement, ResultSet> rs = null;
		try {
			rs = getInstance().getSQLInstance().doQuery("SELECT * FROM `TownInventory` WHERE `TOWN_ID`=" + townId);
			ResultSet res = rs.getValue();
			while (res.next()) {
				blockMap.put(new BlockStack(res.getInt("TOWN_INVENTORY_BLOCK"), res.getInt("TOWN_INVENTORY_BLOCKDAMAGE")), res.getInt("TOWN_INVENTORY_BLOCK_AMOUNT"));
			}
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (rs != null) {
				if (rs.getValue() != null) {
					try {
						rs.getValue().close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
				if (rs.getKey() != null) {
					try {
						rs.getKey().close();
					} catch (SQLException e) {
						printStackTrace(e);
					}
				}
			}
		}
		return new TownBlockBank(townId, blockMap);
	}

	public static Town getTownByPlayer(Player p) {
		return getTownByUser(getOrCreateUser(p));
	}

	public static Town getTownByUser(User user) {
		return getTownById(user.getTownId());
	}

	public static Town getTownById(int townId) {
		if (townId == TownType.WILDERNESS.getBit()) {
			return TOWN_WILDERNESS;
		} else if (townId == TownType.SAFEZONE.getBit()) {
			return TOWN_SAFEZONE;
		} else if (townId == TownType.WARZONE.getBit()) {
			return TOWN_WARZONE;
		} else if (townId == TownType.WORLDBORDER.getBit()) {
			return TOWN_WORLDBORDER;
		} else if (townId == TownType.LOADFAIL.getBit()) {
			return TOWN_LOADFAIL;
		} else {
			if (townMap.contains(townId)) {
				return townMap.get(townId);
			} else {
				Town town = TOWN_WILDERNESS;
				Pair<Statement, ResultSet> result = null;
				try {
					result = getInstance().getSQLInstance().doQuery("SELECT * FROM `Towns` WHERE `TOWN_ID` = " + townId);
					if (result.getValue().next()) {
						ResultSet r = result.getValue();
						TownSpawn townSpawn = new TownSpawn(r.getInt("TOWN_SPAWN_X"), r.getInt("TOWN_SPAWN_Y"), r.getInt("TOWN_SPAWN_Z"), r.getDouble("TOWN_SPAWN_PITCH"), r.getDouble("TOWN_SPAWN_YAW"), r.getString("TOWN_SPAWN_WORLD"));
						// Warps, chunks, taxes and exiles.
						town = new Town(townId,
								TownType.NORMAL,
								r.getInt("TOWN_OWNER"),
								getBlockBankByTownId(townId),
								r.getString("TOWN_DESC"),
								townSpawn, r.getLong("TOWN_BANK"),
								r.getInt("TOWN_TAX_PERCENTAGE"),
								r.getLong("TOWN_ABANDONED_SINCE"),
								r.getString("TOWN_NAME"),
								TownOptions.byValue(r.getInt("TOWN_OPTIONS")),
								getWarpsByTownId(townId),
								getChunkIdsByTownId(townId),
								TownTaxOptions.byValue(r.getInt("TOWN_TAX_TYPE")),
								getExileUserIdsByTownId(townId),
								getTownMembersIds(r.getInt("TOWN_ID")), r.getInt("TOWN_CHUNKS_AUTORESELL"));
						if (town.getOwner().getTownId() != town.getId()) {
							town.getOwner().setTownId(town.getTownId()); // Handle UserDB resets.
						}
					}
				} catch (SQLException e) {
					printStackTrace(e);
				} finally {
					if (result != null) {
						if (result.getValue() != null) {
							try {
								result.getValue().close();
							} catch (SQLException e) {
								printStackTrace(e);
							}
						}
						if (result.getKey() != null) {
							try {
								result.getKey().close();
							} catch (SQLException e) {
								printStackTrace(e);
							}
						}
					}
				}
				if (town.getId() != TOWN_LOADFAIL.getId()) {
					townMap.put(town.getId(), town);
				}
				return town;
			}
		}
	}

	private static List<Integer> getTownMembersIds(int townId) {
		List<Integer> members = new ArrayList<>();
		PreparedStatement ex = null;
		ResultSet rs = null;
		try {
			ex = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT `USER_ID` FROM `Users` WHERE `TOWN_ID` = " + townId);
			rs = ex.executeQuery();
			while (rs.next()) {
				members.add(rs.getInt("USER_ID"));
			}
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (ex != null) {
				try {
					ex.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
		return members;
	}

	private static List<Integer> getExileUserIdsByTownId(int townId) {
		List<Integer> exiles = new ArrayList<>();
		PreparedStatement ex = null;
		ResultSet rs = null;
		try {
			ex = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT `USER_ID` FROM `TownExiles` WHERE `TOWN_ID` = " + townId);
			rs = ex.executeQuery();
			while (rs.next()) {
				exiles.add(rs.getInt("USER_ID"));
			}
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (ex != null) {
				try {
					ex.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
		return exiles;
	}

	private static List<Integer> getChunkIdsByTownId(int townId) {
		List<Integer> chunks = new ArrayList<>();
		PreparedStatement ex = null;
		ResultSet rs = null;
		try {
			ex = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT `CHUNK_ID` FROM `Chunks` WHERE `TOWN_ID` = " + townId);
			rs = ex.executeQuery();
			while (rs.next()) {
				chunks.add(rs.getInt("CHUNK_ID"));
			}
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (ex != null) {
				try {
					ex.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
		return chunks;
	}

	public static List<TownWarp> getWarpsByTownId(int townId) {
		List<TownWarp> warps = new ArrayList<>();
		PreparedStatement ex = null;
		ResultSet rs = null;
		try {
			ex = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT * FROM `TownWarps` WHERE `TOWN_ID` = " + townId);
			rs = ex.executeQuery();
			while (rs.next()) {
				warps.add(new TownWarp(rs.getString("TOWN_WARP_NAME"), rs.getInt("TOWN_WARP_X"), rs.getInt("TOWN_WARP_Y"), rs.getInt("TOWN_WARP_Z"), rs.getDouble("TOWN_WARP_PITCH"), rs.getDouble("TOWN_WARP_YAW"), rs.getString("TOWN_WARP_WORLD")));
			}
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (ex != null) {
				try {
					ex.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
		return warps;
	}

	public static void invalidateCache() {
		userMap.clear();
		townMap.clear();
		chunkMap.clear();
	}

	public static Chunk getChunkById(int chunkId) {
		Chunk chunk = null;
		PreparedStatement check = null;
		ResultSet r = null;
		Statement getMembers = null;
		ResultSet membersRS = null;
		try {
			check = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT * FROM `Chunks` WHERE `CHUNK_ID` = " + chunkId);
			r = check.executeQuery();
			if (r.next()) {
				getMembers = getInstance().getSQLInstance().getSQLConnection().createStatement();
				membersRS = getMembers.executeQuery("SELECT * FROM `ChunkMembers` WHERE `CHUNK_ID` = " + chunkId);
				List<Integer> members = new ArrayList<>();
				while (membersRS.next()) {
					members.add(membersRS.getInt("USER_ID"));
				}
				ChunkLocation loc = new ChunkLocation(r.getInt("CHUNK_X"), r.getInt("CHUNK_Z"));
				chunk = new Chunk(chunkId, loc, r.getString("CHUNK_WORLD"), r.getInt("TOWN_ID"), r.getInt("CHUNK_OWNER"), ChunkType.forBit(r.getInt("CHUNK_TYPE")), members, r.getInt("CHUNK_SALE_PRICE"), r.getInt("CHUNK_TERRAFORM_MAX_Y"));
				membersRS.close();
				getMembers.close();
			}
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (membersRS != null) {
				try {
					membersRS.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (getMembers != null) {
				try {
					getMembers.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (r != null) {
				try {
					r.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
			if (check != null) {
				try {
					check.close();
				} catch (SQLException e) {
					printStackTrace(e);
				}
			}
		}
		return chunk;
	}

	public static void deleteTown(Town town) {
		townMap.remove(town.getId());
		try {
			getInstance().getSQLInstance().doUpdate("DELETE FROM `Towns` WHERE `TOWN_ID`=" + town.getId());
		} catch (SQLException e) {
			printStackTrace(e);
		}
	}

	public static Town getTownByName(String townName) {
		Town town = null;
		if (townName.equalsIgnoreCase("wilderness")) {
			town = TOWN_WILDERNESS;
		} else if (townName.equalsIgnoreCase("safezone")) {
			town = TOWN_SAFEZONE;
		} else if (townName.equalsIgnoreCase("warzone")) {
			town = TOWN_WARZONE;
		} else if (townName.equalsIgnoreCase("worldborder")) {
			town = TOWN_WORLDBORDER;
		} else if (townName.equalsIgnoreCase("loadfail")) {
			town = TOWN_LOADFAIL;
		} else {
			Optional<Town> ot = townMap.values().stream().filter(t -> t.getName().equalsIgnoreCase(townName)).findFirst();
			if (ot.isPresent()) {
				town = ot.get();
			} else {
				PreparedStatement ps = null;
				ResultSet result = null;
				try {
					ps = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT * FROM `Towns` WHERE `TOWN_NAME` LIKE ?");
					ps.setString(1, townName);
					result = ps.executeQuery();
					if (result.next()) {
						TownSpawn townSpawn = new TownSpawn(result.getInt("TOWN_SPAWN_X"), result.getInt("TOWN_SPAWN_Y"), result.getInt("TOWN_SPAWN_Z"), result.getDouble("TOWN_SPAWN_PITCH"), result.getDouble("TOWN_SPAWN_YAW"), result.getString("TOWN_SPAWN_WORLD"));
						int townId = result.getInt("TOWN_ID");
						town = new Town(townId,
								TownType.NORMAL,
								result.getInt("TOWN_OWNER"),
								getBlockBankByTownId(townId),
								result.getString("TOWN_DESC"),
								townSpawn, result.getLong("TOWN_BANK"),
								result.getInt("TOWN_TAX_PERCENTAGE"),
								result.getLong("TOWN_ABANDONED_SINCE"),
								result.getString("TOWN_NAME"),
								TownOptions.byValue(result.getInt("TOWN_OPTIONS")),
								getWarpsByTownId(townId),
								getChunkIdsByTownId(townId),
								TownTaxOptions.byValue(result.getInt("TOWN_TAX_TYPE")),
								getExileUserIdsByTownId(townId),
								getTownMembersIds(result.getInt("TOWN_ID")), result.getInt("TOWN_CHUNKS_AUTORESELL"));
					}
				} catch (SQLException e) {
					printStackTrace(e);
				} finally {
					if (result != null) {
						try {
							result.close();
						} catch (SQLException e) {
							printStackTrace(e);
						}
					}
					if (ps != null) {
						try {
							ps.close();
						} catch (SQLException e) {
							printStackTrace(e);
						}
					}
				}
				if (town != null) {
					townMap.put(town.getId(), town);
				}
			}
		}
		return town;
	}

	public static int chunkOwnCount(User user) {
		try {
			return getInstance().getSQLInstance().countQueryResults("SELECT `CHUNK_ID` FROM `Chunks` WHERE `CHUNK_OWNER` = " + user.getUserId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int chunkMemberofCount(User user) {
		try {
			return getInstance().getSQLInstance().countQueryResults("SELECT `CHUNK_ID` FROM `ChunkMembers` WHERE `USER_ID` = " + user.getUserId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void printStackTrace(SQLException e) {
		if (e.getErrorCode() != 1045) {
			e.printStackTrace();
		}
	}

	public static User getUserByName(String name) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getInstance().getSQLInstance().getSQLConnection().prepareStatement("SELECT `USER_ID` FROM `Users` WHERE `USER_NAME` LIKE ?");
			ps.setString(1, name);
			rs = ps.executeQuery();
			if (rs.next()) return getUserById(rs.getInt("USER_ID"));
		} catch (SQLException e) {
			printStackTrace(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return USER_NOBODY;
	}
}
