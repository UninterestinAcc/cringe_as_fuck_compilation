package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.syslogin_banmanager.listeners.AntiBotListener;

public class ByteValueTest {
	public static void main(String[] args) {
		for (int b = 0; b <= 0xFF; b++) {
			System.out.println(AntiBotListener.toInt((byte) b));
			if (b > 0xFF) {
				return;
			}
		}
	}
}