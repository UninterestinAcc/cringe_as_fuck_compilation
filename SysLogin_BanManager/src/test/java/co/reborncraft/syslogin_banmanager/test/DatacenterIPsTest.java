package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.syslogin_banmanager.api.objects.DatacenterIPs;

import java.io.IOException;
import java.net.InetAddress;

public class DatacenterIPsTest {
	public static void main(String[] args) {
		try {
			DatacenterIPs dcip = new DatacenterIPs();
			long ctm = System.currentTimeMillis();
			System.out.println(dcip.getHostingProviderName(InetAddress.getByName("66.98.128.0")));
			System.out.println("Took: " + (System.currentTimeMillis() - ctm) + "ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}