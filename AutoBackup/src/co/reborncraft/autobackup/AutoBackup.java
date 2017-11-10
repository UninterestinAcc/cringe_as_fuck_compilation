package co.reborncraft.autobackup;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;

import static org.eclipse.jgit.lib.ConfigConstants.CONFIG_BRANCH_SECTION;

public class AutoBackup extends JavaPlugin {
	@Override
	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			if (getConfig().getBoolean("git.enabled", false)) {
				invokeBackup();
			}
		}, 36000, 72000);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.isOp()) {
			if (args.length >= 2) {
				switch (args[0].toLowerCase()) {
					case "init": {
						sender.sendMessage(initGit(args[1]));
						return true;
					}
					case "setuser": {
						getConfig().set("git.user", args[1]);
						saveConfig();
						sender.sendMessage("OK: Username set");
						return true;
					}
					case "setpass": {
						getConfig().set("git.pass", args[1]);
						saveConfig();
						sender.sendMessage("OK: Password set");
						return true;
					}
				}
			} else if (args.length >= 1) {
				switch (args[0].toLowerCase()) {
					case "reload": {
						reloadConfig();
						sender.sendMessage("OK: Reloaded");
						return true;
					}
					case "enable": {
						getConfig().set("git.enabled", true);
						saveConfig();
						sender.sendMessage("OK: Enabled");
						return true;
					}
					case "disable": {
						getConfig().set("git.enabled", false);
						saveConfig();
						sender.sendMessage("OK: Disabled");
						return true;
					}
					case "push": {
						sender.sendMessage(invokeBackup(true));
						return true;
					}
					case "manualbackup": {
						sender.sendMessage(invokeBackup());
						return true;
					}
				}
			}
			sender.sendMessage("/autobackup <init> <arg>");
		} else {
			sender.sendMessage("You need to be op to manage backups.");
		}
		return true;
	}

	private String invokeBackup() {
		return invokeBackup(false);
	}

	private String invokeBackup(boolean pushOnly) {
		if (!getConfig().contains("git.user")) {
			return "FAIL: git.user not defined";
		} else if (!getConfig().contains("git.pass")) {
			return "FAIL: git.pass not defined";
		}
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			Git git = null;
			try {
				git = Git.open(new File("."));
				if (!pushOnly) {
					git.add()
							.addFilepattern(".")
							.call();
					git.commit()
							.setMessage("Backup on " + Instant.now())
							.setCommitter(Bukkit.getServer().getName(), getConfig().getString("git.user") + "-serverbackup@mail.reborncraft.co")
							.call();
				}
				git.push()
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
								getConfig().getString("git.user"),
								getConfig().getString("git.pass")
						))
						.call();
				Bukkit.getLogger().info("Backup completed successfully.");
			} catch (IOException | GitAPIException e) {
				e.printStackTrace();
			} finally {
				if (git != null) {
					git.close();
				}
			}
		});
		return "OK: Backup process started. Check console for errors.";
	}

	private String initGit(String serverName) {
		Git git = null;
		try {
			git = Git.init().call();
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
			remoteAddCommand.setName("origin");
			remoteAddCommand.setUri(new URIish("https://git.reborncraft.co/backups/" + serverName));
			remoteAddCommand.call();

			StoredConfig config = git.getRepository().getConfig();
			config.setString(CONFIG_BRANCH_SECTION, "master", "remote", "origin");
			config.setString(CONFIG_BRANCH_SECTION, "master", "merge", "refs/heads/master");
			config.save();
		} catch (IOException | URISyntaxException | GitAPIException e) {
			return e.getClass().getSimpleName() + ": " + e.getMessage();
		} finally {
			if (git != null) {
				git.close();
			}
		}
		return "OK: Initialized";
	}
}
