package org.reborncraft.gtowny.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.reborncraft.gtowny.GTowny;
import org.reborncraft.gtowny.chat.ChatComponent;
import org.reborncraft.gtowny.chat.ChatOutput;
import org.reborncraft.gtowny.cmds.out.MessageFormatter;
import org.reborncraft.gtowny.cmds.utils.ClaimUtils;
import org.reborncraft.gtowny.data.Chunk;
import org.reborncraft.gtowny.data.Town;
import org.reborncraft.gtowny.data.TownyDataHandler;
import org.reborncraft.gtowny.data.User;
import org.reborncraft.gtowny.data.internal.ChunkType;
import org.reborncraft.gtowny.data.internal.TownPermissions;

import java.util.Map;
import java.util.Optional;

import static org.reborncraft.gtowny.data.internal.TownPermissions.Claim;

public class ChunkCommand implements TownyCommandExecutor {
	@GTownySubcommand (requireTownPermission = Claim)
	public void claim(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		if (c.getTownId() == -1) {
			sender.sendMessage(ChatColor.GOLD + "Mapping area... Please wait...");
			Map<Town, Integer> bd = ClaimUtils.getChunkMapBreakdown(c.getChunkLocation(), c.getWorld());
			if (bd.size() < 5) {
				senderUser.getTown().claimChunk(c);
				info(sender, senderUser, new String[]{});
				sender.sendMessage(MessageFormatter.success("Claimed 1 chunk for your town."));
			} else {
				sender.sendMessage(MessageFormatter.error("This area is too crowded, try find a place where `/town map' shows less than 5 towns."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("This chunk cannot be claimed because it isn't from the wilderness."));
		}
	}

	@GTownySubcommand (chunkTownMustBeSame = true, requireTownPermission = Claim)
	public void unclaim(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		TownyDataHandler.TOWN_WILDERNESS.claimChunk(c);
		info(sender, senderUser, new String[]{});
		sender.sendMessage(MessageFormatter.success("Unclaimed 1 chunk from your town."));
	}

	@GTownySubcommand (chunkTownMustBeSame = true, requireTownPermission = TownPermissions.ModifyChunkType)
	public void settype(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		if (args.length >= 1) {
			try {
				c.setType(ChunkType.forBit(Integer.valueOf(args[0])));
				info(sender, senderUser, new String[]{});
				sender.sendMessage(MessageFormatter.success("Changed chunk type."));
			} catch (NumberFormatException nfe) {
				sender.sendMessage(MessageFormatter.error("Invalid type bit."));
			}
		} else {
			sender.sendMessage(MessageFormatter.info("Click on one of the options to set."));
		}
	}

	@GTownySubcommand
	public void map(CommandSender sender, User senderUser, String[] args) {
		GTowny.getTownCE().map(sender, senderUser, args);
	}

	@GTownySubcommand
	public void buy(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		if (senderUser.getTownId() == c.getTownId() || c.getType() == ChunkType.Consulate) {
			int price = c.getSalePrice();
			if (GTowny.getVault().deduct((Player) sender, price)) {
				//noinspection deprecation
				GTowny.getVault().increase(c.getOwner().getName(), price);
				c.setOwner(senderUser);
				c.getMembers().forEach(c::removeMember);
				c.setSalePrice(-1);
				info(sender, senderUser, new String[]{});
				sender.sendMessage(MessageFormatter.success("Bought chunk, deducted $" + price + " from your account."));
			} else {
				sender.sendMessage(MessageFormatter.error("Transaction error, do you have enough money?"));
			}
		} else {
			info(sender, senderUser, new String[]{});
			sender.sendMessage(MessageFormatter.error("Cannot claim this chunk, it isn't consulate chunk or your town's."));
		}
	}

	@GTownySubcommand
	public void sell(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		if (c.getOwnerId() == senderUser.getUserId() || (senderUser.getTownId() == c.getTownId() && senderUser.getRank().hasPermission(TownPermissions.LandManagement))) {
			if (args[0].equalsIgnoreCase("cancel")) {
				c.setSalePrice(-1);
				info(sender, senderUser, new String[]{});
				sender.sendMessage(MessageFormatter.success("Cancelled sale."));
			} else {
				try {
					c.setSalePrice(Integer.parseInt(args[0]));
					info(sender, senderUser, new String[]{});
					sender.sendMessage(MessageFormatter.success("Price set/updated!"));
				} catch (NumberFormatException nfe) {
					sender.sendMessage(MessageFormatter.error("Invalid price. (Specify a whole number without a dollar sign in front.)"));
				}
			}
		} else {
			sender.sendMessage(MessageFormatter.error("You don't own this chunk or have the LandManagement permission for this town."));
		}
	}

	@GTownySubcommand (chunkTownMustBeSame = true, requireTownPermission = TownPermissions.LandManagement)
	public void own(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		c.setOwnerId(senderUser.getUserId());
		c.setSalePrice(-1);
		info(sender, senderUser, new String[]{});
		sender.sendMessage(MessageFormatter.success("You now own this chunk."));
	}

	@GTownySubcommand (chunkTownMustBeSame = true, requireTownPermission = TownPermissions.LandManagement)
	public void unown(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		c.setOwnerId(-1);
		c.setSalePrice(-1);
		info(sender, senderUser, new String[]{});
		sender.sendMessage(MessageFormatter.success("This chunk is now public town land."));
	}

	@GTownySubcommand
	public void info(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		Town t = c.getTown();
		String rule = MessageFormatter.createSeparator(ChatColor.GREEN + "Chunk " + c.getWorld() + "(" + c.getChunkLocation().getX() + ", " + c.getChunkLocation().getZ() + ")", ChatColor.GOLD, ChatColor.STRIKETHROUGH);
		ChatComponent comp = new ChatComponent()
				.append(rule + "\n")
				.append("Claimed by town: ").setColour(ChatColor.GOLD)
				.append(MessageFormatter.getTownName(t) + "\n")
				.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Click for more information.")
				.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/town info " + t.getName())
				.append(ChatColor.GOLD + "Owner: " + ChatColor.GREEN + c.getOwner().getName() + "\n");
		if (c.getOwnerId() > 0) {
			comp.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Click for more information.")
					.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/user " + c.getOwner().getName());
		}
		comp.append("Type: ").setColour(ChatColor.GOLD)
				.appendComponent(MessageFormatter.chunkTypeClickable(senderUser, c))
				.append("\n");

		comp.append(ChatColor.GOLD + "Ownership: ").setColour(ChatColor.GOLD);
		if (c.getSalePrice() > 0) {
			if (c.getTownId() == senderUser.getTownId() || c.getOwnerId() == senderUser.getUserId()) {
				comp.append("[Price $" + c.getSalePrice() + "] ").setColour(ChatColor.GREEN)
						.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Click to adjust selling price.")
						.setClickable(ChatComponent.ClickAction.SUGGEST_COMMAND, "/chunk sell ");
			} else {
				comp.append("[Selling $" + c.getSalePrice() + "] ").setColour(ChatColor.GREEN);
			}
			if (c.getOwnerId() == senderUser.getUserId()) {
				comp.append("[Cancel] ").setColour(ChatColor.DARK_AQUA)
						.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Cancel selling this chunk.")
						.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/chunk sell cancel");
			} else {
				if (c.getTownId() == senderUser.getTownId() || c.getType() == ChunkType.Consulate) {
					comp.append(ChatColor.GREEN + "[Buy] ")
							.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Click to buy this chunk.")
							.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/chunk buy");
				}
			}
		} else if (c.getTownId() == senderUser.getTownId() && senderUser.getRank().hasPermission(TownPermissions.LandManagement)) {
			comp.append("[Sell] ").setColour(ChatColor.RED)
					.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Set sell price.")
					.setClickable(ChatComponent.ClickAction.SUGGEST_COMMAND, "/chunk sell ");
		}
		if (c.getOwnerId() == senderUser.getUserId()) {
			comp.append("[Unown] ")
					.setColour(ChatColor.DARK_PURPLE)
					.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Unown the chunk to be a public piece of town land.")
					.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/chunk unown");
		} else if ((c.getTownId() == senderUser.getTownId() && senderUser.getRank().hasPermission(TownPermissions.LandManagement))) {
			comp.append(ChatColor.AQUA + "[Own] ")
					.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Click to own this chunk with your permissions.")
					.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/chunk own");
		}

		if (senderUser.getRank().hasPermission(TownPermissions.Claim) || c.getOwnerId() == senderUser.getUserId()) {
			if (c.getTownId() == senderUser.getTownId() || c.getOwnerId() == senderUser.getUserId()) {
				comp.append("[Unclaim] ")
						.setColour(ChatColor.DARK_RED)
						.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Unclaim the land into the wilderness.")
						.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/chunk unclaim");
			} else if (c.getTownId() == -1) {
				comp.append("[Claim] ")
						.setColour(ChatColor.GREEN)
						.setHoverable(ChatComponent.HoverAction.SHOW_TEXT, ChatColor.GRAY + "Claim the land into your town.")
						.setClickable(ChatComponent.ClickAction.RUN_COMMAND, "/chunk claim");
			}
		}
		if (c.getMemberIds().size() >= 1) {
			comp.append("\n");
			comp.append("Members: ").setColour(ChatColor.GOLD);
			c.getMembers().forEach(member -> {
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

	@GTownySubcommand (requireChunkOwner = true)
	public void addUser(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		if (args.length >= 1) {
			Player p = Bukkit.getPlayer(args[0]);
			if (p != null) {
				c.addMember(User.forPlayer(p));
				info(sender, senderUser, new String[]{});
				sender.sendMessage(MessageFormatter.success("Added " + p.getName() + " to your chunk."));
			} else {
				sender.sendMessage(MessageFormatter.error("No player by the name " + args[0] + " was found."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Please specify an online player's name."));
		}
	}

	@GTownySubcommand (requireChunkOwner = true)
	public void removeUser(CommandSender sender, User senderUser, String[] args) {
		Chunk c = senderUser.getCurrentChunk();
		if (args.length >= 1) {
			Optional<User> member = c.getMembers().stream().filter(user -> user.getName().toLowerCase().startsWith(args[0].toLowerCase())).findFirst();
			if (member.isPresent()) {
				c.removeMember(member.get());
				info(sender, senderUser, new String[]{});
				sender.sendMessage(MessageFormatter.success("Removed " + member.get().getName() + " from your chunk."));
			} else {
				sender.sendMessage(MessageFormatter.error("No player was found by that name."));
			}
		} else {
			sender.sendMessage(MessageFormatter.error("Please specify a player name from the list of members."));
		}
	}

	@GTownySubcommand (chunkTownMustBeSame = true, requireTownPermission = TownPermissions.Terraform)
	public void terraform(CommandSender sender, User senderUser, String[] args) { // TODO Future Feature.
		sender.sendMessage(MessageFormatter.error("Feature is not implemented yet. #Soon™"));
	}
}
