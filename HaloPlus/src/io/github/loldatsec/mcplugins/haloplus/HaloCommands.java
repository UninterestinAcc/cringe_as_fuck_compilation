package io.github.loldatsec.mcplugins.haloplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.loldatsec.mcplugins.haloplus.game.Game;
import io.github.loldatsec.mcplugins.haloplus.game.GameManager;
import io.github.loldatsec.mcplugins.haloplus.game.GameManagerDisplayInterpreter;
import io.github.loldatsec.mcplugins.haloplus.utils.RankupSequence;
import io.github.loldatsec.mcplugins.haloplus.utils.SpecialChat;

public class HaloCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("spectate") || cmd.getName().equalsIgnoreCase("spec") || cmd.getName().equalsIgnoreCase("view")) {
			spectateCommand(sender, args);
			return true;
		} else if (cmd.getName().toLowerCase().startsWith("gm")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("haloplus.gamemode")) {
					if (cmd.getName().toLowerCase().endsWith("c")) {
						changeGameMode((Player) sender, GameMode.CREATIVE);
					} else {
						changeGameMode((Player) sender, GameMode.ADVENTURE);
					}
				} else {
					sender.sendMessage("\u00a74Error> \u00a7cYou do not have permissions.");
				}
			} else {
				sender.sendMessage("\u00a74Error> \u00a7cYou are not a player.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("leave")) {
			if (sender instanceof Player) {
				((Player) sender).teleport(Bukkit.getWorld("Lobby").getSpawnLocation());
				((Player) sender).setGameMode(GameMode.ADVENTURE);
			} else {
				sender.sendMessage("\u00a74Error> \u00a7cYou are not a player.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("start")) {
			if (sender instanceof Player) {
				voteStartGame((Player) sender);
			} else {
				sender.sendMessage("\u00a74Error> \u00a7cYou are not a player.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("game") || cmd.getName().equalsIgnoreCase("games")) {
			gameCommand(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("stats") || cmd.getName().equalsIgnoreCase("stat")) {
			statsCommand(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("onvoteaction")) {
			playerVote(sender, args);
			return true;
		}
		sender.sendMessage("\u00a74Error> \u00a7cUnrecognized command \u00a74" + label + "\u00a7c or no permissions.");
		return true;
	}

	public void playerVote(CommandSender sender, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			Player p = Bukkit.getPlayer(String.join("", args));
			p.incrementStatistic(Statistic.CRAFT_ITEM, Material.GOLD_SPADE, 1);
			for (Player ap : Bukkit.getWorld("Lobby").getPlayers()) {
				ap.sendMessage("\u00a72Vote System> \u00a7a" + p.getName() + " just voted and received 200 extra game experiences! /vote to vote.");
			}
		} else {
			sender.sendMessage("\u00a74Error> \u00a7cYou are not Sir Console.");
		}
	}

	public void gameCommand(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args[0].equalsIgnoreCase("Auto")) {
					GameManager.joinGame(p);
				} else {
					if (!GameManager.joinGame(p, String.join("", args))) {
						sender.sendMessage("\u00a75Game> \u00a7dCannot join " + String.join("", args));
					} else {
						// Success
					}
				}
			} else {
				sender.sendMessage("\u00a74Error> \u00a7cYou are not a player.");
			}
		} else {
			if (sender instanceof Player) {
				GameManagerDisplayInterpreter.showInventory((Player) sender);
			} else {
				GameManagerDisplayInterpreter.showText(sender);
			}
		}
	}

	public void voteStartGame(Player p) {
		if (p.getWorld().getName().equalsIgnoreCase("Lobby")) {
			p.sendMessage("\u00a75Game> \u00a7dLobby cannot be a game.");
		} else {
			Game g = GameManager.getGame(p);
			if (!g.vote2start.contains(p) && !g.isRunning() && !g.isEnded()) {
				g.vote2start.add(p);
				for (Player ap : g.w.getPlayers()) {
					ap.sendMessage("\u00a75Game> \u00a7d" + g.vote2start.size() + "/" + g.w.getPlayers().size() + " voted to start.");
				}
			} else {
				p.sendMessage("\u00a75Game> \u00a7dYou already voted.");
			}
		}
	}

	public void changeGameMode(Player p, GameMode gm) {
		p.setGameMode(gm);
		p.sendMessage("\u00a76Gamemode> \u00a7eSet gamemode to \u00a7c" + gm.toString());
	}

	public void spectateCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length >= 1) {
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase("leave") && p.getGameMode() == GameMode.SPECTATOR) {
					p.setGameMode(GameMode.ADVENTURE);
					p.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());
				} else if (subCommand.equalsIgnoreCase("help")) {
					SpecialChat.tell(sender,
						new String[] { "Usage for /spectate", "\u00a7a\u00a7l/spectate leave - Leaves spectator mode.", "\u00a7a\u00a7l/spectate <player> - Spectates that player.", "\u00a7a\u00a7l/spectate help - This help message." },
						ChatColor.GOLD);
				} else {
					if (p.getWorld().getName().equalsIgnoreCase("Lobby")) {
						Player s = Bukkit.getPlayer(subCommand);
						if (s instanceof Player) {
							p.setGameMode(GameMode.SPECTATOR);
							p.teleport(s);
						} else {
							sender.sendMessage("\u00a76Spectate> \u00a7ePlayer \u00a76" + subCommand + " \u00a7enot found.");
						}
					}
				}
			} else {
				sender.sendMessage("\u00a76Spectate> \u00a7eYou need to be in the lobby world or in spectator mode.");
			}
		} else {
			sender.sendMessage("\u00a76Spectate> \u00a7eYou are not a player.");
		}
	}

	public void statsCommand(CommandSender sender, String[] args) {
		String l1 = "\u00a76\u00a7lStats for ";
		String kills = "\u00a7aKills: ";
		String deaths = "\u00a7cDeaths: ";
		String roundWins = "\u00a7dRounds won: ";
		String gameWins = "\u00a75Games won: ";
		String votes = "\u00a7aVotes (/vote): ";
		Player l;
		if (args.length <= 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("\u00a74Error> \u00a7cCannot stat CONSOLE as CONSOLE is not a player.");
				return;
			} else {
				l1 += sender.getName();
				l = (Player) sender;
			}
		} else {
			Player p = Bukkit.getPlayer(String.join("", args));
			if (p instanceof Player) {
				l1 += p.getName();
				l = p;
			} else {
				sender.sendMessage("\u00a74Error> \u00a7cPlayer not found.");
				return;
			}
		}
		int level = HaloPlus.getLevel(l);
		long xp = HaloPlus.getXP(l);
		long levelxp = RankupSequence.getLongByPos(level);
		long nextlevelxp = RankupSequence.getLongByPos(level + 1);
		l1 += " \u00a76(Level: \u00a7a" + level + "\u00a76, XP: \u00a7a" + xp + "\u00a76, \u00a7a" + (xp - levelxp) + "/" + (nextlevelxp - levelxp) + "\u00a76 xp to rank up." + ")";
		kills += l.getStatistic(Statistic.PLAYER_KILLS);
		deaths += l.getStatistic(Statistic.DEATHS);
		roundWins += l.getStatistic(Statistic.CRAFT_ITEM, Material.DIAMOND_SPADE);
		gameWins += l.getStatistic(Statistic.CRAFT_ITEM, Material.DIAMOND_SWORD);
		String rsf = "\u00a7eReload speed modifier: \u00a7a" + HaloPlus.getReloadSpeed(l, 5) + "%";
		votes += l.getStatistic(Statistic.CRAFT_ITEM, Material.GOLD_SPADE);
		SpecialChat.tell(sender, new String[] { l1, "", kills + "\u00a77, " + deaths, roundWins + "\u00a77, " + gameWins, rsf, votes }, ChatColor.GOLD);
	}
}
