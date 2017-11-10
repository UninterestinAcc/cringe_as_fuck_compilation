package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.cloudcord.binds.CloudBungee;
import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Punishment;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EventLogger implements Listener {
	public static void successfulLogin(String p) {
		SysLogin_BanManager.getInstance().getProxy().getConsole().sendMessage(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + " \u00a7dSuccessful login: \u00a7a" + p));
	}

	public static void logPunishment(Punishment punishment) {
		SysLogin_BanManager.getInstance().getProxy().getConsole().sendMessage(Utils.parseIntoComp(punishment.toString()));
		if (punishment.getPunishmentType() == Punishment.PunishmentType.KICK) {
			CloudBungee.getInstance().getAdapter().sendMessage("Reborncraft", "Kick", punishment.getTarget() + "|" + punishment.getBy() + "|" + punishment.getReason());
		}
	}

	public static void logAntibotToggle() {
		SysLogin_BanManager.getInstance().getProxy().getConsole().sendMessage(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aAntibot was toggled \u00a7d" + (AntiBotListener.isAntibotEnabled() ? "on" : "off") + "\u00a7a."));
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(ChatEvent e) {
		if (e.getSender() instanceof ProxiedPlayer) {
			SysLogin_BanManager.getInstance().getProxy().getConsole().sendMessage(Utils.parseIntoComp("\u00a7e[\u00a76CHAT\u00a7e] " +
					(e.isCancelled() ? "\u00a7c[\u00a74CANCELLED\u00a7c] " : "") +
					"\u00a7b[\u00a73" + ((ProxiedPlayer) e.getSender()).getServer().getInfo().getName() + "\u00a7b] \u00a7a" +
					((ProxiedPlayer) e.getSender()).getName() + "\u00a72> \u00a7d" +
					e.getMessage()
			));
		} else {
			System.out.println(e);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onLogin(PreLoginEvent e) {
		String hostAddr = e.getConnection().getAddress().getAddress().getHostAddress();
		if (AntiBotListener.isAntibotEnabled()) {
			SysLogin_BanManager.getInstance().getProxy().getLogger().warning(e.getConnection().getName() + " joined from " + hostAddr + " while antibot was enabled.");
		} else if (AntiBotListener.isPanicEnabled()) {
			SysLogin_BanManager.getInstance().getProxy().getLogger().warning(e.getConnection().getName() + " joined from " + hostAddr + " while antibot-panic was enabled.");
		} else {
			Integer[] asns = Arrays.stream(Utils.loadASNList(hostAddr)).distinct().toArray(Integer[]::new);
			SysLogin_BanManager.getInstance().getProxy().getConsole().sendMessage(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_PREFIX + (e.isCancelled() ? " \u00a7c[\u00a74DENIED\u00a7c]" : "") + " \u00a7b[\u00a73" +
					hostAddr + " AS" + Arrays.stream(asns).map(String::valueOf).collect(Collectors.joining(", AS")) + (asns.length >= 1 ? " " + Utils.loadASNName(asns[0]) : "") + "\u00a7b] \u00a7dlogged in with the name \u00a7a" +
					e.getConnection().getName()
			));
		}
	}
}
