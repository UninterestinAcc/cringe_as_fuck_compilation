package co.reborncraft.stackperms;

import co.reborncraft.stackperms.api.SavePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StackPermsChatManager implements Listener {
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) { // This completely ignores vault!
		SavePlayer sp = StackPerms.getInterface().getOrCreateSavePlayer(e.getPlayer().getName());
		String prefix = sp.getCalculatedPrefix().replaceAll("&([0-9a-fA-Fk-oK-OrR])", "\u00a7$1");
		String suffix = sp.getCalculatedSuffix().replaceAll("&([0-9a-fA-Fk-oK-OrR])", "\u00a7$1");
		e.setFormat(prefix + " %s" + suffix + "%s");
	}
}
