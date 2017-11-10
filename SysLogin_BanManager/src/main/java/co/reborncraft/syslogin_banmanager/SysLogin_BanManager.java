package co.reborncraft.syslogin_banmanager;

import co.reborncraft.cloudcord.binds.CloudBungee;
import co.reborncraft.syslogin_banmanager.api.futures.IFuture;
import co.reborncraft.syslogin_banmanager.api.futures.banmanager.PullPunishmentsFuture;
import co.reborncraft.syslogin_banmanager.api.futures.management.PullStaffFuture;
import co.reborncraft.syslogin_banmanager.api.futures.motd.PullMOTDFuture;
import co.reborncraft.syslogin_banmanager.api.futures.motd.PullServerListFuture;
import co.reborncraft.syslogin_banmanager.api.futures.statistics.PushPlayerCountFuture;
import co.reborncraft.syslogin_banmanager.api.objects.DatacenterIPs;
import co.reborncraft.syslogin_banmanager.api.objects.Inet4AddressBlock;
import co.reborncraft.syslogin_banmanager.api.objects.PunishmentCache;
import co.reborncraft.syslogin_banmanager.api.objects.VoteReminderManager;
import co.reborncraft.syslogin_banmanager.commands.banmanager.*;
import co.reborncraft.syslogin_banmanager.commands.login.*;
import co.reborncraft.syslogin_banmanager.commands.utils.antibot.AntibotPanicCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.antibot.ToggleAntiBotCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.lobby.HubCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.players.GlistCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.players.PlayersCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.security.SecurityCheckCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.staff.StaffsCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.staff.UpdateStaffsCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.vote.FakeVoteCommand;
import co.reborncraft.syslogin_banmanager.commands.utils.vote.VoteCommand;
import co.reborncraft.syslogin_banmanager.listeners.*;
import co.reborncraft.syslogin_banmanager.sql.SQLManager;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SysLogin_BanManager extends Plugin {
	public static final String LOGIN_PREFIX = "\u00a7c[\u00a77Login\u00a7c]";
	public static final String BANMANAGER_PREFIX = "\u00a79[\u00a7bBanManager\u00a79]";

	public static final String SL_INVALID_USERNAME = SysLogin_BanManager.LOGIN_PREFIX + "\n\u00a7c\u00a7lInvalid Username.\n\n\u00a7aMust only contain alpha-numeric characters and the underscore (a-z, A-Z, 0-9, _) and be between 3 and 16 characters long.";
	public static final String BANNED_USERNAME = SysLogin_BanManager.LOGIN_PREFIX + "\n\u00a7c\u00a7lInvalid Username.\n\n\u00a7aThis username is banned.";
	public static final String LOGIN_TIMEOUT = SysLogin_BanManager.LOGIN_PREFIX + "\n\u00a7cLogin/register time out. Please register within a minute.";

	public static final String PERMISSION_ERROR = "\u00a7cAccess denied.";

	public static final String SL_AUTOSWITCH_ATTEMPT = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aAttempting to send you to your set server.";
	public static final String SL_AUTOSWITCH_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aDo \u00a7d/autoswitch <server>\u00a7a to change and \u00a7d/autoswitch clear\u00a7a to reset.";
	public static final String SL_AUTOSWITCH_CMD_SERV_NOT_FOUND = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cThat server was not found.";
	public static final String SL_AUTOSWITCH_SERVER_NOT_FOUND = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cYour autoswitch server was not found.";
	public static final String SL_AUTOSWITCH_UPDATED = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aUpdated your autoswitch preferences!";
	public static final String SL_PASSWORD_SPACES_ERROR = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cSorry, your password cannot have spaces.";
	public static final String SL_PLAYER_ONLY_ERROR = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cOnly a player can do that.";
	public static final String SL_REGISTER_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aPlease register with \u00a7d/register <password>\u00a7a.";
	public static final String SL_LOGIN_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aPlease login with \u00a7d/login <password>\u00a7a.";
	public static final String SL_ALREADY_LOGGED_IN_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cYou are already logged in.";
	public static final String SL_CHANGEPASS_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aTo change your password, type \u00a7d/changepassword <newpass>\u00a7a.";
	public static final String SL_ALTCHECK_STARTING = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aChecking alts for \u00a7d{name}\u00a7a...";
	public static final String SL_ALTCHECK_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aTo check for alts, type \u00a7d/altcheck <username> [ip|pass]\u00a7a.";
	public static final String SL_ALTCHECK_COLLAPSE_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aAdd \u00a7dip \u00a7aor \u00a7dpass \u00a7aat the end of the command if it's too long.";
	public static final String SL_ALLOW_REGISTER_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aTo allow someone to register on an alt-flagged IP, do \u00a7d/allowregister <username>\u00a7a.";
	public static final String SL_ALLOW_REGISTER_SUCCESS = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aThat user has been allowed to register.";
	public static final String SL_UPDATE_STAFF_SUCCESS = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aStaff updated!";
	public static final String SL_UPDATE_STAFF_PERM_NONEXISTANT = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cThe permission you are trying to remove is not assigned to that staff.";
	public static final String SL_UPDATE_STAFF_PERM_ALREADYEXISTS = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cThe permission you are trying to grant is already assigned to that staff.";
	public static final String SL_UPDATE_STAFF_PERM_INPUT_REQ_NUMBER = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cThe permission needs to be a number. Permissions list (mouseover for information):";
	public static final String SL_UPDATE_STAFF_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aTo add/remove permissions from a staff, do \u00a7d/updatestaff <grant|revoke> <username> <permission>\u00a7a.\n" +
			SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aTo promote/demote, do \u00a7d/updatestaff <promote|demote> <username>\u00a7a.\n" +
			SysLogin_BanManager.LOGIN_PREFIX + "\u00a7a To check current permissions, do \u00a7d/updatestaff check <username>\u00a7a.";
	public static final String SL_UPDATE_STAFF_NOT_STAFF = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cThat user is not a staff.";
	public static final String SL_UPDATE_STAFF_ALREADY_STAFF = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cThat user is already a staff.";
	public static final String SL_UPDATE_STAFF_CHECK_MESSAGE = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aPermissions for \u00a7d{name}\u00a7a:";
	public static final String SL_BAD_PASSWORD = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7cYour password is bad, choose another one.";
	public static final String SL_ANTIBOT_TOGGLED = SysLogin_BanManager.LOGIN_PREFIX + " \u00a7aAntibot is now \u00a7d{state}\u00a7a.";

	public static final String BM_INVALID_USERNAME = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cUsername is not valid.";
	public static final String BM_PLAYER_NOT_FOUND = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cPlayer not found.";
	public static final String BM_DENY_SELF_PUNISH = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cYou can't punish yourself.";
	public static final String BM_SUCCESS = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7d{name} \u00a7ahas been punished successfully.";
	public static final String BM_REVOKE_SUCCESS = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7d{name}\u00a7a's punishment has been removed successfully.";
	public static final String BM_TARGET_NOT_FOUND = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTarget not found.";
	public static final String BM_REJECTED = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7aYour attempt to punish \u00a7d{name} \u00a7awas rejected, maybe it is already punished.";
	public static final String BM_KICK_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo kick a player, do \u00a7d/kick <player> <reason>";
	public static final String BM_WARN_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo warn a player, do \u00a7d/warn <player> <reason>";
	public static final String BM_MUTE_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo mute a player, do \u00a7d/mute <player> <time up to 12h> <reason>";
	public static final String BM_BAN_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo ban a player, do \u00a7d/ban <player> <time> <reason>";
	public static final String BM_BANIP_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo ban an IP, do \u00a7d/banip <ip or name of online player> <time> <reason>";
	public static final String BM_BANISP_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo ban an ISP, do \u00a7d/banisp <ip or name of online player> <time> <reason>";
	public static final String BM_UNMUTE_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo unmute a player, do \u00a7d/unmute <player>";

	public static final String BM_UNBAN_MESSAGE = SysLogin_BanManager.BANMANAGER_PREFIX + " \u00a7cTo unban a player, do \u00a7d/unban <player>";
	public static final String OPERATION_WAIT = "\u00a7cPlease wait a few seconds while your operation completes. (You may need to retry.)";
	public static final String DATABASE_ERROR = "\u00a7cA database error occurred with this operation. Please try again in a few seconds.";
	public static final String[] BANNED_NAMES = new String[]{"Console", "SysLogin", "SysLogin_Auto", "BanManager", "ChatManager", "AntiAlt", "AntiAlts", "AntiBot", "AntiBots", "AntiCheat", "AntiCheats", "JudgementDay", "Expired", "INVALID_VOTE"};
	public static final String HUB_REGEX = "^(hub|lobby)(-[a-z]+)?(-[0-9])*$";
	public static final String PLAYERNAME_REGEX = "^\\w{3,16}$";
	public static final String PERMANENT_REGEX = "^p(erm(anent)?)?$";
	public static final String SEC_BLOCKED_CMD = "\u00a7cThat command is blocked.";
	public static final String SEC_BLOCKED_MSG = "\u00a7cMessage blocked due to filter violation.";
	public static final String SEC_MSG_TOO_QUICK = "\u00a7cSlow down! You're typing too fast. (Wait \u00a7e{wait} \u00a7cseconds to type the same message.)";

	private static SQLManager sqlManager;
	private static PunishmentCache punishmentCache;
	private static SysLogin_BanManager instance;
	private static String[] bannedCmds;
	private static String[] messageFilters;
	private static String[] badPasswords;
	private static String[] badUsernames;
	private static String[] muteMessageFilters;
	private static ConcurrentMap<String, String> srvVars = new ConcurrentHashMap<>();
	private static Inet4AddressBlock[] ipWhitelist;
	private final ConcurrentMap<String, Long> allowRegister = new ConcurrentHashMap<>();
	private final ConcurrentMap<ProxiedPlayer, Long> lastMessage = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Long> loggedInPlayers = new ConcurrentHashMap<>();
	private final ConcurrentMap<ProxiedPlayer, Set<String>> blockList = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Long> staffs = new ConcurrentHashMap<>();
	private final Set<IFuture> futures = ConcurrentHashMap.newKeySet();
	private final AtomicBoolean restartQueued = new AtomicBoolean(false);
	private String pingMessage = "Loading MOTD...";
	private VoteReminderManager voteReminderManager = new VoteReminderManager();
	private DatacenterIPs datacenterIPs;
	private ConcurrentMap<Inet4AddressBlock, Long> antiAltsMap = new ConcurrentHashMap<>();

	public SysLogin_BanManager() {
		instance = this;
	}

	public static SysLogin_BanManager getInstance() {
		return instance;
	}

	public static String[] getBannedCommands() {
		return bannedCmds;
	}

	public static String[] getMessageFilters() {
		return messageFilters;
	}

	public static String[] getBadPasswords() {
		return badPasswords;
	}

	public static String[] getBadUsernames() {
		return badUsernames;
	}

	public static Inet4AddressBlock[] getIPWhitelist() {
		return ipWhitelist;
	}

	public static String[] getMuteMessageFilters() {
		return muteMessageFilters;
	}

	public boolean isRestartQueued() {
		return restartQueued.get();
	}

	public void allowRegister(String name) {
		allowRegister.put(name.toLowerCase(), System.currentTimeMillis());
	}

	public boolean isAllowedToRegister(String name) {
		return allowRegister.keySet().contains(name.toLowerCase());
	}

	public PunishmentCache getPunishmentCache() {
		return punishmentCache;
	}

	public boolean isOnline(ProxiedPlayer p) {
		return getProxy().getPlayers().contains(p);
	}

	public boolean isLoggedIn(ProxiedPlayer p) {
		return isLoggedIn(p.getName());
	}

	public boolean isLoggedIn(String name) {
		return loggedInPlayers.keySet().stream().anyMatch(p -> p.equalsIgnoreCase(name));
	}

	public void login(String p) {
		if (!isLoggedIn(p)) {
			loggedInPlayers.put(p, System.currentTimeMillis());
			EventLogger.successfulLogin(p);
		}
	}

	public void logout(String p) {
		if (isLoggedIn(p)) {
			loggedInPlayers.remove(p);
		}
	}


	public double calculateLastChat(ProxiedPlayer p) {
		final long currentMillis = System.currentTimeMillis();
		double time;
		if (lastMessage.containsKey(p)) {
			time = (currentMillis - lastMessage.get(p)) / 1000D;
		} else {
			time = 100D;
		}
		lastMessage.put(p, currentMillis);
		return time;
	}

	@Override
	public void onEnable() {
		new HTTPConnectionListener();
		try {
			datacenterIPs = new DatacenterIPs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PluginManager pluginManager = getProxy().getPluginManager();

		// Graceful restart command, if I put it in another class it will crash or stacktrace if I want to graceful when I swap the plugin files
		pluginManager.registerCommand(this, new Command("queuerestart") {
			@Override
			public void execute(CommandSender sender, String[] args) {
				CommandSender console = getProxy().getConsole();
				if (sender == console) {
					restartQueued.set(true);
					TextComponent ruleComp = Utils.buildTextComponent("----------------------------------------------------", ChatColor.GOLD, false, false, false, true, false);
					TextComponent alertTextComp = Utils.buildTextComponent("Network restart in 60 seconds, please join again in a minute.", ChatColor.RED, true, false, true, false, false);
					List<TextComponent> sendComps = Arrays.asList(ruleComp, alertTextComp, ruleComp);

					getProxy().getPlayers().forEach(p -> sendComps.forEach(p::sendMessage));
					sendComps.forEach(console::sendMessage);

					getProxy().getScheduler().schedule(SysLogin_BanManager.instance, () -> getProxy().stop("\u00a7cNetwork restarting, please join again in a minute."), 60, TimeUnit.SECONDS);
				}
			}
		});


		pluginManager.registerCommand(this, new AltCheckCommand());
		pluginManager.registerCommand(this, new UserInfoCommand());
		pluginManager.registerCommand(this, new AllowRegisterCommand());
		pluginManager.registerCommand(this, new UpdateStaffsCommand());

		pluginManager.registerCommand(this, new WarnCommand());
		pluginManager.registerCommand(this, new KickCommand());
		pluginManager.registerCommand(this, new MuteCommand());
		pluginManager.registerCommand(this, new BanCommand());
		pluginManager.registerCommand(this, new BanIPCommand());
		pluginManager.registerCommand(this, new BanISPCommand());
		pluginManager.registerCommand(this, new UnbanCommand());
		pluginManager.registerCommand(this, new UnmuteCommand());

		pluginManager.registerCommand(this, new ChangePasswordCommand());
		pluginManager.registerCommand(this, new AutoSwitchCommand());
		pluginManager.registerCommand(this, new LoginCommand());
		pluginManager.registerCommand(this, new RegisterCommand());

		pluginManager.registerCommand(this, new HubCommand());
		pluginManager.registerCommand(this, new GlistCommand());
		pluginManager.registerCommand(this, new PlayersCommand());
		pluginManager.registerCommand(this, new StaffsCommand());
		pluginManager.registerCommand(this, new SecurityCheckCommand());

		pluginManager.registerCommand(this, new FakeVoteCommand());
		pluginManager.registerCommand(this, new VoteCommand());
		pluginManager.registerListener(this, new VoteListener());

		pluginManager.registerListener(this, new AntiVPNListener());
		pluginManager.registerListener(this, new ReroutingListener());
		pluginManager.registerListener(this, new LoginProtectionListener());
		pluginManager.registerListener(this, new BanManagerListener());
		pluginManager.registerListener(this, new StatisticsListener());
		pluginManager.registerListener(this, new PingListener());
		pluginManager.registerListener(this, new SecurityListener());
		pluginManager.registerListener(this, new RestartLockListener());
		pluginManager.registerListener(this, new EventLogger());

		CloudBungee.getInstance().getAdapter().registerListener(new ReborncraftCloudListener());

		AntiBotListener antiBotListener = new AntiBotListener();
		pluginManager.registerListener(this, antiBotListener);
		pluginManager.registerCommand(this, new ToggleAntiBotCommand());
		pluginManager.registerCommand(this, new AntibotPanicCommand());

		Set<ProxiedPlayer> notLoggedInAtLastCheck = new HashSet<>();
		getProxy().getScheduler().schedule(this, () -> {
			notLoggedInAtLastCheck.stream().filter(Utils.not(this::isLoggedIn)).forEach(p -> p.disconnect(Utils.parseIntoComp(SysLogin_BanManager.LOGIN_TIMEOUT)));
			notLoggedInAtLastCheck.clear();
			notLoggedInAtLastCheck.addAll(getProxy().getPlayers().stream().filter(Utils.not(loggedInPlayers::containsKey)).collect(Collectors.toList()));
		}, 0, 60, TimeUnit.SECONDS);
		getProxy().getScheduler().schedule(this, () -> voteReminderManager.remindEveryone(), 3, 17, TimeUnit.MINUTES);
		getProxy().getScheduler().schedule(this, () -> { // Garbage cleaner
			long standardTimeout = System.currentTimeMillis() - 3600000; // An hour
			allowRegister.keySet().stream().filter(name -> allowRegister.get(name) < standardTimeout).forEach(allowRegister::remove);
			loggedInPlayers.keySet().stream().filter(p -> getProxy().getPlayer(p) == null).forEach(loggedInPlayers::remove);
			lastMessage.keySet().stream().filter(Utils.not(ProxiedPlayer::isConnected)).forEach(lastMessage::remove);
			blockList.keySet().stream().filter(Utils.not(ProxiedPlayer::isConnected)).forEach(blockList::remove);
			antiAltsMap.keySet().stream().filter(name -> antiAltsMap.get(name) < standardTimeout).forEach(antiAltsMap::remove);
			antiBotListener.gc();
			voteReminderManager.gc();
		}, 30, 60, TimeUnit.SECONDS);
		getProxy().getScheduler().schedule(this, () -> {
			requestPullStaff(); // Pulls the list of staff
			scheduleFuture(new PullMOTDFuture(pingMessage -> this.pingMessage = pingMessage != null ? pingMessage : this.pingMessage)); // Pulls the ping messages
			scheduleFuture(new PullServerListFuture(fetchedServers -> {
				if (fetchedServers != null) {
					Map<String, ServerInfo> serversMap = getProxy().getServers();
					serversMap.keySet().stream()
							.filter(server -> fetchedServers.keySet().stream()
									.noneMatch(server::equals))
							.collect(Collectors.toList())
							.forEach(server -> {
								ServerInfo hub = getHubServer().orElseGet(() -> null);
								ServerInfo serverInfo = serversMap.get(server);
								if (serverInfo != null) {
									serverInfo.getPlayers().forEach(p -> {
										p.sendMessage(Utils.buildTextComponent("The server you were connected on has been removed, connecting you to hub...", ChatColor.RED));
										p.connect(hub);
									});
								}
								serversMap.remove(server);
							});
					fetchedServers.keySet().stream()
							.filter(fetchedServer -> !serversMap.containsKey(fetchedServer) || serversMap.get(fetchedServer).getPlayers().isEmpty())
							.forEach(server -> serversMap.put(server, fetchedServers.get(server)));
				}
			}));
			scheduleFuture(new PullPunishmentsFuture(punishments -> {
				if (punishments != null) {
					punishmentCache = punishments;
					getProxy().getPlayers().stream()
							.filter(punishments::isBanned)
							.forEach(p -> p.disconnect(punishments.getBanReason(p).toBanTextComponent()));
				}
			})); // Pulls all the punishments

			// Update banned commands
			final File bannedCommands = new File("bannedcommands.regex");
			if (!bannedCommands.exists()) {
				try {
					bannedCommands.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			bannedCmds = Utils.readFileWithoutComments(bannedCommands.getPath());

			// Update the message filter
			final File messageFilter = new File("messagefilter.regex");
			if (!messageFilter.exists()) {
				try {
					messageFilter.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			messageFilters = Utils.readFileWithoutComments(messageFilter.getPath());

			// Update the bad passwords
			final File badPassword = new File("badpasswords.regex");
			if (!badPassword.exists()) {
				try {
					badPassword.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			badPasswords = Utils.readFileWithoutComments(badPassword.getPath());

			// Update the bad usernames
			final File badUsername = new File("badusernames.regex");
			if (!badUsername.exists()) {
				try {
					badUsername.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			badUsernames = Utils.readFileWithoutComments(badUsername.getPath());

			// Update the messages mute is to prevent people sending
			final File muteMessageFilter = new File("mutemessagefilter.regex");
			if (!muteMessageFilter.exists()) {
				try {
					muteMessageFilter.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			muteMessageFilters = Utils.readFileWithoutComments(muteMessageFilter.getPath());

			// Update the ip whitelist
			final File ipWhitelistFile = new File("ipwhitelist.cidr");
			if (!ipWhitelistFile.exists()) {
				try {
					ipWhitelistFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ipWhitelist = Arrays.stream(Utils.readFileWithoutComments(ipWhitelistFile.getPath()))
					.map(ip -> {
						try {
							return new Inet4AddressBlock(ip);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return null;
					})
					.filter(Objects::nonNull)
					.toArray(Inet4AddressBlock[]::new);

			// Update server variables
			String hostname = Utils.getHostName();
			String serverId = hostname.substring(0, hostname.length() - 15);
			String locationCode = serverId.replaceAll("bungeecord-([a-z]+)-\\d+", "$1").toUpperCase();
			srvVars.put("server-hostname", hostname);
			srvVars.put("server-id", serverId);
			srvVars.put("server-location-code", locationCode);
			srvVars.put("server-location", Utils.locCodeToLoc(locationCode));
			srvVars.put("server-playercount", SysLogin_BanManager.getInstance().getProxy().getPlayers().size() + "");
			srvVars.put("global-playercount", SysLogin_BanManager.getInstance().getGlobalPlayers().size() + "");
		}, 0, 60, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, () -> {
			scheduleFuture(new PushPlayerCountFuture(Statistics.getLogins().getAndSet(0), Statistics.getLogouts().getAndSet(0), Statistics.getMaxConcurrentPlayers().getAndSet(0))); // Pushes the player data
		}, 300 - ((System.currentTimeMillis() / 1000) % 300), 300, TimeUnit.SECONDS);


		getProxy().getScheduler().runAsync(this, () -> {
			Thread.currentThread().setName("SysLogin_BanManager Dedicated SQL Thread");
			try {
				sqlManager = new SQLManager();
				while (true) {
					Thread.sleep(20);
					try {
						if (futures.size() > 0) {
							futures.removeAll(futures.stream().sequential().map(future -> {
								Connection con = sqlManager.getOrCreateConnection();
								try {
									if (con != null) {
										future.execute(con);
										return future;
									}
								} catch (Throwable t) {
									t.printStackTrace();
								}
								future.getRequester().sendMessage(Utils.parseIntoComp(SysLogin_BanManager.DATABASE_ERROR + " (Type: " + future.getClass().getSimpleName() + ")"));
								return null;
							}).filter(Objects::nonNull).collect(Collectors.toList()));
						}
						continue;
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			} catch (ClassNotFoundException | InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	public void requestPullStaff() {
		scheduleFuture(new PullStaffFuture(staffs -> {
			staffs.forEach(this.staffs::put);
			this.staffs.keySet().stream()
					.filter(Utils.not(staffs::containsKey))
					.collect(Collectors.toList())
					.forEach(this.staffs::remove);
		}));
	}

	public void scheduleFuture(IFuture future) {
		if (this.futures.parallelStream().filter(f -> f.getRequester() == future.getRequester()).count() > 4 && future.getRequester() instanceof ProxiedPlayer) {
			future.getRequester().sendMessage(Utils.parseIntoComp(SysLogin_BanManager.OPERATION_WAIT));
		} else {
			this.futures.add(future);
		}
	}

	public String encodePassword(String password) {
		return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
	}

	public String decodePassword(String passwordB64) {
		return new String(Base64.getDecoder().decode(passwordB64), StandardCharsets.UTF_8);
	}

	public long getStaffPermissions(String name) {
		return isStaff(name) ?
				staffs.entrySet().stream()
						.filter(e -> e.getKey().equalsIgnoreCase(name))
						.map(Map.Entry::getValue)
						.findFirst()
						.orElse(0L)
				: 0;
	}

	public boolean isStaff(String name) {
		return staffs.keySet().stream().anyMatch(name::equalsIgnoreCase);
	}

	public Set<String> getStaffList() {
		return new HashSet<>(staffs.keySet());
	}

	public String getPingMessage() {
		return pingMessage;
	}

	public ProxiedPlayer getPlayer(String name) {
		return getProxy().getPlayers().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public long getLoginTime(String player) {
		return isLoggedIn(player) ? loggedInPlayers.get(player) : 0;
	}

	public List<String> getGlobalOnlineStaffs() {
		return getGlobalPlayers().stream()
				.filter(p -> staffs.keySet().stream()
						.anyMatch(p::equalsIgnoreCase)
				).collect(Collectors.toList());
	}

	public List<String> getGlobalPlayers() {
		return CloudBungee.getInstance().getGlobalPlayers();
	}

	public List<String> getPlayersOnServer(String server) {
		return CloudBungee.getInstance().getPlayersOnServer(server);
	}

	public String getServerOfPlayer(String name) {
		return CloudBungee.getInstance().getServerOfPlayer(name);
	}

	public String getPlayerEdge(String name) {
		return CloudBungee.getInstance().getEdgeServerOfPlayer(name);
	}

	public Map<String, String> getServerVariables() {
		return srvVars;
	}

	public long getJoinTime(String name) {
		return CloudBungee.getInstance().getJoinTime(name);
	}

	public int getLoggedInPlayersCount() {
		return loggedInPlayers.size();
	}

	public Optional<ServerInfo> getHubServer() {
		final String loc = getServerVariables().get("server_location");
		return getProxy().getServers().values().stream()
				.filter(server -> server.getName().toLowerCase().matches(SysLogin_BanManager.HUB_REGEX))
				.sorted(Comparator.comparingInt(server -> server.getPlayers().size()))
				.sorted((s1, s2) -> {
					boolean s1isSameLocation = s1.getName().split("-")[1].equalsIgnoreCase(loc);
					return s1isSameLocation == s2.getName().split("-")[1].equalsIgnoreCase(loc) ? 0 : s1isSameLocation ? 1 : -1;
				})
				.findFirst();
	}

	public Set<String> getBlockedPlayers(ProxiedPlayer player) {
		return blockList.get(player);
	}

	public VoteReminderManager getVoteReminderManager() {
		return voteReminderManager;
	}

	public DatacenterIPs getDatacenterIPs() {
		return datacenterIPs;
	}

	public static class Statistics {
		private static final AtomicInteger joins = new AtomicInteger(0);
		private static final AtomicInteger logouts = new AtomicInteger(0);
		private static final AtomicInteger maxConcurrentPlayers = new AtomicInteger(0);

		public static AtomicInteger getLogins() {
			return joins;
		}

		public static AtomicInteger getLogouts() {
			return logouts;
		}

		public static AtomicInteger getMaxConcurrentPlayers() {
			return maxConcurrentPlayers;
		}
	}
}