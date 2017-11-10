package co.reborncraft.syslogin_banmanager.api.objects;

import co.reborncraft.syslogin_banmanager.listeners.AntiBotListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

public class DatacenterIPs {
	private final Map<String, List<Map.Entry<Long, Long>>> hostToIPMap = new HashMap<>();

	public DatacenterIPs() throws IOException {
		long startMillis = System.currentTimeMillis();
		URL url = new URL("https://raw.github.com/client9/ipcat/master/datacenters.csv");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			String line;
			while ((line = rd.readLine()) != null) {
				String[] split = line.replaceAll("\"", "").split(",");
				long begin = 0;
				long end = 0;
				for (String s : split[0].split("\\.")) {
					begin <<= 8;
					begin += Integer.parseUnsignedInt(s);
				}
				for (String s : split[1].split("\\.")) {
					end <<= 8;
					end += Integer.parseUnsignedInt(s);
				}
				if (!hostToIPMap.containsKey(split[2])) {
					hostToIPMap.put(split[2], new ArrayList<>());
				}
				hostToIPMap.get(split[2]).add(new AbstractMap.SimpleEntry<>(begin, end));
			}
		}
		System.out.println("Populating the datacenter IPs database took " + (System.currentTimeMillis() - startMillis) + "ms.");
	}

	private static long inetAton(InetAddress addr) {
		long aton = 0;
		for (byte b : addr.getAddress()) {
			aton <<= 8;
			aton += AntiBotListener.toInt(b);
		}
		return aton;
	}

	public String getHostingProviderName(InetAddress addr) {
		long aton = inetAton(addr);
		Optional<Map.Entry<String, List<Map.Entry<Long, Long>>>> first = hostToIPMap.entrySet().stream()
				.filter(i -> i.getValue().stream().anyMatch(mm -> mm.getKey() <= aton && aton <= mm.getValue()))
				.findAny();
		return first.isPresent() ? first.get().getKey() : null;
	}

	public boolean isHostingProvider(InetAddress addr) {
		long aton = inetAton(addr);
		return hostToIPMap.keySet().stream()
				.flatMap(hostingProviderName -> hostToIPMap.get(hostingProviderName).stream())
				.anyMatch(mm -> mm.getKey() <= aton && aton <= mm.getValue());
	}

}
