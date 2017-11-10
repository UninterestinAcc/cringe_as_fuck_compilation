package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SorbsProxyTester {
	static List<String> ips = new ArrayList<>();

	public static void main(String[] args) {
		/*
http
socks
misc
.dnsbl.sorbs.net
		 */
		String[] strs = Utils.readFileWithoutComments("src/test/resource/ipsum.txt");
		for (String str : strs) {
			ips.add(str.split("\\s+")[0]);
		}
	}
}