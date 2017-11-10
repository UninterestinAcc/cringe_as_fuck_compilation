package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.syslogin_banmanager.api.objects.Inet4AddressBlock;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

public class Inet4AddressBlockTest {
	public static void main(String[] args) {
		try {
			Inet4AddressBlock inet4AddressBlock = new Inet4AddressBlock("127.0.0.0/8");
			System.out.println(inet4AddressBlock.minimum().getHostAddress());
			System.out.println(inet4AddressBlock.maximum().getHostAddress());
			System.out.println(inet4AddressBlock.matches(InetAddress.getByName("127.0.0.0")));
			System.out.println(inet4AddressBlock.matches(InetAddress.getByName("127.0.0.1")));
			System.out.println(inet4AddressBlock.matches(InetAddress.getByName("127.255.255.255")));
			System.out.println(inet4AddressBlock.matches(InetAddress.getByName("128.0.0.0")));
			System.out.println(inet4AddressBlock.matches(InetAddress.getByName("126.0.0.0")));
			System.out.println(inet4AddressBlock.matches(InetAddress.getByName("126.0.0.1")));
			System.out.println(inet4AddressBlock.isSubBlockOf(new Inet4AddressBlock("127.0.0.0/8")));
			System.out.println(inet4AddressBlock.isSubBlockOf(new Inet4AddressBlock("127.0.0.0/7")));
			System.out.println(inet4AddressBlock.isSubBlockOf(new Inet4AddressBlock("127.0.0.0/9")));
			inet4AddressBlock = new Inet4AddressBlock("127.0.0.1/32");
			System.out.println(inet4AddressBlock.matches(InetAddress.getByName("127.0.0.1")));
		} catch (ParseException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
}