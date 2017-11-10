package io.github.loldatsec.mcplugins.haloplus.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import io.github.loldatsec.mcplugins.haloplus.HaloPlus;
import io.github.loldatsec.mcplugins.haloplus.utils.SpecialChat;
import net.md_5.bungee.api.ChatColor;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class GameManagerDisplayInterpreter {

	public static Scoreboard score;
	public static int o = 0;

	@SuppressWarnings("deprecation")
	public static void updateScoreboard() {
		if (score == null) {
			score = Bukkit.getScoreboardManager().getNewScoreboard();
		}
		if (o >= 10) {
			o = 0;
		}
		o++;
		Objective sidebar = score.registerNewObjective("GameList" + o, "dummy");
		int s = 99;
		sidebar.setDisplayName("\u00a75\u00a7lRunning Games");
		for (Game g : GameManager.games.values()) {
			if (g instanceof Game) {
				sidebar.getScore((g.isRunning() ? "\u00a7a" : "\u00a7c") + "\u00a7l» \u00a7e" + g.w.getName() + " \u00a7d" + g.vote2start.size() + "/\u00a76" + g.w.getPlayers().size() + "/" + HaloPlus.playersToStartGame).setScore(s--);
				if (g.isRunning() && !g.isEnded()) {
					int bs = 0;
					int rs = 0;
					int bm = g.gameTeams.get(GameTeamEnum.BLUE).members.size();
					int rm = g.gameTeams.get(GameTeamEnum.RED).members.size();
					if (g.roundsDone >= 1) {
						bs = g.gameTeams.get(GameTeamEnum.BLUE).score;
						rs = g.gameTeams.get(GameTeamEnum.RED).score;
					}
					sidebar.getScore("\u00a7e\u00a7l→ " + SpecialChat.twoWayGameprogress(bs, rs)).setScore(s--);
					sidebar.getScore("\u00a7e\u00a7l→ " + "\u00a7b" + bm + "\u00a77 vs \u00a7c" + rm + " \u00a77(\u00a78" + (g.w.getPlayers().size() - bm - rm) + "\u00a77)").setScore(s--);
				}
			}
		}
		Objective gc = score.getObjective(DisplaySlot.SIDEBAR);
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		if (gc != null) {
			gc.unregister();
		}
		for (Team t : score.getTeams()) {
			t.unregister();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			Team t = score.registerNewTeam(p.getUniqueId().toString().substring(0, 14));
			String pre = ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(p).getPrefix()) + "\u00a76";
			t.setPrefix(pre.substring(0, pre.length() > 16 ? 16 : pre.length()));
			t.addPlayer(p);
		}
	}

	public static void showInventory(Player p) {
		Inventory inventory = Bukkit.createInventory(p, 18, "\u00a70\u00a7lRunning Halo Games");
		p.openInventory(inventory);
	}

	public static void updateInventories() {
		Material noPlayers = Material.REDSTONE_BLOCK;
		Material waiting = Material.GOLD_BLOCK;
		Material running = Material.EMERALD_BLOCK;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getOpenInventory() instanceof InventoryView) {
				InventoryView inventory = p.getOpenInventory();
				if (inventory.getTitle() != null && inventory.getTitle().equalsIgnoreCase("\u00a70\u00a7lRunning Halo Games")) {
					int s = 0;
					for (Game g : GameManager.games.values()) {
						ItemStack i = new ItemStack(!(g instanceof Game) || g.w.getPlayers().size() <= 0 ? noPlayers : g.isRunning() ? running : waiting);
						if (g.w.getPlayers().size() >= 1) {
							i.setAmount(g.w.getPlayers().size());
						}
						ItemMeta im = i.getItemMeta();
						im.setDisplayName("\u00a7" + (g.isRunning() ? "a" : "c") + "\u00a7l" + g.w.getName());
						List<String> lore = new ArrayList<String>();
						lore.add("\u00a76Players:");
						lore.add("\u00a7e\u00a7l→ \u00a76" + g.w.getPlayers().size());
						if (g.isRunning()) {
							int bs = 0;
							int rs = 0;
							if (g.roundsDone >= 1) {
								bs = g.gameTeams.get(GameTeamEnum.BLUE).score;
								rs = g.gameTeams.get(GameTeamEnum.RED).score;
							}
							lore.add("\u00a76Progress:");
							lore.add("\u00a7e\u00a7l→ " + SpecialChat.twoWayGameprogress(bs, rs));
							lore.add("\u00a76Teams:");
							for (Player gtp : g.w.getPlayers()) {
								lore.add("\u00a7e\u00a7l→ " + GameTeamEnum.getColour(GameManager.getGameTeam(gtp)) + gtp.getName());
							}
						}
						im.setLore(lore);
						i.setItemMeta(im);
						inventory.setItem(s++, i);
					}
					ItemStack i = new ItemStack(Material.NETHER_STAR);
					i.setAmount(Bukkit.getWorld("Lobby").getPlayers().size());
					ItemMeta im = i.getItemMeta();
					im.setLore(Arrays.asList(new String[] { "\u00a76Go back to lobby" }));
					im.setDisplayName("\u00a7b\u00a7lLobby");
					i.setItemMeta(im);
					inventory.setItem(17, i);
				}
			}
		}
	}

	public static void showText(CommandSender sender) {
		for (Game g : GameManager.games.values()) {
			sender.sendMessage("\u00a7" + (g.isRunning() ? "a" : "c") + "\u00a7l" + g.w.getName() + "\u00a76:");
			List<String> ps = new ArrayList<String>();
			if (g.isRunning()) {
				for (Player gtp : g.w.getPlayers()) {
					ps.add(GameTeamEnum.getColour(GameManager.getGameTeam(gtp)) + gtp.getName());
				}
				sender.sendMessage("\u00a7e\u00a7l→ \u00a76Players: [" + String.join("\u00a76, ", ps) + "\u00a76]");
				int bs = 0;
				int rs = 0;
				if (g.roundsDone >= 1) {
					bs = g.gameTeams.get(GameTeamEnum.BLUE).score;
					rs = g.gameTeams.get(GameTeamEnum.RED).score;
				}
				sender.sendMessage("\u00a7e\u00a7l→ \u00a76Progress: " + SpecialChat.twoWayGameprogress(bs, rs));
			} else {
				for (Player gp : g.w.getPlayers()) {
					ps.add(gp.getName());
				}
				sender.sendMessage("\u00a7e\u00a7l→ \u00a76Players: \u00a77[" + String.join(", ", ps) + "]");
			}
		}
	}
}
