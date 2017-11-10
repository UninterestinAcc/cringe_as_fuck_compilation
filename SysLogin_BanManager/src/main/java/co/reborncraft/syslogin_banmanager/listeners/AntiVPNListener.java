package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Inet4AddressBlock;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;

public class AntiVPNListener implements Listener {
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPreLogin(PreLoginEvent e) {
		try {
			Inet4AddressBlock upstream = new Inet4AddressBlock(e.getConnection().getAddress().getAddress());
			if (Arrays.stream(SysLogin_BanManager.getIPWhitelist()).anyMatch(upstream::intersects)) {
				return;
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		String hostingProviderName = SysLogin_BanManager.getInstance().getDatacenterIPs().getHostingProviderName(e.getConnection().getAddress().getAddress());
		if (hostingProviderName != null) {
			e.setCancelled(true);
			e.setCancelReason(Utils.parseIntoComp("\u00a7c[\u00a77Login\u00a7c]\n\u00a7cSorry, we do no allow VPNs.\n\n\u00a73Time now: \u00a7b" + Instant.now() + "\n\u00a76Detected Hosting Provider: \u00a7e" + hostingProviderName));
		}
	}
}
