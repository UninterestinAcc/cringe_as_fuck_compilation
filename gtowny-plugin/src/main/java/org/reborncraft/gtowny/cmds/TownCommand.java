package org.reborncraft.gtowny.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.reborncraft.gtowny.GTowny;
import org.reborncraft.gtowny.chat.ChatComponent;
import org.reborncraft.gtowny.chat.ChatOutput;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.cmds.utils.ClaimUtils;
import org.reborncraft.gtowny.cmds.utils.ComparatorUtil;
import org.reborncraft.gtowny.data.*;
import org.reborncraft.gtowny.data.internal.ChunkType;
import org.reborncraft.gtowny.data.internal.TownOptions;
import org.reborncraft.gtowny.data.internal.TownPermissions;
import org.reborncraft.gtowny.data.internal.TownTaxOptions;
import org.reborncraft.gtowny.data.local.ChunkLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.reborncraft.gtowny.data.TownyDataHandler.getInvites;

public class TownCommand implements TownyCommandExecutor {
	@GTownySubcommand
	public void create(CommandSender sender, User senderUser, String[] args) {
		if (senderUser.getTownId() <= 0) {
			if (sender.hasPermission("gtowny.town.create")) {
				if (args.length >= 1) {
					String name = args[0];
					if (name.length() > 11) {
						sender.sendMessage(MessageFormatter.error("Invalid name, can only be maximum 11 characters."));
					} else if (name.matches("^[a-zA-Z0-9_\\-]+$")) {
						if (TownyDataHandler.createTown((Player) sender, name)) {
							sender.sendMessage(MessageFormatter.success("Town created."));
						} else {
							sender.sendMessage(MessageFormatter.error("Failed to create town, perhaps a town of the same name already exists?"));
						}
					} else {
						sender.sendMessage(MessageFormatter.error("Invalid name, can only be alphanumeric with underscores and dashes."));
					}
				} else {
					sender.sendMessage(MessageFormatter.error("Please provide town name."));
				}
			} else {
				sender.sendMessage(MessageFormatter.error("Insufficient permissions."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("You are already in a town."));
		}
	}

	@GTownySubcommand (requireTownOwner = true)
	public void rename(CommandSender sender, User senderUser, String[] args) {
		if (args.length >= 1) {
			String name = args[0];
			if (name.length() > 11) {
				sender.sendMessage(MessageFormatter.error("Invalid name, can only be maximum 11 characters."));
			} else if (name.matches("^[a-zA-Z0-9_\\-]+$")) {
				if (TownyDataHandler.getTownByName(name) == null) {
					senderUser.getTown().setName(name);
					sender.sendMessage(MessageFormatter.success("Town name updated."));
				} else {
					sender.sendMessage(MessageFormatter.error("A town of the same name already exists."));
				}
			} else {
				sender.sendMessage(MessageFormatter.error("Invalid name, can only be alphanumeric with underscores and dashes."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Please provide the new town name."));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.ModifyTownOptions)
	public void option(CommandSender sender, User senderUser, String[] args) {
		Town town = TownyDataHandler.getTownByUser(senderUser);
		try {
			if (args.length >= 1) {
				TownOptions opt = TownOptions.forBit(Integer.valueOf(args[0]));
				if (opt != null) {
					town.toggleOption(opt);
					info(sender, senderUser, new String[]{});
				} else {
					sender.sendMessage(MessageFormatter.error("Invalid option bit."));
				}
			} else {
				sender.sendMessage(MessageFormatter.error("Please specify option bit. (Or use the /town info interface.)"));
			}
		} catch (NumberFormatException nfe) {
			sender.sendMessage(MessageFormatter.error("Invalid option bit."));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.ModifyTownOptions)
	public void setDesc(CommandSender sender, User senderUser, String[] args) {
		if (args.length >= 1) {
			senderUser.getTown().setTownDesc(String.join(" ", args));
			sender.sendMessage(MessageFormatter.success("Updated description."));
		} else {
			sender.sendMessage(MessageFormatter.error("Please provide the description."));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownOwner = true)
	public void delete(CommandSender sender, User senderUser, String[] args) {
		Town town = senderUser.getTown();
		if (args.length >= 1 && args[0].equalsIgnoreCase("confirm")) {
			long time = System.currentTimeMillis();
			AtomicInteger declaimed = new AtomicInteger(0);
			town.getChunkIds().forEach(chunkId -> {
				Chunk chunk = TownyDataHandler.getChunkById(chunkId);
				TownyDataHandler.TOWN_WILDERNESS.claimChunk(chunk);
				declaimed.getAndIncrement();
			});
			sender.sendMessage(MessageFormatter.success("Declaimed " + declaimed.get() + " chunks."));
			AtomicInteger disassociated = new AtomicInteger(0);
			town.getMemberIds().forEach(userId -> {
				User user = TownyDataHandler.getUserById(userId);
				user.setTownId(-1);
				user.setRankId(-1);
				disassociated.getAndIncrement();
			});
			sender.sendMessage(MessageFormatter.success("Disassociated " + disassociated.get() + " members."));
			AtomicInteger deexiled = new AtomicInteger(0);
			town.getExiledIds().forEach(userId -> {
				town.unExile(userId);
				deexiled.getAndIncrement();
			});
			town.getBlockBank().getBlockList().forEach((block, amt) -> town.getBlockBank().decrement(block, amt));
			sender.sendMessage(MessageFormatter.success("Cleaned blockbank."));
			TownyDataHandler.deleteTown(town);
			sender.sendMessage(MessageFormatter.success("Completely removed town from database."));
			sender.sendMessage(MessageFormatter.success("Operation completed in " + (System.currentTimeMillis() - time) + " ms."));
		} else {
			ChatOutput.confirmAction((Player) sender, "Confirm Delete Town", "Are you sure? This action is irreversible.\nThe banks will be cleared without refunds.", "/t delete confirm");
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.Claim)
	public void claim(CommandSender sender, User senderUser, String[] args) {
		GTowny.getChunkCE().claim(sender, senderUser, args);
	}

	@GTownySubcommand (requireInTown = true, chunkTownMustBeSame = true, requireTownPermission = TownPermissions.Claim)
	public void unclaim(CommandSender sender, User senderUser, String[] args) {
		GTowny.getChunkCE().unclaim(sender, senderUser, args);
	}

	@GTownySubcommand
	public void info(CommandSender sender, User senderUser, String[] args) {
		Town town = TownyDataHandler.getTownByPlayer((Player) sender);
		if (args.length >= 2) {
			town = TownyDataHandler.getTownByName(args[1]);
		}
		if (town == null) {
			town = TownyDataHandler.TOWN_WILDERNESS;
		}
		String rule = MessageFormatter.createSeparator(ChatColor.GREEN + town.getName(), ChatColor.GOLD, ChatColor.STRIKETHROUGH);
		String taxOpts = town.getTaxOptions().stream().map(TownTaxOptions::toString).collect(Collectors.joining(", "));
		if (taxOpts.isEmpty()) {
			taxOpts = "DEFAULT";
		}
		ChatComponent comp = new ChatComponent()
				.append(rule + "\n")
				.append(ChatColor.GOLD + "Town Description: ")
				.append(town.getTownDesc() + "\n").setColour(ChatColor.GREEN)
				.append(ChatColor.GOLD + "Town Bank: " + ChatColor.GREEN + "$" + town.getMoneyBank() + "\n")
				.append(ChatColor.GOLD + "Town Tax Settings: " + ChatColor.GREEN + taxOpts + "\n")
				.append(ChatColor.GOLD + "Town Options: ")
				.appendComponent(MessageFormatter.optionsClickable(senderUser, town))
				.append("\n")
				.append(ChatColor.GOLD + "Town Owner: " + ChatColor.GREEN + town.getOwner().getName() + "\n")
				.append(ChatColor.GOLD + "Town Members: ");
		if (town.getId() < 0) {
			comp.append(ChatColor.GREEN + "Nobody");
		} else {
			town.getMembers().forEach(member -> {
				comp.append(member.getName()).setColour(ChatColor.GREEN);
				comp.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Click for more information.");
				comp.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/user " + member.getName());
			});
		}
		comp.append("\n" + rule);
		ChatOutput.clearChat((Player) sender);
		ChatOutput.sendRecentMessages((Player) sender);
		ChatOutput.chat((Player) sender, comp);
	}

	@GTownySubcommand
	public void map(CommandSender sender, User senderUser, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Loading map... Please wait...");
		ChunkLocation currentCL = ChunkLocation.forLocation(((Player) sender).getLocation());
		final List<ChatComponent> map = new ArrayList<>();
		Map<Town, Integer> townMap = new HashMap<>();
		String worldName = ((Player) sender).getWorld().getName();
		AtomicInteger nom = new AtomicInteger(0);
		final String reps = "ABCDEFGHJKLMNOPQRSTUVWXYZ";
		Map<Integer, Map<Integer, Chunk>> chunkMap = ClaimUtils.getChunkMap(currentCL, worldName);
		for (int z = -6; z <= 6; z++) {
			int amt = 0;
			ChatComponent row = new ChatComponent();
			ChatColor currentColour = ChatColor.GRAY;
			Town lastTown = null;
			int lastChunkOwner = Integer.MIN_VALUE;
			ChunkType lastChunkType = null;
			for (int x = -14; x <= 14; x++) {
				Chunk chunk = chunkMap.get(z).get(x);
				Town town = chunk.getTown();
				int chunkOwner = chunk.getOwnerId();
				ChunkType chunkType = chunk.getType();
				if (lastTown == null) lastTown = town;
				if (lastChunkOwner == Integer.MIN_VALUE) lastChunkOwner = chunkOwner;
				if (lastChunkType == null) lastChunkType = chunkType;
				ChatColor cc = ChatColor.GRAY;
				if (x == 0 && z == 0) {
					cc = ChatColor.LIGHT_PURPLE;
				} else if (chunk.getTownId() > 0) {
					cc = ChatColor.GREEN;
				} else if (chunk.getTownId() == -2) {
					cc = ChatColor.YELLOW;
				} else if (chunk.getTownId() <= -3) {
					cc = ChatColor.DARK_RED;
				}
				if (currentColour != cc || lastTown != town || lastChunkOwner != chunkOwner || lastChunkType != chunkType) {
					row.appendHoverable(currentColour + MessageFormatter.repeat(amt, lastTown.getId() == -1 ? "+" : reps.charAt(townMap.get(lastTown)) + ""), ChatComponent.HoverAction.SHOW_TEXT,
							currentColour + lastTown.getName() + " " + ChatColor.DARK_AQUA + "[" + lastChunkType + "]" + "\n" +
									ChatColor.AQUA + "Owned by: " + TownyDataHandler.getUserById(lastChunkOwner).getName() + "\n" +
									MessageFormatter.optionsHovertext(lastTown) +
									(currentColour == ChatColor.LIGHT_PURPLE ? "\n\n" + currentColour + "You are here." : ""));
					currentColour = cc;
					amt = 0;
				}
				if (town.getId() != -1 && !townMap.containsKey(town)) {
					townMap.put(town, nom.getAndIncrement());
				}
				amt++;
				lastTown = town;
				lastChunkOwner = chunkOwner;
				lastChunkType = chunkType;
			}
			row.appendHoverable(currentColour + MessageFormatter.repeat(amt, lastTown.getId() == -1 ? "+" : reps.charAt(townMap.get(lastTown)) + ""), ChatComponent.HoverAction.SHOW_TEXT,
					currentColour + lastTown.getName() + " " + ChatColor.DARK_AQUA + "[" + lastChunkType + "]" + "\n" +
							ChatColor.AQUA + "Owned by: " + TownyDataHandler.getUserById(lastChunkOwner).getName() + "\n" +
							MessageFormatter.optionsHovertext(lastTown) +
							(currentColour == ChatColor.LIGHT_PURPLE ? "\n\n" + currentColour + "You are here." : ""));
			map.add(row);
		}
		List<String> legend = new ArrayList<>();
		double yaw = ((Player) sender).getLocation().getYaw();
		yaw += 180;
		yaw %= 360;
		if (yaw >= 22.5 && yaw < 337.5) {
			if (yaw < 67.5) {
				legend.add("\\N" + ChatColor.GOLD + "/");
				legend.add("W+E");
				legend.add("/S\\");
			} else if (yaw < 112.5) {
				legend.add("\\N/");
				legend.add("W+" + ChatColor.GOLD + "E");
				legend.add("/S\\");
			} else if (yaw < 157.5) {
				legend.add("\\N/");
				legend.add("W+E");
				legend.add("/S" + ChatColor.GOLD + "\\");
			} else if (yaw < 207.5) {
				legend.add("\\N/");
				legend.add("W+E");
				legend.add("/" + ChatColor.GOLD + "S" + ChatColor.GRAY + "\\");
			} else if (yaw < 252.5) {
				legend.add("\\N/");
				legend.add("W+E");
				legend.add(ChatColor.GOLD + "/" + ChatColor.GRAY + "S\\");
			} else if (yaw < 297.5) {
				legend.add("\\N/");
				legend.add(ChatColor.GOLD + "W" + ChatColor.GRAY + "+E");
				legend.add("/S\\");
			} else {
				legend.add(ChatColor.GOLD + "\\" + ChatColor.GRAY + "N/");
				legend.add("W+E");
				legend.add("/S\\");
			}
		} else {
			legend.add("\\" + ChatColor.GOLD + "N" + ChatColor.GRAY + "/");
			legend.add("W+E");
			legend.add("/S\\");
		}
		legend.add("");
		townMap.keySet().stream().sorted((t1, t2) -> townMap.get(t1) - townMap.get(t2)).forEachOrdered(t -> legend.add(reps.charAt(townMap.get(t)) + ": " + t.getName()));
		AtomicInteger i = new AtomicInteger(0);
		List<ChatComponent> finalMap = map.stream().map(row -> (i.get() < legend.size()) ? row.append(ChatColor.GRAY + "    " + legend.get(i.getAndIncrement())) : row).collect(Collectors.toList());
		String rule = MessageFormatter.createSeparator(ChatColor.GREEN + "Map " + worldName + "(" + currentCL.getX() + ", " + currentCL.getZ() + ")", ChatColor.GOLD, ChatColor.STRIKETHROUGH);
		ChatComponent result = new ChatComponent().append(rule + "\n");
		finalMap.forEach(row -> result.appendComponent(row).append("\n"));
		result.append(rule);
		ChatOutput.clearChat((Player) sender);
		ChatOutput.sendRecentMessages((Player) sender);
		ChatOutput.chat((Player) sender, result);
	}

	@GTownySubcommand (requireInTown = true)
	public void chat(CommandSender sender, User senderUser, String[] args) {
		senderUser.setInTownChat(!senderUser.isInTownChat());
		sender.sendMessage(MessageFormatter.success("Toggled town chat mode to " + ChatColor.RED + senderUser.isInTownChat()));
	}

	@GTownySubcommand (requireInTown = true)
	public void leave(CommandSender sender, User senderUser, String[] args) {
		Town town = senderUser.getTown();
		if (town.getId() <= 0) {
			sender.sendMessage(MessageFormatter.error("Erm try join a town?"));
		} else if (town.getOwnerId() == senderUser.getUserId()) {
			sender.sendMessage(MessageFormatter.error("You can't leave your own town, try delete instead."));
		} else {
			if (args.length >= 1 && args[0].equalsIgnoreCase("confirm")) {
				town.removeMember(senderUser);
				sender.sendMessage(MessageFormatter.success("You left " + town.getName() + "."));
			} else {
				ChatOutput.confirmAction((Player) sender, "Confirm leaving town", "Are you sure you want to leave your town?", "/town leave confirm");
			}
		}
	}

	@GTownySubcommand (requireTownPermission = TownPermissions.ManageTownMembers)
	public void kick(CommandSender sender, User senderUser, String[] args) {
		Town town = senderUser.getTown();
		if (args.length >= 1) {
			String name = args[0];
			if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
				Player p = Bukkit.getPlayerExact(name);
				if (p != null) {
					name = p.getName();
				}
				User kickUser = User.forName(name);
				if (kickUser != null && kickUser.getTownId() > 0 && kickUser.getTownId() == senderUser.getTownId()) {
					town.removeMember(kickUser);
					sender.sendMessage(MessageFormatter.error("Player kicked."));
				} else {
					sender.sendMessage(MessageFormatter.error("The player you specified isn't in your town."));
				}
			} else {
				ChatOutput.confirmAction((Player) sender, "Confirm kick " + args[0], "Are you sure you want to kick " + args[0] + "?", "/town kick " + args[0]);
			}
		} else {
			String rule = MessageFormatter.createSeparator(ChatColor.GREEN + "Select user to kick");
			ChatComponent comp = new ChatComponent().append(rule);
			town.getMembers().stream().sorted((u1, u2) -> ComparatorUtil.compareStringIgnoreCase(u1.getName(), u2.getName())).forEach(member -> comp.append(member.getName()).setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/town kick " + member.getName()).setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Kick this player from town."));
			ChatOutput.chat((Player) sender, comp.append("\n" + rule));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.SetSpawn)
	public void setSpawn(CommandSender sender, User senderUser, String[] args) {
		senderUser.getTown().setSpawn(new TownSpawn(((Player) sender).getLocation()));
		sender.sendMessage(MessageFormatter.success("Spawn set."));
	}

	@GTownySubcommand
	public void spawn(CommandSender sender, User senderUser, String[] args) {
		Town town = null;
		if (args.length >= 1) {
			town = TownyDataHandler.getTownByName(args[0]);
		}
		if (town == null || town.getId() == -1) {
			town = senderUser.getTown();
		}
		if (town.getId() > 0) {
			if (town.getSpawn().getY() >= 1) {
				Player p = (Player) sender;
				p.teleport(town.getSpawn(), PlayerTeleportEvent.TeleportCause.COMMAND);
				p.sendMessage(ChatColor.GOLD + "Teleporting...");
			} else {
				sender.sendMessage(MessageFormatter.error("The town spawn is not a valid location."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Must specify a valid town or none if you are teleporting back to your own town."));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.ManageTownMembers)
	public void invite(CommandSender sender, User senderUser, String[] args) {
		String name = null;
		if (args.length >= 1) {
			Player p = Bukkit.getPlayer(args[0]);
			if (p != null) {
				name = p.getName();
			} else {
				name = args[0];
			}
		}
		if (name != null) {
			User target = User.forName(name);
			Town town = senderUser.getTown();
			if (target != null && target.getUserId() > 0) {
				if (!getInvites().containsKey(town)) {
					getInvites().put(town, new ConcurrentHashMap<>());
				}
				Map<Integer, Long> invites = getInvites().get(town);
				if (!invites.containsKey(target.getUserId()) && invites.get(target.getUserId()) < System.currentTimeMillis()) {
					invites.put(target.getUserId(), System.currentTimeMillis() + 60000);
					sender.sendMessage(MessageFormatter.success("Invite sent. It will expire in 60 seconds if the recipient doesn't join."));
					Player targetPlayer = Bukkit.getPlayer(target.getName());
					if (targetPlayer != null) {
						ChatOutput.confirmAction(targetPlayer, "Invitation to join town.", "Do you want to join " + town.getName() + "?\nInvite will expire in 60 seconds.", "/town join " + town.getName());
					}
				} else {
					sender.sendMessage(MessageFormatter.success("Player is already invited."));
				}
			} else {
				sender.sendMessage(MessageFormatter.success("Invalid player."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Player not found."));
		}
	}

	@GTownySubcommand
	public void join(CommandSender sender, User senderUser, String[] args) {
		if (senderUser.getTownId() <= 0) {
			if (args.length >= 1) {
				Optional<Town> targetTown = TownyDataHandler.getInvites().keySet().stream().filter(town -> {
					if (town.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
						Map<Integer, Long> invites = getInvites().get(town);
						return invites.containsKey(senderUser.getUserId()) && invites.get(senderUser) < System.currentTimeMillis();
					}
					return false;
				}).findFirst();
				if (targetTown.isPresent()) {
					targetTown.get().addMember(senderUser);
					sender.sendMessage(MessageFormatter.success("Joined town " + targetTown.get().getName()));
				} else {
					sender.sendMessage(MessageFormatter.error("You aren't invited to that town, or the invite expired."));
				}
			} else {
				sender.sendMessage(MessageFormatter.error("Specify the town you want to join..."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("You are already in a town. Leave your current town first by doing `/town leave'."));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.ManageTownMembers)
	public void exile(CommandSender sender, User senderUser, String[] args) {
		Town town = senderUser.getTown();
		if (args.length >= 1) {
			String name = args[0];
			if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
				Player p = Bukkit.getPlayerExact(name);
				if (p != null) {
					name = p.getName();
				}
				User exileUser = User.forName(name);
				if (exileUser != null && exileUser.getUserId() > 0) {
					if (exileUser.getTownId() > 0 && exileUser.getTownId() == senderUser.getTownId()) {
						town.removeMember(exileUser);
					}
					town.exile(exileUser);
					sender.sendMessage(MessageFormatter.error("Player exiled."));
				} else {
					sender.sendMessage(MessageFormatter.error("The player you specified isn't in your town."));
				}
			} else {
				ChatOutput.confirmAction((Player) sender, "Confirm kick " + args[0], "Are you sure you want to kick " + args[0] + "?", "/town kick " + args[0]);
			}
		} else {
			String rule = MessageFormatter.createSeparator(ChatColor.GREEN + "Select user to exile");
			ChatComponent comp = new ChatComponent().append(rule);
			town.getMembers().stream().sorted((u1, u2) -> ComparatorUtil.compareStringIgnoreCase(u1.getName(), u2.getName())).forEach(member -> comp.append(member.getName()).setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/town kick " + member.getName()).setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Exile this player from town."));
			ChatOutput.chat((Player) sender, comp.append("\n" + rule));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.SetWarp)
	public void setWarp(CommandSender sender, User senderUser, String[] args) {
		if (args.length >= 1) {
			Town town = senderUser.getTown();
			String warpName = args[0];
			if (town.getWarps().size() < 16) {
				if (!town.getWarps().stream().filter(warp -> warp.getName().equalsIgnoreCase(warpName)).findFirst().isPresent()) {
					town.addWarp(new TownWarp(warpName, ((Player) sender).getLocation()));
					sender.sendMessage(MessageFormatter.success("Warp added."));
				} else {
					sender.sendMessage(MessageFormatter.error("A teleport with that name already exists."));
				}
			} else {
				sender.sendMessage(MessageFormatter.error("You can't have mre than 16 warps."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Please specify teleport name."));
		}
	}

	@GTownySubcommand (requireInTown = true)
	public void warp(CommandSender sender, User senderUser, String[] args) {
		if (args.length >= 1) {
			Town town = senderUser.getTown();
			String warpName = args[0];
			Optional<TownWarp> targetWarp = town.getWarps().stream().filter(warp -> warp.getName().equalsIgnoreCase(warpName)).findFirst();
			if (targetWarp.isPresent()) {
				((Player) sender).teleport(targetWarp.get().getLocation());
				sender.sendMessage(MessageFormatter.teleport(targetWarp.get().getName()));
			} else {
				sender.sendMessage(MessageFormatter.error("A warp with that name does not exists."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Please specify a warp name."));
		}
	}

	@GTownySubcommand (requireInTown = true, requireTownPermission = TownPermissions.SetWarp)
	public void delWarp(CommandSender sender, User senderUser, String[] args) {
		if (args.length >= 1) {
			Town town = senderUser.getTown();
			String warpName = args[0];
			Optional<TownWarp> targetWarp = town.getWarps().stream().filter(warp -> warp.getName().equalsIgnoreCase(warpName)).findFirst();
			if (targetWarp.isPresent()) {
				town.removeWarp(targetWarp.get());
				sender.sendMessage(MessageFormatter.success("Warp deleted."));
			} else {
				sender.sendMessage(MessageFormatter.error("A warp with that name does not exists."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Please specify a warp name."));
		}
	}
}