package co.reborncraft.syslogin_banmanager.test;

import co.reborncraft.syslogin_banmanager.api.objects.Permissions;

public class PermissionsTest {
	public static void main(String[] args) {
		System.out.println(Permissions.has(Permissions.HELPER, 3));

	}
}