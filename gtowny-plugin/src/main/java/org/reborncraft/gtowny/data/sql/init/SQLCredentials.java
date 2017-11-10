package org.reborncraft.gtowny.data.sql.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class SQLCredentials {
	private final String host;
	private final Integer port;
	private final String user;
	private final String pass;

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public SQLCredentials() {
		String[] conf = new String[]{"localhost", "3306", "GTowny", "Failed to load."};
		try (InputStream is = DBCreate.class.getResourceAsStream("/config.yml")) {
			Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			conf = (s.hasNext() ? s.next() : "").split("\r\n|\r|\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		host = conf[0].split(": ")[1];
		port = Integer.valueOf(conf[1].split(": ")[1]);
		user = conf[2].split(": ")[1];
		pass = conf[3].split(": ")[1];
	}
}
