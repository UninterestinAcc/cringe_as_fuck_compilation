package org.reborncraft.gtowny.data.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum TownTaxOptions {
	CommonChunks(1, "Must pay an equal fraction of the town's expenses."),
	ShopQuadruple(2, "Shop chunks gets taxed at 4x more than standard rate.");

	private final int bit;
	private final String desc;

	TownTaxOptions(int bit, String desc) {
		this.bit = bit;
		this.desc = desc;
	}

	public int getBit() {
		return bit;
	}

	public String getDescription() {
		return desc;
	}

	public static List<TownTaxOptions> byValue(int bitmask) {
		List<TownTaxOptions> list = new ArrayList<>();
		Arrays.stream(values()).filter(enumeration -> ((bitmask >> enumeration.getBit()) % 2 == 1)).forEach(list::add);
		return list;
	}

	public static int toValue(List<TownTaxOptions> enumerations) {
		AtomicInteger bitmask = new AtomicInteger(0);
		enumerations.forEach(enumeration -> bitmask.addAndGet(1 << enumeration.getBit()));
		return bitmask.get();
	}

	public static TownTaxOptions forBit(int bit) {
		for (TownTaxOptions v : values()) {
			if (v.getBit() == bit) {
				return v;
			}
		}
		return null;
	}

}
