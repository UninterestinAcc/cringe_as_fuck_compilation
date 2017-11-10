package io.github.loldatsec.mcplugs.antiads;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Match {

	public List<String> advmanager = new ArrayList<String>();

	public void advertisement(final String str, final String pn) {
		Thread aa = new Thread() {

			@Override
			public void run() {
				for (String potAddr : dotRemover(str.replaceAll("[^a-zA-Z0-9\\-\\. ]+", ".").toLowerCase()).split(" ")) {
					if (potAddr.contains(".") && !potAddr.contains("reborncraft") && !potAddr.startsWith(".")) {
						try {
							InetAddress.getByName(potAddr);
							advmanager.add(pn);
						} catch (UnknownHostException uhe) {
						}
					}
				}
				for (String potAddr : dotRemover(str.replaceAll("[^a-zA-Z0-9\\-\\.]+", ".").toLowerCase()).split(".")) {
					if (!potAddr.contains("reborncraft")) {
						try {
							InetAddress.getByName(potAddr);
							advmanager.add(pn);
						} catch (UnknownHostException uhe) {
						}
					}
				}
			}
		};
		aa.run();
	}

	public String dotRemover(String str) {
		for (int a = 2; a <= 100; a++) {
			if (str.contains("..")) {
				str.replaceAll("\\.\\.", ".");
			}
		}
		return str;
	}
}
