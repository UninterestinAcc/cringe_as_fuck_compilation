package co.reborncraft.stackperms;

import co.reborncraft.stackperms.api.StackPermsInterface;
import co.reborncraft.stackperms.consolecommandhandler.ChargebackCommandHandler;
import co.reborncraft.stackperms.consolecommandhandler.DemoteCommandHandler;
import co.reborncraft.stackperms.consolecommandhandler.PromoteCommandHandler;
import co.reborncraft.stackperms.consolecommandhandler.TestPermissionCommandHandler;
import co.reborncraft.stackperms.debug.DebugWrapper;
import co.reborncraft.stackperms.http.StackPermsHTTPServerlet;
import co.reborncraft.stackperms.impl.StackPermsImpl;
import co.reborncraft.stackperms.utils.WebhookSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StackPerms extends JavaPlugin implements Listener {
	private static StackPerms instance;
	private ConcurrentMap<String, YamlConfiguration> confMap = new ConcurrentHashMap<>();
	private StackPermsInterface stackPermsInterface;
	private StackPermsHTTPServerlet serverlet = null;
	private long serverletLaunchTime = 0;

	public StackPerms() {
		instance = this;
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); // Fucking hell...
	}

	public static StackPerms getInstance() {
		return instance;
	}

	public static StackPermsInterface getInterface() {
		return instance.stackPermsInterface;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender || sender.hasPermission("stackperms.launchcontroller")) {
			if (args != null && args.length >= 1) {
				switch (args[0].toLowerCase()) {
					case "create": {
						if (serverlet == null) {
							serverletLaunchTime = System.currentTimeMillis();
							try {
								serverlet = new StackPermsHTTPServerlet();
							} catch (IOException e) {
								e.printStackTrace();
							}
							sender.sendMessage("\u00a7eServerlet launched. Navigate to http://" + serverlet.getBindingAddr().getAddress().getHostAddress() + ":" + serverlet.getBindingAddr().getPort() + "/auth to begin. (You have 1 hour before the serverlet is destroyed, although you can always refresh your session by doing /stackperms addtime. Don't press F5 after doing a serverlet restart because restarting changes the port.)");
						}
						break;
					}
					case "destroy": {
						if (serverlet != null) {
							sender.sendMessage("Destroying old session.");
							serverlet.destroy();
							serverletLaunchTime = 0;
							serverlet = null;
						} else {
							sender.sendMessage("No old session.");
						}
						break;
					}
					case "addtime": {
						serverletLaunchTime = System.currentTimeMillis();
						sender.sendMessage("Time of the old session has been extended by an hour.");
						break;
					}
					case "reload": {
						long startTime = System.currentTimeMillis();
						sender.sendMessage("Trying to reload files from disk.");
						loadConfig();
						reloadConfig();
						sender.sendMessage("Files loaded from disk, replacing cached data with disk data...");
						stackPermsInterface.populateData();
						sender.sendMessage("Reloaded! (Took " + (System.currentTimeMillis() - startTime) + "ms)");
						break;
					}
					default: {
						sender.sendMessage("Unknown subcommand. Only [create, addtime, destroy, reload] are accepted.");
					}
				}
			} else {
				sender.sendMessage("Please use a subcommand. [create, addtime, destroy, reload] are accepted.");
			}
		} else {
			sender.sendMessage("\u00a7cNo permissions.");
		}
		return true;
	}

	public ConcurrentMap<String, YamlConfiguration> getConfMap() {
		return confMap;
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		loadConfig();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			if (serverlet != null && serverletLaunchTime != 0 && serverletLaunchTime + 3600000 < System.currentTimeMillis()) {
				getLogger().info("Permission editor serverlet destroyed after 1 hour.");
				serverlet.destroy();
				serverletLaunchTime = 0;
				serverlet = null;
			}
		}, 1200, 1200);
		stackPermsInterface = new StackPermsImpl();
		stackPermsInterface.populateData();
		if (!getConfig().contains("bind-addr")) {
			getLogger().severe("bind-addr not set in config.yml, disabling StackPerms start.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			stackPermsInterface.indexPermissions();
			if (getConfig().contains("debug")) {
				if (getConfig().isBoolean("debug")) {
					if (getConfig().getBoolean("debug")) {
						new DebugWrapper().run();
					}
				}
			}
		}, 1);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, WebhookSender::flushSendBuffer, 400, 400);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, stackPermsInterface::runSaveRoutine, 400, 400);
		getCommand("chargeback").setExecutor(new ChargebackCommandHandler());
		getCommand("demote").setExecutor(new DemoteCommandHandler());
		getCommand("promote").setExecutor(new PromoteCommandHandler());
		getCommand("testpermission").setExecutor(new TestPermissionCommandHandler());
		bindChat();
	}

	private void bindChat() {
		if (getConfig().contains("managechat")) {
			if (getConfig().isBoolean("managechat")) {
				if (!getConfig().getBoolean("managechat")) {
					getLogger().info("Not manging chat because managechat is explicitly set to false.");
					return;
				}
			}
		}
		Bukkit.getPluginManager().registerEvents(new StackPermsChatManager(), this);
	}

	private void loadConfig() {
		for (String key : new String[]{"groups", "stacks", "players"}) {
			saveResource("permissions/" + key + ".yml", false);
			confMap.put(key, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "permissions/" + key + ".yml")));
		}
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		stackPermsInterface.hookIntoPlayer(e.getPlayer());
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e) {
		stackPermsInterface.unhookPlayer(e.getPlayer());
	}
}
