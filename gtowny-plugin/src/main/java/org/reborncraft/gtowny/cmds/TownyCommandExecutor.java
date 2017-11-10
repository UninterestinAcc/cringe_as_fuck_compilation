package org.reborncraft.gtowny.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.GTowny;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.data.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public interface TownyCommandExecutor extends org.bukkit.command.CommandExecutor {
	@Override
	default boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			Bukkit.getScheduler().runTaskAsynchronously(GTowny.getInstance(), () -> {
				if (!args[0].equalsIgnoreCase("help")) {
					try {
						Method method = findMethod(args[0]);
						if (method != null) {
							if (method.isAnnotationPresent(GTownySubcommand.class)) {
								GTownySubcommand annotation = method.getAnnotation(GTownySubcommand.class);
								if (annotation.requirePlayer()) {
									if (sender instanceof Player) {
										User senderUser = User.forPlayer((Player) sender);
										if (annotation.requireInTown()) {
											if (senderUser.getTownId() <= 0) {
												sender.sendMessage(MessageFormatter.error("You don't belong to a town."));
												return;
											}
										}
										if (annotation.requireTownPermission() != null) {
											if (!senderUser.getRank().hasPermission(annotation.requireTownPermission())) {
												sender.sendMessage(MessageFormatter.error("You need a town permission to do this."));
												return;
											}
										}
										if (annotation.chunkTownMustBeSame()) {
											if (senderUser.getTownId() != senderUser.getCurrentChunk().getTownId()) {
												sender.sendMessage(MessageFormatter.error("Your town does not own this chunk."));
												return;
											}
										}
										if (annotation.requireChunkOwner()) {
											if (senderUser.getCurrentChunk().getOwnerId() != senderUser.getUserId()) {
												sender.sendMessage(MessageFormatter.error("You are not the owner of this chunk."));
												return;
											}
										}
										if (annotation.requireTownOwner()) {
											if (senderUser.getTown().getOwnerId() != senderUser.getUserId()) {
												sender.sendMessage(MessageFormatter.error("You are not the owner of your town."));
												return;
											}
										}
										method.invoke(this, sender, senderUser, Arrays.copyOfRange(args, 1, args.length));
									} else {
										sender.sendMessage(MessageFormatter.error("You need to be a player."));
									}
								} else {
									method.invoke(this, sender, null, Arrays.copyOfRange(args, 1, args.length));
								}
								return;
							}
						}
						unknownSubcommand(cmd, sender, args);
					} catch (InvocationTargetException ite) {
						ite.getCause().printStackTrace();
						sender.sendMessage(MessageFormatter.exception(ite.getCause()));
					} catch (Throwable t) {
						t.printStackTrace();
						sender.sendMessage(MessageFormatter.exception(t));
					}
				} else {
					sendHelp(sender);
				}
			});
		} else {
			try {
				defaultCommand(sender);
			} catch (Throwable t) {
				t.printStackTrace();
				sender.sendMessage(MessageFormatter.exception(t));
			}
		}
		return true;
	}

	default Method findMethod(final String cmd) {
		Optional<Method> m = Arrays.stream(this.getClass().getMethods()).filter(method -> method.getParameterCount() == 3 &&
				method.getName().equalsIgnoreCase(cmd) &&
				method.getParameterTypes()[0] == CommandSender.class &&
				method.getParameterTypes()[1] == User.class &&
				method.getParameterTypes()[2] == String[].class).findFirst();
		if (m.isPresent()) {
			return m.get();
		} else {
			return null;
		}
	}

	default void sendHelp(CommandSender sender) {
		String rule = MessageFormatter.createSeparator(ChatColor.GREEN + this.getClass().getSimpleName().replace("Command", "") + " Subcommands", ChatColor.GOLD, ChatColor.STRIKETHROUGH);
		String methods = stream(this.getClass().getMethods()).filter(method -> method.isAnnotationPresent(GTownySubcommand.class)).map(method ->
				ChatColor.GREEN + method.getName().toUpperCase()
		).collect(Collectors.joining(ChatColor.GOLD + ", "));
		sender.sendMessage(rule + "\n" +
				methods +
				"\n" + rule);
	}

	default void unknownSubcommand(Command cmd, CommandSender sender, String[] args) {
		sender.sendMessage(MessageFormatter.error("Subcommand not found, try `/" + cmd.getName() + " help'."));
	}

	default void defaultCommand(CommandSender sender) throws Throwable {
		if (sender instanceof Player) {
			try {
				Method info = this.getClass().getMethod("info", CommandSender.class, User.class, String[].class);
				info.invoke(this, sender, User.forPlayer((Player) sender), new String[]{});
			} catch (NoSuchMethodException nsme) {
				sendHelp(sender);
			}
		} else {
			sender.sendMessage(MessageFormatter.error("You aren't a player."));
		}
	}
}
