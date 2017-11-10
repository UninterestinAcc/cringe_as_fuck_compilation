package co.reborncraft.syslogin_banmanager.api.objects;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class VoteReminderManager {
	private final ConcurrentMap<String, Long> reminds = new ConcurrentHashMap<>();

	public void gc() {
		reminds.keySet().removeAll(reminds.keySet().stream()
				.filter(p -> SysLogin_BanManager.getInstance().getPlayer(p) == null)
				.collect(Collectors.toList()));
	}

	public void remindEveryone() {
		final long oneDayAgo = (System.currentTimeMillis() / 1000 - 86400);
		reminds.forEach((pName, lastVoteTime) -> {
			ProxiedPlayer p = SysLogin_BanManager.getInstance().getPlayer(pName);
			if (p != null && p.isConnected() && lastVoteTime < oneDayAgo) {
				TextComponent comp = Utils.parseIntoComp("\u00a7b\u00a7lHello, " + p.getName() + "! " +
						"\u00a7aWe noticed you " +
						(
								lastVoteTime == 0 ?
										"\u00a7cnever voted\u00a7a" :
										"haven't voted in \u00a7e" + Utils.secondsToString((System.currentTimeMillis() / 1000) - lastVoteTime) + "\u00a7a"
						) + ". Have you considered voting? " +
						"Voting for us on the server lists helps support us getting more players and also gives you rewards like items and ranks over time. " +
						"You can easily vote by following the instructions through the \u00a76/vote\u00a7a command or by clicking this message. " +
						"\u00a7bThankyou."
				);

				comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote"));
				comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{Utils.buildTextComponent("Click me to list all the vote links.", ChatColor.GOLD)}));

				p.sendMessage(comp);
			}
		});
	}

	public void setVoteTime(ProxiedPlayer p, Timestamp time) {
		setVoteTime(p.getName(), time == null ? 0 : time.getTime() / 1000);
	}

	public void setVoteTime(String playerName, long epochSecond) {
		ProxiedPlayer p = SysLogin_BanManager.getInstance().getPlayer(playerName);
		if (p != null) {
			reminds.put(p.getName(), epochSecond);
		}
	}
}
