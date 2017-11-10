package org.reborncraft.gtowny.data.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum TownOptions {
	PvP(1, "Toggles PvP inside chumk."),
	Fire(2, "Toggles lighting fires / firespread."),
	Explosions(3, "Toggles explosions inside town."),
	MobSpawning(4, "Toggles mobspawning inside town."),
	ExileEveryone(5, "Prevents non town members from entering the town.");

	private final int bit;
	private final String desc;

	TownOptions(int bit, String desc) {
		this.bit = bit;
		this.desc = desc;
	}

	public int getBit() {
		return bit;
	}

	public String getDescription() {
		return desc;
	}

	public static List<TownOptions> byValue(int bitmask) {
		List<TownOptions> list = new ArrayList<>();
		Arrays.stream(values()).filter(enumeration -> ((bitmask >> enumeration.getBit()) % 2 == 1)).forEach(list::add);
		return list;
	}

	public static int toValue(List<TownOptions> enumerations) {
		AtomicInteger bitmask = new AtomicInteger(0);
		enumerations.forEach(enumeration -> bitmask.addAndGet(1 << enumeration.getBit()));
		return bitmask.get();
	}

	public static TownOptions forBit(int bit) {
		for (TownOptions v : values()) {
			if (v.getBit() == bit) {
				return v;
			}
		}
		return null;
	}

}
