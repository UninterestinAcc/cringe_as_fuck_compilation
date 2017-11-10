package io.github.loldatsec.mcplugins.haloplus.utils;

public class RankupSequence {

	public static long getLongByPos(int pos) {
		long f = 1;
		for (int i = 1; i < pos; i++) {
			f += lo(i);
		}
		return f;
	}

	public static int getPosByLong(long lonq) {
		int pos = 1;
		long f = 1;
		while (true) {
			f += lo(pos);
			if (lonq <= f) {
				break;
			}
			pos++;
		}
		return pos;
	}

	public static long lo(int p) {
		int sf = (int) (p / 1.2 + 1);
		int f = 0;
		f += 2 * sf;
		return (long) (p * f);
	}
}
