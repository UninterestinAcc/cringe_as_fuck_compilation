package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PingListener implements Listener {
	public PingListener() {
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onJoin(ProxyPingEvent e) {
		String msg = SysLogin_BanManager.getInstance().getPingMessage().replaceAll("&([0-9a-fA-Fk-oK-Or])", "\u00a7$1");
		for (String srvVar : SysLogin_BanManager.getInstance().getServerVariables().keySet()) {
			msg = msg.replaceAll("\\{" + srvVar + "}", SysLogin_BanManager.getInstance().getServerVariables().get(srvVar));
		}
		e.getResponse().setDescriptionComponent(Utils.parseIntoComp(msg));
	}
}
