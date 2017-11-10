/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.utils;

import java.util.Map;

/**
 * A Java/CraftInSpace implementation of <a href="https://github.com/twitter/snowflake/tree/updated_deps">Twitter snowflake.</a>
 * <p>
 * Specs: Can generate 100*4096 data points per second per machine.
 * </p>
 * <p>
 * The snowflake consists of:
 * > Time - 41 bits - 10ms precision - gives us about 696.82 years from the start of the unix epoch.
 * > Configured machine id - 10 bits - gives us up to 1024 machines
 * > Sequence number - 12 bits - rolls over every 4096 per machine (with protection to avoid rollover in the same ms)
 * </p>
 */
public final class Snowflake { // TODO Move to a database/dedicated plugin.
	private static final String HOSTNAME_REGEX = "^[a-zA-Z_\\-.]+([0-9]+)$";

	private static Snowflake instance;
	private final short machineID;
	private long lastRollover = 0;
	private long lastGenTime = 0;
	private short sequence = 0; // 0 - 4095 for 12 bits.

	private Snowflake() throws InstantiationException {
		instance = this;

		String hostname;
		Map<String, String> env = System.getenv();
		if (env.containsKey("COMPUTERNAME")) {
			hostname = env.get("COMPUTERNAME");
		} else if (env.containsKey("HOSTNAME")) {
			hostname = env.get("HOSTNAME"); // On linux this requires export HOSTNAME in .bashrc
		} else {
			throw new InstantiationException("This computer's name can't be detected.");
		}
		if (!hostname.matches(HOSTNAME_REGEX)) {
			throw new InstantiationException("This computer isn't named properly with an ID (a number) at the end, please rectify. Regex: " + HOSTNAME_REGEX);
		}
		machineID = Short.parseShort(hostname.replaceAll(HOSTNAME_REGEX, "$1"));
		if (machineID > 1023 || machineID < 0) {
			throw new InstantiationException("This computer's machine ID is greater than 1023 or smaller than 0.");
		}
	}

	/**
	 * Gets or creates the snowflake server for this JVM.
	 *
	 * @return Snowflake server.
	 * @throws InstantiationException When the hostname is invalid.
	 */
	public static Snowflake getServer() throws InstantiationException {
		return instance != null ? instance : new Snowflake();
	}

	/**
	 * Generates the snowflake.
	 *
	 * @return A uniquely-identifiable snowflake.
	 * @throws InterruptedException Will sleep for rollover protection and when the system time goes backwards.
	 */
	public synchronized long generate() throws InterruptedException {
		while (lastGenTime > getTime()) {
			Thread.sleep(1);
		}
		if (sequence >= 4096) {
			while (lastRollover >= getTime()) {
				Thread.sleep(1);
			}
			lastRollover = getTime();
			sequence = 0;
		}

		lastGenTime = getTime();

		long output = 0;
		output += lastGenTime << 22;
		output += machineID << 12;
		output += sequence++;
		return output;
	}

	private long getTime() {
		return System.currentTimeMillis() / 10;
	}
}
