package co.reborncraft.bungeecordjoin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import java.util.Arrays;
import java.util.Hashtable;

public class BungeecordJoin extends JavaPlugin implements Listener {
	private String txt = "";

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void ipFilterer(AsyncPlayerPreLoginEvent e) {
		if (!isValidOrigin(e.getAddress().getHostAddress())) {
			e.setKickMessage("[Security]\nInvalid origin IP.");
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void auditorReply(ServerListPingEvent e) {
		e.setMaxPlayers((isValidOrigin(e.getAddress().getHostAddress()) ? 1 : 0) | (txt.length() << 1));
		e.setMotd("[Security] Invalid origin.");
	}

	private boolean isValidOrigin(String hostAddress) {
		txt = loadTXTRecord("edge-servers.reborncraft.co");
		return Arrays.stream(txt.split(",\\s*")).anyMatch(hostAddress::equalsIgnoreCase);
	}

	private String loadTXTRecord(String hostName) {
		Hashtable<String, String> env = new Hashtable<>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		String txtRecord = "";
		try {
			Attribute attr = new InitialDirContext(env).getAttributes(hostName, new String[]{"TXT"}).get("TXT");
			if (attr != null) {
				txtRecord = attr.get().toString();
			}
		} catch (NamingException ignored) {
		}
		return txtRecord.replaceAll("^\"|\"$", "");
	}
}
