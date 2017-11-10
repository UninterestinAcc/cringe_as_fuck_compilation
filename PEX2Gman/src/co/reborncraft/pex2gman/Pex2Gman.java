package co.reborncraft.pex2gman;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Pex2Gman extends JavaPlugin {
	/* Moved it away from a initiated state for easier unit testing. */
	public static void runCommand(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			char targetType;
			switch (args[0].toLowerCase()) {
				case "user": {
					targetType = 'u';
					break;
				}
				case "groups":
				case "group": {
					targetType = 'g';
					break;
				}
				default: {
					sendHelp(sender);
					return;
				}
			}
			String modifyState = "list";
			String modifyType = "p";
			if (args.length >= 2) {
				String var1 = args[1];
				String var2 = "";
				if (args.length >= 3) {
					switch (args[2].toLowerCase()) {
						case "add": {
							modifyState = "add";
							break;
						}
						case "remove": {
							modifyState = "del";
							break;
						}
						case "create": {
							if (targetType == 'u') {
								sender.sendMessage("Command is redundant. Not executing.");
								return;
							}
							modifyState = "add";
							modifyType = "";
							break;
						}
						case "check": {
							modifyState = "check";
							modifyType = "p";
							break;
						}
						case "list": {
							modifyState = "list";
							modifyType = "p";
							break;
						}
						case "delete": {
							modifyState = "del";
							modifyType = "";
							break;
						}
						case "user": {
							if (targetType == 'u') {
								sender.sendMessage("'/pex user ... user' is not valid.");
								return;
							}
							targetType = 'u';
							modifyType = "sub";
							break;
						}
						case "group": {
							if (targetType == 'g') {
								sender.sendMessage("'/pex group ... group' is not valid.");
								return;
							}
							modifyType = "sub";
							break;
						}
						default: {
							sendHelp(sender);
							return;
						}
					}
					if (args.length >= 4) {
						if (args.length >= 5 && modifyType.equals("sub")) {
							switch (args[3].toLowerCase()) {
								case "add": {
									modifyState = "add";
									break;
								}
								case "remove": {
									modifyState = "del";
									break;
								}
								default: {
									sendHelp(sender);
									return;
								}
							}
							switch (args[0].toLowerCase()) {
								case "user": {
									var2 = args[4];
									break;
								}
								case "group": {
									var1 = args[4];
									var2 = args[1];
									break;
								}
							}
						}
						if (args[2].toLowerCase().matches("^(add|remove|check)$")) {
							var2 = args[3];
						}
					}
				}
				Bukkit.dispatchCommand(sender, "man" + targetType + modifyState + modifyType + " " + var1 + " " + var2);
			} else {
				if (targetType == 'u') {
					Bukkit.dispatchCommand(sender, "manulistp " + sender.getName());
				} else {
					Bukkit.dispatchCommand(sender, "listgroups");
					return;
				}
			}
			return;
		}
		sendHelp(sender);
	}

	private static void sendHelp(CommandSender sender) {
		sender.sendMessage("/pex <user|group> <name> <add|remove|create|check|list|delete|group|user> ...");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		runCommand(sender, args);
		return true;
	}
}
