package io.github.loldatsec.mcplugins.gtowny.test.bitwisefuck;

import org.reborncraft.gtowny.data.internal.TownOptions;

import java.util.Arrays;
import java.util.List;

public class EnumEncoding {
	public static void main(String[] args) {
		List<TownOptions> test = Arrays.asList(TownOptions.Fire, TownOptions.ExileEveryone);
		test.forEach(System.out::println);
		
		TownOptions.byValue(TownOptions.toValue(test)).forEach(System.out::println);
	}
}
