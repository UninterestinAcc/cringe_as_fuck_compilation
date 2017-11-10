package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.utils.Utils;

import java.util.Arrays;

public class AltMapTest {
	public static void main(String[] args) {
		String[] arr = Arrays.stream(new String[]{"1.2.3.4", "5.6.7.8", "8.9.10.11", "12.13.14.15"}).parallel()
				.map(Utils::loadASNList)
				.map(i -> {
					for (Integer ints : i) {
						System.out.println("F: " + ints);
					}
					return i;
				})
				.flatMap(Arrays::stream)
				.distinct()
				.map(Utils::loadASNName)
				.toArray(String[]::new);
		for (String strs : arr) {
			System.out.println(strs);
		}
	}
}