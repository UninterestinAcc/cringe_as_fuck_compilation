package org.reborncraft.gtowny.data.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum TownPermissions {
	Member(0, "Member permission (buy, sell, etc.)"),
	Claim(1, "Claim/unclaim land for the town."),
	SetWarp(2, "Create/delete warps for the town."),
	Terraform(3, "Use the terraformer."),
	LandManagement(4, "Own/unown/sell someone's piece of land."),
	ModifyChunkType(5, "Change chunk types."),
	ManageTownMembers(6, "Ability to invite, kick and exile members."),
	ModifyTownOptions(7, "Change the town's standard & tax options. As well as the town description."),
	SetSpawn(8, "Set the town's spawn."),
	TownBuild(10, "Build anywhere in the town."),
	BankWithdraw(11, "Withdraw money/blocks from town bank."), // Future TODO Feature
	ManageRanks(12, "Create and modify town ranks."), // TODO Future Feature
	TownAdmin(13, "Can modify other's ranks.");
	private final int bit;
	private final String desc;

	TownPermissions(int bit, String desc) {
		this.bit = bit;
		this.desc = desc;
	}

	public int getBit() {
		return bit;
	}

	public String getDescription() {
		return desc;
	}

	public static List<TownPermissions> byValue(int bitmask) {
		List<TownPermissions> list = new ArrayList<>();
		Arrays.stream(values()).filter(enumeration -> ((bitmask >> enumeration.getBit()) % 2 == 1)).forEach(list::add);
		return list;
	}

	public static int toValue(List<TownPermissions> enumerations) {
		AtomicInteger bitmask = new AtomicInteger(0);
		enumerations.forEach(enumeration -> bitmask.addAndGet(1 << enumeration.getBit()));
		return bitmask.get();
	}

	public static TownPermissions forBit(int bit) {
		for (TownPermissions v : values()) {
			if (v.getBit() == bit) {
				return v;
			}
		}
		return null;
	}

}
