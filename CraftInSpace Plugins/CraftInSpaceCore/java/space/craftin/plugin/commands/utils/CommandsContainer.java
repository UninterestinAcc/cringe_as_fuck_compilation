/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.commands.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.craftin.plugin.core.impl.core.ChatUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CommandsContainer extends CommandExecutor {
	@Override
	default boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			defaultSubcommand(sender, args);
		} else {
			if (args[0].equalsIgnoreCase("help")) {
				helpSubcommand(sender);
			} else {
				Optional<Method> subCommandOpt = Arrays.stream(this.getClass().getMethods()).filter(method -> {
					if (method.isAnnotationPresent(CommandHandler.class)) {
						return method.getAnnotation(CommandHandler.class).name().equalsIgnoreCase(args[0]);
					}
					return false;
				}).findAny();
				if (subCommandOpt.isPresent()) {
					final Method subCommand = subCommandOpt.get();
					final CommandHandler subAnnotation = subCommand.getAnnotation(CommandHandler.class);
					if (subAnnotation.requirePlayer()) {
						if (sender instanceof Player) {
							if (subCommand.isAnnotationPresent(RequireAllianceRank.class)) {
								// TODO
							} else if (subCommand.isAnnotationPresent(RequireFactionRank.class)) {
								// TODO
							}
						} else {
							nonPlayerExecutingPlayerCommand(sender, args);
							return true;
						}
					}
					try {
						subCommand.invoke(this, sender, Arrays.copyOfRange(args, 1, args.length));
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				} else {
					subCommandNotFound(sender, args);
				}
			}
		}
		return true;
	}

	default void helpSubcommand(CommandSender sender) {
		final String cmds = Arrays.stream(this.getClass().getMethods())
				.filter(m -> m.isAnnotationPresent(CommandHandler.class))
				.map(m -> m.getAnnotation(CommandHandler.class).name())
				.collect(Collectors.joining(ChatColor.DARK_GRAY + ", " + ChatColor.GRAY));
		ChatUtil.send(sender, ChatUtil.ChatMessage.HELP_PREFIX + cmds);
	}

	void defaultSubcommand(CommandSender sender, String[] args);

	default void subCommandNotFound(CommandSender sender, String[] args) {
		ChatUtil.send(sender, ChatUtil.ChatMessage.NO_SUCH_SUBCOMMAND);
	}

	default void nonPlayerExecutingPlayerCommand(CommandSender sender, String[] args) {
		ChatUtil.send(sender, ChatUtil.ChatMessage.NOT_PLAYER);
	}
}
