package org.reborncraft.gtowny.data.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum ChunkType {
	Normal(1, "Normal chunk, common and uninteresting."),
	Shop(2, "Shop chunk, is able to have buy/sell chests."),
	Consulate(3, "Non-town members can claim."),
	Quarry(4, "All mined blocks goes straight to the town block bank.");

	private final int bit;
	private final String desc;

	ChunkType(int bit, String desc) {
		this.bit = bit;
		this.desc = desc;
	}

	public int getBit() {
		return bit;
	}

	public String getDescription() {
		return desc;
	}

	public static List<ChunkType> byValue(int bitmask) {
		List<ChunkType> list = new ArrayList<>();
		Arrays.stream(values()).filter(enumeration -> ((bitmask >> enumeration.getBit()) % 2 == 1)).forEach(list::add);
		return list;
	}

	public static int toValue(List<ChunkType> enumerations) {
		AtomicInteger bitmask = new AtomicInteger(0);
		enumerations.forEach(enumeration -> bitmask.addAndGet(1 << enumeration.getBit()));
		return bitmask.get();
	}

	public static ChunkType forBit(int bit) {
		for (ChunkType v : values()) {
			if (v.getBit() == bit) {
				return v;
			}
		}
		return ChunkType.Normal;
	}

}
