package co.reborncraft.syslogin_banmanager.api.objects;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.banmanager.PushPunishmentFuture;
import co.reborncraft.syslogin_banmanager.api.futures.banmanager.PushPunishmentRemoveFuture;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class PunishmentCache {
	private final ConcurrentMap<String, Punishment> usernameBans, usernameMutes, ipBans, ispBans;

	public PunishmentCache(ConcurrentMap<String, Punishment> usernameBans, ConcurrentMap<String, Punishment> usernameMutes, ConcurrentMap<String, Punishment> ipBans, ConcurrentMap<String, Punishment> ispBans) {
		this.usernameBans = usernameBans;
		this.usernameMutes = usernameMutes;
		this.ipBans = ipBans;
		this.ispBans = ispBans;
	}

	public Punishment getBanReason(ProxiedPlayer p) {
		return getBanReason(p.getName(), p.getAddress().getAddress());
	}

	public Punishment getBanReason(String name, InetAddress address) {
		if (isBanned(name, address)) {
			if (checkIPBan(address)) {
				Optional<String> first = ipBans.keySet().stream()
						.filter(block -> {
							try {
								return new Inet4AddressBlock(block).matches(address);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							return false;
						})
						.findFirst();
				return first.isPresent() ? ipBans.get(first.get()) : null;
			} else if (checkUsernameBan(name)) {
				return usernameBans.get(name.toLowerCase());
			} else if (checkISPBan(address)) {
				Optional<Integer> first = Arrays.stream(Utils.loadASNList(address.getHostAddress())).filter(this::checkISPBan).findFirst();
				if (first.isPresent()) {
					return ispBans.get("" + first.get());
				}
			}
		}
		return null;
	}

	public Punishment getMuteReason(ProxiedPlayer player) {
		return getMuteReason(player.getName().toLowerCase());
	}

	public Punishment getMuteReason(String name) {
		return checkUsernameMute(name) ? usernameMutes.get(name) : null;
	}

	public boolean isBanned(ProxiedPlayer p) {
		return isBanned(p.getName(), p.getAddress().getAddress());
	}

	public boolean isBanned(String name, InetAddress address) {
		return isBanned(name) || isBanned(address);
	}

	public boolean isBanned(String name) {
		return checkUsernameBan(name);
	}

	public boolean isBanned(InetAddress address) {
		return checkIPBan(address) || checkISPBan(address);
	}

	public boolean isMuted(ProxiedPlayer player) {
		return isMuted(player.getName());
	}

	public boolean isMuted(String player) {
		return checkUsernameMute(player);
	}

	private boolean checkUsernameBan(String name) {
		return usernameBans.containsKey(name.toLowerCase()) && checkExpiry(usernameBans.get(name.toLowerCase()));
	}

	private boolean checkUsernameMute(String name) {
		return usernameMutes.containsKey(name.toLowerCase()) && checkExpiry(usernameMutes.get(name.toLowerCase()));
	}

	private boolean checkIPBan(Inet4AddressBlock addressBlock) {
		return !checkIPWhitelisted(addressBlock) && ipBans.keySet().stream()
				.filter(ip -> checkExpiry(ipBans.get(ip)))
				.anyMatch(ip -> {
					try {
						return new Inet4AddressBlock(ip).intersects(addressBlock);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return false;
				});
	}

	private boolean checkIPRangeBanCompletelyCovered(Inet4AddressBlock addressBlock) {
		return ipBans.keySet().stream()
				.filter(ip -> checkExpiry(ipBans.get(ip)))
				.anyMatch(ip -> {
					try {
						return addressBlock.isSubBlockOf(new Inet4AddressBlock(ip));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return false;
				});
	}

	private boolean checkIPBan(InetAddress address) {
		try {
			return checkIPBan(new Inet4AddressBlock(address));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @return true if it's not expired, false otherwise.
	 */
	private boolean checkExpiry(Punishment punishment) {
		return punishment.getEndTime() <= 0 || punishment.getEndTime() > System.currentTimeMillis() / 1000;
	}

	private boolean checkISPBan(String target) throws UnknownHostException {
		if (target.matches("[0-9]+")) {
			return checkISPBan(Integer.parseInt(target));
		} else {
			return checkISPBan(InetAddress.getByName(target));
		}
	}

	private boolean checkISPBan(InetAddress address) {
		return !checkIPWhitelisted(address) && Arrays.stream(Utils.loadASNList(address)).anyMatch(this::checkISPBan);
	}

	private boolean checkIPWhitelisted(InetAddress address) {
		try {
			return checkIPWhitelisted(new Inet4AddressBlock(address));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkIPWhitelisted(Inet4AddressBlock addressBlock) {
		return Arrays.stream(SysLogin_BanManager.getIPWhitelist())
				.anyMatch(i4ab -> i4ab.intersects(addressBlock));
	}

	private boolean checkISPBan(int asn) {
		return ispBans.containsKey("" + asn) && checkExpiry(ispBans.get("" + asn));
	}

	public boolean unban(CommandSender staff, String search) {
		search = search.toLowerCase();
		String asn = search.replaceAll("^as", "");
		Punishment punishment;
		if (ipBans.containsKey(search)) {
			punishment = ipBans.get(search);
			ipBans.remove(search);
		} else if (usernameBans.containsKey(search)) {
			punishment = usernameBans.get(search);
			usernameBans.remove(search);
		} else if (ispBans.containsKey(search)) {
			punishment = ispBans.get(search);
			ispBans.remove(search);
		} else if (search.matches("^as([0-9]+)$") && ispBans.containsKey(asn)) {
			punishment = ispBans.get(asn);
			ispBans.remove(asn);
		} else {
			return false;
		}
		SysLogin_BanManager.getInstance().scheduleFuture(new PushPunishmentRemoveFuture(staff, punishment));
		return true;
	}

	public boolean unmute(CommandSender staff, String search) {
		search = search.toLowerCase();
		if (usernameMutes.containsKey(search)) {
			SysLogin_BanManager.getInstance().scheduleFuture(new PushPunishmentRemoveFuture(staff, usernameMutes.get(search)));
			usernameMutes.remove(search);
			return true;
		} else {
			return false;
		}
	}

	public boolean punish(CommandSender staff, Punishment punishment) {
		if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN) {
			if (punishment.getTargetType() == Punishment.PunishmentTarget.USERNAME) {
				if (checkUsernameBan(punishment.getTarget())) {
					return false;
				}
			} else try {
				if (punishment.getTargetType() == Punishment.PunishmentTarget.IP) {
					if (checkIPRangeBanCompletelyCovered(new Inet4AddressBlock(punishment.getTarget()))) {
						return false;
					}
				} else if (punishment.getTargetType() == Punishment.PunishmentTarget.ISP) {
					if (checkISPBan(punishment.getTarget())) {
						return false;
					}
				}
			} catch (UnknownHostException | ParseException e) {
				e.printStackTrace();
			}
		} else if (punishment.getPunishmentType() == Punishment.PunishmentType.MUTE) {
			if (checkUsernameMute(punishment.getTarget())) {
				return false;
			}
		} else if (punishment.getPunishmentType() == Punishment.PunishmentType.KICK) {
			return false;
		}

		SysLogin_BanManager.getInstance().scheduleFuture(new PushPunishmentFuture(staff, punishment));

		if (punishment.getPunishmentType() == Punishment.PunishmentType.MUTE) {
			usernameMutes.put(punishment.getTarget().toLowerCase(), punishment);
		} else if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN) {
			if (punishment.getTargetType() == Punishment.PunishmentTarget.USERNAME) {
				usernameBans.put(punishment.getTarget().toLowerCase(), punishment);
				// TODO
			} else if (punishment.getTargetType() == Punishment.PunishmentTarget.IP) {
				ipBans.put(punishment.getTarget().toLowerCase(), punishment);
			} else if (punishment.getTargetType() == Punishment.PunishmentTarget.ISP) {
				ispBans.put(punishment.getTarget().toLowerCase(), punishment);
			}
		}
		return true;
	}
}