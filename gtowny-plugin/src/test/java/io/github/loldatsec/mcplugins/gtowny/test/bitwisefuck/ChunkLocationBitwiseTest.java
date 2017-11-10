package io.github.loldatsec.mcplugins.gtowny.test.bitwisefuck;

import org.reborncraft.gtowny.data.local.ChunkLocation;

import java.util.ArrayList;
import java.util.List;

public class ChunkLocationBitwiseTest {
	public static void main(String[] args) {
		try {
			List<Integer> muchKek = new ArrayList<>();
			for (int x = -64; x <= 64; x++) {
				for (int z = -64; z <= 64; z++) {
					int i = new ChunkLocation(x, z).hashCode();
					if (muchKek.contains(i)) {
						throw new Exception("Duplicate found: " + i + ": x" + x + ", z:" + z);
					} else {
						muchKek.add(i);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
