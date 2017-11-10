package co.reborncraft.syslogin_banmanager.listeners;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Inet4AddressBlock;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AntiBotListener implements Listener {
	private static final String[] DNSBL_PROVIDERS = {"v4.fullbogons.cymru.com", "dnsbl.sorbs.net", "b.barracudacentral.org", "bl.spamcop.net", "dnsbl-1.uceprotect.net"};
	private static final AtomicBoolean antibotEnabled = new AtomicBoolean(false);
	private static final AtomicBoolean panicEnabled = new AtomicBoolean(false);
	private static final List<Inet4AddressBlock> botIPList = new ArrayList<>();
	private static final List<String> antibotLogger = new ArrayList<>();
	private final TextComponent antibotBlockMessage = Utils.buildTextComponent("[Antibot] Login blocked, please try again in a few minutes.", ChatColor.RED);
	private final ConcurrentMap<Long, AtomicInteger> joins = new ConcurrentHashMap<>();

	public static void toggleAntibot() {
		setAntibotEnabled(!isAntibotEnabled());
	}

	public static boolean isAntibotEnabled() {
		return antibotEnabled.get();
	}

	public static void setAntibotEnabled(boolean enable) {
		antibotEnabled.set(enable);
		if (!enable) {
			panicEnabled.set(false);
			File file = new File("Bot attack " + Instant.now() + ".txt");
			try {
				if (!file.exists()) {
					if (!file.createNewFile()) {
						return;
					}
				}
				try (FileOutputStream fos = new FileOutputStream(file, true)) {
					for (String s : antibotLogger) {
						fos.write(s.getBytes(StandardCharsets.UTF_8));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		EventLogger.logAntibotToggle();
	}

	public static void toggleAntibotPanic() {
		panicEnabled.set(!panicEnabled.get() && isAntibotEnabled());
		botIPList.clear();
	}

	public static boolean isPanicEnabled() {
		return panicEnabled.get();
	}

	public static short toInt(byte b) {
		short s = b;
		return s < 0 ? (short) (256 + s) : s;
	}

	public void gc() {
		long tLimit = (System.currentTimeMillis() / 1000) - 10;
		joins.keySet().stream().filter(time -> time < tLimit).forEach(joins::remove);
	}

	public void incrementJoin() {
		long t = System.currentTimeMillis() / 1000;
		if (joins.containsKey(t)) {
			joins.get(t).incrementAndGet();
		} else {
			joins.put(t, new AtomicInteger(1));
		}
	}

	public long evalJoinCountForLastPeriod(int seconds) {
		long t = System.currentTimeMillis() / 1000;
		AtomicLong l = new AtomicLong(0);
		joins.keySet().stream()
				.filter(time -> time < t && time > t - seconds)
				.map(joins::get)
				.forEach(i -> l.addAndGet(i.get()));
		return l.get();
	}

	private void toggleAntibotIfNeeded() {
		if (antibotEnabled.get()) {
			if (evalJoinCountForLastPeriod(10) < 5) {
				setAntibotEnabled(false);
			}
		} else {
			SysLogin_BanManager slbm_inst = SysLogin_BanManager.getInstance();
			if (ManagementFactory.getRuntimeMXBean().getUptime() > 60000) {
				int lpc = slbm_inst.getLoggedInPlayersCount();
				if (evalJoinCountForLastPeriod(10) > (lpc > 100 ? lpc / 4 : 25)) {
					setAntibotEnabled(true);
					slbm_inst.getProxy().getLogger().warning("Bot attack detected, activating antibot.");
					if (slbm_inst.getProxy().getPlayers().size() - slbm_inst.getLoggedInPlayersCount() > 15 && !isPanicEnabled()) {
						toggleAntibotPanic();
					}
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onPreLogin(PreLoginEvent e) {
		toggleAntibotIfNeeded();
		if (isPanicEnabled()) {
			e.setCancelled(true);
			e.setCancelReason(antibotBlockMessage);
		} else if (antibotEnabled.get() && !e.isCancelled()) {
			try {
				Inet4AddressBlock block = new Inet4AddressBlock(e.getConnection().getAddress().getAddress().getHostAddress().replaceAll("\\.\\d+$", ".0/24"));
				if (botIPList.stream().anyMatch(block::intersects)) {
					antibotBlock(e);
				} else {
					botIPList.add(block);
					byte[] addr = e.getConnection().getAddress().getAddress().getAddress();
					String prefix = toInt(addr[3]) + "." + toInt(addr[2]) + "." + toInt(addr[1]) + "." + toInt(addr[0]) + ".";
					if (Arrays.stream(DNSBL_PROVIDERS).parallel().anyMatch(provider -> {
						try {
							InetAddress.getByName(prefix + provider);
							return true;
						} catch (UnknownHostException ignored) {
							return false;
						}
					})) {
						antibotBlock(e);
					}
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		incrementJoin();
	}

	private void antibotBlock(PreLoginEvent e) {
		e.isCancelled();
		e.setCancelReason(antibotBlockMessage);
		antibotLogger.add(Instant.now() + ": " + e.getConnection().getName() + " from " + e.getConnection().getAddress());
	}
}
