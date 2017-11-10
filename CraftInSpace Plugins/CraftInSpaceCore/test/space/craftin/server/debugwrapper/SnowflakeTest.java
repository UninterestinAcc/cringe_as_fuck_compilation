/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.server.debugwrapper;

import space.craftin.plugin.utils.Snowflake;

public class SnowflakeTest {
	public static void main(String[] args) {
		try {
			for (short counter = 0; counter < Short.MAX_VALUE; counter++) {
				long snowflake = Snowflake.getServer().generate();
				if (snowflake % 4096 == 0) {
					System.out.println("Rollover: UNIX-" + (snowflake >> 22) + "0ms");
				}
			}
		} catch (InterruptedException | InstantiationException e) {
			e.printStackTrace();
		}
	}
}
