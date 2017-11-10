package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.utils.Utils;

public class TimeParserTest {
	public static void main(String[] args) {
		for (String ts : new String[]{"1d", "2h", "69x", "3h2m1s", "1", "1s1", "3s2", "s69", "test"}) {
			System.out.println(Utils.parseTime(ts));
		}
	}
}