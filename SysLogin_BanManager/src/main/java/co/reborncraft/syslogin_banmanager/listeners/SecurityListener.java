package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class SecurityListener implements Listener {
	public static final short CHAR_PER_SEC_CAP = 14; // 840 CPM, pretty impossible said noone ever

	private ConcurrentMap<ProxiedPlayer, AtomicInteger> violationCount = new ConcurrentHashMap<>();

	public SecurityListener() {
		SysLogin_BanManager.getInstance().getProxy().getScheduler().runAsync(SysLogin_BanManager.getInstance(), () -> {
			violationCount.keySet().stream()
					.filter(pp -> pp == null || !pp.isConnected())
					.forEach(violationCount::remove);
		});
	}

	@EventHandler
	public void onBadUsernameJoin(PreLoginEvent e) {
		String name = e.getConnection().getName();
		if (Arrays.stream(SysLogin_BanManager.getBadUsernames()).anyMatch(name::matches)) {
			e.setCancelled(true);
			e.setCancelReason(Utils.buildTextComponent("Bot username detected.", ChatColor.RED));
			SysLogin_BanManager.getInstance().getPunishmentCache().punish(SysLogin_BanManager.getInstance().getProxy().getConsole(), new Punishment(
					Punishment.PunishmentTarget.IP,
					Punishment.PunishmentType.BAN,
					e.getConnection().getAddress().getAddress().getHostAddress(),
					"[AUTO] Bot attack attempt. Flag: [" + name + "]",
					System.currentTimeMillis() / 1000,
					0,
					"AntiBot"
			));
		}
	}

	@EventHandler
	public void onChat(ChatEvent e) {
		Connection sender = e.getSender();
		if (sender instanceof ProxiedPlayer && !Permissions.permissed((CommandSender) sender, Permissions.SECURITY_BYPASS)) {
			e.setMessage(e.getMessage().replaceAll("\\s+", " "));
			final String msg = e.getMessage().toLowerCase();
			final ProxiedPlayer p = (ProxiedPlayer) sender;
			if (msg.contains("\u00a7")) {
				e.setCancelled(true);
				p.disconnect(Utils.buildTextComponent("You cannot send the colour control character.", ChatColor.RED));
				return;
			}
			if (msg.replaceAll("\\s", "").isEmpty()) {
				e.setCancelled(true);
			}
			if (!e.isCancelled() && msg.length() > SysLogin_BanManager.getInstance().calculateLastChat(p) * CHAR_PER_SEC_CAP) {
				e.setCancelled(true);
				p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SEC_MSG_TOO_QUICK.replaceAll("\\{wait}", "" + (msg.length() / CHAR_PER_SEC_CAP + 1))));
			}
			if (!e.isCancelled() && !msg.startsWith("/login ") && Arrays.stream(SysLogin_BanManager.getMessageFilters()).anyMatch(filter -> Pattern.compile(filter).matcher(msg).find())) {
				e.setCancelled(true);
				p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SEC_BLOCKED_MSG));
			}
			if (!e.isCancelled() && msg.startsWith("/")) {
				final String[] comps = msg.split("\\s+");
				if (comps.length >= 1) {
					String inputCmd = comps[0].substring(1);
					if (Arrays.stream(SysLogin_BanManager.getBannedCommands()).anyMatch(inputCmd::matches)) {
						e.setCancelled(true);
						p.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SEC_BLOCKED_CMD));
					}
				}
			}
			if (e.isCancelled()) {
				int count = 1;
				if (violationCount.containsKey(sender)) {
					count = violationCount.get(sender).getAndIncrement();
				} else {
					violationCount.put((ProxiedPlayer) sender, new AtomicInteger(1));
				}
				if (count < 3) return;
				sender.disconnect(new Punishment(
						Punishment.PunishmentTarget.USERNAME,
						Punishment.PunishmentType.KICK,
						((ProxiedPlayer) sender).getName(),
						"You have breached our chat policy 4 times in a row.",
						System.currentTimeMillis() / 1000,
						0,
						"ChatManager").toKickTextComponent());
			} else if (violationCount.containsKey(sender)) {
				violationCount.remove(sender);
			}
		}
	}

	@EventHandler
	public void onTab(TabCompleteEvent e) {
		if (e.getSender() instanceof ProxiedPlayer && !Permissions.permissed((CommandSender) e.getSender(), Permissions.SECURITY_BYPASS)) {
			final String msg = e.getCursor();
			if (msg.equals("/")) {
				e.setCancelled(true);
			}
		}
	}
}
