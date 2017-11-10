package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.utils.Utils;

import javax.xml.bind.DatatypeConverter;

public class TXTTest {
	public static void main(String[] args) {
		final String edges = Utils.loadTXTRecord("edge-servers.reborncraft.co")[0];
		System.out.println(edges);
		System.out.println(DatatypeConverter.printHexBinary(edges.getBytes()));
	}
}