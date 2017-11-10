package io.github.loldatsec.mcplugins.staffmode;

public class Info {

	public long hits = 0;
	public long miss = 0;

	public double accuracy() {
		return (double) (hits / hits + miss);
	}
}
