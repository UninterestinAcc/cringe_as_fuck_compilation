package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.syslogin_banmanager.listeners.AntiBotListener;

public class AntibotTest {
	public static void main(String[] args) {
		AntiBotListener abl = new AntiBotListener();
		try {
			for (int i = 0; i < 30; i++) {
				System.out.println(abl.evalJoinCountForLastPeriod(10));
				abl.incrementJoin();
				Thread.sleep(999);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}