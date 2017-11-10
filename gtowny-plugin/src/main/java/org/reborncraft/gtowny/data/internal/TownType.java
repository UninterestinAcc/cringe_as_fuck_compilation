package org.reborncraft.gtowny.data.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum TownType {
	NORMAL(0, "Normal, town-claimed land."),
	WILDERNESS(-1, "Unclaimed land"),
	SAFEZONE(-2, "Claimed by server as safezone."),
	WARZONE(-3, "Claimed by server as warzone."),
	WORLDBORDER(-4, "Cannot enter or claim."),
	LOADFAIL(-5, "Fallback town when initialization fails, cannot build or claim.");

	private final int bit;
	private final String desc;

	TownType(int bit, String desc) {
		this.bit = bit;
		this.desc = desc;
	}

	public int getBit() {
		return bit;
	}

	public String getDescription() {
		return desc;
	}

	public static List<TownType> byValue(int bitmask) {
		List<TownType> list = new ArrayList<>();
		Arrays.stream(values()).filter(enumeration -> ((bitmask >> enumeration.getBit()) % 2 == 1)).forEach(list::add);
		return list;
	}

	public static int toValue(List<TownType> enumerations) {
		AtomicInteger bitmask = new AtomicInteger(0);
		enumerations.forEach(enumeration -> bitmask.addAndGet(1 << enumeration.getBit()));
		return bitmask.get();
	}

	public static TownType forBit(int bit) {
		for (TownType v : values()) {
			if (v.getBit() == bit) {
				return v;
			}
		}
		return TownType.NORMAL;
	}

}
